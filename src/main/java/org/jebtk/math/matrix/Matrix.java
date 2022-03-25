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

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jebtk.core.Indexed;
import org.jebtk.core.Mathematics;
import org.jebtk.core.collections.CollectionUtils;
import org.jebtk.core.text.Parser;
import org.jebtk.core.text.TextUtils;
import org.jebtk.math.functions.LnFunction;
import org.jebtk.math.functions.LogFunction;

/**
 * Basis for a numerical matrix. Note that Double.NaN is used to indicate an
 * unset/absent value, so valid matrices should not contain this value (it is
 * ignored by matrix operations). Matrices support double long, int, and String
 * types only.
 *
 * @author Antony Holmes
 */
public abstract class Matrix extends MatrixEventListeners {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The Constant NULL_NUMBER. */
  //public static final double NULL_NUMBER = Double.NaN;

  /** The Constant NULL_INT_NUMBER. */
  // public static final int NULL_INT_NUMBER = Integer.MIN_VALUE;

  // public static final long NULL_LONG_NUMBER = Long.MIN_VALUE;

  /** The Constant NUMBER_MATRIX_TYPES. */
  private static final Set<CellType> NUMBER_MATRIX_TYPES = CollectionUtils
      .toSet(CellType.NUMBER);

  /** The Constant TEXT_MATRIX_TYPES. */
  private static final Set<CellType> TEXT_MATRIX_TYPES = CollectionUtils
      .toSet(CellType.TEXT);

  public static CellFunction ADD_FUNCTION = new CellFunction() {
    @Override
    public double f(int r, int c, double x, double... y) {
      return x + y[0];
    }
  };

  public static CellFunction SUB_FUNCTION = new CellFunction() {
    @Override
    public double f(int r, int c, double x, double... y) {
      return x - y[0];
    }
  };

  public static CellFunction MULT_FUNCTION = new CellFunction() {
    @Override
    public double f(int r, int c, double x, double... y) {
      return x * y[0];
    }
  };

  public static CellFunction DIV_FUNCTION = new CellFunction() {
    @Override
    public double f(int r, int c, double x, double... y) {
      return x * y[0];
    }
  };

  /** The Constant CONCURRENT_ROWS. */
  public static final int CONCURRENT_ROWS = 4;

  /**
   * Instantiates a new matrix.
   *
   * @param rows the rows
   * @param columns the columns
   */
  public Matrix(int rows, int columns) {
    init(rows, columns);
  }

  /**
   * Guaranteed to be called during initialization so can be used to 
   * size internal data structures before trying to set values.
   *
   * @param rows      The number of rows
   * @param columns   The number of columns
   */
  protected void init(int rows, int columns) {
    // Do nothing
  }

  /**
   * Returns the dimensions of the matrix.
   * 
   * @return
   */
  public abstract MatrixDim getShape();

  public int getRows() {
    return getShape().getRows();
  }

  public int getCols() {
    return getShape().getCols();
  }

  /**
   * Returns the type of the matrix indicating whether it store numbers, 
   * text or both.
   *
   * @return the type
   */
  public MatrixType getType() {
    return MatrixType.NUMBER;
  }

  /**
   * Returns the total number of elements in the matrix.
   *
   * @return the num cells
   */
  public abstract int size();

  /**
   * Returns a copy the matrix.
   *
   * @return the matrix
   */
  public Matrix copy() {
    return this;
  }

  /**
   * Set a cell to a given value.
   *
   * @param row the row
   * @param column the column
   * @param v the v
   */
  public void set(int row, int column, double v) {
    update(row, column, v);

    fireMatrixChanged();
  }

  /**
   * Update a cell with a number value, but does not trigger any events.
   *
   * @param row the row
   * @param column the column
   * @param mValue the value
   */
  public void update(int row, int column, double v) {
    // Do nothing
  }

  public void set(int row, int column, int v) {
    update(row, column, v);

    fireMatrixChanged();
  }

  public void update(int row, int column, int v) {
    update(row, column, (double) v);
  }

  public void set(int row, int column, long v) {
    update(row, column, v);

    fireMatrixChanged();
  }

  public void update(int row, int column, long v) {
    update(row, column, (double) v);
  }

  public void set(double v) {
    update(v);

    fireMatrixChanged();
  }
  
  public void set(int row, int column, boolean v) {
    update(row, column, v);

    fireMatrixChanged();
  }

  public void update(int row, int column, boolean v) {
    update(row, column,  v);
  }

  public void set(boolean v) {
    update(v);

    fireMatrixChanged();
  }
  
  public void update(boolean value) {
    for (int i = 0; i < getRows(); ++i) {
      for (int j = 0; j < getCols(); ++j) {
        update(i, j, value);
      }
    }
  }

  /**
   * Update value.
   *
   * @param value the value
   */
  public void update(double value) {
    for (int i = 0; i < getRows(); ++i) {
      for (int j = 0; j < getCols(); ++j) {
        update(i, j, value);
      }
    }
  }

  public void set(int v) {
    update(v);

    fireMatrixChanged();
  }

  /**
   * Update text.
   *
   * @param value the value
   */
  public void update(int value) {
    update((double) value);
  }

  public void set(long v) {
    update(v);

    fireMatrixChanged();
  }

  /**
   * Update text.
   *
   * @param value the value
   */
  public void update(long value) {
    update((double) value);
  }

  public void set(String v) {
    update(v);

    fireMatrixChanged();
  }

  /**
   * Update text.
   *
   * @param value the value
   */
  public void update(String value) {
    for (int i = 0; i < getRows(); ++i) {
      for (int j = 0; j < getCols(); ++j) {
        update(i, j, value);
      }
    }
  }

  public void set(Object v) {
    update(v);

    fireMatrixChanged();
  }

