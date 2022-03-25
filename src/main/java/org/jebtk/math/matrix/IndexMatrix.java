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
 * For matrices that intending to store data in a 1D array format. This class
 * provides some of the required implementation.
 * 
 * @author Antony Holmes
 */
public abstract class IndexMatrix extends RegularMatrix {

  /**
   * The constant serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new index matrix.
   *
   * @param rows the rows
   * @param columns the columns
   */
  public IndexMatrix(int rows, int columns) {
    super(rows, columns);
  }

  /**
   * Instantiates a new index matrix.
   *
   * @param m the m
   */
  public IndexMatrix(Matrix m) {
    this(m.getRows(), m.getCols());

    update(m);
  }

  /**
   * Instantiates a new index matrix.
   *
   * @param m the m
   */
  public IndexMatrix(IndexMatrix m) {
    this(m.getRows(), m.getCols());

    update(m);
  }

  /**
   * Copy the values from a matrix to this matrix.
   *
   * @param m the m
   */
  public void set(IndexMatrix m) {
    update(m);

    fireMatrixChanged();
  }

  /**
   * Copy the values from a matrix to this matrix.
   *
   * @param m the m
   */
  public void update(IndexMatrix m) {
    int r = Math.min(size(), m.size());

    for (int i = 0; i < r; ++i) {
      set(i, m.get(i));
    }
  }

