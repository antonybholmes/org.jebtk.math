/**
 * Copyright 2016 Antony Holmes
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
package org.jebtk.math.matrix;

/**
 * Immutable integer dimension.
 * 
 * @author Antony Holmes
 *
 */
public class MatrixDim implements Comparable<MatrixDim> {

  /** The Constant DIM_ZERO. */
  public static final MatrixDim DIM_ZERO = new MatrixDim(0, 0);

  /**
   * The member w.
   */
  public final int mRows;

  /**
   * The member h.
   */
  public final int mCols;

  /**
   * Instantiates a new int dim.
   *
   * @param rows the w
   * @param cols the h
   */
  public MatrixDim(int rows, int cols) {
    mRows = rows;
    mCols = cols;
  }

  /**
   * Gets the w.
   *
   * @return the w
   */
  public int getRows() {
    return mRows;
  }

  /**
   * Gets the h.
   *
   * @return the h
   */
  public int getCols() {
    return mCols;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return mRows + " " + mCols;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object o) {
    if (o instanceof MatrixDim) {
      return compareTo((MatrixDim) o) == 0;
    } else {
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(MatrixDim d) {
    if (mRows > d.mRows) {
      if (mCols > d.mCols) {
        return 1;
      } else {
        return -1;
      }
    } else if (mRows < d.mRows) {
      if (mCols > d.mCols) {
        return 1;
      } else {
        return -1;
      }
    } else {
      // Same width so just consider height

      if (mCols > d.mCols) {
        return 1;
      } else if (mCols < d.mCols) {
        return -1;
      } else {
        return 0;
      }
    }
  }
}