  /**
   * Attempt to convert an object to a supported matrix type and then set all
   * cells to the value.
   * 
   * @param v
   */
  public void update(Object v) {
    if (v != null) {
      if (v instanceof Double) {
        update(((Double) v).doubleValue());
      } else if (v instanceof Long) {
        update(((Long) v).longValue());
      } else if (v instanceof Integer) {
        update(((Integer) v).intValue());
      } else if (v instanceof Boolean) {
        update(((Boolean)v).booleanValue());
      } else {
        update(v.toString());
      }
    }
  }

  /**
   * Update cell with a value. If the value can be converted to a number update
   * the cell as a number, else try to update as a string
   *
   * @param row the row
   * @param column the column
   * @param value the value
   */
  public void update(int row, int column, Object v) {
    if (v == null) {
      return;
    }
    
    if (v instanceof Double) {
      update(row, column, (double) v);
    } else if (v instanceof Integer) {
      update(row, column, (int) v);
    } else if (v instanceof Number) {
      update(row, column, ((Number) v).doubleValue());
    } else {
      String s = v.toString();

      try {
        update(row, column, Parser.toDouble(s));
      } catch (ParseException e) {
        update(row, column, s);
      }
    }
    
    /*
    if (value != null) {
      if (value instanceof Number) {
        update(row, column, ((Number) value).doubleValue());
      } else {
        String s = value.toString();

        try {
          // First try to parse the string as number.
          update(row, column, Double.parseDouble(s));
        } catch (Exception e) {
          // If that fails, use the string value for the cell value.
          update(row, column, s);
        }
      }
    }
    */
  }

  /**
   * Copy the values from a matrix to this matrix.
   *
   * @param m the m
   */
  public void update(Matrix m) {
    int r = Math.min(getRows(), m.getRows());
    int c = Math.min(getCols(), m.getCols());

    for (int i = 0; i < r; ++i) {
      for (int j = 0; j < c; ++j) {
        set(i, j, m.get(i, j));
      }
    }
  }

  /**
   * Sets the text.
   *
   * @param row the row
   * @param column the column
   * @param v the v
   */
  public void set(int row, int column, String v) {
    update(row, column, v);

    fireMatrixChanged();
  }

  /**
   * Update a cell with a string.
   *
   * @param row the row
   * @param column the column
   * @param value the value
   */
  public void update(int row, int column, String value) {
    // Do nothing
  }

  /**
   * Set a cell element by analyzing the object type.
   *
   * @param row the row
   * @param column the column
   * @param value the value
   */
  public void set(int row, int column, Object value) {
    update(row, column, value);

    fireMatrixChanged();
  }

  /**
   * Copy the values from a matrix to this matrix.
   *
   * @param m the m
   */
  public void set(Matrix m) {
    update(m);

    fireMatrixChanged();
  }

  public void setColumn(int column, Object[] values) {
    for (int i = 0; i < values.length; ++i) {
      set(i, column, values[i]);
    }

    fireMatrixChanged();
  }

  public void setColumn(int column, double[] values) {
    for (int i = 0; i < values.length; ++i) {
      set(i, column, values[i]);
    }

    fireMatrixChanged();
  }
  
  public void setColumn(int column, int[] values) {
    for (int i = 0; i < values.length; ++i) {
      set(i, column, values[i]);
    }

    fireMatrixChanged();
  }
  
  public void setColumn(int column, String[] values) {
    for (int i = 0; i < values.length; ++i) {
      set(i, column, values[i]);
    }

    fireMatrixChanged();
  }

  /**
   * Set the value of a row.
   * 
   * @param row
   * @param values
   */
  public void setRow(int row, Object[] values) {
    for (int i = 0; i < values.length; ++i) {
      set(row, i, values[i]);
    }

    fireMatrixChanged();
  }

  public void setRow(int row, double[] values) {
    for (int i = 0; i < values.length; ++i) {
      set(row, i, values[i]);
    }

    fireMatrixChanged();
  }
  
  public void setRow(int row, int[] values) {
    for (int i = 0; i < values.length; ++i) {
      set(row, i, values[i]);
    }

    fireMatrixChanged();
  }
  
  public void setRow(int row, long[] values) {
    for (int i = 0; i < values.length; ++i) {
      set(row, i, values[i]);
    }

    fireMatrixChanged();
  }
  
  public void setRow(int row, boolean[] values) {
    for (int i = 0; i < values.length; ++i) {
      set(row, i, values[i]);
    }

    fireMatrixChanged();
  }
  
  public void setRow(int row, String[] values) {
    for (int i = 0; i < values.length; ++i) {
      set(row, i, values[i]);
    }

    fireMatrixChanged();
  }

  /**
   * Copy the row from the "from" matrix to this matrix.
   *
   * @param from the from
   * @param row the row
   * @param toRow the to row
   */
  public void copyRow(final Matrix from, int row, int toRow) {
    int c = Math.min(from.getCols(), getCols());
 
    for (int i = 0; i < c; ++i) {
      set(toRow, i, from.get(row, i));
    }
  }


  /**
   * Copy row.
   *
   * @param from the from
   * @param row the row
   * @param toRow the to row
   */
  public void copyRow(final MixedMatrix from, int row, int toRow) {
    int c = Math.min(from.getCols(), getCols());

    for (int i = 0; i < c; ++i) {
      set(toRow, i, from.getText(row, i));
    }

    for (int i = 0; i < c; ++i) {
      set(toRow, i, from.getValue(row, i));
    }
  }

  /**
   * Copy a column from one matrix to another.
   *
   * @param from the from
   * @param column the column
   */
  public void copyColumn(final Matrix from, int column) {
    copyColumn(from, column, column);
  }

