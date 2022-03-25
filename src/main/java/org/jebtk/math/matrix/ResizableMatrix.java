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
 * Matrix that can be dynamically resized to match maximum row/column. This
 * matrix uses lazy instantiation so cells are only created as they are
 * referenced. Thus there will be a performance loss at the expense of
 * flexibility. This matrix has both the Props of a matrix and a map. The
 * matrix's nominal dimensions are increased to match the largest row and column
 * encountered.
 *
 * @author Antony Holmes
 * @param <T> the generic type
 */
public abstract class ResizableMatrix extends ObjectMatrix {

  /**
   * The constant serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The member cells.
   */
  protected int mSize = 0;

  /** The m rows. */
  protected MatrixDim mDim = MatrixDim.DIM_ZERO;

  /**
   * Instantiates a new mixed sparse matrix.
   *
   * @param rows the rows
   * @param columns the columns
   * @param v the v
   */
  public ResizableMatrix(int rows, int columns) {
    super(rows, columns);
    
    updateSize(rows - 1, columns - 1);
  }

  /**
   * Keep track of the maximum row and column requested so far. This is so the
   * matrix can dynamically resize if necessary.
   *
   * @param row the row
   * @param column the column
   */
  protected void updateSize(int row, int col) {
    boolean changed = false;

    if (row >= mDim.mRows) {
      changed = true;
    }

    if (col >= mDim.mCols) {
      changed = true;
    }

    if (changed) {
      mDim = new MatrixDim(Math.max(mDim.mRows, row + 1),
          Math.max(mDim.mCols, col + 1));

      mSize = mDim.mRows * mDim.mCols;

      // fireMatrixChanged();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#update(int, int, double)
   */
  @Override
  public void update(int row, int column, double v) {
    updateSize(row, column);
    
    //super.update(row, column, v);
  }

  @Override
  public void update(int row, int column, long v) {
    updateSize(row, column);
    
    //super.update(row, column, v);
  }

  @Override
  public void update(int row, int column, int v) {
    updateSize(row, column);
    
    //super.update(row, column, v);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#update(int, int, java.lang.String)
   */
  @Override
  public void update(int row, int column, String v) {
    updateSize(row, column);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.Matrix#getNumCells()
   */
  @Override
  public int size() {
    return mSize;
  }

  @Override
  public MatrixDim getShape() {
    return mDim;
  }
}
