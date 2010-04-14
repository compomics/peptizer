package com.compomics.peptizer.main;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.SelectedPeptideIdentifications;
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
import com.compomics.util.general.CommandLineParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 26-nov-2007
 * Time: 16:45:58
 */

/**
 * Class description: ------------------ This class was developed to start peptizer by command line.
 */
public class Peptizer {


    /**
     * Default Constructor.
     */
    public Peptizer() {
    }

    /**
     * The main method takes the start-up parameters and processes the input to .
     *
     * @param args String[] with the start-up arguments.
     */
    public static void main(String[] args) {
        // First see if we should output anything useful.
        if (args == null || args.length == 0) {
            flagError("Usage:\n\tPeptizer  --sourcetype <file|folder> " +
                    "--source <input file|folder> " +
                    "--target <target_file_name>" +
                    "--table <table_setting_xml_file>" +
                    "--agent <agent_setting_xml_file>" +
                    "--aggregator <aggregator_setting_xml_file>" +
                    "--general <general_setting_xml_file>" +
                    "--toprank <true|false>" +
                    "\n\t--sourcetype flags the type of input, can be a folder or a file." +
                    "\n\t--source is the input file or folder with identifications that will be profiled." +
                    "\n\t--agent is the configuration file for agents that define the profile. Each agent will be a csv column." +
                    "\n\t--aggregator is the configuration file for the aggregator that must judge a peptideidentification based on the agents. The first aggregator in the configuration file will be active!!" +
                    "\n\t--general is the general configuration file that contains parameters such as confidence." +
                    "\n\t--table is configures the tablerows, wherein each tablerow will be a csv column." +
                    "\n\t--toprank defines whether the first peptidehit is printed, or all confident peptide hits." +
                    "\n\n\tNote that an existing target file will be silently overwritten!");
        }
        CommandLineParser clp =
                new CommandLineParser(args, new String[]{"sourcetype", "source", "target", "table", "agent", "aggregator", "general", "toprank"});
        String sourcetype = clp.getOptionParameter("sourcetype");
        String source = clp.getOptionParameter("source");
        String target = clp.getOptionParameter("target");
        String tablePath = clp.getOptionParameter("table");
        String agentPath = clp.getOptionParameter("agent");
        String agentaggregatorPath = clp.getOptionParameter("aggregator");
        String generalPath = clp.getOptionParameter("general");
        String toprankState = clp.getOptionParameter("toprank");

        int lSource = 0;

        // See if all of this is correct.
        if (sourcetype == null) {
            flagError("You did not specify the '--sourcetype <file|folder>' parameter!\n\nRun program without parameters for help.");
        } else if (source == null) {
            flagError("You did not specify the '--source <input file|folder>' parameter!\n\nRun program without parameters for help.");
        } else if (target == null) {
            flagError("You did not specify the '--target <target_file_name>' parameter!\n\nRun program without parameters for help.");
        } else if (tablePath == null) {
            flagError("You did not specify the '--table <table_setting_xml_file>\"' parameter!\n\nRun program without parameters for help.");
        } else if (agentPath == null) {
            flagError("You did not specify the '--agent <agent_setting_xml_file>' parameter!\n\nRun program without parameters for help.");
        } else if (agentaggregatorPath == null) {
            flagError("You did not specify the '--aggregator <aggregator_setting_xml_file>' parameter!\n\nRun program without parameters for help.");
        } else if (generalPath == null) {
            flagError("You did not specify the '--general <general_setting_xml_file>' parameter!\n\nRun program without parameters for help.");
        } else if (toprankState == null) {
            toprankState = "true"; // Default, only the top ranked peptide hit is printed. 
        } else {
            // Parameters were all found. Let's see if we can access all files that should be accessed.
            // Note that an existing target_file will result in clean and silent overwrite of the file!
            File input = new File(source);
            File output = new File(target);
            File agent = new File(agentPath);
            File agentaggregator = new File(agentaggregatorPath);
            File general = new File(generalPath);
            File table = new File(tablePath);
            boolean toprank = Boolean.parseBoolean(toprankState);


            if (!input.exists()) {
                flagError("The input file you specified (" + input + ") does not exist!\nExiting...");
            } else if (!agent.exists()) {
                flagError("The agent setting file you specified (" + agent + ") does not exist!\nExiting...");
            } else if (!agentaggregator.exists()) {
                flagError("The agentaggregator setting file you specified (" + agentaggregator + ") does not exist!\nExiting...");
            } else if (!general.exists()) {
                flagError("The general setting file you specified (" + general + ") does not exist!\nExiting...");
            } else if (!table.exists()) {
                flagError("The table setting file you specified (" + table + ") does not exist!\nExiting...");
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

                // Create the iterator.
                IdentificationFactory.getInstance().load(input);
                PeptideIdentificationIterator iter = IdentificationFactory.getInstance().getIterator();

                // Create a holder for the selected peptideidentifications.
                SelectedPeptideIdentifications results = new SelectedPeptideIdentifications();

                //System.out.println("*****************");
                System.out.println("Peptizer to CSV");
                System.out.println("*****************");
                System.out.println("\tAll data gathered by all active agents and preset tablerows on the input will be written to the output in the csv file format.\n");
                System.out.println("\tSource " + sourcetype + ":" + "\t\t" + input);
                System.out.println("\tTarget:" + "\t\t\t\t" + output);
                System.out.println("\n\tSettings");
                System.out.println("\t\tAgent:" + "\t\t\t\t" + agent);
                System.out.println("\t\tAgentAggregator:" + "\t" + agentaggregator);
                System.out.println("\t\tGeneral:" + "\t\t\t" + general + "\n");

                AgentAggregator lAgentAggregator = AgentAggregatorFactory.getInstance().getAgentAggregators()[0];
                lAgentAggregator.setAgentsCollection(AgentFactory.getInstance().getActiveAgents());


                // Iterate all the PeptideIdentifications.
                int lMatchCounter = 0;
                int lNonConfidentCounter = 0;
                int lNonMatchCounter = 0;
                int lNoIdentificationCounter = 0;
                int lIterationCounter = 0;


                long start = System.currentTimeMillis();
                System.out.println("1) Peptizer started applying profile \"" + agent.getName() + "\" to \"" + input.getName() + "\" at " + new Date(System.currentTimeMillis()) + ".");

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
                            saver.setIncludeNonPrimary(!toprank);
                            saver.initHeader();
                        }
                        // Save the identification!
                        saver.savePeptideIdentification(lPeptideIdentification);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (lIterationCounter % 100 == 0) {
                        System.out.print(".");
                    }
                    if (lIterationCounter % 5000 == 0) {
                        System.out.print("\n");
                    }

                    //updateProgressBar();
                }


                long end = System.currentTimeMillis();
                System.out.println("");
                System.out.println("2) Finished processing " + lIterationCounter + " MS/MS spectra after " + ((end - start) / 1000) + " seconds.");
                System.out.println("");
                System.out.println(lNonMatchCounter + " MS/MS spectra were not matched by the profile.");
                System.out.println(lMatchCounter + " MS/MS spectra were matched by the profile.");
                System.out.println(lNonConfidentCounter + " MS/MS spectra had no confident identification.");
                System.out.println(lNoIdentificationCounter + " MS/MS spectra had no identification.");
                System.out.println("\nExit.");

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
        System.err.println("\n\n" + aMessage + "\n\n");
        System.exit(1);
    }

}