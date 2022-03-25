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
import org.jebtk.core.collections.HashMapCreator;
import org.jebtk.core.collections.IterMap;

/**
 * Matrix that can be dynamically resized to match maximum row/column.
 * 
 * @author Antony Holmes
 */
public class MixedWorksheet extends Worksheet<Object> {

  /**
   * The constant serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new dynamic mixed matrix.
   */
  public MixedWorksheet(int rows, int columns) {
    super(rows, columns);
  }

  /**
   * Clone a matrix optionally copying the core matrix values and the
   * annotation.
   *
   * @param m the m
   */
  public MixedWorksheet(Matrix m) {
    super(m);
  }
  
  @Override
  protected IterMap<Integer, IterMap<Integer, Object>> createMap(Object v) {
    return DefaultHashMap.create(new HashMapCreator<Integer, Object>());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#getType()
   */
  @Override
  public MatrixType getType() {
    return MatrixType.MIXED;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#copy()
   */
  @Override
  public Matrix copy() {
    return new MixedWorksheet(this);
  }

  @Override
  public Matrix ofSameType(int rows, int cols) {
    return newDynamicMixedMatrix(rows, cols);
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

    if (v instanceof Double) {
      mData.get(row).put(column, (Double) v);
    } else if (v instanceof Number) {
      mData.get(row).put(column, ((Number) v).doubleValue());
    } else if (v instanceof String) {
      mData.get(row).put(column, (String) v);
    } else {
      mData.get(row).put(column, v.toString());
    }
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
   * @see org.abh.common.math.matrix.DynamicMatrix#update(double)
   */
  @Override
  public void update(double v) {
    for (int i = 0; i < mDim.mRows; ++i) {
      for (int j = 0; j < mDim.mCols; ++j) {
        mData.get(i).put(j, v);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.DynamicMatrix#update(int, int, double)
   */
  @Override
  public void update(int row, int column, double v) {
    mData.get(row).put(column, v);

    super.update(row, column, v);
  }

  @Override
  public void update(int row, int column, int v) {
    mData.get(row).put(column, v);

    super.update(row, column, v);
  }

  @Override
  public void update(int row, int column, long v) {
    mData.get(row).put(column, v);

    super.update(row, column, v);
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

    super.update(row, column, v);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.IndexMatrix#getCellType(int)
   */
  @Override
  public CellType getCellType(int row, int column) {
    Object v = get(row, column);

    if (v != null && v instanceof Number) {
      return CellType.NUMBER;
    } else {
      return CellType.TEXT;
    }
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

  public static Matrix transpose(MixedWorksheet m) {

    // Return a fixed sized array where possible
    MixedMatrix ret = MixedMatrix.createMixedMatrix(m.getCols(), m.getRows());

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
   * Creates the dynamic mixed matrix.
   *
   * @return the matrix
   */
  public static Matrix newDynamicMixedMatrix(int rows, int cols) {
    return new MixedWorksheet(rows, cols);
  }
}
