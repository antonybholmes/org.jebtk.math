package org.jebtk.math.matrix.stream;

import org.jebtk.core.Mathematics;
import org.jebtk.math.matrix.CellFunction;
import org.jebtk.math.matrix.Matrix;
import org.jebtk.math.matrix.MatrixDimFunction;
import org.jebtk.math.matrix.MatrixReduceFunction;

public class MatrixStream
    implements CellFunction, MatrixDimFunction, MatrixReduceFunction {

  private Matrix mM;

  public MatrixStream(Matrix m) {
    mM = m;
  }

  public static MatrixStream applied(Matrix m) {
    return apply(m.copy());
  }

  private static MatrixStream apply(Matrix m) {
    return new MatrixStream(m);
  }

  public void apply() {
    for (int i = 0; i < mM.getRows(); ++i) {
      for (int j = 0; j < mM.getCols(); ++j) {
        double v = mM.getValue(i, j);

        if (Mathematics.isValidNumber(v)) {
          mM.set(i, j, f(i, j, v));
        }
      }
    }

    mM.fireMatrixChanged();
  }

  @Override
  public double f(int row, int col, double x, double... y) {
    return 0;
  }

  @Override
  public void apply(int index, double[] data, double[] ret) {
    // TODO Auto-generated method stub

  }

  @Override
  public double apply(int index, double[] data) {
    return 0;
  }
}
