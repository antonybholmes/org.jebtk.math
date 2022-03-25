package org.jebtk.math.matrix;

import java.util.Collection;

import org.jebtk.core.text.TextUtils;

public class DoubleColMatrixParser extends MixedMatrixParser {

  public DoubleColMatrixParser(int headers, int rowAnnotations,
      String delimiter) {
    this(headers, TextUtils.EMPTY_LIST, rowAnnotations, delimiter);
  }

  public DoubleColMatrixParser(int headers,
      Collection<String> skipMatches, int rowAnnotations, String delimiter) {
    super(headers, skipMatches, rowAnnotations, delimiter);
  }

  @Override
  public DataFrame createMatrix(int rows, int columns) {
    return DataFrame.createDataFrame(new DoubleColMatrix(rows, columns));
  }
}
