package de.akesting.bollinger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.akesting.bollinger.Statistics.LinearRegressionResult;

public class OutputData {
    private static final Logger LOG = LoggerFactory.getLogger(OutputData.class);

    private static final double SMALL_VALUE = 0.000001;
    static final String COMMENT_LINE_BEGIN = "#";

    private double[] yExpected;
    private double[] yDiffSqr;
    private double[] yVariationExpected;

    private void init(int size) {
        yExpected = new double[size];
        yDiffSqr = new double[size];
        yVariationExpected = new double[size];
    }

    public void doSmoothing(InputData inputData, double sigmaSmooth) {
        init(inputData.size());
        final double[] xValues = inputData.getXValues();
        final double[] yValues = inputData.getYValues();
        for (int i = 0, N = inputData.size(); i < N; i++) {
            final double xCenter = inputData.getX(i);
            LinearRegressionResult regr = Statistics.gaussLinRegression(xValues, yValues, xCenter, sigmaSmooth);
            yExpected[i] = regr.a + regr.b * xCenter;
            for (int j = 0; j < inputData.size(); j++) {
                yDiffSqr[j] = Math.pow(inputData.getY(j) - regr.a - regr.b * inputData.getX(j), 2);
            }
            yVariationExpected[i] = Math.sqrt(Statistics.gGaussAverage(xValues, yDiffSqr, xCenter, sigmaSmooth));
        }
    }

    public void write(File file, InputData inputData, double outputWidth) {
        Writer writer = null;
        try {
            LOG.info("xMin={}, xMmax={}", inputData.xMin(), inputData.xMax());
            writer = new BufferedWriter(new FileWriter(file));
            writeCommentLine(writer, "variation coefficient defined by sigma/avg (col4/col3)\n");
            writeCommentLine(writer,
                    String.format("%s\t %s\t %s\t %s \n", "xCol", "yCol", "Exp(y)", "Exp.variation(y)"));

            if (outputWidth <= SMALL_VALUE) {
                LOG.info("no user defined outWidth. Output only original 2D data");
                for (int i = 0; i < inputData.size(); i++) {
                    writeValueToLine(writer, String.format("%.5f, %.5f, %.5f, %.5f\n", inputData.getX(i),
                            inputData.getY(i), yExpected[i], yVariationExpected[i]));
                }
            } else {
                int nOut = (int) ((inputData.xMax() - inputData.xMin()) / outputWidth) + 1;
                LOG.info("outputWidth={}, nOut={}", outputWidth, nOut);
                for (int i = 0; i < nOut; i++) {
                    double x = inputData.xMin() + i * outputWidth;
                    double[] xValues = inputData.getXValues();
                    double[] yValues = inputData.getYValues();
                    writeValueToLine(writer, String.format("%.5f, %.5f, %.5f, %.5f\n", x,
                            Statistics.intpextp(xValues, yValues, x), Statistics.intpextp(xValues, yExpected, x),
                            Statistics.intpextp(xValues, yVariationExpected, x)));
                }
            }
        } catch (IOException e) {
            // error handling (e.g. on flushing)
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    private void writeValueToLine(Writer output, String value) throws IOException {
        output.write(value);
    }

    private void writeCommentLine(Writer output, String value) throws IOException {
        writeValueToLine(output, COMMENT_LINE_BEGIN + value);
    }

}
