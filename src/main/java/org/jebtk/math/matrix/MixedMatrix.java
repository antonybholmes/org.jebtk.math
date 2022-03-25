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
 * Allows strings and numbers to exist in same matrix.
 * 
 * @author Antony Holmes
 */
public class MixedMatrix extends IndexRowMatrix {

  /**
   * The constant serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /** Stores the matrix data in a row format. */
  public final Object[] mData;

  /** Storest the cell types in a row format. */
  // public final CellType[] mCellType;

  /**
   * Create a new matrix defaulting to being entirely numeric.
   *
   * @param rows the rows
   * @param columns the columns
   */
  public MixedMatrix(int rows, int columns) {
    super(rows, columns);

    mData = new Object[mSize];
    // mCellType = new CellType[mSize];
  }

  /**
   * Instantiates a new mixed matrix.
   *
   * @param rows the rows
   * @param columns the columns
   * @param v the v
   */
  public MixedMatrix(int rows, int columns, double v) {
    this(rows, columns);

    set(v);
  }

  /**
   * Instantiates a new mixed matrix.
   *
   * @param rows the rows
   * @param columns the columns
   * @param v the v
   */
  public MixedMatrix(int rows, int columns, String v) {
    this(rows, columns);

    set(v);
  }

  /**
   * Clone a matrix optionally copying the core matrix values and the
   * annotation.
   *
   * @param m the m
   */
  public MixedMatrix(Matrix m) {
    this(m.getRows(), m.getCols());

    update(m);
  }

