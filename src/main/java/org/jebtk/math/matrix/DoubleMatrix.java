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

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import org.jebtk.core.Mathematics;
import org.jebtk.core.sys.SysUtils;
import org.jebtk.math.statistics.Statistics;

/**
 * Matrix for storing doubles.
 *
 * @author Antony Holmes
 */
public class DoubleMatrix extends IndexRowMatrix {

  /**
   * The constant serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  private static class TransposeRecursiveAction extends RecursiveAction {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The m data. */
    private final double[] mData;

    /** The m I. */
    private final int mI;

    /** The m R. */
    private final int mR;

    /** The m ret. */
    private final double[] mRet;

    /** The m cols. */
    private final int mCols;

    /** The m steps. */
    private final int mSteps;

    private final int mRows;

    /**
     * Instantiates a new double matrix recursive action.
     *
     * @param data the data.
     * @param f the function.
     * @param i the starting index.
     * @param r the r the starting row.
     * @param cols the number of colums in the matrix.
     * @param steps the number of elements to process in a thread.
     * @param ret the array to write results to.
     */
    public TransposeRecursiveAction(final double[] data, final int i,
        final int r, final int cols, final int rows, final int steps,
        final double[] ret) {
      mData = data;
      mR = r;
      mCols = cols;
      mRows = rows;
      mI = i;
      mSteps = steps;
      mRet = ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.RecursiveAction#compute()
     */
    @Override
    protected void compute() {

      int i2 = -1;
      int c = mR;
      int ix = mI;

      for (int i = 0; i < mSteps; ++i) {
        if (ix == mData.length) {
          break;
        }

        // Each time we end a row, reset i2 back to the next column
        if (i % mCols == 0) {
          i2 = c++;
        }

        mRet[i2] = mData[ix];

        // Skip blocks
        i2 += mRows;

        ++ix;
      }
    }
  }

  /**
   * Applies a binary function to all cells of a matrix where the second operand
   * is a constant. For example this can be used to add a constant value to a
   * matrix
   */
  private static class MatConstRecAction extends RecursiveAction {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The m F. */
    private final CellFunction mF;

    /** The m I. */
    private final int mI;

    /** The m steps. */
    private final int mSteps;

    private double mB;

    private DoubleMatrix mM1;

    private final int mR;

    /**
     * Instantiates a new double matrix recursive action.
     *
     * @param data the data.
     * @param f the function.
     * @param i the starting index.
     * @param r the r the starting row.
     * @param cols the number of colums in the matrix.
     * @param steps the number of elements to process in a thread.
     * @param ret the array to write results to.
     */
    public MatConstRecAction(final CellFunction f, final DoubleMatrix m1,
        final double b, final int i, final int r, final int steps) {
      mM1 = m1;
      mB = b;
      mF = f;
      mI = i;
      mR = r;
      mSteps = steps;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.RecursiveAction#compute()
     */
    @Override
    protected void compute() {

      int r = mR;
      int c = 0;

      for (int ix = mI; ix < mI + mSteps; ++ix) {
        // Stop if we reach the end of the data
        if (ix == mM1.mData.length) {
          break;
        }

        mM1.mData[ix] = mF.f(r, c, mM1.mData[ix], mB);

        if (c++ == mM1.mDim.mCols) {
          c = 0;
          ++r;
        }
      }
    }
  }

  private static class MatMatRecAction extends RecursiveAction {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The m F. */
    private final CellFunction mF;

    /** The m I. */
    private final int mI;

    /** The m steps. */
    private final int mSteps;

    private final double[] mData2;

    private int mR;

    private final DoubleMatrix mM1;

    /**
     * Instantiates a new double matrix recursive action.
     *
     * @param data the data.
     * @param f the function.
     * @param i the starting index.
     * @param r the r the starting row.
     * @param cols the number of colums in the matrix.
     * @param steps the number of elements to process in a thread.
     * @param ret the array to write results to.
     */
    public MatMatRecAction(final CellFunction f, final DoubleMatrix m1,
        final double[] data2, final int i, final int r, final int steps) {
      mM1 = m1;
      mData2 = data2;
      mF = f;
      mI = i;
      mR = r;
      mSteps = steps;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.RecursiveAction#compute()
     */
    @Override
    protected void compute() {

      int r = mR;
      int c = 0;

      for (int ix = mI; ix < mI + mSteps; ++ix) {
        // Stop if we reach the end of the data
        if (ix == mM1.mData.length) {
          break;
        }

        mM1.mData[ix] = mF.f(r, c, mM1.mData[ix], mData2[ix]);

        if (c++ == mM1.mDim.mCols) {
          c = 0;
          ++r;
        }
      }
    }
  }

