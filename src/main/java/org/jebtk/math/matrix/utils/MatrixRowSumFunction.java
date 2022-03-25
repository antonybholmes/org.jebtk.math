package org.jebtk.math.matrix.utils;

import org.jebtk.math.matrix.MatrixReduceFunction;
import org.jebtk.math.statistics.Stats;

public class MatrixRowSumFunction implements MatrixReduceFunction {

  @Override
  public double apply(int index, double[] data) {
    return new Stats(data).sum();
  }

}
