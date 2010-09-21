package com.compomics.peptizer.main;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.model.AbstractTableRow;
import com.compomics.peptizer.gui.model.TableRowManager;
import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.interfaces.AgentAggregator;
import com.compomics.peptizer.interfaces.PeptideIdentificationIterator;
import com.compomics.peptizer.util.AgentAggregatorFactory;
import com.compomics.peptizer.util.AgentFactory;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.IdentificationFactory;
import com.compomics.peptizer.util.enumerator.AgentAggregationResult;
import com.compomics.peptizer.util.fileio.MatLogger;
import com.compomics.peptizer.util.fileio.ValidationSaveToCSV;
import com.compomics.util.enumeration.CompomicsTools;
import com.compomics.util.general.CommandLineParser;
import com.compomics.util.io.PropertiesManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 26-nov-2007
 * Time: 16:45:58
 */

/**
 * Class description: ------------------ This class was developed to start peptizer by command line.
 */
public class Peptizer_MsLims {
// Class specific log4j logger for Peptizer instances.
    private static Logger logger = Logger.getLogger(Peptizer_MsLims.class);
    
    /**
     * Default Constructor.
     */
    public Peptizer_MsLims() {
    }

    /**
     * The main method takes the start-up parameters and processes the input to .
     *
     * @param args String[] with the start-up arguments.
     */
    public static void main(String[] args) {

        PropertiesManager.getInstance().updateLog4jConfiguration(logger, CompomicsTools.PEPTIZER);
        logger.debug("Starting peptizer-cli-mslims");
        logger.debug("OS : " + System.getProperties().getProperty("os.name"));

        // First see if we should output anything useful.
        if (args == null || args.length == 0) {
            flagError("Usage:\n\tPeptizer  " +
                    "--project <projectid> " +
                    "--driver <'ms_lims database driver'> " +
                    "--url <'ms_lims database url'> " +
                    "--user <'ms_lims username'> " +
                    "--pass <'ms_lims password for username'> " +
                    "--target <target_file_name>" +
                    "--table <table_setting_xml_file>" +
                    "--agent <agent_setting_xml_file>" +
                    "--aggregator <aggregator_setting_xml_file>" +
                    "--general <general_setting_xml_file>" +
                    "\n" +
                    "\n\t--project requires a ms_lims project id to be profiled." +
                    "\n\t--driver is a Java classpath to the Driver class. ex: 'com.mysql.jdbc.Driver'." +
                    "\n\t--url is the path to the databse. ex: 'jdbc:mysql://localhost/ms_lims/" +
                    "\n\t--user is the username for the ms_lims database." +
                    "\n\t--pass is the authentication for the specified username." +
                    "\n\t--target is the csv output of the profiling." +
                    "\n\t--agent is the configuration file for agents that define the profile. Each agent will be a csv column." +
                    "\n\t--aggregator is the configuration file for the aggregator that must judge a peptideidentification based on the agents. The first aggregator in the configuration file will be active!!" +
                    "\n\t--general is the general configuration file that contains parameters such as confidence." +
                    "\n\t--table is configures the tablerows, wherein each tablerow will be a csv column." +
                    "\n\n\tNote that an existing target file will be silently overwritten!!");
        }
        CommandLineParser clp =
                new CommandLineParser(args, new String[]{"project", "driver", "url", "user", "pass", "target", "table", "agent", "aggregator", "general",});

        String project = clp.getOptionParameter("project");
        String driver = clp.getOptionParameter("driver");
        String url = clp.getOptionParameter("url");
        String user = clp.getOptionParameter("user");
        String pass = clp.getOptionParameter("pass");
        String target = clp.getOptionParameter("target");
        String tablePath = clp.getOptionParameter("table");
        String agentPath = clp.getOptionParameter("agent");
        String agentaggregatorPath = clp.getOptionParameter("aggregator");
        String generalPath = clp.getOptionParameter("general");

        int lSource = 0;

        // See if all of this is correct.
        if (project == null) {
            flagError("You did not specify the '--project <projectid>' parameter!");
        } else if (driver == null) {
            flagError("You did not specify the '--driver <ms_lims database driver>' parameter!");
        } else if (url == null) {
            flagError("You did not specify the '--url <ms_lims database url>' parameter!");
        } else if (user == null) {
            flagError("You did not specify the '--user <ms_lims username>' parameter!");
        } else if (pass == null) {
            flagError("You did not specify the '--pass <ms_lims password for username>' parameter!");
        } else if (target == null) {
            flagError("You did not specify the '--target <target_file_name>' parameter!");
        } else if (tablePath == null) {
            flagError("You did not specify the '--table <table_setting_xml_file>\"' parameter!");
        } else if (agentPath == null) {
            flagError("You did not specify the '--agent <agent_setting_xml_file>' parameter!");
        } else if (agentaggregatorPath == null) {
            flagError("You did not specify the '--aggregator <aggregator_setting_xml_file>' parameter!");
        } else if (generalPath == null) {
            flagError("You did not specify the '--general <general_setting_xml_file>' parameter!");
        } else {
            // Parameters were all found. Let's see if we can access all files that should be accessed.
            // Note that an existing target_file will result in clean and silent overwrite of the file!

            Driver d = null;
            Connection lConnection = null;
            try {
                d = (Driver) Class.forName(driver).newInstance();

                Properties lProps = new Properties();
                if (user != null) {
                    lProps.put("user", user);
                }
                if (pass != null) {
                    lProps.put("password", pass);
                }
                lConnection = d.connect(url, lProps);
                if (lConnection == null) {
                    flagError("Could not connect to the database. Either your driver is incorrect for this database, or your URL is malformed.");
                }

            } catch (InstantiationException e) {
                logger.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
            } catch (ClassNotFoundException e) {
                logger.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
            }

            long lProjectID = -1l;
            try {
                lProjectID = Long.parseLong(project);
            } catch (NumberFormatException e) {
                flagError("'" + project + "' is not a valid project project identifier!! ");
            }


            File output = new File(target);
            File agent = new File(agentPath);
            File agentaggregator = new File(agentaggregatorPath);
            File general = new File(generalPath);
            File table = new File(tablePath);

            if (!agent.exists()) {
                flagError("The agent setting file you specified (" + agent + ") does not exist!\nExiting...");
            } else if (!agentaggregator.exists()) {
                flagError("The agentaggregator setting file you specified (" + agentaggregator + ") does not exist!\nExiting...");
            } else if (!general.exists()) {
                flagError("The general setting file you specified (" + general + ") does not exist!\nExiting...");
            } else if (!table.exists()) {
                flagError("The tablke setting file you specified (" + table + ") does not exist!\nExiting...");
            } else {
                // If the output file does not yet exist, create it.
                if (!output.exists()) {
                    try {
                        output.createNewFile();
                    } catch (IOException ioe) {
                        flagError("Could not create outputfile (" + output + "): " + ioe.getMessage());
                    }
                }


                MatConfig.getInstance().reloadConfigurationFile(agent, MatConfig.AGENT_CONFIG);
                MatConfig.getInstance().reloadConfigurationFile(agentaggregator, MatConfig.AGENTAGGREGATOR_CONFIG);
                MatConfig.getInstance().reloadConfigurationFile(general, MatConfig.GENERAL_CONFIG);
                MatConfig.getInstance().reloadConfigurationFile(table, MatConfig.TABLE_CONFIG);
                // The settings we've received as input seems to be OK.

                // Enable system.out logging.
                MatLogger.setSystemOut(true);

                // Set the iterator we will be using.
                IdentificationFactory.getInstance().load(lConnection, lProjectID);
                PeptideIdentificationIterator iter = IdentificationFactory.getInstance().getIterator();


                //System.out.println("*****************");
                logger.info("Peptizer_MsLims to CSV");
                logger.info("*****************");
                logger.info("\tAll data gathered by all active agents and preset tablerows on the input will be written to the output in the csv file format.\n");
                logger.info("\tSource '" + url + "':" + "\t\tProject" + project);
                logger.info("\tTarget:" + "\t\t\t\t" + output);
                logger.info("\n\tSettings");
                logger.info("\t\tAgent:" + "\t\t\t\t" + agent);
                logger.info("\t\tAgentAggregator:" + "\t" + agentaggregator);
                logger.info("\t\tGeneral:" + "\t\t\t" + general + "\n");

                AgentAggregator lAgentAggregator = AgentAggregatorFactory.getInstance().getAgentAggregators()[0];
                lAgentAggregator.setAgentsCollection(AgentFactory.getInstance().getActiveAgents());

                // Iterate all the PeptideIdentifications.
                int lMatchCounter = 0;
                int lNonConfidentCounter = 0;
                int lNonMatchCounter = 0;
                int lNoIdentificationCounter = 0;
                int lIterationCounter = 0;


                long start = System.currentTimeMillis();
                logger.info("1) Peptizer started applying profile \"" + agent.getName() + "\" to \"" + agent.getName() + "\" at " + new Date(System.currentTimeMillis()) + ".");

                ValidationSaveToCSV saver = null;


                while (iter.hasNext()) {
                    lIterationCounter++;

                    PeptideIdentification lPeptideIdentification = (PeptideIdentification) iter.next();
                    // If the PeptideIdentification matches the AgentAggregator.
                    AgentAggregationResult lAggregationResult = lAgentAggregator.match(lPeptideIdentification);

                    switch (lAggregationResult) {

                        case MATCH:
                            lMatchCounter++;
                            lPeptideIdentification.getValidationReport().setComment("MATCH");
                            break;

                        case NON_CONFIDENT:
                            lNonConfidentCounter++;
                            lPeptideIdentification.getValidationReport().setComment("NON_CONFIDENT");
                            break;

                        case NON_MATCH:
                            lNonMatchCounter++;
                            lPeptideIdentification.getValidationReport().setComment("NON_MATCH");
                            break;

                        case NO_IDENTIFICATION:
                            lNoIdentificationCounter++;
                            lPeptideIdentification.getValidationReport().setComment("NO_IDENTIFICATION");
                            break;
                    }

                    try {
                        if (saver == null) {
                            saver = initSaver(output, saver);
                            saver.setIncludeNonPrimary(false);
                            saver.initHeader();
                        }
                        // Save the identification!
                        saver.savePeptideIdentification(lPeptideIdentification);
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }

                    if (lIterationCounter % 1000 == 0) {
                        logger.info(".");
                    }
                }

                try {
                    // close the connection, not needed anymore now.
                    lConnection.close();
                    logger.info("Connection to '" + url + "' closed.");
                } catch (SQLException e) {
                    logger.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                }


                // Rename the file if successfull!
                logger.info("\nExit.");
                System.exit(0);
            }
        }
    }