  /**
   * Returns the lookup index associated with the row and column.
   *
   * @param row the row
   * @param column the column
   * @return the index
   */
  public abstract int getIndex(int row, int column);

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#update(double)
   */
  @Override
  public void update(double value) {
    for (int i = 0; i < mSize; ++i) {
      update(i, value);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#update(java.lang.String)
   */
  @Override
  public void update(String value) {
    for (int i = 0; i < mSize; ++i) {
      update(i, value);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.Matrix#updateValue(int, int, double)
   */
  @Override
  public void update(int row, int column, double v) {
    update(getIndex(row, column), v);
  }

  /**
   * Update.
   *
   * @param index the index
   * @param v the v
   */
  public void update(int index, double v) {
    // Do nothing
  }

  @Override
  public void update(int row, int column, int v) {
    update(getIndex(row, column), v);
  }
  
  public void update(int index, boolean v) {
    // Do nothing
  }

  @Override
  public void update(int row, int column, boolean v) {
    update(getIndex(row, column), v);
  }

  /**
   * Update.
   *
   * @param index the index
   * @param v the v
   */
  public void update(int index, int v) {
    update(index, (double) v);
  }

  @Override
  public void update(int row, int column, long v) {
    update(getIndex(row, column), v);
  }

  /**
   * Update.
   *
   * @param index the index
   * @param v the v
   */
  public void update(int index, long v) {
    update(index, (double) v);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.Matrix#updateText(int, int, java.lang.String)
   */
  @Override
  public void update(int row, int column, String v) {
    update(getIndex(row, column), v);
  }

  /**
   * Update text.
   *
   * @param index the index
   * @param v the v
   */
  public void update(int index, String v) {
    // Do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.Matrix#setText(int, int, java.lang.String)
   */
  @Override
  public void set(int row, int column, String v) {
    set(getIndex(row, column), v);
  }

  /**
   * Sets the text.
   *
   * @param index the index
   * @param v the v
   */
  public void set(int index, String v) {
    update(index, v);

    fireMatrixChanged();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.Matrix#setValue(int, int, double)
   */
  @Override
  public void set(int row, int column, double v) {
    set(getIndex(row, column), v);
  }

  /**
   * Sets the.
   *
   * @param index the index
   * @param v the v
   */
  public void set(int index, double v) {
    update(index, v);

    fireMatrixChanged();
  }

  @Override
  public void set(int row, int column, long v) {
    set(getIndex(row, column), v);
  }

  public void set(int index, long v) {
    update(index, v);

    fireMatrixChanged();
  }

  @Override
  public void set(int row, int column, int v) {
    set(getIndex(row, column), v);
  }

  /**
   * Sets the.
   *
   * @param index the index
   * @param v the v
   */
  public void set(int index, int v) {
    update(index, v);

    fireMatrixChanged();
  }

  /**
   * Sets the.
   *
   * @param index the index
   * @param v the v
   */
  public void set(int index, Object v) {
    update(index, v);

    fireMatrixChanged();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#update(int, int, java.lang.Object)
   */
  @Override
  public void update(int row, int column, Object v) {
    update(getIndex(row, column), v);
  }

  /**
   * Update.
   *
   * @param index the index
   * @param v the v
   */
  public void update(int index, Object v) {
    if (v != null) {
      if (v instanceof Double) {
        update(index, ((Double) v).doubleValue());
      } else if (v instanceof Long) {
        update(index, ((Long) v).longValue());
      } else if (v instanceof Integer) {
        update(index, ((Integer) v).intValue());
      } else {
        update(index, v.toString());
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.Matrix#get(int, int)
   */
  @Override
  public Object get(int row, int column) {
    return get(getIndex(row, column));
  }

  /**
   * Gets the.
   *
   * @param index the index
   * @return the object
   */
  public Object get(int index) {
    if (getCellType(index) == CellType.NUMBER) {
      return getValue(index);
    } else {
      return getText(index);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.Matrix#getValue(int, int)
   */
  @Override
  public double getValue(int row, int column) {
    return getValue(getIndex(row, column));
  }

  /**
   * Gets the value.
   *
   * @param index the index
   * @return the value
   */
  public double getValue(int index) {
    return 0;
  }

  @Override
  public int getInt(int row, int column) {
    return getInt(getIndex(row, column));
  }

  /**
   * Returns the int value of the cell at the given index.
   * 
   * @param index   a lookup index into the matrix.
   * @return
   */
  public int getInt(int index) {
    return 0;
  }

  @Override
  public long getLong(int row, int column) {
    return getLong(getIndex(row, column));
  }

  public long getLong(int index) {
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.Matrix#getText(int, int)
   */
  @Override
  public String getText(int row, int column) {
    return getText(getIndex(row, column));
  }

  /**
   * Gets the text.
   *
   * @param index the index
   * @return the text
   */
  public String getText(int index) {
    return TextUtils.EMPTY_STRING;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.Matrix#getCellType(int, int)
   */
  @Override
  public CellType getCellType(int row, int column) {
    return getCellType(getIndex(row, column));
  }

  /**
   * Gets the cell type.
   *
   * @param index the index
   * @return the cell type
   */
  public CellType getCellType(int index) {
    return CellType.NUMBER;
  }

  @Override
  public void apply(CellFunction f) {
    applySimple(f, this);
  }

  public static void applySimple(CellFunction f, IndexMatrix m1) {
    int r = 0;
    int c = 0;

    for (int i = 0; i < m1.size(); ++i) {
      m1.set(i, f.f(r, c, m1.getValue(i)));

      if (c++ == m1.mDim.mCols) {
        ++r;
        c = 0;
      }
    }
  }

  @Override
  public void apply(CellFunction f, double v) {
    apply(f, this, v);
  }

  public static void apply(CellFunction f, IndexMatrix m1, double v) {
    int r = 0;
    int c = 0;

    for (int i = 0; i < m1.size(); ++i) {
      m1.set(i, f.f(r, c, m1.getValue(i), v));

      if (c++ == m1.mDim.mCols) {
        ++r;
        c = 0;
      }
    }
  }

  @Override
  public void apply(CellFunction f, Matrix m) {
    if (m instanceof IndexMatrix) {
      apply(f, (IndexMatrix) m);
    } else {
      super.apply(f, m);
    }
  }

  public void apply(CellFunction f, IndexMatrix m) {
    apply(f, this, m);
  }

  public static void apply(CellFunction f, IndexMatrix m1, IndexMatrix m2) {
    int r = 0;
    int c = 0;

    for (int i = 0; i < m1.size(); ++i) {
      m1.set(i, f.f(r, c, m1.getValue(i), m2.getValue(i)));

      if (c++ == m1.mDim.mCols) {
        ++r;
        c = 0;
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#columnAsDouble(int)
   */
  @Override
  public void columnToDouble(int col, double[] ret) {

    int r = getRows();

    int i1 = col;

    int cols = getCols();

    for (int row = 0; row < r; ++row) {
      ret[row] = getValue(i1);

      i1 += cols;
    }
  }

  /*
  @Override
  public List<String> columnAsText(int column) {
    int r = getRows();

    List<String> values = new ArrayList<String>(r);

    int i1 = getIndex(0, column);

    int cols = getCols();

    for (int row = 0; row < r; ++row) {
      values.add(getText(i1));

      i1 += cols;
    }

    return values;
  }
  */

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#rowAsDouble(int)
   */
  @Override
  public void rowToDouble(int row, double[] ret) {
    int c = getCols();

    int i1 = getIndex(row, 0);

    for (int col = 0; col < c; ++col) {
      ret[col] = getValue(i1++);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#rowAsText(int)
   */
  @Override
  public String[] rowToText(int row) {
    int c = getCols();

    String[] values = new String[c];

    int i1 = getIndex(row, 0);

    for (int col = 0; col < c; ++col) {
      values[col] = getText(i1++);
    }

    return values;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.abh.common.math.matrix.Matrix#copyColumn(org.abh.common.math.matrix.
   * Matrix, int, int)
   */
  @Override
  public void copyColumn(final Matrix from, int column, int toColumn) {
    int r = Math.min(from.getRows(), getRows());

    int i = getIndex(0, toColumn);

    int cols = getCols();

    for (int row = 0; row < r; ++row) {
      update(i, from.get(row, column));

      i += cols;
    }

    fireMatrixChanged();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#copyRow(org.abh.common.math.matrix.
   * Matrix, int, int)
   */
  @Override
  public void copyRow(final Matrix from, int row, int toRow) {
    int c = Math.min(from.getCols(), getCols());

    int i = getIndex(toRow, 0);

    for (int col = 0; col < c; ++col) {
      update(i++, from.get(row, col));
    }

    fireMatrixChanged();
  }
}
