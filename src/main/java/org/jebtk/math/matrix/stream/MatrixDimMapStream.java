package org.jebtk.math.matrix.stream;

import org.jebtk.math.matrix.MatrixDimFunction;

public class MatrixDimMapStream extends MatrixDimContStream {

  private MatrixDimFunction mF;

  public MatrixDimMapStream(MatrixDimStream s, MatrixDimFunction f) {
    super(s);

    mF = f;
  }

  @Override
  public void apply(int index, double[] data, double[] ret) {
    double[] data2 = new double[ret.length];

    mF.apply(index, data, data2);

    super.apply(index, data2, ret);
  }
}