    private static ValidationSaveToCSV initSaver(final File aOutput, ValidationSaveToCSV aSaver) {
        List<Agent> iActiveAgents = AgentFactory.getInstance().getActiveAgents();

        List iAgentIDs = new ArrayList();
        for (int i = 0; i < iActiveAgents.size(); i++) {
            Agent lAgent = iActiveAgents.get(i);
            iAgentIDs.add(lAgent.getUniqueID());

        }

        TableRowManager data = new TableRowManager(iAgentIDs);
        ArrayList list = new ArrayList();
        for (int i = 0; i < data.getNumberOfVisibleRows(); i++) {
            list.add(data.getTableRow(i));
        }

        AbstractTableRow.setHTML(false);

        aSaver = new ValidationSaveToCSV(aOutput, list);
        aSaver.setSeparator(";");
        aSaver.setComments(true);
        aSaver.setIncludeConfidentNotSelected(true);
        aSaver.setIncludeNonConfident(false);
        return aSaver;
    }


    /**
     * This method prints the specified error message to standard out, after prepending and appending two blank lines
     * each. It then exits the JVM!
     *
     * @param aMessage String with the error message to display.
     */
    private static void flagError(String aMessage) {
        System.err.println("\n\n" + aMessage + "\n\nRun program without parameters for help.");
        System.exit(1);
    }

}
