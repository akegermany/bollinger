package de.akesting.bollinger;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.primitives.Doubles;

public class InputData {

    private static final Logger LOG = LoggerFactory.getLogger(InputData.class);

    private final List<Double> xData = new ArrayList<>(1000);
    private final List<Double> yData = new ArrayList<>(1000);

    private double xMin = Double.MAX_VALUE;
    private double xMax = Double.MIN_VALUE;

    public void add(double x, double y) {
        xData.add(x);
        yData.add(y);
        xMin = Math.min(xMin, x);
        xMax = Math.max(xMax, x);
        LOG.info("added x={}, y={}", x, y);
    }

    public void sort() throws IllegalAccessException {
        throw new IllegalAccessException("sorting not yet implemented");
    }

    public double[] getXValues() {
        return Doubles.toArray(xData);
    }

    public double[] getYValues() {
        return Doubles.toArray(yData);
    }

    public double xMin() {
        return xMin;
    }

    public double xMax() {
        return xMax;
    }

    public int size() {
        return xData.size();
    }

    public double getX(int index) {
        return xData.get(index);
    }

    public double getY(int index) {
        return yData.get(index);
    }

}