  /**
   * Copy column.
   *
   * @param from the from
   * @param column the column
   * @param toColumn the to column
   */
  public void copyColumn(final Matrix from, int column, int toColumn) {
    if (from instanceof DoubleMatrix) {
      copyColumn((DoubleMatrix) from, column, toColumn);
    } else if (from instanceof TextMatrix) {
      copyColumn((TextMatrix) from, column, toColumn);
    } else {
      int r = Math.min(from.getRows(), getRows());

      for (int i = 0; i < r; ++i) {
        set(i, toColumn, from.get(i, column));
      }
    }
  }

  /**
   * Copy column.
   *
   * @param from the from
   * @param column the column
   * @param toColumn the to column
   */
  public void copyColumn(final DoubleMatrix from, int column, int toColumn) {
    int r = Math.min(from.getRows(), getRows());

    for (int i = 0; i < r; ++i) {
      set(i, toColumn, from.getValue(i, column));
    }
  }

  /**
   * Copy column.
   *
   * @param from the from
   * @param column the column
   * @param toColumn the to column
   */
  public void copyColumn(final TextMatrix from, int column, int toColumn) {
    int r = Math.min(from.getRows(), getRows());

    for (int i = 0; i < r; ++i) {
      set(i, toColumn, from.getText(i, column));
    }
  }

  /**
   * Copy column.
   *
   * @param from the from
   * @param column the column
   * @param toColumn the to column
   */
  public void copyColumn(final MixedMatrix from, int column, int toColumn) {
    int r = Math.min(from.getRows(), getRows());

    for (int i = 0; i < r; ++i) {
      set(i, toColumn, from.getText(i, column));
    }

    for (int i = 0; i < r; ++i) {
      set(i, toColumn, from.getValue(i, column));
    }
  }
  
  /**
   * Copy columns out of a matrix
   * @param col
   * @param cols
   * @return
   */
  public Matrix cols(int col, int... cols) {
    return null;
  }


  /**
   * Should return the numerical value at a cell location or Double.NaN if the
   * cell is not in use.
   *
   * @param row the row
   * @param column the column
   * @return the value
   */
  public double getValue(int row, int column) {
    return 0;
  }

  public int getInt(int row, int column) {
    return (int) getValue(row, column);
  }

  public long getLong(int row, int column) {
    return (long) getValue(row, column);
  }

  /**
   * Gets the text.
   *
   * @param row the row
   * @param column the column
   * @return the text
   */
  public String getText(int row, int column) {
    return TextUtils.EMPTY_STRING;
  }

  /**
   * Gets the.
   *
   * @param row the row
   * @param column the column
   * @return the object
   */
  public Object get(int row, int column) {
    if (getCellType(row, column) == CellType.NUMBER) {
      return getValue(row, column);
    } else {
      return getText(row, column);
    }
  }

  /**
   * Should return whether the cell contains a number or string.
   *
   * @param row the row
   * @param column the column
   * @return the cell type
   */
  public CellType getCellType(int row, int column) {
    return CellType.NUMBER;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder buffer = new StringBuilder("[").append(getRows())
        .append(" x ").append(getCols()).append("]");

    return buffer.toString();
  }

  /**
   * Converts a matrix column into a list.
   *
   * @param column the column
   * @return the list
   */
  public Object[] columnToObject(int column) {
    Object[] ret = new Object[getRows()];

    columnToObject(column, ret);

    return ret;
  }

  /**
   * Converts a matrix column to a list of strings.
   *
   * @param column the column
   * @return the list
   */
  public String[] columnToText(int column) {
    String[] ret = new String[getRows()];
    columnToText(column, ret);
    
    return ret;
  }
  
  public double[] columnToDouble(int column) {
    double[] ret = new double[getRows()];
    columnToDouble(column, ret);
    
    return ret;
  }

  public void columnToDouble(int column, double[] ret) {
    int r = getRows();

    for (int row = 0; row < r; ++row) {
      double v = getValue(row, column);

      ret[row] = v;
    }
  }

  public void columnToInt(int column, int[] ret) {
    int r = getRows();

    for (int row = 0; row < r; ++row) {
      int v = getInt(row, column);

      ret[row] = v;
    }
  }

  public void columnToLong(int column, long[] ret) {
    int r = getRows();

    for (int row = 0; row < r; ++row) {
      long v = getLong(row, column);

      ret[row] = v;
    }
  }
  
  public void columnToText(int column, String[] ret) {
    int r = getRows();

    for (int row = 0; row < r; ++row) {
      String v = getText(row, column);

      ret[row] = v;
    }
  }
  

  
  public void columnToObject(int column, Object[] ret) {
    int r = getRows();

    for (int row = 0; row < r; ++row) {
      Object v = getValue(row, column);

      ret[row] = v;
    }
  }

  /**
   * Converts a matrix row into a list.
   *
   * @param row the row
   * @return the list
   */
  public List<Object> rowAsList(int row) {
    Object[] ret = new Object[getRows()];
    rowToObject(row, ret);
    
    return Arrays.asList(ret);
  }

  public double[] rowToDouble(int row) {
    double[] ret = new double[getCols()];
    rowToDouble(row, ret);
    
    return ret;
  }
  
  /**
   * Extract row data into a double array.
   * 
   * @param row     the row of interest.
   * @param data    an array to copy data into. Array must be able to contain
   *                all row values.
   */
  public void rowToDouble(int row, double[] data) {
    for (int c = 0; c < data.length; ++c) {
      data[c] = getValue(row, c);
    }
  }

  public int[] rowToInt(int row) {
    int n = getCols();

    int[] ret = new int[n];

    rowToInt(row, ret);

    return ret;
  }

  public void rowToInt(int row, int[] data) {
    for (int c = 0; c < data.length; ++c) {
      data[c] = getInt(row, c);
    }
  }

