/**
 * Copyright (C) 2016, Antony Holmes
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. Neither the name of copyright holder nor the names of its contributors 
 *     may be used to endorse or promote products derived from this software 
 *     without specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.jebtk.math.matrix;

import org.jebtk.core.collections.DefaultHashMap;
import org.jebtk.core.collections.DefaultHashMapCreator;
import org.jebtk.core.collections.IterMap;
import org.jebtk.core.text.TextUtils;

/**
 * Matrix that can be dynamically resized to match maximum row/column.
 * 
 * @author Antony Holmes
 */
public class TextWorksheet extends Worksheet<String> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new mixed sparse matrix.
   *
   * @param rows the rows
   * @param columns the columns
   */
  public TextWorksheet(int rows, int columns) {
    super(rows, columns);
  }

  /**
   * Instantiates a new mixed sparse matrix.
   *
   * @param rows the rows
   * @param columns the columns
   * @param v the v
   */
  public TextWorksheet(int rows, int columns, String v) {
    super(rows, columns, v);
  }

  /**
   * Clone a matrix optionally copying the core matrix values and the
   * annotation.
   *
   * @param m the m
   */
  public TextWorksheet(Matrix m) {
    super(m);
  }
  
  @Override
  protected IterMap<Integer, IterMap<Integer, String>> createMap(String v) {
    if (v != null) {
      return DefaultHashMap.create(new DefaultHashMapCreator<Integer, String>(v));
    } else {
      return DefaultHashMap.create(new DefaultHashMapCreator<Integer, String>(TextUtils.EMPTY_STRING));
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#getType()
   */
  @Override
  public MatrixType getType() {
    return MatrixType.TEXT;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#copy()
   */
  @Override
  public Matrix copy() {
    return new TextWorksheet(this);
  }

  @Override
  public Matrix ofSameType(int rows, int cols) {
    return new TextWorksheet(rows, cols);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.DynamicMatrix#update(java.lang.String)
   */
  @Override
  public void update(String v) {
    for (int i = 0; i < mDim.mRows; ++i) {
      for (int j = 0; j < mDim.mCols; ++j) {
        mData.get(i).put(j, v);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.DynamicMatrix#update(int, int,
   * java.lang.Object)
   */
  @Override
  public void update(int row, int column, Object v) {
    if (v == null) {
      return;
    }

    if (v instanceof String) {
      mData.get(row).put(column, (String) v);
    } else {
      mData.get(row).put(column, v.toString());
    }

    updateSize(row, column);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.DynamicMatrix#update(int, int,
   * java.lang.String)
   */
  @Override
  public void update(int row, int column, String v) {
    mData.get(row).put(column, v);

    updateSize(row, column);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.DynamicMatrix#update(int, int, double)
   */
  @Override
  public void update(int row, int column, double v) {
    update(row, column, Double.toString(v));
  }

  @Override
  public void update(int row, int column, int v) {
    update(row, column, Integer.toString(v));
  }

  @Override
  public void update(int row, int column, long v) {
    update(row, column, Long.toString(v));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#getCellType(int, int)
   */
  @Override
  public CellType getCellType(int row, int column) {
    return CellType.TEXT;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#transpose()
   */
  @Override
  public Matrix transpose() {
    return transpose(this);
  }

  public static Matrix transpose(TextWorksheet m) {

    // Return a fixed sized array where possible
    TextMatrix ret = TextMatrix.createTextMatrix(m.getCols(), m.getRows());

    // Swap row and column indices. We use index lookup to reduce
    // the number of number of times indices must be looked up to
    // set cell elements.

    for (int i = 0; i < m.getRows(); ++i) {
      for (int j = 0; j < m.getCols(); ++j) {
        ret.mData[ret.mRowOffsets[i] + j] = m.mData.get(i).get(j);
      }
    }

    return ret;
  }

  /**
   * Creates the matrix.
   *
   * @return the matrix
   */
  public static Matrix createMatrix(int rows, int columns) {
    return new TextWorksheet(rows, columns);
  }
}
