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

import org.jebtk.core.collections.DefaultHashMapMap;
import org.jebtk.core.collections.IterMap;

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
public abstract class Worksheet<T> extends ResizableMatrix {

  /**
   * The constant serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Dynamically resizing map of maps to store text for each cell.
   */
  protected final IterMap<Integer, IterMap<Integer, T>> mData;

  /**
   * Instantiates a new dynamic matrix.
   *
   * @param rows the rows
   * @param columns the columns
   */
  public Worksheet(int rows, int columns) {
    this(rows, columns, null);
  }
  
  public Worksheet(int rows, int columns, T v) {
    super(rows, columns);
    
    mData = createMap(v);
  }

  protected abstract IterMap<Integer, IterMap<Integer, T>> createMap(T v);



  /**
   * Clone a matrix optionally copying the core matrix values and the
   * annotation.
   *
   * @param m the m
   */
  public Worksheet(Matrix m) {
    this(m.getRows(), m.getCols());

    update(m);
  }

  /**
   * Instantiates a new dynamic matrix.
   *
   * @param m the m
   */
  public Worksheet(Worksheet<T> m) {
    super(m.getRows(), m.getCols());

    mData = DefaultHashMapMap.create(m.getRows(), m.getCols());

    update(m);
  }

  /**
   * Specialized update method for dynamic matrices to quickly copy data from
   * one matrix to another.
   *
   * @param m the m
   */
  public void update(Worksheet<T> m) {
    for (int row : m.mData.keySet()) {
      mData.put(row, m.mData.get(row));
    }
  }
  
  @Override
  public Object get(int row, int column) {
    return mData.get(row).get(column);
  }
}
