package de.akesting.bollinger;

import java.io.File;
import java.util.Locale;

import org.apache.commons.cli.ParseException;


public class BollingerMain {

    private static final String LOG4J_PATH = "/config/";
    private static final String LOG4J_PROPERTIES = "log4j.properties";

    public static void main(String[] args) throws ParseException {
        System.out.println("(c) Arne Kesting, 2013");
        Locale.setDefault(Locale.US);
        Logger.initialize(LOG4J_PATH + LOG4J_PROPERTIES);

        BollingerCommandLine cmdLine = new BollingerCommandLine();
        cmdLine.parse(args);

        InputData inputData = new InputData();
        File inputFile = new File(cmdLine.getInputFilename());
        InputDataReader reader = new InputDataReader(cmdLine.getFirstInputColumn(), cmdLine.getSecondInputColumn(),
                cmdLine.getSeparator());
        reader.readData(inputFile, inputData);

        inputData.sort(); // TODO not yet impl

        OutputData outputData = new OutputData();
        outputData.doSmoothing(inputData, cmdLine.getSmoothingParameter());

        String outputFilename = createOutputFilename(cmdLine);
        File outputFile = new File(outputFilename);
        outputData.write(outputFile, inputData, cmdLine.getOutputWidth());
        System.out.println("bollinger done");
    }

    private static String createOutputFilename(BollingerCommandLine cmdLine) {
        StringBuilder sb = new StringBuilder();
        sb.append(cmdLine.getInputFilename()).append(".smooth_");
        sb.append(cmdLine.getFirstInputColumn());
        sb.append("_");
        sb.append(cmdLine.getSecondInputColumn());
        return sb.toString();
    }

}