  public long[] rowToLong(int row) {
    int n = getCols();

    long[] ret = new long[n];

    rowToLong(row, ret);

    return ret;
  }

  public void rowToLong(int row, long[] data) {
    for (int c = 0; c < data.length; ++c) {
      data[c] = getLong(row, c);
    }
  }
  
  public Object[] rowToObject(int row) {
    int n = getCols();

    Object[] ret = new Object[n];

    rowToObject(row, ret);

    return ret;
  }
  
  public void rowToObject(int row, Object[] data) {
    for (int c = 0; c < data.length; ++c) {
      data[c] = get(row, c);
    }
  }
  
  public void rowToText(int row, String[] data) {
    for (int c = 0; c < data.length; ++c) {
      data[c] = getText(row, c);
    }
  }

  /**
   * Row to string.
   *
   * @param row the row
   * @return the list
   */
  public String[] rowToText(int row) {
    String[] ret = new String[getCols()];
    rowToText(row, ret);
    
    return ret;
  }
  


  /**
   * Returns the transpose of the matrix.
   * 
   * @return
   */
  public Matrix transpose() {
    Matrix ret = ofSameType(getCols(), getRows());

    for (int i = 0; i < getRows(); ++i) {
      for (int j = 0; j < getCols(); ++j) {
        ret.set(j, i, get(i, j));
      }
    }

    return ret;
  }

  /**
   * Dot.
   *
   * @param m the m
   * @return the matrix
   */
  public Matrix dot(Matrix m) {
    return f(MULT_FUNCTION, m);
  }

  /**
   * Add a constant value to the matrix.
   * 
   * @param v
   * @return
   */
  public Matrix add(double v) {
    return f(ADD_FUNCTION, v);
  }

  /**
   * Subtract a constant value from the matrix.
   * 
   * @param v
   * @return
   */
  public Matrix subtract(double v) {
    return add(-v);
  }

  public Matrix add(Matrix m) {
    return f(ADD_FUNCTION, m);
  }

  public Matrix mult(double x) {
    return f(MULT_FUNCTION, x);
  }

  public Matrix div(double x) {
    return f(DIV_FUNCTION, x);
  }

  public Matrix log2() {
    return log(2);
  }

  public Matrix log10() {
    return log(10);
  }

  public Matrix log(int base) {
    return f(new LogFunction(base));
  }

  public Matrix ln() {
    return f(LnFunction.LN_FUNCTION);
  }

  public Matrix multiply(final Matrix m) {
    return multiply(this, m);
  }

  public static Matrix multiply(final Matrix m1, final Matrix m2) {
    int n = m1.getRows();
    int m = m1.getCols();
    int p = m2.getCols();

    Matrix ret = m1.ofSameType(n, p);

    for (int i = 0; i < n; ++i) {
      for (int j = 0; i < p; ++j) {
        for (int k = 0; i < m; ++k) {
          ret.set(i,
              j,
              ret.getValue(i, j) + m1.getValue(i, k) * m2.getValue(k, j));
        }
      }
    }

    return ret;
  }

  /**
   * Return a matrix of the same dimension and type as this one, but
   * uninitialized.
   * 
   * @return
   */
  public Matrix ofSameType() {
    return ofSameType(getRows(), getCols());
  }

  /**
   * Return an uninittialized matrix of the same type as this one, but with
   * given dimensions.
   * 
   * @param rows
   * @param cols
   * @return
   */
  public abstract Matrix ofSameType(int rows, int cols);

  /**
   * Apply a function to a copy of this matrix.
   * 
   * @param f
   * @return
   */
  public Matrix f(CellFunction f) {
    // Copy the matrix
    Matrix ret = copy();

    ret.apply(f);

    return ret;
  }

  public Matrix f(CellFunction f, double v) {
    // Copy the matrix
    Matrix ret = copy();

    ret.apply(f, v);

    return ret;
  }

  public Matrix f(CellFunction f, Matrix m) {
    // Copy the matrix
    Matrix ret = copy();

    ret.apply(f, m);

    return ret;
  }

  /**
   * Apply a function to this matrix.
   * 
   * @param f
   */
  public void apply(CellFunction f) {
    for (int i = 0; i < getRows(); ++i) {
      for (int j = 0; j < getCols(); ++j) {
        set(i, j, f.f(i, j, getValue(i, j)));
      }
    }

    fireMatrixChanged();
  }

  public void apply(CellFunction f, double v) {
    for (int i = 0; i < getRows(); ++i) {
      for (int j = 0; j < getCols(); ++j) {
        set(i, j, f.f(i, j, getValue(i, j), v));
      }
    }

    fireMatrixChanged();
  }

  public void apply(CellFunction f, Matrix m) {
    for (int i = 0; i < getRows(); ++i) {
      for (int j = 0; j < getCols(); ++j) {
        set(i, j, f.f(i, j, getValue(i, j), m.getValue(i, j)));
      }
    }

    fireMatrixChanged();
  }

  public Matrix rowf(CellFunction f, int row) {
    // Copy the matrix
    Matrix ret = copy();

    ret.rowApply(f, row);

    return ret;
  }

  public Matrix colf(CellFunction f, int col) {
    // Copy the matrix
    Matrix ret = copy();

    ret.colApply(f, col);

    return ret;
  }

  public void rowApply(CellFunction f) {
    for (int i = 0; i < getRows(); ++i) {
      for (int j = 0; j < getCols(); ++j) {
        set(i, j, f.f(i, j, getValue(i, j)));
      }
    }

    fireMatrixChanged();
  }

  public void rowApply(CellFunction f, int row) {
    for (int i = 0; i < getCols(); ++i) {
      set(row, i, f.f(row, i, getValue(row, i)));
    }

    fireMatrixChanged();
  }

