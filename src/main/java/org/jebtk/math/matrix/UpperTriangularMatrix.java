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

import org.jebtk.core.Mathematics;

/**
 * Representation of an upper triangular square matrix. This stores only the
 * upper half of the matrix so scales better with size and reduces redundancy in
 * a symmetrical matrix. There is a small time penalty since lookups are not
 * conventional.
 * 
 * @author Antony Holmes
 *
 */
public abstract class UpperTriangularMatrix extends IndexMatrix {

  /**
   * The constant serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /** Pre compute the row offset required to find an element. */
  public final int[] mOffsets;

  /**
   * How many elements out of the square matrix are in use (since approx one
   * half is empty to save space).
   */
  public final int mOccupied;

  /**
   * Instantiates a new distance matrix.
   *
   * @param size the size
   */
  public UpperTriangularMatrix(int size) {
    super(size, size);

    mOccupied = Mathematics.sum(size);

    mOffsets = new int[size];

    // Since the new offset is always starts one more than the previous offset
    // we start c at one so that we do not need to add one in each for loop

    int c = 1;

    mOffsets[0] = 0;

    for (int i = 1; i < size; ++i) {
      // mOffsets[i] = i * size - Mathematics.sum(i);
      mOffsets[i] = mOffsets[i - 1] + size - c++;

      // System.err.println("offset " + i + " " + mOffsets[i]);
    }
  }

  /**
   * Returns the number of occupied cells in the UT matrix, i.e. how many cells
   * are actually in use to create the matrix, rather than rows * columns.
   *
   * @return the num occupied cells
   */
  public int getNumOccupiedCells() {
    return mOccupied;
  }

  /**
   * Returns the index position corresponding to the row and column. Since the
   * matrix is in upper triangular form. The row is always the smaller of the
   * parameters and the column the largest.
   *
   * @param row the row
   * @param column the column
   * @return the index
   */
  @Override
  public int getIndex(int row, int column) {
    // int r = Math.min(row, column);
    // int c = Math.max(row, column);

    // return mOffsets[r] + c;

    if (row > column) {
      // Referencing in the lower triangle, so swap to use the upper
      // triangle
      return mOffsets[column] + row;
    } else {
      // In the upper triangle
      return mOffsets[row] + column;
    }
  }

}
