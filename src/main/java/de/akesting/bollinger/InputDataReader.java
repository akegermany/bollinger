package de.akesting.bollinger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.base.Preconditions;

public class InputDataReader {

    private static final Logger LOG = LoggerFactory.getLogger(InputDataReader.class);

    private final char separator;
    private final int column1;
    private final int column2;
    
    private String timeFormat = ""; // TODO

    public InputDataReader(int columnX, int columnY, char separator) {
        this.column1 = columnX - 1;
        this.column2 = columnY - 1;
        this.separator = separator;
        LOG.info("read x from column={} and y from column={}", columnX, columnY);
        LOG.info("use separator character='{}'", String.valueOf(separator));
    }

    public void readData(File file, InputData inputData) {
        Preconditions.checkNotNull(inputData);
        Preconditions.checkArgument(file != null && file.isFile() && file.exists(), "cannot find input file=" + file);
        LOG.info("read data from file={}", file.getAbsolutePath());

        int validLinesParsed = inputData.size();
        int totalLineCount = 0;
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(file), separator);
            List<String[]> lines = reader.readAll();
            if (lines == null || lines.isEmpty()) {
                LOG.error("no input read from file={}", file.getAbsolutePath());
                return;
            }
            totalLineCount = lines.size();
            for (String[] line : lines) {
                addInputLine(line, inputData);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        
        validLinesParsed = inputData.size() - validLinesParsed;
        if(validLinesParsed>0){
            LOG.info("parsed successfully {} from {} lines in input file.", validLinesParsed, totalLineCount);
        }
        else{
            LOG.error("no valid lines from {} lines in input file parsed!", totalLineCount);
        }
    }

    private void addInputLine(String[] line, InputData inputData) {
        if (line.length < Math.max(column1, column2)) {
            LOG.info("cannot parse data from {} columns, ignore line={}", line.length, Arrays.toString(line));
            return;
        }
        try {
            double x = Double.parseDouble(line[column1]);
            double y = Double.parseDouble(line[column2]);
            inputData.add(x, y);
        } catch (NumberFormatException e) {
            LOG.info("cannot parse data. Ignore line={}", Arrays.toString(line));
        }
    }

}