  public void rowApply(MatrixDimFunction f) {
    int c = getCols();

    double[] data = new double[c];
    double[] ret = new double[c];

    for (int i = 0; i < getRows(); ++i) {
      rowToDouble(i, data);
      f.apply(i, data, ret);
      setRow(i, ret);
    }
  }

  public void rowApply(MatrixDimFunction f, int row) {
    double[] data = new double[getCols()];
    double[] ret = new double[data.length];

    rowToDouble(row, data);
    f.apply(row, data, ret);

    setRow(row, ret);
  }

  public void colApply(CellFunction f, int col) {
    for (int i = 0; i < getRows(); ++i) {
      double v = getValue(i, col);

      if (Mathematics.isValidNumber(v)) {
        set(i, col, f.f(i, col, v));
      }
    }

    fireMatrixChanged();
  }

  public void colApply(MatrixDimFunction f) {
    int r = getRows();

    double[] data = new double[r];
    double[] ret = new double[r];

    for (int i = 0; i < getCols(); ++i) {
      columnToDouble(i, data);
      f.apply(i, data, ret);
      setColumn(i, ret);
    }

    fireMatrixChanged();
  }

  public void colApply(MatrixDimFunction f, int col) {
    double[] data = new double[getRows()];
    
    columnToDouble(col, data);
    
    double[] ret = new double[data.length];

    f.apply(col, data, ret);

    setColumn(col, ret);

    fireMatrixChanged();
  }

  public void rowEval(MatrixReduceFunction f, double[] ret) {
    double[] data = new double[getCols()];
    
    for (int i = 0; i < getRows(); ++i) {
      rowToDouble(i, data);

      ret[i] = f.apply(i, data);
    }
  }

  public void rowEval(MatrixDimFunction f, int row, double[] ret) {
    double[] data = new double[getCols()];
    
    rowToDouble(row, data);

    f.apply(row, data, ret);
  }

  public void colEval(MatrixDimFunction f, int col, double[] ret) {
    double[] data = new double[getRows()];
    
    columnToDouble(col, data);

    f.apply(col, data, ret);
  }

  /**
   * Evaluate the a function across each column.
   * 
   * @param f
   * @param ret
   */
  public void colEval(MatrixDimFunction f, double[] ret) {
    double[] data = new double[getRows()];
    
    for (int i = 0; i < getCols(); ++i) {
      columnToDouble(i, data);

      f.apply(i, data, ret);
    }
  }

  /**
   * Apply a stat function over a matrix.
   * 
   * @param f
   * @return
   */
  public double stat(MatrixStatFunction f) {
    f.init();

    for (int i = 0; i < getRows(); ++i) {
      for (int j = 0; j < getCols(); ++j) {
        f.f(i, j, getValue(i, j));
      }
    }

    return f.getStat();
  }

  public double rowStat(MatrixStatFunction f, int row) {
    f.init();

    for (int i = 0; i < getCols(); ++i) {
      f.f(row, i, getValue(row, i));
    }

    return f.getStat();
  }

  public double colStat(MatrixStatFunction f, int col) {
    f.init();

    for (int i = 0; i < getRows(); ++i) {
      f.f(i, col, getValue(i, col));
    }

    return f.getStat();
  }

  public double[] toDoubleArray() {
    double[] ret = new double[size()];

    toDoubleArray(this, ret);

    return ret;
  }

  public void toDoubleArray(double[] ret) {
    toDoubleArray(this, ret);
  }

  public int[] toIntArray() {
    int[] ret = new int[size()];

    toIntArray(this, ret);

    return ret;
  }

  public void toIntArray(int[] ret) {
    toIntArray(this, ret);
  }

  public long[] toLongArray() {
    long[] ret = new long[size()];

    toLongArray(this, ret);

    return ret;
  }

  public void toLongArray(long[] ret) {
    toLongArray(this, ret);
  }

  public String[] toStringArray() {
    String[] ret = new String[size()];

    toStringArray(this, ret);

    return ret;
  }

  public void toStringArray(String[] ret) {
    toStringArray(this, ret);
  }

  //
  // Static methods
  //

  /**
   * Creates a submatrix containing only the text cells in the matrix.
   *
   * @param m the m
   * @param rows Will be populated with the rows containing text.
   * @param columns Will be populated with the columns containing text.
   * @return the matrix
   */
  public static Matrix extractText(Matrix m,
      List<Integer> rows,
      List<Integer> columns) {
    if (m instanceof MixedMatrix) {
      return extractText((MixedMatrix) m, rows, columns);
    }

    if (columns == null || rows == null) {
      return null;
    }

    int n = m.getCols();

    for (int i = 0; i < n; ++i) {
      if (m.getCellType(0, i) == CellType.TEXT) {
        columns.add(i);
      }
    }

    if (columns.size() == 0) {
      return null;
    }

    int rn = m.getRows();

    for (int i = 0; i < rn; ++i) {
      if (m.getCellType(i, columns.get(0)) == CellType.TEXT) {
        rows.add(i);
      }
    }

    if (rows.size() == 0) {
      return null;
    }

    TextMatrix ret = TextMatrix.createTextMatrix(rows.size(), columns.size());

    for (int i = 0; i < rows.size(); ++i) {
      int r = rows.get(i);

      for (int j = 0; j < columns.size(); ++j) {
        int c = columns.get(j);

        ret.set(i, j, m.getText(r, c));
      }
    }

    return ret;
  }

  /**
   * Extract text.
   *
   * @param m the m
   * @param rows the rows
   * @param columns the columns
   * @return the matrix
   */
  public static Matrix extractText(MixedMatrix m,
      List<Integer> rows,
      List<Integer> columns) {
    return extractData(m, CellType.TEXT, rows, columns);
  }