  private static class MatRecAction extends RecursiveAction {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The m F. */
    private final CellFunction mF;

    /** The m I. */
    private final int mI;

    /** The m steps. */
    private final int mSteps;

    private int mR;

    private final DoubleMatrix mM1;

    /**
     * Instantiates a new double matrix recursive action.
     *
     * @param data the data.
     * @param f the function.
     * @param i the starting index.
     * @param r the r the starting row.
     * @param cols the number of colums in the matrix.
     * @param steps the number of elements to process in a thread.
     * @param ret the array to write results to.
     */
    public MatRecAction(final CellFunction f, final DoubleMatrix m1,
        final int i, final int r, final int steps) {
      mM1 = m1;
      mF = f;
      mI = i;
      mR = r;
      mSteps = steps;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.RecursiveAction#compute()
     */
    @Override
    protected void compute() {

      int r = mR;
      int c = 0;

      for (int ix = mI; ix < mI + mSteps; ++ix) {
        // Stop if we reach the end of the data
        if (ix == mM1.mData.length) {
          break;
        }

        mM1.mData[ix] = mF.f(r, c, mM1.mData[ix]);

        if (c++ == mM1.mDim.mCols) {
          c = 0;
          ++r;
        }
      }
    }
  }

  /** The m data. */
  public final double[] mData;

  /**
   * Instantiates a new numerical matrix.
   *
   * @param rows the rows
   * @param columns the columns
   */
  public DoubleMatrix(int rows, int columns) {
    super(rows, columns);

    // We use a 1d array to store a 2d matrix for speed.
    mData = new double[mSize];
  }

  /**
   * Create a new matrix and initialize all cells to a common value.
   *
   * @param rows the rows
   * @param columns the columns
   * @param v the v
   */
  public DoubleMatrix(int rows, int columns, double v) {
    this(rows, columns);

    // Set the default value
    update(v);
  }

  /**
   * Clone a matrix.
   *
   * @param m the m
   */
  public DoubleMatrix(Matrix m) {
    this(m.getRows(), m.getCols());

    update(m);
  }

  /**
   * Instantiates a new double matrix.
   *
   * @param m the m
   */
  public DoubleMatrix(IndexRowMatrix m) {
    this(m.getRows(), m.getCols());

    update(m);
  }