  /**
   * Instantiates a new mixed matrix.
   *
   * @param m the m
   */
  public MixedMatrix(IndexRowMatrix m) {
    this(m.getRows(), m.getCols());

    update(m);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#getType()
   */
  @Override
  public MatrixType getType() {
    return MatrixType.MIXED;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#copy()
   */
  @Override
  public Matrix copy() {
    return new MixedMatrix(this);
  }

  @Override
  public Matrix ofSameType(int rows, int cols) {
    return createMixedMatrix(rows, cols);
  }

  @Override
  public Matrix cols(int col, int... cols) {
    int fromCols = getShape().mCols;
    int toCols = 1 + cols.length;
    int rows = getShape().mRows;

    MixedMatrix ret = createMixedMatrix(rows, toCols);

    cols(col,
        0,
        fromCols,
        toCols,
        rows, 
        this, 
        ret);

    for (int i = 0; i < cols.length; ++i) {
      cols(cols[i],
          i + 1,
          fromCols,
          toCols,
          rows, 
          this, 
          ret);
    }

    return ret;
  }

  private static void cols(int fromCol,
      int toCol,
      int fromCols,
      int toCols,
      int rows, 
      final MixedMatrix mixedMatrix, 
      MixedMatrix ret) {
    int fromIdx = fromCol;
    int toIdx = toCol;
    for (int i = 0; i < rows; ++i) {
      ret.mData[toIdx] = mixedMatrix.mData[fromIdx];

      toIdx += toCols;
      fromIdx += fromCols;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.IndexMatrix#getCellType(int)
   */
  @Override
  public CellType getCellType(int index) {
    return mData[index] instanceof Number ? CellType.NUMBER : CellType.TEXT; // mCellType[index];
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.IndexMatrix#get(int)
   */
  @Override
  public Object get(int index) {
    Object v = mData[index];

    return v != null ? v : TextUtils.EMPTY_STRING;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.IndexMatrix#getValue(int)
   */
  @Override
  public double getValue(int index) {
    Object v = mData[index];

    if (v != null) {
      if (v instanceof Double) {
        return (double) v;
      } else if (v instanceof Long) {
        return (long) v;
      } else if (v instanceof Integer) {
        return (int) v;
      } else {
        return super.getValue(index);
      }
    } else {
      return super.getValue(index);
    }
  }

  @Override
  public long getLong(int index) {
    Object v = mData[index];

    if (v != null) {
      if (v instanceof Double) {
        return ((Double) v).longValue();
      } else if (v instanceof Long) {
        return (long) v;
      } else if (v instanceof Integer) {
        return ((Integer) v).longValue();
      } else {
        return super.getLong(index);
      }
    } else {
      return super.getLong(index);
    }
  }

  @Override
  public int getInt(int index) {
    Object v = mData[index];

    if (v != null) {
      if (v instanceof Double) {
        return ((Double) v).intValue();
      } else if (v instanceof Integer) {
        return (int) v;
      } else if (v instanceof Long) {
        return ((Long) v).intValue();
      } else {
        return super.getInt(index);
      }
    } else {
      return super.getInt(index);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.IndexMatrix#getText(int)
   */
  @Override
  public String getText(int index) {
    Object v = mData[index];

    return v != null ? v.toString() : TextUtils.EMPTY_STRING;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.Matrix#update(double)
   */
  @Override
  public void update(double v) {
    Arrays.fill(mData, v);
  }

  @Override
  public void update(long v) {
    Arrays.fill(mData, v);
  }

  @Override
  public void update(int v) {
    Arrays.fill(mData, v);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.Matrix#update(java.lang.String)
   */
  @Override
  public void update(String v) {
    Arrays.fill(mData, v);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.IndexMatrix#update(int, java.lang.Object)
   */
  @Override
  public void update(int index, Object v) {
    if (v != null) {
      // This is so only a limited number of object types can go in the
      // matrix, either numbers or strings.
      if (v instanceof Double) {
        update(index, (double) v);
      } else if (v instanceof Integer) {
        update(index, (int) v);
      } else if (v instanceof Long) {
        update(index, (long) v);
      } else {
        update(index, v.toString());
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.IndexMatrix#update(int, java.lang.String)
   */
  @Override
  public void update(int index, String v) {
    mData[index] = v;
  }

  @Override
  public void update(int index, int v) {
    mData[index] = v;
  }

  @Override
  public void update(int index, long v) {
    mData[index] = v;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.IndexMatrix#update(int, double)
   */
  @Override
  public void update(int index, double v) {
    mData[index] = v;
  }

  /**
   * Specialized instance of column copying for numerical matrices.
   *
   * @param from the from
   * @param column the column
   * @param toColumn the to column
   */
  @Override
  public void copyColumn(final DoubleMatrix from, int column, int toColumn) {
    int i1 = from.getIndex(0, column);
    int i2 = getIndex(0, toColumn);

    int r = Math.min(from.getRows(), getRows());

    for (int i = 0; i < r; ++i) {
      mData[i2] = from.mData[i1];

      i1 += from.mDim.mCols;
      i2 += mDim.mCols;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.abh.common.math.matrix.Matrix#copyColumn(org.abh.common.math.matrix.
   * TextMatrix, int, int)
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
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.abh.common.math.matrix.Matrix#copyColumn(org.abh.common.math.matrix.
   * MixedMatrix, int, int)
   */
  @Override
  public void copyColumn(final MixedMatrix from, int column, int toColumn) {
    int i1 = from.getIndex(0, column);
    int i2 = getIndex(0, toColumn);

    int r = Math.min(from.getRows(), getRows());

    for (int i = 0; i < r; ++i) {
      mData[i2] = from.mData[i1];

      i1 += from.mDim.mCols;
      i2 += mDim.mCols;
    }
  }

  @Override
  public void copyRow(final Matrix from, int row, int toRow) {
    if (from instanceof MixedMatrix) {
      copyRow((MixedMatrix) from, row, toRow);
    } else if (from instanceof TextMatrix) {
      copyRow((TextMatrix) from, row, toRow);
    } else if (from instanceof DoubleMatrix) {
      copyRow((DoubleMatrix) from, row, toRow);
    } else {
      super.copyRow(from, row, toRow);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#copyRow(org.abh.common.math.matrix.
   * DoubleMatrix, int, int)
   */
  public void copyRow(final DoubleMatrix from, int row, int toRow) {
    int c = Math.min(from.getCols(), getCols());

    System.arraycopy(from.mData,
        from.mRowOffsets[row],
        mData,
        mRowOffsets[toRow],
        c);
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
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#copyRow(org.abh.common.math.matrix.
   * MixedMatrix, int, int)
   */
  public void copyRow(final MixedMatrix from, int row, int toRow) {
    int c = Math.min(from.getCols(), getCols());

    // SysUtils.err().println("copy row", from.getColumnCount(),
    // getColumnCount());

    System.arraycopy(from.mData,
        from.mRowOffsets[row],
        mData,
        mRowOffsets[toRow],
        c);
  }

  @Override
  public void setRow(int row, String[] values) {
    SysUtils.arraycopy(values, mData, getIndex(row, 0), values.length);
  }

  @Override
  public void rowToObject(int row, Object[] ret) {
    SysUtils.arraycopy(mData, getIndex(row, 0), ret, mDim.mCols);
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

  public static Matrix transpose(MixedMatrix m) {
    MixedMatrix ret = createMixedMatrix(m.mDim.mCols, m.mDim.mRows);

    int i2 = 0;
    int c = 0;

    for (int i = 0; i < m.mData.length; ++i) {
      // Each time we end a row, reset i2 back to the next column
      if (i % m.mDim.mCols == 0) {
        i2 = c++;
      }

      ret.mData[i2] = m.mData[i];

      // Skip blocks
      i2 += m.mDim.mRows;
    }

    return ret;
  }

  //
  // Static methods
  //

  /**
   * Returns a new empty matrix the same dimensions as the input matrix.
   *
   * @param m the m
   * @return the mixed matrix
   */
  public static MixedMatrix createMixedMatrix(Matrix m) {
    return createMixedMatrix(m.getRows(), m.getCols());
  }

  /**
   * Creates the mixed matrix.
   *
   * @param rows the rows
   * @param cols the cols
   * @return the mixed matrix
   */
  public static MixedMatrix createMixedMatrix(int rows, int cols) {
    return new MixedMatrix(rows, cols);
  }
}
