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

/**
 * A conventional matrix with a fixed number of rows and columns.
 * 
 * @author Antony Holmes
 */
public abstract class RegularMatrix extends Matrix {

  /**
   * The constant serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  public final MatrixDim mDim;

  public final int mSize;

  /**
   * Create a new matrix defaulting to being entirely numeric.
   *
   * @param rows the rows
   * @param columns the columns
   */
  public RegularMatrix(int rows, int columns) {
    super(rows, columns);

    mDim = new MatrixDim(rows, columns);

    mSize = rows * columns;
  }

  @Override
  public MatrixDim getShape() {
    return mDim;
  }

  public int size() {
    return mSize;
  }

  @Override
  public void apply(CellFunction f, Matrix m) {
    for (int i = 0; i < mDim.mRows; ++i) {
      for (int j = 0; j < mDim.mCols; ++j) {
        set(i, j, f.f(i, j, getValue(i, j), m.getValue(i, j)));
      }
    }
  }

  @Override
  public double[] toDoubleArray() {
    double[] ret = new double[mSize];

    toDoubleArray(ret);

    return ret;
  }

  @Override
  public int[] toIntArray() {
    int[] ret = new int[mSize];

    toIntArray(ret);

    return ret;
  }

  @Override
  public long[] toLongArray() {
    long[] ret = new long[mSize];

    toLongArray(ret);

    return ret;
  }

  @Override
  public String[] toStringArray() {
    String[] ret = new String[mSize];

    toStringArray(ret);

    return ret;
  }

  @Override
  public Matrix ofSameType() {
    return ofSameType(mDim.mRows, mDim.mCols);
  }
}
