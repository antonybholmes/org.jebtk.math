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

import org.jebtk.core.sys.SysUtils;

/**
 * Representation of an upper triangular square matrix. This stores only the
 * upper half of the matrix so scales better with size and reduces redundancy in
 * a symmetrical matrix. There is a small time penalty since lookups are not
 * conventional.
 * 
 * @author Antony Holmes
 *
 */
public class UpperTriangularIntMatrix extends UpperTriangularMatrix {

  /**
   * The constant serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The member data.
   */
  public final int[] mData;

  /**
   * Instantiates a new distance matrix.
   *
   * @param size the size
   */
  public UpperTriangularIntMatrix(int size) {
    super(size);

    mData = new int[mOccupied];
  }

  /**
   * Instantiates a new upper triangular int matrix.
   *
   * @param m the m
   */
  public UpperTriangularIntMatrix(UpperTriangularIntMatrix m) {
    this(m.mSize);

    SysUtils.arraycopy(m.mData, mData);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#copy()
   */
  @Override
  public Matrix copy() {
    return new UpperTriangularIntMatrix(this);
  }

  @Override
  public Matrix ofSameType(int rows, int cols) {
    return new UpperTriangularIntMatrix(Math.max(rows, cols));
  }

  /**
   * Sets the value in every matrix element.
   *
   * @param value the value
   */
  @Override
  public void update(int value) {
    for (int i = 0; i < mData.length; ++i) {
      mData[i] = value;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.Matrix#getValue(int, int)
   */
  @Override
  public double getValue(int index) {
    return mData[index];
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.IndexMatrix#getText(int)
   */
  @Override
  public String getText(int index) {
    return Integer.toString(mData[index]);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.Matrix#get(int, int)
   */
  @Override
  public Object get(int index) {
    return getValue(index);
  }

  @Override
  public void update(int index, double value) {
    update(index, (int) value);
  }

  @Override
  public void update(int index, long value) {
    update(index, (int) value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.Matrix#update(int, int, int)
   */
  @Override
  public void update(int index, int value) {
    mData[index] = value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.Matrix#update(java.lang.String)
   */
  @Override
  public void update(String value) {
    // Do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.UpperTriangularMatrix#transpose()
   */
  @Override
  public Matrix transpose() {
    return copy();
  }

  @Override
  public void apply(CellFunction f) {
    int ix = 0;

    for (int i = 0; i < mDim.mRows; ++i) {
      for (int j = i; i < mDim.mCols; ++j) {
        mData[ix] = (int) f.f(i, j, mData[ix]);

        ++ix;
      }
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

  //
  // Static methods
  //

  /**
   * Creates the upper triangular matrix.
   *
   * @param m the m
   * @return the upper triangular int matrix
   */
  public static UpperTriangularIntMatrix createUpperTriangularMatrix(Matrix m) {
    return createUpperTriangularMatrix(m.getRows());
  }

  /**
   * Creates the upper triangular matrix.
   *
   * @param rows the rows
   * @return the upper triangular int matrix
   */
  public static UpperTriangularIntMatrix createUpperTriangularMatrix(int rows) {
    return new UpperTriangularIntMatrix(rows);
  }
}
