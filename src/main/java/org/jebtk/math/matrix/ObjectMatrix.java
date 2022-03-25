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

import org.jebtk.core.text.TextUtils;

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
public abstract class ObjectMatrix extends Matrix {

  /**
   * The constant serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  
  public ObjectMatrix(int rows, int columns) {
    super(rows, columns);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.IndexMatrix#getValue(int)
   */
  @Override
  public double getValue(int row, int column) {
    Object v = get(row, column);

    if (v != null && v instanceof Number) {
      return ((Double) v).doubleValue();
    } else {
      return 0;
    }
  }

  @Override
  public int getInt(int row, int column) {
    Object v = get(row, column);

    if (v != null && v instanceof Number) {
      return ((Number) v).intValue();
    } else {
      return 0;
    }
  }

  @Override
  public long getLong(int row, int column) {
    Object v = get(row, column);

    if (v != null && v instanceof Number) {
      return ((Number) v).longValue();
    } else {
      return 0;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.IndexMatrix#getText(int)
   */
  @Override
  public String getText(int row, int column) {
    Object v = get(row, column);

    if (v != null) {
      return v.toString();
    } else {
      return TextUtils.EMPTY_STRING;
    }
  }
}
