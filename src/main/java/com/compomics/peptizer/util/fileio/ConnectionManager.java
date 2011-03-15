package com.compomics.peptizer.util.fileio;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Kenny
 * Date: 14-mrt-2008
 * Time: 14:36:00
 * To change this template use File | Settings | File Templates.
 */

/**
 * This Class has a Singleton structure that holds one Connection.
 */
public class ConnectionManager {
	// Class specific log4j logger for ConnectionManager instances.
	 private static Logger logger = Logger.getLogger(ConnectionManager.class);

    /**
     * Singleton instance.
     */
    private static ConnectionManager iSingleton = null;
    private Connection iConnection = null;

    /**
     * Empty constructor!
     */
    private ConnectionManager() {
    }

    /**
     * Returns the Singleton ConnectionManager instance.
     *
     * @return ConnectionManager.
     */
    public static ConnectionManager getInstance() {
        if (iSingleton == null) {
            iSingleton = new ConnectionManager();
        }
        return iSingleton;
    }

    /**
     * Returns the Connection. Returns null if not setup.
     *
     * @return Connection to the ms_lims database.
     */
    public Connection getConnection() {
        return iConnection;
    }

    /**
     * Returns whether a connection is availlable.
     *
     * @return boolean.
     */
    public boolean hasConnection() {
        return (iConnection != null);
    }

    /**
     * This method tests a simple query on the database connection, if it exists!
     *
     * @return boolean whether or not a query could be executed to the Connection in the ConnectionManager.
     * @throws java.sql.SQLException while thesting the Connection.
     */
    public boolean testConnection() throws SQLException {
        if (hasConnection()) {
            String lQuery = "Select * from project group by projectid";
            PreparedStatement ps = ConnectionManager.getInstance().getConnection().prepareStatement(lQuery);

            ResultSet rs = ps.executeQuery();

            // If the Query could be executed, a resultset will be returned and a call the next() method must be true.
            return rs.next();
        } else {
            return false;
        }
    }

    /**
     * Sets the current database connection.
     */
    public void setConnection(Connection aConnection) {
        // If there was allready a connection set, close that one first!!
        if(!aConnection.equals(iConnection)){
            closeConnection();
            iConnection = aConnection;
        }
    }

    /**
     * Close the current database connection.
     */
    public void closeConnection() {
        if (hasConnection()) {
            try {
                iConnection.close();
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