  /**
   * Instantiates a new double matrix.
   *
   * @param m the m
   */
  public DoubleMatrix(DoubleMatrix m) {
    this(m.getRows(), m.getCols());

    update(m);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.abh.common.math.matrix.Matrix#update(org.abh.common.math.matrix.Matrix)
   */
  @Override
  public void update(Matrix m) {
    if (m instanceof DoubleMatrix) {
      update((DoubleMatrix) m);
    } else {
      super.update(m);
    }
  }

  /**
   * Update.
   *
   * @param m the m
   */
  public void update(DoubleMatrix m) {
    SysUtils.arraycopy(m.mData, mData);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jebtk.math.matrix.RegularMatrix#toDoubleArray()
   */
  @Override
  public double[] toDoubleArray() {
    return mData;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#copy()
   */
  @Override
  public Matrix copy() {
    return new DoubleMatrix(this);
  }
  
  @Override
  public Matrix cols(int col, int... cols) {
    int fromCols = getShape().mCols;
    int toCols = 1 + cols.length;
    int rows = getShape().mRows;

    DoubleMatrix ret = createDoubleMatrix(rows, toCols);

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
      final DoubleMatrix doubleMatrix, 
      DoubleMatrix ret) {
    int fromIdx = fromCol;
    int toIdx = toCol;
    for (int i = 0; i < rows; ++i) {
      ret.mData[toIdx] = doubleMatrix.mData[fromIdx];

      toIdx += toCols;
      fromIdx += fromCols;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jebtk.math.matrix.Matrix#ofSameType()
   */
  @Override
  public Matrix ofSameType(int rows, int cols) {
    return createDoubleMatrix(rows, cols);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.Matrix#getNumCells()
   */
  @Override
  public int size() {
    return mData.length;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.IndexMatrix#get(int)
   */
  @Override
  public Object get(int index) {
    return getValue(index);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.IndexMatrix#getValue(int)
   */
  @Override
  public double getValue(int index) {
    return mData[index];
  }
  
  @Override
  public int getInt(int index) {
    return (int)getValue(index);
  }
  
  @Override
  public long getLong(int index) {
    return (long)getValue(index);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.Matrix#updateValue(double)
   */
  @Override
  public void update(double v) {
    Arrays.fill(mData, v);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.IndexMatrix#updateValue(int, double)
   */
  @Override
  public void update(int index, double v) {
    mData[index] = v;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.IndexMatrix#getText(int)
   */
  @Override
  public String getText(int index) {
    return Double.toString(mData[index]);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jebtk.math.matrix.Matrix#setRow(int, double[])
   */
  @Override
  public void setRow(int row, double[] values) {
    SysUtils.arraycopy(values, mData, mRowOffsets[row], mDim.mCols);

    fireMatrixChanged();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jebtk.math.matrix.Matrix#setColumn(int, double[])
   */
  @Override
  public void setColumn(int column, double[] values) {
    SysUtils.arraycopy(values, mData, column, mDim.mCols, mDim.mRows);

    fireMatrixChanged();
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
        mData[i1] = from.getValue(i, column);

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
  public void copyColumn(final DoubleMatrix from, int column, int toColumn) {
    // if (from.getRowCount() == 0 || getRowCount() == 0) {
    // return;
    // }

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
    if (from instanceof DoubleMatrix) {
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

    fireMatrixChanged();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.IndexMatrix#columnAsDouble(int)
   */
  @Override
  public void columnToDouble(int column, double[] ret) {
    /*
     * int i1 = column;
     * 
     * for (int row = 0; row < mDim.mRows; ++row) { ret[row] = mData[i1];
     * 
     * i1 += mDim.mCols; }
     */

    SysUtils.arraycopy(mData, column, mDim.mCols, ret, 0, mDim.mRows);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.IndexMatrix#rowAsDouble(int)
   */
  @Override
  public void rowToDouble(int row, double[] ret) {
    SysUtils.arraycopy(mData, mRowOffsets[row], ret, mDim.mCols);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.jebtk.math.matrix.IndexMatrix#f(org.jebtk.math.matrix.CellFunction,
   * org.jebtk.math.matrix.IndexMatrix)
   */
  @Override
  public void apply(CellFunction f, IndexMatrix m) {
    if (m instanceof DoubleMatrix) {
      apply(f, (DoubleMatrix) m);
    } else {
      super.apply(f, m);
    }
  }

  /**
   * F.
   *
   * @param f the f
   * @param ret the ret
   * @return
   */
  public void apply(CellFunction f, DoubleMatrix m) {
    apply(f, this, m);
  }

  /**
   * F.
   *
   * @param f the f
   * @param m the m
   * @param ret the ret
   */
  public static void apply(CellFunction f, DoubleMatrix m1, DoubleMatrix m2) {

    /*
     * DoubleMatrix ret = ofSameType(m1);
     * 
     * for (int i = 0; i < m1.mData.length; ++i) { ret.mData[i] =
     * f.f(m1.mData[i], m2.mData[i]); }
     * 
     * return ret;
     */

    applyconc(f, m1, m2, CONCURRENT_ROWS);
  }

  /**
   * Fconc.
   *
   * @param f the f
   * @param m the m
   * @param ret the ret
   * @param rows the rows
   * @return
   */
  public static void applyconc(CellFunction f,
      DoubleMatrix m1,
      DoubleMatrix m2,
      int rows) {
    //System.err.println("f concurrent");

    int steps = rows * m1.mDim.mCols;

    ForkJoinPool forkJoinPool = new ForkJoinPool(); // 16);

    // for (int i = 0; i < m1.mData.length; i += steps) {
    int ix = 0;

    for (int r = 0; r < m1.mDim.mRows; r += rows) {
      RecursiveAction a = new MatMatRecAction(f, m1, m2.mData, ix, r, steps);

      forkJoinPool.invoke(a);

      ix += steps;
    }

    m1.fireMatrixChanged();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.jebtk.math.matrix.IndexMatrix#f(org.jebtk.math.matrix.CellFunction,
   * org.jebtk.math.matrix.IndexMatrix)
   */
  @Override
  public void apply(CellFunction f, double v) {
    apply(f, this, v);
  }

  /**
   * F.
   *
   * @param f the f
   * @param m the m
   * @param ret the ret
   */
  public static void apply(CellFunction f, DoubleMatrix m1, double v) {

    /*
     * DoubleMatrix ret = ofSameType(m1);
     * 
     * for (int i = 0; i < m1.mData.length; ++i) { ret.mData[i] =
     * f.f(m1.mData[i], v); }
     * 
     * return ret;
     */

    applyconc(f, m1, v, CONCURRENT_ROWS);
  }

  /**
   * Fconc.
   *
   * @param f the f
   * @param m the m
   * @param ret the ret
   * @param rows the rows
   * @return
   */
  public static void applyconc(CellFunction f,
      DoubleMatrix m1,
      double v,
      int rows) {
    //System.err.println("f concurrent");

    int steps = rows * m1.mDim.mCols;

    ForkJoinPool forkJoinPool = new ForkJoinPool(); // 16);

    int ix = 0;

    for (int r = 0; r < m1.mDim.mRows; r += rows) {
      RecursiveAction a = new MatConstRecAction(f, m1, v, ix, r, steps);

      forkJoinPool.invoke(a);

      ix += steps;
    }

    m1.fireMatrixChanged();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.jebtk.math.matrix.IndexMatrix#f(org.jebtk.math.matrix.CellFunction,
   * org.jebtk.math.matrix.IndexMatrix)
   */
  @Override
  public void apply(CellFunction f) {
    applySimple(f, this);
  }

  /**
   * F.
   *
   * @param f the f
   * @param m the m
   * @param ret the ret
   */
  public static void applySimple(CellFunction f, DoubleMatrix m1) {

    /*
     * DoubleMatrix ret = ofSameType(m1);
     * 
     * for (int i = 0; i < m1.mData.length; ++i) { ret.mData[i] =
     * f.f(m1.mData[i]); }
     * 
     * return ret;
     */

    applyconc(f, m1, CONCURRENT_ROWS);
  }

  /**
   * Fconc.
   *
   * @param f the f
   * @param m the m
   * @param ret the ret
   * @param rows the rows
   * @return
   */
  public static void applyconc(CellFunction f, DoubleMatrix m1, int rows) {
    //System.err.println("f concurrent");

    int steps = rows * m1.mDim.mCols;

    ForkJoinPool forkJoinPool = new ForkJoinPool(); // 16);

    int ix = 0;

    for (int r = 0; r < m1.mDim.mRows; r += rows) {
      RecursiveAction a = new MatRecAction(f, m1, ix, r, steps);

      forkJoinPool.invoke(a);

      ix += steps;
    }

    m1.fireMatrixChanged();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.jebtk.math.matrix.Matrix#rowApply(org.jebtk.math.matrix.CellFunction)
   */
  @Override
  public void rowApply(CellFunction f) {
    for (int i = 0; i < mDim.mRows; ++i) {
      rowApply(f, i);
    }

    fireMatrixChanged();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.jebtk.math.matrix.Matrix#rowApply(org.jebtk.math.matrix.CellFunction,
   * int)
   */
  @Override
  public void rowApply(CellFunction f, int index) {
    int offset = mRowOffsets[index];

    for (int i = 0; i < mDim.mCols; ++i) {
      mData[offset] = f.f(i, 0, mData[offset]);

      ++offset;
    }

    fireMatrixChanged();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.jebtk.math.matrix.Matrix#colApply(org.jebtk.math.matrix.CellFunction,
   * int)
   */
  @Override
  public void colApply(CellFunction f, int col) {
    int offset = col;

    for (int i = 0; i < mDim.mCols; ++i) {
      mData[offset] = f.f(i, 0, mData[offset]);

      offset += mDim.mCols;
    }

    fireMatrixChanged();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.jebtk.math.matrix.Matrix#stat(org.jebtk.math.matrix.MatrixStatFunction)
   */
  @Override
  public double stat(MatrixStatFunction f) {
    f.init();

    for (int i = 0; i < mData.length; ++i) {
      f.f(i, 0, mData[i]);
    }

    return f.getStat();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jebtk.math.matrix.Matrix#rowStat(org.jebtk.math.matrix.
   * MatrixStatFunction, int)
   */
  @Override
  public double rowStat(MatrixStatFunction f, int index) {
    f.init();

    int offset = mRowOffsets[index];

    for (int i = 0; i < mDim.mCols; ++i) {
      f.f(i, 0, mData[offset]);

      ++offset;
    }

    return f.getStat();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jebtk.math.matrix.Matrix#colStat(org.jebtk.math.matrix.
   * MatrixStatFunction, int)
   */
  @Override
  public double colStat(MatrixStatFunction f, int index) {
    int offset = index;

    for (int i = 0; i < mDim.mCols; ++i) {
      f.f(i, 0, mData[offset]);

      offset += mDim.mCols;
    }

    return f.getStat();
  }

  @Override
  public Matrix multiply(final Matrix m) {
    if (m instanceof DoubleMatrix) {
      return multiply(this, (DoubleMatrix) m);
    } else {
      return super.multiply(m);
    }
  }

  public static Matrix multiply(final DoubleMatrix m1, final DoubleMatrix m2) {

    int of = 0;
    int of1 = 0;

    int n = m1.mDim.mRows;
    int m = m1.mDim.mCols;
    int p = m2.mDim.mCols;

    DoubleMatrix ret = createDoubleMatrix(n, p);

    for (int i = 0; i < n; ++i) {
      int ix = of;

      for (int j = 0; j < p; ++j) {
        int ix1 = of1;

        int ix2 = j;

        for (int k = 0; k < m; ++k) {
          SysUtils.err().println("mm", n, m, p, i, j, k, ix, ix1, ix2);

          // Dot product
          ret.mData[ix] += m1.mData[ix1++] * m2.mData[ix2];

          ix2 += p;
        }

        ++ix;
      }

      of += p;
      of1 += m;
    }

    return ret;
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

  /**
   * Transpose.
   *
   * @param m the m
   * @return the matrix
   */
  public static Matrix transpose(final DoubleMatrix m) {
    /*
     * DoubleMatrix ret = DoubleMatrix.createDoubleMatrix(m.mDim.mCols,
     * m.mDim.mRows);
     * 
     * int i2 = 0; int c = 0;
     * 
     * for (int i = 0; i < m.mData.length; ++i) { // Each time we end a row,
     * reset i2 back to the next column if (i % m.mDim.mCols == 0) { i2 = c++; }
     * 
     * ret.mData[i2] = m.mData[i];
     * 
     * // Skip blocks i2 += m.mDim.mRows; }
     * 
     * return ret;
     */

    return transposeC(m);
  }

  public static Matrix transposeC(final DoubleMatrix m) {
    return transposeC(m, CONCURRENT_ROWS);
  }

  public static Matrix transposeC(final DoubleMatrix m, int rows) {
    DoubleMatrix ret = DoubleMatrix.createDoubleMatrix(m.mDim.mCols,
        m.mDim.mRows);

    int r = 0;

    System.err.println("transpose concurrent");

    int steps = rows * m.mDim.mCols;

    ForkJoinPool forkJoinPool = new ForkJoinPool(); // 16);

    for (int i = 0; i < m.mData.length; i += steps) {
      TransposeRecursiveAction a = new TransposeRecursiveAction(m.mData, i, r,
          m.mDim.mCols, m.mDim.mRows, steps, ret.mData);

      forkJoinPool.invoke(a);

      r += rows;
    }

    return ret;
  }

  //
  // Static methods
  //

  /**
   * Finds the minimum in each row.
   *
   * @param matrix the matrix
   * @return the double[]
   */
  public static double[] minInRow(Matrix matrix) {
    double[] ret = new double[matrix.getRows()];

    int r = matrix.getRows();
    int c = matrix.getCols();

    for (int i = 0; i < r; ++i) {
      double min = Double.MAX_VALUE;

      for (int j = 0; j < c; ++j) {
        double v = matrix.getValue(i, j);

        if (Mathematics.isValidNumber(v)) {
          if (v < min) {
            min = v;
          }
        }
      }

      ret[i] = min;
    }

    return ret;
  }

  /**
   * Finds the maximum in each row of the matrix.
   *
   * @param matrix the matrix
   * @return the double[]
   */
  public static double[] maxInRow(Matrix matrix) {
    double[] ret = new double[matrix.getRows()];

    int r = matrix.getRows();
    int c = matrix.getCols();

    for (int i = 0; i < r; ++i) {
      double max = Double.MIN_VALUE;

      for (int j = 0; j < c; ++j) {
        double v = matrix.getValue(i, j);

        if (Mathematics.isValidNumber(v)) {
          if (v > max) {
            max = v;
          }
        }
      }

      ret[i] = max;
    }

    return ret;
  }

  /**
   * Finds the minimum in each row.
   *
   * @param matrix the matrix
   * @return the double[]
   */
  public static double[] minInColumn(Matrix matrix) {
    double[] ret = new double[matrix.getCols()];

    int r = matrix.getRows();
    int c = matrix.getCols();

    for (int i = 0; i < c; ++i) {
      double min = Double.MAX_VALUE;

      for (int j = 0; j < r; ++j) {
        double v = matrix.getValue(j, i);

        if (Mathematics.isValidNumber(v)) {
          if (v < min) {
            min = v;
          }
        }
      }

      ret[i] = min;
    }

    return ret;
  }

  /**
   * Finds the maximum in each row of the matrix.
   *
   * @param matrix the matrix
   * @return the double[]
   */
  public static double[] maxInColumn(Matrix matrix) {
    double[] ret = new double[matrix.getCols()];

    int c = matrix.getCols();

    for (int i = 0; i < c; ++i) {
      ret[i] = maxInColumn(matrix, i);
    }

    return ret;
  }

  /**
   * Max in column.
   *
   * @param matrix the matrix
   * @param column the column
   * @return the double
   */
  public static double maxInColumn(Matrix matrix, int column) {
    double ret = Double.MIN_VALUE;

    int r = matrix.getRows();

    for (int j = 0; j < r; ++j) {
      ret = Math.max(ret, matrix.getValue(j, column));
    }

    return ret;
  }

  /**
   * Min in column.
   *
   * @param matrix the matrix
   * @param column the column
   * @return the double
   */
  public static double minInColumn(Matrix matrix, int column) {
    double ret = Double.MAX_VALUE;

    int r = matrix.getRows();

    for (int j = 0; j < r; ++j) {
      ret = Math.min(ret, matrix.getValue(j, column));
    }

    return ret;
  }

  /**
   * Returns the differential z score between two column groups in a matrix.
   *
   * @param matrix the matrix
   * @param phenGroup the g1
   * @param controlGroup the g2
   * @return the list
   */
  public static double[] diffGroupZScores(DataFrame matrix,
      MatrixGroup phenGroup,
      MatrixGroup controlGroup) {
    List<Integer> phenIndices = MatrixGroup.findColumnIndices(matrix,
        phenGroup);

    List<Integer> controlIndices = MatrixGroup.findColumnIndices(matrix,
        controlGroup);

    Matrix im = matrix.getMatrix();

    double[] zscores = new double[im.getRows()];

    for (int i = 0; i < im.getRows(); ++i) {
      List<Double> d1 = new ArrayList<Double>();

      for (int c : phenIndices) {
        d1.add(im.getValue(i, c));
      }

      double mean1 = Statistics.mean(d1);
      double sd1 = Statistics.popStdDev(d1); // sampleStandardDeviation(d1);

      List<Double> d2 = new ArrayList<Double>();

      for (int c : controlIndices) {
        d2.add(im.getValue(i, c));
      }

      double mean2 = Statistics.mean(d2);
      double sd2 = Statistics.popStdDev(d2);

      double sd = (sd1 + sd2); // / 2.0;

      double zscore;

      if (sd > 0) {
        zscore = (mean1 - mean2) / sd;
      } else {
        zscore = 0;
      }

      zscores[i] = zscore;
    }

    return zscores;
  }

  /**
   * Returns the log fold changes between groups of a log transformed matrix.
   *
   * @param matrix the matrix
   * @param g1 the g1
   * @param g2 the g2
   * @return the list
   */
  public static double[] logFoldChange(DataFrame matrix,
      MatrixGroup g1,
      MatrixGroup g2) {
    List<Integer> g11 = MatrixGroup.findColumnIndices(matrix, g1);
    List<Integer> g22 = MatrixGroup.findColumnIndices(matrix, g2);

    Matrix im = matrix.getMatrix();

    double[] foldChanges = new double[im.getRows()];

    for (int i = 0; i < im.getRows(); ++i) {
      List<Double> d1 = new ArrayList<Double>(g11.size());

      for (int c : g11) {
        d1.add(im.getValue(i, c));
      }

      double mean1 = Statistics.mean(d1);

      List<Double> d2 = new ArrayList<Double>(g22.size());

      for (int c : g22) {
        d2.add(im.getValue(i, c));
      }

      double mean2 = Statistics.mean(d2);

      double foldChange = mean1 - mean2;

      foldChanges[i] = foldChange;
    }

    return foldChanges;
  }

  /**
   * Calculate the fold changes between two groups.
   *
   * @param matrix the matrix
   * @param g1 the g1
   * @param g2 the g2
   * @return the list
   */
  public static double[] foldChange(DataFrame matrix,
      MatrixGroup g1,
      MatrixGroup g2) {
    List<Integer> g11 = MatrixGroup.findColumnIndices(matrix, g1);
    List<Integer> g22 = MatrixGroup.findColumnIndices(matrix, g2);

    Matrix im = matrix.getMatrix();

    double[] foldChanges = new double[im.getRows()];

    for (int i = 0; i < im.getRows(); ++i) {
      List<Double> d1 = new ArrayList<Double>();

      for (int c : g11) {
        d1.add(im.getValue(i, c));
      }

      double mean1 = Statistics.mean(d1);

      List<Double> d2 = new ArrayList<Double>();

      for (int c : g22) {
        d2.add(im.getValue(i, c));
      }

      double mean2 = Statistics.mean(d2);

      double foldChange = mean1 / mean2;

      if (Double.isNaN(foldChange)) {
        foldChange = 0;
      }

      if (Double.isInfinite(foldChange)) {
        foldChange = 0;
      }

      // Division mistake most likely
      if (foldChange > 1000000) {
        foldChange = 0;
      }

      foldChanges[i] = foldChange;
    }

    return foldChanges;
  }

  /**
   * Parses the est matrix.
   *
   * @param file the file
   * @return the annotation matrix
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static DataFrame parseEstMatrix(Path file) throws IOException {
    return new EstDoubleMatrixParser().parse(file);
  }

  /**
   * Return the row wise means of a matrix group.
   *
   * @param m the m
   * @param g the g
   * @return the list
   */
  public static double[] means(DataFrame m, MatrixGroup g) {
    List<Integer> g1 = MatrixGroup.findColumnIndices(m, g);

    Matrix im = m.getMatrix();

    double[] means = new double[im.getRows()];

    for (int i = 0; i < im.getRows(); ++i) {
      List<Double> values = new ArrayList<Double>(g1.size());

      for (int c : g1) {
        values.add(im.getValue(i, c));
      }

      double mean = Statistics.mean(values);

      means[i]= mean;
    }

    return means;
  }

  /**
   * Sum.
   *
   * @param m the m
   * @return the double
   */
  public static double sum(Matrix m) {
    double sum = 0;

    for (int i = 0; i < m.getRows(); ++i) {
      for (int j = 0; j < m.getCols(); ++j) {
        double v = m.getValue(i, j);

        if (Mathematics.isValidNumber(v)) {
          sum += v;
        }
      }
    }

    return sum;
  }

  /**
   * Return the maximum sum of values in a given row.
   *
   * @param m the m
   * @return the double
   */
  public static double maxRowSum(DataFrame m) {
    double max = Double.MIN_VALUE;

    for (int i = 0; i < m.getRows(); ++i) {
      double sum = 0;

      for (int j = 0; j < m.getCols(); ++j) {
        double v = m.getValue(i, j);

        if (Mathematics.isValidNumber(v)) {
          sum += v;
        }
      }

      if (sum > max) {
        max = sum;
      }
    }

    return max;
  }

  /**
   * Column means.
   *
   * @param m the m
   * @return the double[]
   */
  public static double[] columnMeans(Matrix m) {
    if (m instanceof DoubleMatrix) {
      return columnMeans((DoubleMatrix) m);
    } else if (m instanceof IndexRowMatrix) {
      return columnMeans((IndexRowMatrix) m);
    } else {
      int r = m.getRows();
      int c = m.getCols();

      double[] means = new double[c];

      for (int i = 0; i < c; ++i) {
        double[] values = new double[r];

        for (int j = 0; j < r; ++j) {
          values[j] = m.getValue(j, i);
        }

        double mean = Statistics.mean(values);

        means[i] = mean;
      }

      return means;
    }
  }

  /**
   * Return the means of the matrix columns.
   *
   * @param m the m
   * @return the double[]
   */
  public static double[] columnMeans(IndexRowMatrix m) {
    int r = m.getRows();
    int c = m.getCols();

    double[] means = new double[c];

    for (int i = 0; i < c; ++i) {
      double[] values = new double[r];

      int index = i;

      for (int j = 0; j < r; ++j) {
        values[j] = m.getValue(index);

        index += c;
      }

      double mean = Statistics.mean(values);

      means[i] = mean;
    }

    return means;
  }

  /**
   * Column means.
   *
   * @param m the m
   * @return the double[]
   */
  public static double[] columnMeans(DoubleMatrix m) {
    int r = m.mDim.mRows;
    int c = m.mDim.mCols;

    double[] means = new double[c];

    for (int i = 0; i < c; ++i) {
      double[] values = new double[r];

      int index = i;

      for (int j = 0; j < r; ++j) {
        values[j] = m.mData[index];

        index += c;
      }

      double mean = Statistics.mean(values);

      means[i] = mean;
    }

    return means;
  }

  /**
   * Column pop std dev.
   *
   * @param m the m
   * @return the double[]
   */
  public static double[] columnPopStdDev(Matrix m) {
    if (m instanceof DoubleMatrix) {
      return columnPopStdDev((DoubleMatrix) m);
    } else if (m instanceof IndexRowMatrix) {
      return columnPopStdDev((IndexRowMatrix) m);
    } else {
      int r = m.getRows();
      int c = m.getCols();

      double[] ret = new double[c];

      for (int i = 0; i < c; ++i) {
        double[] values = new double[r];

        for (int j = 0; j < r; ++j) {
          values[j] = m.getValue(j, i);
        }

        double sd = Statistics.popStdDev(values);

        ret[i] = sd;
      }

      return ret;
    }
  }

  /**
   * Return the means of the matrix columns.
   *
   * @param m the m
   * @return the double[]
   */
  public static double[] columnPopStdDev(IndexRowMatrix m) {
    int r = m.getRows();
    int c = m.getCols();

    double[] ret = new double[c];

    for (int i = 0; i < c; ++i) {
      double[] values = new double[r];

      int index = i;

      for (int j = 0; j < r; ++j) {
        values[j] = m.getValue(index);

        index += c;
      }

      double sd = Statistics.popStdDev(values);

      ret[i] = sd;
    }

    return ret;
  }

  /**
   * Column pop std dev.
   *
   * @param m the m
   * @return the double[]
   */
  public static double[] columnPopStdDev(DoubleMatrix m) {
    int r = m.getRows();
    int c = m.getCols();

    double[] ret = new double[c];

    for (int i = 0; i < c; ++i) {
      double[] values = new double[r];

      int index = i;

      for (int j = 0; j < r; ++j) {
        values[j] = m.mData[index];

        index += c;
      }

      double sd = Statistics.popStdDev(values);

      ret[i] = sd;
    }

    return ret;
  }

  /**
   * Returns a new empty matrix the same dimensions as the input matrix.
   *
   * @param m the m
   * @return the double matrix
   */
  public static DoubleMatrix createDoubleMatrix(Matrix m) {
    return createDoubleMatrix(m.getRows(), m.getCols());
  }

  /**
   * Creates the double matrix.
   *
   * @param rows the rows
   * @param cols the cols
   * @return the double matrix
   */
  public static DoubleMatrix createDoubleMatrix(int rows, int cols) {
    return new DoubleMatrix(rows, cols);
  }

  /**
   * Creates the double matrix.
   *
   * @param rows the rows
   * @param cols the cols
   * @param v1 the v 1
   * @param values the values
   * @return the double matrix
   */
  public static DoubleMatrix createDoubleMatrix(int rows,
      int cols,
      double v1,
      double... values) {
    DoubleMatrix m = createDoubleMatrix(rows, cols);

    int c = 0;

    m.mData[c++] = v1;

    for (double v : values) {
      m.mData[c++] = v;
    }

    return m;
  }

  /**
   * Create a zero matrix.
   *
   * @param rows the rows
   * @param cols the cols
   * @return the double matrix
   */
  public static DoubleMatrix createZerosMatrix(int rows, int cols) {
    return new DoubleMatrix(rows, cols, 0);
  }

  /**
   * Creates the ones matrix.
   *
   * @param rows the rows
   * @param cols the cols
   * @return the double matrix
   */
  public static DoubleMatrix createOnesMatrix(int rows, int cols) {
    return new DoubleMatrix(rows, cols, 1);
  }

  /**
   * Return an empty double matrix of the same dimension as the matrix argument.
   * 
   * @param m
   * @return
   */
  public static DoubleMatrix ofSameType(final DoubleMatrix m) {
    return new DoubleMatrix(m);
  }
}
