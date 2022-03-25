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

import java.util.Arrays;

import org.jebtk.core.sys.SysUtils;
import org.jebtk.core.text.TextUtils;

/**
 * Concrete implementation of the annotation matrix which stores n * m String
 * elements. Each cell must contain a string where the empty string is used in
 * place of null so that calls to getText() always return a string object so
 * that null checks are not required.
 * 
 * @author Antony Holmes
 */
public class TextMatrix extends IndexRowMatrix {

  /**
   * The constant serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The member data.
   */
  public final String[] mData;

  /**
   * Instantiates a new text matrix.
   *
   * @param rows the rows
   * @param columns the columns
   */
  public TextMatrix(int rows, int columns) {
    super(rows, columns);

    // We use a 1d array to store a 2d matrix for speed.
    mData = new String[mSize];
  }

  /**
   * Instantiates a new text matrix.
   *
   * @param rows the rows
   * @param columns the columns
   * @param v the v
   */
  public TextMatrix(int rows, int columns, String v) {
    this(rows, columns);

    // Set the default value
    set(v);
  }

  /**
   * Instantiates a new text matrix.
   *
   * @param m the m
   */
  public TextMatrix(Matrix m) {
    this(m.getRows(), m.getCols());

    update(m);
  }

  /**
   * Instantiates a new text matrix.
   *
   * @param m the m
   */
  public TextMatrix(TextMatrix m) {
    this(m.getRows(), m.getCols());

    update(m);
  }

  /**
   * Instantiates a new text matrix.
   *
   * @param m the m
   */
  public TextMatrix(IndexRowMatrix m) {
    this(m.getRows(), m.getCols());

    update(m);
  }

  /**
   * Copy the values from a matrix to this matrix.
   *
   * @param m the m
   */
  public void set(TextMatrix m) {
    update(m);

    fireMatrixChanged();
  }

