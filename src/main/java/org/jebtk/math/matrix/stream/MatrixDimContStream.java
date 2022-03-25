package org.jebtk.math.matrix.stream;

import org.jebtk.math.matrix.MatrixDimFunction;

public class MatrixDimContStream implements MatrixDimFunction {

  private MatrixDimStream mS;

  public MatrixDimContStream(MatrixDimStream s) {
    mS = s;
  }

  @Override
  public void apply(int index, double[] data, double[] ret) {
    mS.apply(index, data, ret);
  }
}
