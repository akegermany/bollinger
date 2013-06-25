package de.akesting.bollinger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public final class Statistics {

    private static final Logger LOG = LoggerFactory.getLogger(Statistics.class);

    private static final double TINY_VALUE = 1.e-10;

    private Statistics() {
        // private cstr
    }

    public static final class LinearRegressionResult {
        double a;
        double b;
        double B;
        double sumdev;
    }

    public static double gGaussAverage(double[] xValues, double[] yValues, double xCenter, double sigma) {
        Preconditions.checkArgument(xValues.length == yValues.length, "x and y arrays of different size: "
                + xValues.length + ", " + yValues.length);
        final int size = xValues.length;
        final double cutOff = 4 * sigma;
        if (size < 3) {
            LOG.error("trying to calculate smoothed values from {} < 3 data points!", size);
        }
        double norm = 0;
        double weightedSum = 0;
        double gaussfactor = 0;
        double gaussAverage = 0;
        for (int i = 0; i < size; i++) {
            double dx = xValues[i] - xCenter;
            if (Math.abs(dx) <= cutOff) {
                gaussfactor = Math.exp(-Math.pow(dx / (2 * sigma), 2));
                norm += gaussfactor;
                weightedSum += gaussfactor * yValues[i];
            }
        }
        if (norm == 0) {
            LOG.error("norm[i]=0");
        }
        gaussAverage = weightedSum / norm;
        return gaussAverage;
    }

    public static LinearRegressionResult gaussLinRegression(double[] xValues, double[] yValues, double xCenter,
            double sigma) {
        Preconditions.checkArgument(xValues.length == yValues.length, "x and y arrays of different size: "
                + xValues.length + ", " + yValues.length);
        final int size = xValues.length;
        final double cutOff = 4 * sigma;
        LinearRegressionResult result = new LinearRegressionResult();

        if (size < 3) {
            LOG.error("cannot calculate linear regression from less than 3 data points! size="
                    + size);
            return result;
        }

        double xbar = gGaussAverage(xValues, xValues, xCenter, sigma);
        double ybar = gGaussAverage(xValues, yValues, xCenter, sigma);

        LOG.debug("gaussLinRegression: xCenter={}, xBar={}, yBar={}", xCenter, xbar, ybar);

        double sumxx = 0;
        double sumxy = 0;
        double sumyy = 0;
        double norm = 0;
        for (int i = 0; i < size; i++) {
            double dx = xValues[i] - xCenter;
            if (Math.abs(dx) <= cutOff) {
                double gaussfactor = Math.exp(-Math.pow(dx / (2 * sigma), 2));
                norm += gaussfactor;
                sumxx += gaussfactor * xValues[i] * xValues[i];
                sumxy += gaussfactor * xValues[i] * yValues[i];
                sumyy += gaussfactor * yValues[i] * yValues[i];
            }
        }

        if (norm == 0) {
            LOG.error("gSmoothGauss:error: norm[i]=0\n!");
            return result;
        }

        LOG.debug("linRegression: sumxx={}, sumxy={}, sumyy={}", sumxx, sumxy, sumyy);

        double sxx = sumxx / norm - xbar * xbar;
        double sxy = sumxy / norm - xbar * ybar;
        double syy = sumyy / norm - ybar * ybar;

        double r = sxy / Math.sqrt(sxx * syy);

        result.b = sxy / sxx;
        result.a = ybar - result.b * xbar;
        result.B = r * r;
        result.sumdev = 0;
        for (int i = 0; i < size; i++) {
            result.sumdev += Math.pow(result.a + result.b * xValues[i] - yValues[i], 2);
        }
        return result;
    }

    public static double intpextp(double[] xValues, double[] yValues, double x) {
        Preconditions.checkArgument(xValues.length == yValues.length, "x and y arrays of different size: "
                + xValues.length + ", " + yValues.length);
        final int nLocal = xValues.length - 1;
        if (nLocal == 0) {
            LOG.error("cannot interpolate from arrays with zero length(s) = {}, return 0 ", nLocal);
            return 0;
        }

        int i = 0;
        double intp_value = 0;

        while ((xValues[i] <= x) && (i < nLocal)) {
            i++;
        }

        if (i == 0) {
            intp_value = yValues[0];
        } else if ((i == nLocal) && (x > xValues[i])) {
            intp_value = yValues[nLocal];
        } else if (Math.abs(xValues[i] - xValues[i - 1]) < TINY_VALUE) {
            intp_value = yValues[i];
        } else {
            intp_value = yValues[i - 1] + (yValues[i] - yValues[i - 1]) * (x - xValues[i - 1])
                    / (xValues[i] - xValues[i - 1]);
        }
        LOG.debug(" return = {}", intp_value);
        return intp_value;
    }

}