  /**
   * Copy the values from a matrix to this matrix.
   *
   * @param m the m
   */
  public void update(TextMatrix m) {
    SysUtils.arraycopy(m.mData, mData);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#getType()
   */
  @Override
  public MatrixType getType() {
    return MatrixType.TEXT;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#copy()
   */
  @Override
  public Matrix copy() {
    return new TextMatrix(this);
  }

  @Override
  public Matrix ofSameType(int rows, int cols) {
    return createTextMatrix(rows, cols);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.IndexMatrix#getCellType(int)
   */
  @Override
  public CellType getCellType(int index) {
    return CellType.TEXT;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.Matrix#updateValue(double)
   */
  @Override
  public void update(double v) {
    update(Double.toString(v));
  }

  @Override
  public void update(long v) {
    update(Long.toString(v));
  }

  @Override
  public void update(int v) {
    update(Integer.toString(v));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.Matrix#updateText(java.lang.String)
   */
  @Override
  public void update(String v) {
    Arrays.fill(mData, v);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.IndexMatrix#updateValue(int, double)
   */
  @Override
  public void update(int index, double v) {
    update(index, Double.toString(v));
  }

  @Override
  public void update(int index, long v) {
    update(index, Long.toString(v));
  }

  @Override
  public void update(int index, int v) {
    update(index, Integer.toString(v));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.IndexMatrix#updateText(int, java.lang.String)
   */
  @Override
  public void update(int index, String v) {
    mData[index] = v;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.IndexMatrix#getText(int)
   */
  @Override
  public String getText(int index) {
    return mData[index];
  }
  
  @Override
  public double getValue(int index) {
    return TextUtils.parseDouble(getText(index));
  }

  @Override
  public int getInt(int index) {
    return TextUtils.parseInt(getText(index));
  }

  @Override
  public long getLong(int index) {
    return TextUtils.parseLong(getText(index));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.IndexMatrix#get(int)
   */
  @Override
  public Object get(int index) {
    return getText(index);
  }
  
  @Override
  public void setRow(int row, String[] values) {
    SysUtils.arraycopy(values, mData, getIndex(row, 0), values.length);
  }
  
  @Override
  public void rowToText(int row, String[] ret) {
    SysUtils.arraycopy(mData, getIndex(row, 0), ret, mDim.mCols);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.IndexMatrix#copyColumn(org.abh.common.math.
   * matrix.Matrix, int, int)
   */
  @Override
  public void copyColumn(final Matrix from, int column, int toColumn) {
    if (from instanceof DoubleMatrix) {
      copyColumn((DoubleMatrix) from, column, toColumn);
    } else if (from instanceof TextMatrix) {
      copyColumn((TextMatrix) from, column, toColumn);
    } else {
      int i1 = getIndex(0, toColumn);

      int r = Math.min(from.getRows(), getRows());

      for (int i = 0; i < r; ++i) {
        mData[i1] = from.getText(i, column);

        i1 += mDim.mCols;
      }
    }
  }

  /**
   * Specialized instance of column copying for numerical matrices.
   *
   * @param from the from
   * @param column the column
   * @param toColumn the to column
   */
  @Override
  public void copyColumn(final TextMatrix from, int column, int toColumn) {
    int i1 = from.getIndex(0, column);
    int i2 = getIndex(0, toColumn);

    int r = Math.min(from.getRows(), getRows());

    for (int i = 0; i < r; ++i) {
      mData[i2] = from.mData[i1];

      i1 += from.mDim.mCols;
      i2 += mDim.mCols;
    }

    fireMatrixChanged();
  }

  @Override
  public void copyRow(final Matrix from, int row, int toRow) {
    if (from instanceof TextMatrix) {
      copyRow((TextMatrix) from, row, toRow);
    } else {
      super.copyRow(from, row, toRow);
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#copyRow(org.abh.common.math.matrix.
   * TextMatrix, int, int)
   */
  public void copyRow(final TextMatrix from, int row, int toRow) {
    int c = Math.min(from.getCols(), getCols());

    System.arraycopy(from.mData,
        from.mRowOffsets[row],
        mData,
        mRowOffsets[toRow],
        c);

    fireMatrixChanged();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.IndexMatrix#columnAsText(int)
   */
  /*
  Override
  public List<String> columnAsText(int column) {
    int r = getRows();

    List<String> values = new ArrayList<String>(r);

    int i1 = getIndex(0, column);

    for (int row = 0; row < r; ++row) {
      values.add(mData[i1]);

      i1 += mDim.mCols;
    }

    return values;
  }
  */
  
  public void columnToText(int column, String[] ret) {
    int rows = getRows();
    int cols = getCols();
    
    int offset = column;
    
    for (int r = 0; r < rows; ++r) {
      ret[r] = mData[offset];
      
      offset += cols;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.IndexMatrix#rowAsText(int)
   */
  @Override
  public String[] rowToText(int row) {
    int c = getCols();

    String[] values = new String[c];

    int i1 = getIndex(row, 0);

    for (int col = 0; col < c; ++col) {
      values[col] = mData[i1++];
    }

    return values;
  }

  public void apply(CellFunction f) {
    // Do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.IndexMatrix#transpose()
   */
  @Override
  public Matrix transpose() {
    return transpose(this);
  }

  public static Matrix transpose(TextMatrix m) {
    TextMatrix ret = new TextMatrix(m.mDim.mCols, m.mDim.mRows);

    int c = 0;
    int i2 = 0;

    for (int i = 0; i < m.mData.length; ++i) {
      if (i % m.mDim.mCols == 0) {
        i2 = c++;
      }

      ret.mData[i2] = m.mData[i];

      i2 += m.mDim.mRows;
    }

    return ret;
  }

  @Override
  public void toStringArray(String[] ret) {
    SysUtils.arraycopy(mData, ret);
  }

  //
  // Static methods
  //

  /**
   * Creates the text matrix.
   *
   * @param m the m
   * @return the text matrix
   */
  public static TextMatrix createTextMatrix(Matrix m) {
    return createTextMatrix(m.getRows(), m.getCols());
  }

  /**
   * Creates the text matrix.
   *
   * @param rows the rows
   * @param columns the columns
   * @return the text matrix
   */
  public static TextMatrix createTextMatrix(int rows, int columns) {
    return new TextMatrix(rows, columns);
  }
}
