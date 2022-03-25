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

import java.util.Arrays;

import org.jebtk.core.sys.SysUtils;

/**
 * Matrix for storing doubles.
 *
 * @author Antony Holmes
 */
public class DoubleColMatrix extends IndexColMatrix {

  /**
   * The constant serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /** The m data. */
  public final double[] mData;

  /**
   * Create a new matrix and initialize all cells to a common value.
   *
   * @param rows the rows
   * @param columns the columns
   * @param v the v
   */
  public DoubleColMatrix(int rows, int columns) {
    super(rows, columns);

    mData = new double[mSize];
  }

  public DoubleColMatrix(int rows, int columns, double v) {
    this(rows, columns);

    // Set the default value
    update(v);
  }

  /**
   * Clone a matrix.
   *
   * @param m the m
   */
  public DoubleColMatrix(Matrix m) {
    this(m.getRows(), m.getCols());

    update(m);
  }

  /**
   * Instantiates a new double matrix.
   *
   * @param m the m
   */
  public DoubleColMatrix(IndexRowMatrix m) {
    this(m.getRows(), m.getCols());

    update(m);
  }

  /**
   * Instantiates a new double matrix.
   *
   * @param m the m
   */
  public DoubleColMatrix(DoubleColMatrix m) {
    this(m.getRows(), m.getCols());

    update(m);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.abh.common.math.matrix.Matrix#update(org.abh.common.math.matrix.Matrix)
   */
  @Override
  public void update(Matrix m) {
    if (m instanceof DoubleColMatrix) {
      update((DoubleColMatrix) m);
    } else {
      super.update(m);
    }
  }

  /**
   * Update.
   *
   * @param m the m
   */
  public void update(DoubleColMatrix m) {
    SysUtils.arraycopy(m.mData, mData);
  }

  @Override
  public double[] toDoubleArray() {
    return mData;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#copy()
   */
  @Override
  public Matrix copy() {
    return new DoubleColMatrix(this);
  }

  @Override
  public Matrix ofSameType(int rows, int cols) {
    return new DoubleColMatrix(rows, cols);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.Matrix#getNumCells()
   */
  @Override
  public int size() {
    return mData.length;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.IndexMatrix#get(int)
   */
  @Override
  public Object get(int index) {
    return getValue(index);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.IndexMatrix#getValue(int)
   */
  @Override
  public double getValue(int index) {
    return mData[index];
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.Matrix#updateValue(double)
   */
  @Override
  public void update(double v) {
    Arrays.fill(mData, v);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.IndexMatrix#updateValue(int, double)
   */
  @Override
  public void update(int index, double v) {
    mData[index] = v;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.IndexMatrix#getText(int)
   */
  @Override
  public String getText(int index) {
    return Double.toString(mData[index]);
  }

  @Override
  public void setColumn(int col, double[] values) {
    SysUtils.arraycopy(values, mData, mColOffsets[col], mDim.mRows);

    fireMatrixChanged();
  }

  public void copyColumn(final DoubleColMatrix from, int col, int toCol) {

    int c = Math.min(from.getCols(), getCols());

    System.arraycopy(from.mData,
        from.mColOffsets[col],
        mData,
        mColOffsets[toCol],
        c);

    fireMatrixChanged();
  }

  @Override
  public void rowToDouble(int row, double[] ret) {
    SysUtils.arraycopy(mData, getIndex(row, 0), ret, mDim.mCols);
  }

  @Override
  public void columnToDouble(int column, double[] ret) {
    SysUtils.arraycopy(mData, column, mDim.mCols, ret, mDim.mRows);
  }

  @Override
  public void colApply(CellFunction f, int index) {
    int offset = mColOffsets[index];

    for (int i = 0; i < mDim.mRows; ++i) {
      mData[offset] = f.f(i, 0, mData[offset]);

      ++offset;
    }

    fireMatrixChanged();
  }

  @Override
  public double stat(MatrixStatFunction f) {
    f.init();

    for (int i = 0; i < mData.length; ++i) {
      f.f(i, 0, mData[i]);
    }

    return f.getStat();
  }

  @Override
  public double colStat(MatrixStatFunction f, int index) {
    f.init();

    int offset = mColOffsets[index];

    for (int i = 0; i < mDim.mRows; ++i) {
      f.f(i, 0, mData[offset]);

      ++offset;
    }

    return f.getStat();
  }

  @Override
  public Matrix dot(final Matrix m) {
    if (m instanceof DoubleColMatrix) {
      return dot((DoubleColMatrix) m);
    } else {
      return super.dot(m);
    }
  }

  public Matrix dot(final DoubleColMatrix m) {
    dot(this, m);

    fireMatrixChanged();

    return this;
  }

  public static void dot(DoubleColMatrix m1, final DoubleColMatrix m2) {
    for (int i = 0; i < m1.mData.length; ++i) {
      m1.mData[i] *= m2.mData[i];
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.IndexMatrix#transpose()
   */
  @Override
  public Matrix transpose() {
    return transpose(this);
  }

  public static Matrix transpose(final DoubleColMatrix m) {
    DoubleColMatrix ret = DoubleColMatrix.createDoubleColMatrix(m.mDim.mCols,
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

  //
  // Static methods
  //

  /**
   * Returns a new empty matrix the same dimensions as the input matrix.
   *
   * @param m the m
   * @return the double matrix
   */
  public static DoubleColMatrix createDoubleColMatrix(Matrix m) {
    return createDoubleColMatrix(m.getRows(), m.getCols());
  }

  /**
   * Creates the double matrix.
   *
   * @param rows the rows
   * @param cols the cols
   * @return the double matrix
   */
  public static DoubleColMatrix createDoubleColMatrix(int rows, int cols) {
    return new DoubleColMatrix(rows, cols);
  }

  /**
   * Create a data frame from a matrix by changing the inner matrix to a Double
   * Column Matrix.
   * 
   * @param f
   * @return
   */
  public static DataFrame createDataFrame(DataFrame f) {
    return new DataFrame(f, new DoubleColMatrix(f));
  }
}
