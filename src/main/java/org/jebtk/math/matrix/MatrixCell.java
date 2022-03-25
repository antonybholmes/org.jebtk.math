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

import java.util.ArrayList;
import java.util.List;

import org.jebtk.core.Mathematics;
import org.jebtk.core.NumConvertable;
import org.jebtk.core.event.ChangeListeners;
import org.jebtk.core.text.TextUtils;

/**
 * Represents a matrix entry which can either be a double or a string.
 */
public class MatrixCell extends ChangeListeners implements NumConvertable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The Constant NULL_CELL. */
  public static final MatrixCell NULL_CELL = new MatrixCell(Double.NaN);

  /**
   * The maximum number of characters that can occupy a cell.
   */
  public static final int MAX_CHARS = 16384;

  /**
   * The member num data.
   */
  private double mNumData = Double.NaN;

  /**
   * The member data.
   */
  private String mData = TextUtils.EMPTY_STRING;

  /** The m type. */
  private CellType mType;

  /**
   * Instantiates a new matrix cell.
   */
  public MatrixCell() {
    this(Double.NaN);
  }

  /**
   * Instantiates a new matrix cell.
   *
   * @param v the v
   */
  public MatrixCell(double v) {
    setValue(v);
  }

  /**
   * Instantiates a new matrix cell.
   *
   * @param v the v
   */
  public MatrixCell(String v) {
    setValue(v);
  }

  /**
   * Instantiates a new matrix cell.
   *
   * @param v the v
   */
  public MatrixCell(MatrixCell v) {
    setValue(v);
  }

  /**
   * Instantiates a new matrix cell.
   *
   * @param v the v
   */
  public MatrixCell(Object v) {
    setValue(v);
  }

  /**
   * Sets the value.
   *
   * @param v the new value
   */
  public void setValue(Object v) {
    if (v instanceof MatrixCell) {
      setValue((MatrixCell) v);
    } else if (v instanceof Double) {
      setValue((double) v);
    } else if (v instanceof Integer) {
      setValue((int) v);
    } else {
      setValue(v.toString());
    }

    // System.err.println(v + " " + v.getClass() + " " + this.getCellType());
  }

  /**
   * Sets the value.
   *
   * @param v the new value
   */
  public void setValue(MatrixCell v) {
    mData = v.mData;
    mNumData = v.mNumData;
    mType = v.mType;
  }

  /**
   * Sets the value.
   *
   * @param v the new value
   */
  public void setValue(double v) {
    mNumData = v;

    if (isValidNumber(v)) {
      if (Mathematics.isInt(v)) {
        mData = Integer.toString((int) v);
      } else {
        mData = Double.toString(v);
      }

      mType = CellType.NUMBER;
    } else {
      mData = TextUtils.EMPTY_STRING; // TextUtils.NAN;
      mType = CellType.TEXT;
    }

    fireChanged();
  }

  /**
   * Sets the value.
   *
   * @param v the new value
   */
  public void setValue(String v) {
    // Set to NaN so that getText() and toString() will return the
    // string itself rather than a string representation of mNumData.
    mNumData = Double.NaN;

    mData = v; // TextUtils.truncate(v, MAX_CHARS);

    mType = CellType.TEXT;

    fireChanged();
  }

  /**
   * Gets the double.
   *
   * @return the double
   */
  public double getDouble() {
    return mNumData;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.NumConvertable#getInt()
   */
  public int getInt() {
    return (int) mNumData;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.NumConvertable#getFloat()
   */
  public float getFloat() {
    return (float) mNumData;
  }

  /**
   * Gets the text.
   *
   * @return the text
   */
  public String getText() {
    return mData;
  }

  /**
   * Gets the.
   *
   * @return the object
   */
  public Object get() {
    if (mType == CellType.NUMBER) {
      return mNumData;
    } else {
      return mData;
    }
  }

  /**
   * Gets the cell type.
   *
   * @return the cell type
   */
  public CellType getCellType() {
    return mType;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getText();
  }

  /**
   * Returns true if the number is not NaN. NaN is used to indicate an invalid
   * cell since it is not used in any real world calculations so can be safely
   * ignored in most instances.
   *
   * @param v the v
   * @return true, if is valid number
   */
  public static boolean isValidNumber(double v) {
    return !Double.isNaN(v);
  }

  /**
   * Convert a list of doubles to matrix cells.
   *
   * @param values the values
   * @return the list
   */
  public static List<MatrixCell> doublesToList(List<Double> values) {
    List<MatrixCell> ret = new ArrayList<MatrixCell>(values.size());

    for (double v : values) {
      ret.add(new MatrixCell(v));
    }

    return ret;
  }
}
