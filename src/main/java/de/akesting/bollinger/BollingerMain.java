package de.akesting.bollinger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.text.ParseException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.PropertyConfigurator;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class BollingerMain {

    private static final String LOG4J_PATH = "/config/";
    private static final String LOG4J_PROPERTIES = "log4j.properties";

    private static final Logger LOG = LoggerFactory.getLogger(BollingerMain.class);

    private BollingerCommandLine cmdLine;
    private InputData inputData;
    private OutputData outputData;

    public BollingerMain() {
    }

    public static void main(String[] args) throws ParseException, IOException {
        System.out.println("(c) Arne Kesting, 2013");
        Locale.setDefault(Locale.US);
        final URL log4jConfig = Logger.class.getResource(LOG4J_PATH + LOG4J_PROPERTIES);
        PropertyConfigurator.configure(log4jConfig);

        BollingerMain bollinger = new BollingerMain();
        bollinger.parseCommandLine(args);
        bollinger.readData();
        bollinger.doSmoothing();
        bollinger.writeData();
        System.out.println("bollinger done");
    }

    private void writeData() {
        Preconditions.checkNotNull(outputData);
        String outputFilename = createOutputFilename(cmdLine);
        File outputFile = new File(outputFilename);
        outputData.write(outputFile, inputData, cmdLine.getOutputWidth());
    }

    private void doSmoothing() {
        Preconditions.checkNotNull(inputData);
        outputData = new OutputData();
        outputData.doSmoothing(inputData, cmdLine.getSmoothingParameter());
    }

    private void readData() throws IOException {
        Preconditions.checkNotNull(cmdLine);
        inputData = new InputData();
        parseInputData();
        // inputData.sort();
    }

    private void parseCommandLine(String[] args) {
        cmdLine = new BollingerCommandLine();
        cmdLine.parse(args);
    }

    private String createOutputFilename(BollingerCommandLine cmdLine) {
        StringBuilder sb = new StringBuilder();
        sb.append(cmdLine.getInputFilename()).append(".smooth_");
        sb.append(cmdLine.getFirstInputColumn());
        sb.append("_");
        sb.append(cmdLine.getSecondInputColumn());
        return sb.toString();
    }

    private void parseInputData() throws IOException {
        Preconditions.checkNotNull(cmdLine);
        Preconditions.checkNotNull(inputData);

        int xColum = cmdLine.getFirstInputColumn();
        int yColum = cmdLine.getSecondInputColumn();

        File inputFile = new File(cmdLine.getInputFilename());
        Reader in = new FileReader(inputFile);
        Iterable<CSVRecord> parser = CSVFormat.DEFAULT.withDelimiter(cmdLine.getSeparator()).parse(in);


        for (CSVRecord record : parser) {
            LOG.info("record={}", record);
            try {
                double x;
                if (cmdLine.isTimeFormat()) {
                    DateTimeFormatter timeFormatter = DateTimeFormat.forPattern(cmdLine.getTimeFormat());
                    DateTime dateTime = timeFormatter.parseDateTime(record.get(xColum)).toDateTime(DateTimeZone.UTC);
                    x = TimeUnit.MILLISECONDS.toSeconds(dateTime.getMillis());
                    LOG.info("time={} --> dateTime={} --> seconds=" + (long) x, record.get(xColum), dateTime);
                } else {
                    x = Double.parseDouble(record.get(xColum));
                }
                double y = Double.parseDouble(record.get(yColum));
                inputData.add(x, y);
            } catch (NumberFormatException e) {
                LOG.warn("cannot parse data. Ignore csv record={}", record.toString());
            }
        }
    }

}
