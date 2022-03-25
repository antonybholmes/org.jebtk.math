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
 * For matrices that use an index approach to store values in a 1D array with
 * appropriate offset calculations.
 * 
 * @author Antony Holmes
 */
public abstract class IndexRowMatrix extends IndexMatrix {

  /**
   * The constant serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The offsets in the array where each new row begins.
   */
  public final int[] mRowOffsets;

  /**
   * Instantiates a new index matrix.
   *
   * @param rows the rows
   * @param columns the columns
   */
  public IndexRowMatrix(int rows, int columns) {
    super(rows, columns);

    mRowOffsets = new int[mDim.mRows];

    createOffsets();
  }

  /**
   * Instantiates a new indexable matrix.
   *
   * @param m the m
   */
  public IndexRowMatrix(Matrix m) {
    super(m);

    mRowOffsets = new int[mDim.mRows];

    createOffsets();
  }

  /**
   * Instantiates a new indexable matrix.
   *
   * @param m the m
   */
  public IndexRowMatrix(IndexRowMatrix m) {
    super(m);

    mRowOffsets = new int[mDim.mRows];

    createOffsets();
  }

  private void createOffsets() {
    // Cache the offsets to improve lookup times

    mRowOffsets[0] = 0;

    for (int i = 1; i < mDim.mRows; ++i) {
      // Use only additions
      mRowOffsets[i] = mRowOffsets[i - 1] + mDim.mCols; // i * columns;
    }
  }

  /**
   * Gets the index of a row cell lookup. This is the position in a 1D row
   * centric array corresponding to the cell indicated by row and column.
   *
   * @param row the row
   * @param column the column
   * @return the index
   */
  @Override
  public int getIndex(int row, int column) {
    return mRowOffsets[row] + column;
  }
  
  @Override
  public void setRow(int row, String[] values) {
    
    int index = mRowOffsets[row];
    
    for (int i = 0; i < values.length; ++i) {
      set(index++, values[i]);
    }

    fireMatrixChanged();
  }
  
  @Override
  public void setRow(int row, double[] values) {
    
    int index = mRowOffsets[row];
    
    for (int i = 0; i < values.length; ++i) {
      set(index++, values[i]);
    }

    fireMatrixChanged();
  }
  
  @Override
  public void setRow(int row, int[] values) {
    
    int index = mRowOffsets[row];
    
    for (int i = 0; i < values.length; ++i) {
      set(index++, values[i]);
    }

    fireMatrixChanged();
  }
  
  @Override
  public void setRow(int row, Object[] values) {
    
    int index = mRowOffsets[row];
    
    for (int i = 0; i < values.length; ++i) {
      set(index++, values[i]);
    }

    fireMatrixChanged();
  }
}
