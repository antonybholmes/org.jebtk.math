/**
 * Copyright 2017 Antony Holmes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jebtk.math.matrix.utils;

import org.jebtk.math.matrix.DataFrame;
import org.jebtk.math.matrix.DoubleMatrix;
import org.jebtk.math.matrix.IndexMatrix;
import org.jebtk.math.matrix.IntMatrix;
import org.jebtk.math.matrix.LongMatrix;
import org.jebtk.math.matrix.Matrix;
import org.jebtk.math.matrix.MixedMatrix;

/**
 * The Class MatrixUtils.
 */
public class Transpose {

  /**
   * Instantiates a new matrix utils.
   */
  private Transpose() {

  }

  /**
   * Transpose.
   *
   * @param m the m
   * @return the matrix
   */
  public Matrix transpose(final DataFrame m) {
    // Transpose the main matrix
    Matrix innerM = transpose(m.getMatrix());

    DataFrame ret = new DataFrame(innerM);

    // The first name is the row-name, which must be swapped for the
    // column name so we only copy the annotation for names(1, end)
    // verbatim. The same is true for the columns
    // ret.setColumnNames(getRowNames());
    // ret.setRowNames(getColumnNames());

    for (String name : m.getIndex().getNames()) { // CollectionUtils.tail(getIndex().getNames()))
                                                    // {
      ret.getColumnHeader().setAnnotation(name, m.getIndex().getAnnotation(name));
    }

    for (String name : m.getColumnHeader().getNames()) { // CollectionUtils.tail(getColumns().getNames()))
                                                       // {
      ret.getIndex().setAnnotation(name, m.getColumnHeader().getAnnotation(name));
    }

    return ret;
  }

  /**
   * Transpose.
   *
   * @param m the m
   * @return the matrix
   */
  public Matrix transpose(final Matrix m) {
    if (m instanceof DoubleMatrix) {
      return transpose((DoubleMatrix) m);
    } else if (m instanceof IntMatrix) {
      return transpose((IntMatrix) m);
    } else if (m instanceof IndexMatrix) {
      return transpose((IndexMatrix) m);
    } else {
      MixedMatrix ret = MixedMatrix.createMixedMatrix(m.getCols(), m.getRows());

      // Swap row and column indices. We use index lookup to reduce
      // the number of number of times indices must be looked up to
      // set cell elements.

      for (int i = 0; i < m.getRows(); ++i) {
        for (int j = 0; j < m.getCols(); ++j) {
          ret.set(j, i, m.get(i, j));
        }
      }

      return ret;
    }
  }

  /**
   * Transpose.
   *
   * @param m the m
   * @return the matrix
   */
  public static Matrix transpose(final IndexMatrix m) {
    MixedMatrix ret = MixedMatrix.createMixedMatrix(m.mDim.mCols, m.mDim.mRows);

    int i2 = 0;
    int c = 0;

    for (int i = 0; i < m.size(); ++i) {
      // Each time we end a row, reset i2 back to the next column
      if (i % m.mDim.mCols == 0) {
        i2 = c++;
      }

      ret.set(i2, m.get(i));

      // Skip blocks
      i2 += m.mDim.mRows;
    }

    return ret;
  }

  /**
   * Transpose.
   *
   * @param m the m
   * @return the matrix
   */
  public static Matrix transpose(final DoubleMatrix m) {
    DoubleMatrix ret = DoubleMatrix.createDoubleMatrix(m.mDim.mCols,
        m.mDim.mRows);

    int i2 = 0;
    int c = 0;

    for (int i = 0; i < m.mData.length; ++i) {
      // Each time we end a row, reset i2 back to the next column
      if (i % m.mDim.mCols == 0) {
        i2 = c++;
      }

      ret.mData[i2] = m.mData[i];

      // Skip blocks
      i2 += m.mDim.mRows;
    }

    return ret;
  }

  /**
   * Transpose.
   *
   * @param m the m
   * @return the matrix
   */
  public static Matrix transpose(final IntMatrix m) {
    IntMatrix ret = IntMatrix.createIntMatrix(m.mDim.mCols, m.mDim.mRows);

    int i2 = 0;
    int c = 0;

    for (int i = 0; i < m.mData.length; ++i) {
      // Each time we end a row, reset i2 back to the next column
      if (i % m.mDim.mCols == 0) {
        i2 = c++;
      }

      ret.mData[i2] = m.mData[i];

      // Skip blocks
      i2 += m.mDim.mRows;
    }

    return ret;
  }

  public static Matrix transpose(final LongMatrix m) {
    LongMatrix ret = LongMatrix.createLongMatrix(m.mDim.mCols, m.mDim.mRows);

    int i2 = 0;
    int c = 0;

    for (int i = 0; i < m.mData.length; ++i) {
      // Each time we end a row, reset i2 back to the next column
      if (i % m.mDim.mCols == 0) {
        i2 = c++;
      }

      ret.mData[i2] = m.mData[i];

      // Skip blocks
      i2 += m.mDim.mRows;
    }

    return ret;
  }
}
