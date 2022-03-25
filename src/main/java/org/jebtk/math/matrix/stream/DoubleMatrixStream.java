package org.jebtk.math.matrix.stream;

import org.jebtk.math.matrix.DoubleMatrix;

public class DoubleMatrixStream extends MatrixStream {

  private DoubleMatrix mDM;

  public DoubleMatrixStream(DoubleMatrix m) {
    super(m);

    mDM = m;
  }

  public void apply() {
    int r = 0;
    int c = 0;

    for (int i = 0; i < mDM.mData.length; ++i) {
      mDM.mData[i] = f(r, c++, mDM.mData[i]);

      if (c == mDM.mDim.mCols) {
        c = 0;
        ++r;
      }
    }

    mDM.fireMatrixChanged();
  }
}