  /**
   * Extract the numerical portion of a matrix. The supplied rows and columns
   * lists will be populated by the rows and indices of the matrix where
   * numerical data is found.
   *
   * @param m the m
   * @param rows the rows
   * @param columns the columns
   * @return the matrix
   */
  public static Matrix extractNumbers(Matrix m,
      List<Integer> rows,
      List<Integer> columns) {
    if (m instanceof MixedMatrix) {
      return extractNumbers((MixedMatrix) m, rows, columns);
    }

    if (columns == null || rows == null) {
      return null;
    }

    int n = m.getCols();

    for (int i = 0; i < n; ++i) {
      if (m.getCellType(0, i) == CellType.NUMBER) {
        columns.add(i);
      }
    }

    if (columns.size() == 0) {
      return null;
    }

    int rn = m.getRows();

    for (int i = 0; i < rn; ++i) {
      if (m.getCellType(i, columns.get(0)) == CellType.NUMBER) {
        rows.add(i);
      }
    }

    if (rows.size() == 0) {
      return null;
    }

    DoubleMatrix ret = DoubleMatrix.createDoubleMatrix(rows.size(),
        columns.size());

    for (int i = 0; i < rows.size(); ++i) {
      int r = rows.get(i);

      for (int j = 0; j < columns.size(); ++j) {
        int c = columns.get(j);

        ret.set(i, j, m.getValue(r, c));
      }
    }

    return ret;
  }

  /**
   * Extract numbers from a matrix and create a new matrix from them.
   *
   * @param m the m An input matrix.
   * @param rows This list will be populated with the rows containing numbers.
   * @param columns the columns This list will be populated with the columns
   *          containing the numbers.
   * @return the matrix A matrix containing just the numbers from the input
   *         matrix. Its dimensions will the size of the rows list x the size of
   *         columns list.
   */
  public static Matrix extractNumbers(MixedMatrix m,
      List<Integer> rows,
      List<Integer> columns) {
    return extractData(m, CellType.NUMBER, rows, columns);
  }

  /**
   * Extract data.
   *
   * @param m the m
   * @param cellType the cell type
   * @param rows the rows
   * @param columns the columns
   * @return the matrix
   */
  public static Matrix extractData(MixedMatrix m,
      CellType cellType,
      List<Integer> rows,
      List<Integer> columns) {
    if (columns == null || rows == null) {
      return null;
    }

    int cn = m.mDim.mCols;

    for (int i = 0; i < cn; ++i) {
      if (m.getCellType(i) == cellType) {
        columns.add(i);
      }
    }

    if (columns.size() == 0) {
      return null;
    }

    int rn = m.mDim.mRows;

    int c = columns.get(0);

    for (int i = 0; i < rn; ++i) {
      if (m.getCellType(c) == cellType) {
        rows.add(i);
      }

      c += cn;
    }

    if (cellType == CellType.NUMBER) {
      return extractDoubleData(m, rows, columns);
    } else {
      return extractTextData(m, rows, columns);
    }
  }

  /**
   * Extract double data.
   *
   * @param m the m
   * @param rows the rows
   * @param columns the columns
   * @return the matrix
   */
  public static Matrix extractDoubleData(MixedMatrix m,
      List<Integer> rows,
      List<Integer> columns) {
    int rn = m.mDim.mRows;
    int cn = m.mDim.mCols;
    int cn2 = columns.size();

    DoubleMatrix ret = DoubleMatrix.createDoubleMatrix(rows.size(), cn2);

    for (int i = 0; i < cn2; ++i) {
      int origColIndex = columns.get(i);
      int newIndex = i;

      for (int j = 0; j < rn; ++j) {
        ret.mData[newIndex] = ((Number) m.mData[origColIndex]).doubleValue();

        origColIndex += cn;
        newIndex += cn2;
      }
    }

    return ret;
  }

  /**
   * Extract the text data from the rows and columns specified.
   *
   * @param m the m
   * @param rows the rows
   * @param columns the columns
   * @return the matrix
   */
  public static Matrix extractTextData(MixedMatrix m,
      List<Integer> rows,
      List<Integer> columns) {
    int rn = m.mDim.mRows;
    int cn = m.mDim.mCols;
    int cn2 = columns.size();

    TextMatrix ret = TextMatrix.createTextMatrix(rows.size(), cn2);

    for (int i = 0; i < cn2; ++i) {
      int origColIndex = columns.get(i);
      int newIndex = i;

      for (int j = 0; j < rn; ++j) {
        ret.mData[newIndex] = (String) m.mData[origColIndex];

        origColIndex += cn;
        newIndex += cn2;
      }
    }

    return ret;
  }

  /**
   * Copy columns from one matrix to another.
   *
   * @param from the from
   * @param to the to
   */
  public static void copyColumns(Matrix from, Matrix to) {
    int cols = Math.min(from.getCols(), to.getCols());

    for (int i = 0; i < cols; ++i) {
      to.copyColumn(from, i);
    }
  }

  /**
   * Copy columns.
   *
   * @param from the from
   * @param to the to
   * @param column the column
   * @param columns the columns
   */
  public static void copyColumns(Matrix from,
      Matrix to,
      int column,
      int... columns) {
    copyColumns(from, to, 0, column, columns);
  }

  /**
   * Copy columns.
   *
   * @param from the from
   * @param to the to
   * @param toOffset the to offset
   * @param column the column
   * @param columns the columns
   */
  public static void copyColumns(Matrix from,
      Matrix to,
      int toOffset,
      int column,
      int... columns) {
    int tc = toOffset;

    to.copyColumn(from, column, tc++);

    for (int c : columns) {
      to.copyColumn(from, c, tc++);
    }
  }

