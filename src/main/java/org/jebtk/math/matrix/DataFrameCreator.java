package org.jebtk.math.matrix;

import org.jebtk.core.text.TextUtils;

public class DataFrameCreator {
  public DataFrame createMatrix(int rows, int columns) {
    return DataFrame.createDataFrame(rows, columns);
  }

  protected void set(Matrix matrix, int row, int column, String value) {

    if (TextUtils.isNumber(value)) {
      matrix.update(row, column, Double.parseDouble(value));
    } else {
      matrix.update(row, column, value);
    }
  }
}