  /**
   * Copy columns.
   *
   * @param from the from
   * @param to the to
   * @param columns the columns
   */
  public static void copyColumns(Matrix from,
      Matrix to,
      Collection<Integer> columns) {
    copyColumns(from, to, 0, columns);
  }

  /**
   * Copy columns.
   *
   * @param from the from
   * @param to the to
   * @param toOffset the to offset
   * @param columns the columns
   */
  public static void copyColumns(Matrix from,
      Matrix to,
      int toOffset,
      Collection<Integer> columns) {
    int tc = toOffset;

    for (int c : columns) {
      to.copyColumn(from, c, tc++);
    }
  }

  /**
   * Copy columns indexed.
   *
   * @param <V> the value type
   * @param from the from
   * @param to the to
   * @param cols the cols
   */
  public static <V extends Comparable<? super V>> void copyColumnsIndexed(
      Matrix from,
      Matrix to,
      Collection<Indexed<Integer, V>> cols) {
    copyColumnsIndexed(from, to, 0, cols);
  }

  /**
   * Copy columns indexed.
   *
   * @param <V> the value type
   * @param from the from
   * @param to the to
   * @param toOffset the to offset
   * @param cols the cols
   */
  public static <V extends Comparable<? super V>> void copyColumnsIndexed(
      Matrix from,
      Matrix to,
      int toOffset,
      Collection<Indexed<Integer, V>> cols) {
    int tc = toOffset;

    for (Indexed<Integer, ?> c : cols) {
      to.copyColumn(from, c.getIndex(), tc++);
    }
  }

  /**
   * Copy rows.
   *
   * @param from the from
   * @param to the to
   * @param rows the rows
   */
  public static void copyRows(Matrix from, Matrix to, int... rows) {
    copyRows(from, to, 0, rows);
  }

  /**
   * Copy rows from one matrix to another.
   *
   * @param from The matrix to copy from
   * @param to The matrix to copy to.
   * @param toOffset The offset where to begin copying rows into the target
   *          matrix. Thus 2 would start copying row 0 of the source matrix to
   *          row 2 of the target and row 1 to row 3 etc.
   * @param rows A list of the rows to copy.
   */
  public static void copyRows(Matrix from,
      Matrix to,
      int toOffset,
      int... rows) {
    int tr = toOffset;

    for (int r : rows) {
      to.copyRow(from, r, tr++);
    }
  }

  /**
   * Copy rows.
   *
   * @param from the from
   * @param to the to
   * @param rows the rows
   */
  public static void copyRows(final Matrix from,
      Matrix to,
      Collection<Integer> rows) {
    copyRows(from, to, 0, rows);
  }

  /**
   * Copy rows.
   *
   * @param from the from
   * @param to the to
   * @param toOffset the to offset
   * @param rows the rows
   */
  public static void copyRows(final Matrix from,
      Matrix to,
      int toOffset,
      Collection<Integer> rows) {
    int tc = toOffset;

    for (int c : rows) {
      to.copyRow(from, c, tc++);
    }
  }

  /**
   * Copy rows indexed.
   *
   * @param <V> the value type
   * @param from the from
   * @param to the to
   * @param rows the rows
   */
  public static <V extends Comparable<? super V>> void copyRowsIndexed(
      final Matrix from,
      Matrix to,
      Collection<Indexed<Integer, V>> rows) {
    copyRowsIndexed(from, to, 0, rows);
  }

  /**
   * Copy rows indexed.
   *
   * @param <V> the value type
   * @param from the from
   * @param to the to
   * @param toOffset the to offset
   * @param rows the rows
   */
  public static <V extends Comparable<? super V>> void copyRowsIndexed(
      final Matrix from,
      Matrix to,
      int toOffset,
      Collection<Indexed<Integer, V>> rows) {
    int tc = toOffset;

    for (Indexed<Integer, ?> c : rows) {
      to.copyRow(from, c.getIndex(), tc++);
    }
  }

  /**
   * Count how many rows in a column have a number in them.
   *
   * @param m the m
   * @param c the c
   * @return the int
   */
  public static int countNumericalRows(final Matrix m, int c) {
    int ret = 0;

    for (int i = 0; i < m.getRows(); ++i) {
      if (Mathematics.isValidNumber(m.getValue(i, c))) {
        ++ret;
      }
    }

    return ret;
  }

  /**
   * Set the values in a column. If the values are too long, the outside portion
   * will be omitted, if the values are too short, the column will be updated to
   * the length of the values.
   *
   * @param <T> the generic type
   * @param column the column
   * @param values the values
   * @param m the m
   */
  public static <T> void setColumn(int column, List<T> values, Matrix m) {
    for (int i = 0; i < Math.min(m.getRows(), values.size()); ++i) {
      m.set(i, column, values.get(i));
    }
  }

  /**
   * Sets the row NA.
   *
   * @param from the from
   * @param to the to
   * @param ret the ret
   */
  public static void setRowNA(int from, int to, Matrix ret) {
    setRowValue(from, to, TextUtils.NA, ret);
  }

  /**
   * Sets the row value.
   *
   * @param from the from
   * @param to the to
   * @param v the v
   * @param ret the ret
   */
  public static void setRowValue(int from, int to, String v, Matrix ret) {
    for (int row = from; row <= to; ++row) {
      for (int column = 0; column < ret.getCols(); ++column) {
        ret.set(row, column, v);
      }
    }
  }

  /**
   * Sets the row value.
   *
   * @param from the from
   * @param to the to
   * @param v the v
   * @param ret the ret
   */
  public static void setRowValue(int from, int to, double v, Matrix ret) {
    for (int row = from; row <= to; ++row) {
      for (int column = 0; column < ret.getCols(); ++column) {
        ret.set(row, column, v);
      }
    }
  }

  /**
   * Returns true if the number is a valid double.
   *
   * @param v the v
   * @return true, if is valid matrix num
   */
  public static boolean isValidMatrixNum(double v) {
    return !Double.isNaN(v);
  }

  /**
   * Convert all of the values in a matrix to a list.
   *
   * @param m the m
   * @return the list
   */
  public static void toDoubleArray(Matrix m, double[] ret) {
    int v = 0;

    for (int i = 0; i < m.getRows(); ++i) {
      for (int j = 0; j < m.getCols(); ++j) {
        ret[v++] = m.getValue(i, j);
      }
    }
  }

  public static long[] toLongArray(Matrix m, long[] ret) {
    int v = 0;

    for (int i = 0; i < m.getRows(); ++i) {
      for (int j = 0; j < m.getCols(); ++j) {
        ret[v++] = m.getLong(i, j);
      }
    }

    return ret;
  }

  public static void toIntArray(Matrix m, int[] ret) {
    int v = 0;

    for (int i = 0; i < m.getRows(); ++i) {
      for (int j = 0; j < m.getCols(); ++j) {
        ret[v++] = m.getInt(i, j);
      }
    }
  }

  public static void toStringArray(Matrix m, String[] ret) {
    int v = 0;

    for (int i = 0; i < m.getRows(); ++i) {
      for (int j = 0; j < m.getCols(); ++j) {
        ret[v++] = m.getText(i, j);
      }
    }
  }

  /**
   * Create a new matrix of the same type as given with the specified rows and
   * columns.
   *
   * @param m the m
   * @return the matrix
   */
  public static Matrix ofSameType(final Matrix m) {
    if (m instanceof DoubleMatrix) {
      return DoubleMatrix.createDoubleMatrix(m);
    } else if (m instanceof IntMatrix) {
      return IntMatrix.createIntMatrix(m);
    } else if (m instanceof TextMatrix) {
      return TextMatrix.createTextMatrix(m);
    } else {
      return MixedMatrix.createMixedMatrix(m);
    }

    // return ofSameType(m, m.getRowCount(), m.getColumnCount());
  }

  public static Matrix ofSameType(final DataFrame m, int rows, int columns) {
    return ofSameType(m.getMatrix(), rows, columns);
  }

  /**
   * Create a new matrix of the same type as given with the specified rows and
   * columns.
   *
   * @param m the m
   * @param rows the rows
   * @param columns the columns
   * @return the matrix
   */
  public static Matrix ofSameType(final Matrix m, int rows, int columns) {
    // return ofSameType(m.getType(), rows, columns);

    if (m instanceof DoubleMatrix) {
      return DoubleMatrix.createDoubleMatrix(rows, columns);
    } else if (m instanceof LongMatrix) {
      return LongMatrix.createLongMatrix(rows, columns);
    } else if (m instanceof IntMatrix) {
      return IntMatrix.createIntMatrix(rows, columns);
    } else if (m instanceof TextMatrix) {
      return TextMatrix.createTextMatrix(rows, columns);
    } else {
      return MixedMatrix.createMixedMatrix(rows, columns);
    }
  }

  /**
   * Create a new matrix of the same type as given with the specified rows and
   * columns.
   *
   * @param type the type
   * @param rows the rows
   * @param columns the columns
   * @return the matrix
   */
  public static Matrix ofSameType(MatrixType type, int rows, int columns) {
    switch (type) {
    case NUMBER:
      return new DoubleMatrix(rows, columns);
    case TEXT:
      return new TextMatrix(rows, columns);
    default:
      return new MixedMatrix(rows, columns);
    }
  }

  /**
   * Returns true if the matrix is a TextMatrix, false otherwise.
   *
   * @param m the m
   * @return true, if is text
   */
  public static boolean isText(Matrix m) {
    return m.getType() == MatrixType.TEXT;
  }

  /**
   * Returns the cell types in the matrix. This is predominately for use with
   * mixed matrices to check whether they contain a mixture of text or numbers.
   * If a matrix contains only numbers, we might treat it as a numerical matrix
   * for future calculations.
   *
   * @param m the m
   * @return the sets the
   */
  public static Set<CellType> cellTypes(Matrix m) {
    if (m instanceof DoubleMatrix) {
      return cellTypes((DoubleMatrix) m);
    } else if (m instanceof TextMatrix) {
      return cellTypes((TextMatrix) m);
    } else if (m instanceof MixedMatrix) {
      return cellTypes((MixedMatrix) m);
    } else {
      Set<CellType> ret = new HashSet<CellType>();

      int n = CellType.values().length;

      for (int i = 0; i < m.getRows(); ++i) {
        for (int j = 0; j < m.getCols(); ++j) {
          ret.add(m.getCellType(i, j));

          if (ret.size() == n) {
            // Contains all types so no point checking further
            break;
          }
        }

        if (ret.size() == n) {
          // Contains all types so no point checking further
          break;
        }
      }

      return ret;
    }
  }

  /**
   * Cell types.
   *
   * @param m the m
   * @return the sets the
   */
  public static Set<CellType> cellTypes(DoubleMatrix m) {
    return NUMBER_MATRIX_TYPES;
  }

  /**
   * Cell types.
   *
   * @param m the m
   * @return the sets the
   */
  public static Set<CellType> cellTypes(TextMatrix m) {
    return TEXT_MATRIX_TYPES;
  }

  /**
   * Cell types.
   *
   * @param m the m
   * @return the sets the
   */
  public static Set<CellType> cellTypes(MixedMatrix m) {
    Set<CellType> ret = new HashSet<CellType>();

    int n = CellType.values().length;

    for (int i = 0; i < m.mData.length; ++i) {
      ret.add(m.getCellType(i));

      if (ret.size() == n) {
        break;
      }
    }

    return ret;
  }

 
}
