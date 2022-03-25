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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jebtk.core.Indexed;
import org.jebtk.core.NameGetter;
import org.jebtk.core.collections.ArrayUtils;
import org.jebtk.core.collections.CollectionUtils;
import org.jebtk.core.collections.UniqueArrayList;
import org.jebtk.core.event.ChangeEvent;
import org.jebtk.core.event.ChangeListener;
import org.jebtk.core.io.FileUtils;
import org.jebtk.core.text.Join;
import org.jebtk.core.text.Splitter;
import org.jebtk.core.text.TextUtils;

/**
 * Wraps a matrix in annotatable columns and rows to make it more useful in data
 * analysis.
 * 
 * @author Antony Holmes
 *
 */
public class DataFrame extends Matrix
    implements NameGetter {

  /**
   * The constant serialVersionUID.
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * The constant EST_ANNOTATION_ROWS.
   */
  public static final String EST_ANNOTATION_ROWS = "#annotation-rows";

  /**
   * The constant EST_ANNOTATION_COLUMNS.
   */
  public static final String EST_ANNOTATION_COLUMNS = "#annotation-columns";

  /**
   * The constant EST_ANNOTATION_ROW.
   */
  public static final String EST_ANNOTATION_ROW = "#annotation-row";

  /**
   * The constant EST_ANNOTATION_COLUMN.
   */
  public static final String EST_ANNOTATION_COLUMN = "#annotation-column";

  /**
   * The constant EST_ANNOTATION_GROUPS.
   */
  public static final String EST_ANNOTATION_GROUPS = "#groups";

  /**
   * The constant EST_ROWS.
   */
  public static final String EST_ROWS = "#rows";

  /**
   * The constant EST_COLUMNS.
   */
  public static final String EST_COLUMNS = "#columns";

  /**
   * The constant EST_VERSION_1.
   */
  public static final String EST_VERSION_1 = "#1.0";

  /**
   * The constant EST_VERSION_2.
   */
  public static final String EST_VERSION_2 = "#2.0";

  /**
   * The constant EST_MATRIX.
   */
  private static final String EST_MATRIX = "#matrix";

  /**
   * The constant START_CELL.
   */
  public static final MatrixCellRef START_CELL = new MatrixCellRef(0, 0);

  private static final String ROW_NAMES = "Row Names";

  /** The m name. */
  private String mName = TextUtils.EMPTY_STRING;

  /**
   * The member row annotation.
   */
  protected DataFrameIndex mIndexAnnotation;

  /**
   * The member column annotation.
   */
  protected DataFrameIndex mColumnAnnotation;

  /** The m M. */
  private Matrix mM;

  /**
   * Instantiates a new annotable matrix.
   *
   * @param m the m
   */
  public DataFrame(Matrix m) {
    this(m, false);
  }

  /**
   * Create a new Annotatable matrix wrapping m.
   *
   * @param m the m
   * @param copy If true, causes the underlying matrix to be copied rather than
   *          referenced.
   */
  public DataFrame(Matrix m, boolean copy) {
    super(-1, -1);

    if (copy) {
      mM = m.copy();
    } else {
      mM = m;
    }

    mIndexAnnotation = new DataFrameIndex(m.getRows());
    mColumnAnnotation = new DataFrameIndex(m.getCols());

    mIndexAnnotation.addChangeListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent e) {
        fireMatrixChanged();
      }
    });

    mColumnAnnotation.addChangeListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent e) {
        fireMatrixChanged();
      }
    });

    /*
     * getMatrix().addMatrixListener(new MatrixEventListener() {
     * 
     * @Override public void matrixChanged(ChangeEvent e) { changed(); }});
     */

    fireMatrixChanged();
  }

  /**
   * Instantiates a new annotatable matrix.
   *
   * @param frame the matrix
   */
  public DataFrame(DataFrame frame) {
    this(frame, false);
  }

  /**
   * Create a new annotation matrix by copying another.
   *
   * @param frame the matrix
   * @param copy If true causes both the annotatable and its underlying matrix
   *          to be copied. This deep copy should only be used when there is a
   *          need to manipulate the matrix itself.
   */
  public DataFrame(DataFrame frame, boolean copy) {
    this(frame.getMatrix(), copy);

    copyAnnotations(frame, this);
  }

  /**
   * Clone the annotations from an existing DataFrame, but replace the inner
   * matrix with a different one. Useful for when creating a copy of another
   * matrix with updated values.
   *
   * @param matrix the matrix
   * @param m the m
   */
  public DataFrame(DataFrame matrix, Matrix m) {
    this(m);

    copyAnnotations(matrix, this);
  }
  
  public DataFrameIndex getIndex() {
    return mIndexAnnotation;
  }
  
  public DataFrameIndex getColumnHeader() {
    return mColumnAnnotation;
  }
  
  
  /**
   * Copy columns out of a data frame.
   */
  public DataFrame cols(int col, int... cols) {
    DataFrame ret = new DataFrame(getMatrix().cols(col, cols));
    
    copyIndex(this, ret);
    
    return ret;
  }

  /**
   * Optionally give the matrix a name.
   *
   * @param name the name
   * @return the annotation matrix
   */
  public DataFrame setName(String name) {
    mName = name;

    return this;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.NameProperty#getName()
   */
  @Override
  public String getName() {
    return mName;
  }

  /**
   * If row is negative, this will cause the column annotations to be edited
   * rather than the matrix. If column annotations are are negative, this will
   * cause the row annotations to be edited.
   *
   * @param row the row
   * @param column the column
   * @param value the value
   */
  @Override
  public void set(int row, int column, double value) {
    if (row >= 0 && column >= 0) {
      getMatrix().set(row, column, value);
    } else if (row < 0 && column >= 0) {
      getColumnHeader().getAnnotation(-row - 1).set(0, column, value);
    } else if (row >= 0 && column < 0) {
      getIndex().getAnnotation(-column - 1).set(0, row, value);
    } else {
      // Do nothing
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#set(int, int, java.lang.String)
   */
  @Override
  public void set(int row, int column, String value) {
    if (row >= 0 && column >= 0) {
      getMatrix().set(row, column, value);
    } else if (row < 0 && column >= 0) {
      getColumnHeader().getAnnotation(row).set(0, column, value);
    } else if (row >= 0 && column < 0) {
      getIndex().getAnnotation(column).set(0, row, value);
    } else {
      // Do nothing
    }
  }
  
  @Override
  public void setRow(int row, double[] values) {
    if (row < 0) {
      getColumnHeader().getAnnotation(getIndex().size() + row).setRow(0, values);
    } else {
      getMatrix().setRow(row, values);
    }
  }
  
  @Override
  public void setRow(int row, int[] values) {
    if (row < 0) {
      getColumnHeader().getAnnotation(getIndex().size() + row).setRow(0, values);
    } else {
      getMatrix().setRow(row, values);
    }
  }
  
  @Override
  public void setRow(int row, String[] values) {
    if (row < 0) {
      getColumnHeader().getAnnotation(getIndex().size() + row).setRow(0, values);
    } else {
      getMatrix().setRow(row, values);
    }
  }
  
  @Override
  public void setRow(int row, Object[] values) {
    if (row < 0) {
      getColumnHeader().getAnnotation(getIndex().size() + row).setRow(0, values);
    } else {
      getMatrix().setRow(row, values);
    }
  }
  
  @Override
  public void setColumn(int col, double[] values) {
    if (col < 0) {
      getIndex().getAnnotation(getIndex().size() + col).setRow(0, values);
    } else {
      getMatrix().setColumn(col, values);
    }
  }
  
  @Override
  public void setColumn(int col, int[] values) {
    if (col < 0) {
      getIndex().getAnnotation(getIndex().size() + col).setRow(0, values);
    } else {
      getMatrix().setColumn(col, values);
    }
  }
  
  @Override
  public void setColumn(int col, Object[] values) {
    if (col < 0) {
      getIndex().getAnnotation(getIndex().size() + col).setRow(0, values);
    } else {
      getMatrix().setColumn(col, values);
    }
  }
  
  @Override
  public void setColumn(int col, String[] values) {
    if (col < 0) {
      getIndex().getAnnotation(getIndex().size() + col).setRow(0, values);
    } else {
      getMatrix().setColumn(col, values);
    }
  }
  

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#getCellType(int, int)
   */
  @Override
  public CellType getCellType(int row, int column) {
    if (row >= 0 && column >= 0) {
      return getMatrix().getCellType(row, column);
    } else if (row < 0 && column < 0) {
      return CellType.TEXT;
    } else if (row < 0) {
      int s = altIndexModulo(row, getColumnHeader().getNames().size());
      return getColumnHeader().getAnnotation(s).getCellType(0, column);
    } else {
      int s = altIndexModulo(column, getIndex().getNames().size());
      return getIndex().getAnnotation(s).getCellType(0, row);
    }
  }

  /**
   * Sets the row names.
   *
   * @param names the new row names
   */
  public void setRowNames(List<String> names) {
    if (CollectionUtils.isNullOrEmpty(names)) {
      return;
    }

    int r = 0;

    for (String name : names) {
      getIndex().setHeading(r++, name);
    }
  }

  /**
   * Sets the row names.
   *
   * @param names the new row names
   */
  public void setRowNames(String... names) {
    setRowNames(Arrays.asList(names));
  }

  /**
   * Sets the row name.
   *
   * @param row the row
   * @param name the name
   */
  public void setRowName(int row, String name) {
    getIndex().setHeading(row, name);
  }

  /**
   * Gets the row names.
   *
   * @return the row names
   */
  public String[] getRowNames() {
    return getIndex().getText(0);
    
    //List<String> names = getIndex().getNames();

    //if (names.size() > 0) {
    //  return getIndex().getAnnotation(names.get(0)).rowToText(0);
    //} else {
    //  return ArrayUtils.EMPTY_STRING_ARRAY;
    //}
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.MatrixAnnotations#getRowName(int)
   */
  public String getRowName(int i) {
    return getRowNames()[i];
  }

  //
  // Columns
  //

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.MatrixAnnotations#getColumnName(int)
   */
  public String getColumnName(int i) {
    if (i >= 0) {
      return getColumnNames()[i % getCols()];
    } else {
      // If there is a negative number, choose the row annotation name
      // going right to left
      int s = altIndexModulo(i, getIndex().getNames().size());
      return getIndex().getName(s);
    }
  }

  /**
   * Sets the column names.
   *
   * @param names the new column names
   */
  public void setColumnNames(String... names) {
    getColumnHeader().setHeadings(names);
  }
  
  public void setColumnNames(Collection<String> names) {
    getColumnHeader().setHeadings(names);
  }

  /**
   * Sets the column name.
   *
   * @param column the column
   * @param name the name
   */
  public void setColumnName(int column, String name) {
    getColumnHeader().setHeading(column, name);
  }

  /**
   * Gets the column names. This does not include the names of row annotations
   * so its length will match the number of columns in the matrix.
   *
   * @return the column names
   */
  public String[] getColumnNames() {
    return getColumnHeader().getText(0);

    //if (names.length > 0) {
    //  return getColumnHeader().getAnnotation(names.get(0)).rowToText(0);
    //} else {
    //  return ArrayUtils.EMPTY_STRING_ARRAY;
    //}
  }

  /**
   * Returns true if the matrix has a header.
   *
   * @return the checks for header
   */
  public boolean getHasHeader() {
    return getColumnHeader().size() > 0;
  }


  /*
   * @Override public List<Object> columnAsList(int column) { if (column >= 0) {
   * return getInnerMatrix().columnAsList(column); } else { return
   * getIndex().getAnnotation(column).rowAsList(0); } }
   * 
   * @Override public List<Double> columnAsDouble(int column) { if (column >= 0)
   * { return getInnerMatrix().columnAsDouble(column); } else { return
   * getIndex().getAnnotation(column).rowAsDouble(0); } }
   * 
   * @Override public List<String> columnAsText(int column) {
   * System.err.println("col as text " + column);
   * 
   * if (column >= 0) { return getInnerMatrix().columnAsText(column); } else {
   * return getIndex().getAnnotation(column).rowAsText(0); } }
   */

  @Override
  public Matrix f(CellFunction f) {
    // Copy the matrix
    DataFrame ret = new DataFrame(this, true);

    ret.apply(f);

    return ret;
  }

  @Override
  public Matrix rowf(CellFunction f, int index) {
    // Copy the matrix
    DataFrame ret = new DataFrame(this, true);

    ret.rowApply(f, index);

    return ret;
  }

  @Override
  public Matrix colf(CellFunction f, int index) {
    // Copy the matrix
    DataFrame ret = new DataFrame(this, true);

    ret.colApply(f, index);

    return ret;
  }

  @Override
  public void apply(CellFunction f) {
    getMatrix().apply(f);
  }

  @Override
  public void apply(CellFunction f, Matrix m) {
    if (m instanceof DataFrame) {
      m = ((DataFrame) m).getMatrix();
    }

    getMatrix().apply(f, m);
  }

  @Override
  public void apply(CellFunction f, double v) {
    getMatrix().apply(f, v);
  }

  @Override
  public void rowApply(MatrixDimFunction f) {
    getMatrix().rowApply(f);
  }

  @Override
  public void rowApply(CellFunction f, int index) {
    getMatrix().rowApply(f, index);
  }

  @Override
  public void colApply(MatrixDimFunction f) {
    getMatrix().colApply(f);
  }

  @Override
  public void colApply(CellFunction f, int index) {
    getMatrix().colApply(f, index);
  }

  @Override
  public void rowEval(MatrixReduceFunction f, double[] ret) {
    getMatrix().rowEval(f, ret);
  }

  @Override
  public void rowEval(MatrixDimFunction f, int col, double[] ret) {
    getMatrix().rowEval(f, col, ret);
  }

  @Override
  public void colEval(MatrixDimFunction f, double[] ret) {
    getMatrix().colEval(f, ret);
  }

  @Override
  public void colEval(MatrixDimFunction f, int col, double[] ret) {
    getMatrix().colEval(f, col, ret);
  }

  /**
   * Apply a stat function over a matrix.
   * 
   * @param f
   * @return
   */
  @Override
  public double stat(MatrixStatFunction f) {
    return getMatrix().stat(f);
  }

  @Override
  public double rowStat(MatrixStatFunction f, int index) {
    return getMatrix().rowStat(f, index);
  }

  @Override
  public double colStat(MatrixStatFunction f, int index) {
    return getMatrix().colStat(f, index);
  }

  @Override
  public Matrix f(CellFunction f, double v) {
    return new DataFrame(this, getMatrix().f(f, v));
  }

  @Override
  public Matrix f(CellFunction f, Matrix m) {
    if (m instanceof DataFrame) {
      m = ((DataFrame) m).getMatrix();
    }

    return new DataFrame(this, getMatrix().f(f, m));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#transpose()
   */
  @Override
  public Matrix transpose() {
    // Transpose the main matrix
    DataFrame ret = new DataFrame(getMatrix().transpose());

    // The first name is the row-name, which must be swapped for the
    // column name so we only copy the annotation for names(1, end)
    // verbatim. The same is true for the columns
    // ret.setColumnNames(getRowNames());
    // ret.setRowNames(getColumnNames());

    for (String name : getIndex().getNames()) { // CollectionUtils.tail(getIndex().getNames()))
                                                  // {
      ret.getColumnHeader().setAnnotation(name, getIndex().getAnnotation(name));
    }

    for (String name : getColumnHeader().getNames()) { // CollectionUtils.tail(getColumns().getNames()))
                                                     // {
      ret.getIndex().setAnnotation(name, getColumnHeader().getAnnotation(name));
    }

    return ret;
  }

  @Override
  public double[] toDoubleArray() {
    return getMatrix().toDoubleArray();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#copy()
   */
  @Override
  public Matrix copy() {
    return new DataFrame(this);
  }

  @Override
  public Matrix ofSameType(int rows, int cols) {
    return getMatrix().ofSameType(rows, cols);
  }

  /**
   * Returns the matrix underlying the data frame.
   * 
   * @return
   */
  public Matrix getMatrix() {
    return mM;
  }

  @Override
  public int size() {
    return getMatrix().size();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#getType()
   */
  @Override
  public MatrixType getType() {
    return getMatrix().getType();
  }

  @Override
  public MatrixDim getShape() {
    return getMatrix().getShape();
  }

  public MatrixDim getExtShape() {
    MatrixDim dim = getShape();
    
    dim = new MatrixDim(
        dim.getRows() + getColumnHeader().getNames().size(),
        dim.getCols() + getIndex().getNames().size());

    return dim;
  }

  /**
   * Return the total number of elements including annotations.
   * 
   * @return
   */
  public int getExtSize() {
    MatrixDim dim = getExtShape();
    
    return dim.mRows * dim.mCols;
  }

  /**
   * Returns the row count inclusive of the number of annotation rows.
   * 
   * @return a row count.
   */
  public int getExtRows() {
    return getExtShape().getRows();
  }

  /**
   * Returns the column count inclusive of the number of annotation rows.
   * 
   * @return a column count.
   */
  public int getExtCols() {
    return getExtShape().getCols();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.DataFrame#getValue(int, int)
   */
  @Override
  public double getValue(int row, int column) {
    // return getInnerMatrix().getValue(row, column);

    if (row >= 0 && column >= 0) {
      return getMatrix().getValue(row, column);
    } else if (row < 0 && column < 0) {
      return 0;
    } else if (row < 0) {
      return getColumnHeader().getAnnotation(row).getValue(0, column);
    } else {
      // col < 0
      return getIndex().getAnnotation(column).getValue(0, row);
    }
  }
  
  @Override
  public int getInt(int row, int column) {
    // return getInnerMatrix().getValue(row, column);

    if (row >= 0 && column >= 0) {
      return getMatrix().getInt(row, column);
    } else if (row < 0 && column < 0) {
      return 0;
    } else if (row < 0) {
      return getColumnHeader().getAnnotation(row).getInt(0, column);
    } else {
      // col < 0
      return getIndex().getAnnotation(column).getInt(0, row);
    }
  }
  
  @Override
  public long getLong(int row, int column) {
    // return getInnerMatrix().getValue(row, column);

    if (row >= 0 && column >= 0) {
      return getMatrix().getLong(row, column);
    } else if (row < 0 && column < 0) {
      return 0;
    } else if (row < 0) {
      return getColumnHeader().getAnnotation(row).getLong(0, column);
    } else {
      // col < 0
      return getIndex().getAnnotation(column).getLong(0, row);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.DataFrame#getText(int, int)
   */
  @Override
  public String getText(int row, int column) {
    // return getInnerMatrix().getText(row, column);

    if (row >= 0 && column >= 0) {
      return getMatrix().getText(row, column);
    } else if (row < 0 && column < 0) {
      return getIndex().getName(column);
    } else if (row < 0) {
      return getColumnHeader().getAnnotation(row).getText(0, column);
    } else {
      // col < 0
      return getIndex().getAnnotation(column).getText(0, row);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#get(int, int)
   */
  @Override
  public Object get(int row, int column) {
    if (row >= 0 && column >= 0) {
      return getMatrix().get(row, column);
    } else if (row < 0 && column < 0) {
      return getIndex().getName(column);
    } else if (row < 0) {
      return getColumnHeader().getAnnotation(row).get(0, column);
    } else {
      // col < 0
      return getIndex().getAnnotation(column).get(0, row);
    }

    /*
     * MatrixCellRef c = translate(row, column);
     * 
     * if (c.row < 0 && c.column < 0) { return
     * getIndex().getNames().get(column); } else if (c.row < 0) { return
     * getColumns().getAnnotation(row).get(0, c.column); } else if (c.column < 0) {
     * return getIndex().getAnnotation(column).get(0, c.row); } else { return
     * getInnerMatrix().get(c.row, c.column); }
     */
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#update(int, int, double)
   */
  @Override
  public void update(int row, int column, double v) {
    // getInnerMatrix().updateValue(row, column, v);

    if (row >= 0 && column >= 0) {
      getMatrix().update(row, column, v);
    } else if (row < 0 && column < 0) {
      // Do nothing
    } else if (row < 0) {
      getColumnHeader().getAnnotation(row).update(0, column, v);
    } else {
      // col < 0
      getIndex().getAnnotation(column).update(0, row, v);
    }

    /*
     * MatrixCellRef c = translate(row, column);
     * 
     * if (c.row < 0 && c.column < 0) { // Do nothing } else if (c.row < 0) {
     * getColumns().getAnnotation(row).updateValue(0, c.column, v); } else if
     * (c.column < 0) { getIndex().getAnnotation(column).updateValue(0, c.row, v); }
     * else { getInnerMatrix().updateValue(c.row, c.column, v); }
     */
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#update(int, int, java.lang.String)
   */
  @Override
  public void update(int row, int column, String v) {
    // getInnerMatrix().updateText(row, column, v);

    if (row >= 0 && column >= 0) {
      getMatrix().update(row, column, v);
    } else if (row < 0 && column < 0) {
      // Do nothing
    } else if (row < 0) {
      getColumnHeader().getAnnotation(row).update(0, column, v);
    } else {
      // col < 0
      getIndex().getAnnotation(column).update(0, row, v);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#update(int, int, java.lang.Object)
   */
  @Override
  public void update(int row, int column, Object v) {
    // getInnerMatrix().update(row, column, v);

    if (row >= 0 && column >= 0) {
      getMatrix().update(row, column, v);
    } else if (row < 0 && column < 0) {
      // Do nothing
    } else if (row < 0) {
      getColumnHeader().getAnnotation(row).update(0, column, v);
    } else {
      // col < 0
      getIndex().getAnnotation(column).update(0, row, v);
    }
  }
  

  @Override
  public void columnToDouble(int column, double[] data) {
    if (column < 0) {
      getIndex().getAnnotation(
          getIndex().getNames().get(getIndex().getNames().size() + column))
              .columnToDouble(0, data);
    } else {
      getMatrix().columnToDouble(column, data);
    }
  }
  
  @Override
  public void columnToInt(int column, int[] data) {
    if (column < 0) {
      getIndex().getAnnotation(
          getIndex().getNames().get(getIndex().getNames().size() + column))
              .columnToInt(0, data);
    } else {
      getMatrix().columnToInt(column, data);
    }
  }
  
  @Override
  public void columnToText(int column, String[] data) {
    if (column < 0) {
      getIndex().getAnnotation(
          getIndex().getNames().get(getIndex().getNames().size() + column))
              .rowToText(0, data);
    } else {
      getMatrix().columnToText(column, data);
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.DataFrame#rowAsDouble(int)
   */
  @Override
  public void rowToDouble(int row, double[] data) {
    if (row < 0) {
      getIndex().getAnnotation(
          getIndex().getNames().get(getIndex().getNames().size() + row))
              .rowToDouble(0, data);
    } else {
      getMatrix().rowToDouble(row, data);
    }
  }
  
  @Override
  public void rowToText(int row, String[] data) {
    if (row < 0) {
      getIndex().getAnnotation(
          getIndex().getNames().get(getIndex().getNames().size() + row))
              .rowToText(0, data);
    } else {
      getMatrix().rowToText(row, data);
    }
  }
  
  @Override
  public void rowToObject(int row, Object[] data) {
    if (row < 0) {
      getIndex().getAnnotation(
          getIndex().getNames().get(getIndex().getNames().size() + row))
              .rowToObject(0, data);
    } else {
      getMatrix().rowToObject(row, data);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.DataFrame#rowAsText(int)
   */
  @Override
  public String[] rowToText(int column) {
    if (column < 0) {
      return getIndex().getAnnotation(
          getIndex().getNames().get(getIndex().getNames().size() + column))
              .rowToText(0);
    } else {
      return getMatrix().rowToText(column);
    }
  }
  
  
   
  
  

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.DataFrame#extractText()
   */
  public DataFrame extractText() {
    Matrix innerM = getMatrix();

    int cn = innerM.getCols();

    List<Integer> columns = new ArrayList<Integer>(cn);

    int rn = innerM.getRows();

    List<Integer> rows = new ArrayList<Integer>(rn);

    Matrix m = Matrix.extractText(innerM, rows, columns);

    if (columns.size() == 0 || rows.size() == 0) {
      return null;
    }

    DataFrame ret = DataFrame.createDataFrame(m);

    copyAnnotations(rows, columns, this, ret);

    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.DataFrame#extractNumbers()
   */
  public DataFrame extractNumbers() {
    Matrix innerM = getMatrix();

    int cn = innerM.getCols();

    List<Integer> columns = new ArrayList<Integer>(cn);

    int rn = innerM.getRows();

    List<Integer> rows = new ArrayList<Integer>(rn);

    Matrix m = Matrix.extractNumbers(innerM, rows, columns);

    if (columns.size() == 0 || rows.size() == 0) {
      return null;
    }

    DataFrame ret = DataFrame.createDataFrame(m);

    copyAnnotations(rows, columns, this, ret);

    return ret;
  }

  //
  // Static methods
  //

  /**
   * Copy inner columns.
   *
   * @param m the m
   * @param columns the columns
   * @return the annotation matrix
   */
  public static DataFrame copyInnerColumns(final DataFrame m, int... columns) {
    return copyInnerColumns(m, CollectionUtils.toList(columns));
  }

  /**
   * Copy inner columns.
   *
   * @param m the m
   * @param iter the iter
   * @return the annotation matrix
   */
  public static DataFrame copyInnerColumns(final DataFrame m,
      final Iterable<? extends MatrixGroup> iter) {
    return copyColumns(m, iter);
  }

  /**
   * Copy columns.
   *
   * @param m the m
   * @param iter the iter
   * @return the annotation matrix
   */
  public static DataFrame copyColumns(final DataFrame m,
      final Iterable<? extends MatrixGroup> iter) {
    List<Integer> columns = new UniqueArrayList<Integer>();

    for (MatrixGroup g : iter) {
      columns.addAll(MatrixGroup.findColumnIndices(m, g));
    }

    return copyColumns(m, columns);
  }

  /**
   * Copy inner columns.
   *
   * @param <T> the generic type
   * @param m the m
   * @param g the g
   * @return the annotation matrix
   */
  public static <T extends MatrixGroup> DataFrame copyInnerColumns(
      final DataFrame m,
      T g) {
    List<Integer> columns = MatrixGroup.findColumnIndices(m, g);

    return copyInnerColumns(m, columns);
  }

  /**
   * Copy columns.
   *
   * @param m the m
   * @param columns the columns
   * @return the annotation matrix
   */
  public static DataFrame copyColumns(final DataFrame m,
      final List<Integer> columns) {
    Matrix innerM = m.getMatrix();

    Matrix newInnerM = ofSameType(m, m.getRows(), columns.size());

    copyColumns(innerM, newInnerM, columns);

    DataFrame ret = new DataFrame(newInnerM);

    copyIndex(m, ret);
    copyColumnHeaders(m, ret, columns);

    return ret;
  }

  /**
   * Copy inner columns.
   *
   * @param m the m
   * @param columns the columns
   * @return the annotation matrix
   */
  public static DataFrame copyInnerColumns(final DataFrame m,
      final List<Integer> columns) {
    return copyColumns(m, columns);
  }

  /**
   * Copy inner columns indexed.
   *
   * @param <V> the value type
   * @param m the m
   * @param columns the columns
   * @return the annotation matrix
   */
  public static <V extends Comparable<? super V>> DataFrame copyInnerColumnsIndexed(
      DataFrame m,
      List<Indexed<Integer, V>> columns) {
    Matrix innerM = m.getMatrix();

    Matrix newInnerM = ofSameType(m, m.getRows(), columns.size());

    copyColumnsIndexed(innerM, newInnerM, columns);

    DataFrame ret = new DataFrame(newInnerM);

    copyIndex(m, ret);
    copyColumnHeadersIndexed(m, ret, columns);

    return ret;
  }

  /**
   * Copy inner rows.
   *
   * @param m the m
   * @param rows the rows
   * @return the annotation matrix
   */
  public static DataFrame copyInnerRows(DataFrame m, int... rows) {
    return copyRows(m, CollectionUtils.toList(rows));
  }

  /**
   * Copy inner rows.
   *
   * @param m the m
   * @param iter the iter
   * @return the annotation matrix
   */
  public static DataFrame copyInnerRows(DataFrame m,
      Iterable<? extends MatrixGroup> iter) {
    List<Integer> columns = new UniqueArrayList<Integer>();

    for (MatrixGroup g : iter) {
      columns.addAll(MatrixGroup.findColumnIndices(m, g));
    }

    return copyRows(m, columns);
  }

  /**
   * Copy inner rows.
   *
   * @param <T> the generic type
   * @param m the m
   * @param g the g
   * @return the annotation matrix
   */
  public static <T extends MatrixGroup> DataFrame copyInnerRows(DataFrame m,
      T g) {
    List<Integer> columns = MatrixGroup.findColumnIndices(m, g);

    return copyRows(m, columns);
  }

  /**
   * Copy some rows from an annotation matrix to a new annotation matrix.
   * Indices begin at zero and do not include the column annotations, i.e. row
   * indices refer to the inner matrix
   *
   * @param f the m
   * @param rows the rows
   * @return the annotation matrix
   */
  public static DataFrame copyRows(DataFrame f, List<Integer> rows) {
    Matrix m = f.getMatrix();

    Matrix m2 = ofSameType(m, rows.size(), f.getCols());

    copyRows(m, m2, rows);

    DataFrame ret = new DataFrame(m2);

    copyIndex(f, ret, rows);
    copyColumnHeaders(f, ret);

    return ret;
  }
  
  /**
   * Return a subset of the matrix by rows. The subset rows are returned in
   * the order specified.
   * 
   * @param rows
   * @return
   */
  public DataFrame copyRows(int[] rows) {
    return copyRows(this, rows);
  }
  
  public static DataFrame copyRows(DataFrame f, int[] rows) {
    Matrix m = f.getMatrix();

    Matrix m2 = ofSameType(m, rows.length, f.getCols());

    copyRows(m, m2, rows);

    DataFrame ret = new DataFrame(m2);

    copyIndex(f, ret, rows);
    copyColumnHeaders(f, ret);

    return ret;
  }

  /**
   * Copy inner rows indexed.
   *
   * @param <V> the value type
   * @param m the m
   * @param rows the rows
   * @return the annotation matrix
   */
  public static <V extends Comparable<? super V>> DataFrame copyInnerRowsIndexed(
      DataFrame m,
      List<Indexed<Integer, V>> rows) {
    Matrix innerM = m.getMatrix();

    Matrix newInnerM = ofSameType(m, rows.size(), m.getCols());

    copyRowsIndexed(innerM, newInnerM, rows);

    DataFrame ret = new DataFrame(newInnerM);

    copyIndexFromIndexed(m, ret, rows);
    copyColumnHeaders(m, ret);

    return ret;
  }

  /**
   * Creates an annotatable matrix with an underlying general purpose matrix for
   * storing numbers and strings.
   *
   * @param rows the rows
   * @param columns the columns
   * @return the annotation matrix
   */
  public static DataFrame createDataFrame(int rows, int columns) {
    return createMixedMatrix(rows, columns);
  }

  /**
   * Creates a new annotation matrix of size row x columns. The size of the new
   * matrix includes the row and column annotations copied from m to the new
   * matrix. Thus if m has 1 row annotation and 1 column annotation (row names
   * and column names) and rows = 5 and columns = 4, the new matrix will have an
   * inner matrix 4 x 3 and annotations to give it a final size of 5 X 4.
   *
   * @param m the m
   * @return the annotation matrix
   */
  public static DataFrame createDataFrame(final DataFrame m) {
    Matrix im = m.getMatrix();

    DataFrame ret = createDataFrame(im.getRows(), im.getCols());

    copyAnnotations(m, ret);

    return ret;
  }

  /**
   * Creates the annotatable mixed matrix.
   *
   * @param rows the rows
   * @param columns the columns
   * @return the annotation matrix
   */
  public static DataFrame createMixedMatrix(int rows, int columns) {
    return new DataFrame(new MixedMatrix(rows, columns));
  }

  /**
   * Creates an annotation matrix from an underlying numerical matrix.
   *
   * @param rows the rows
   * @param columns the columns
   * @return the annotation matrix
   */
  public static DataFrame createNumericalMatrix(int rows, int columns) {
    return new DataFrame(new DoubleMatrix(rows, columns));
  }

  /**
   * Creates the numerical matrix.
   *
   * @param m the m
   * @return the annotation matrix
   */
  public static DataFrame createNumericalMatrix(DataFrame m) {
    DataFrame ret = createNumericalMatrix(m.getRows(), m.getCols());

    copyAnnotations(m, ret);

    return ret;
  }

  public static DataFrame createDoubleMatrix(int rows, int columns) {
    return new DataFrame(new DoubleMatrix(rows, columns));
  }

  /**
   * Creates the numerical matrix.
   *
   * @param m the m
   * @return the annotation matrix
   */
  public static DataFrame createDoubleMatrix(DataFrame m) {
    DataFrame ret = createDoubleMatrix(m.getRows(), m.getCols());

    copyAnnotations(m, ret);

    return ret;
  }

  /**
   * Creates the text matrix.
   *
   * @param rows the rows
   * @param columns the columns
   * @return the annotation matrix
   */
  public static DataFrame createTextMatrix(int rows, int columns) {
    return new DataFrame(new TextMatrix(rows, columns));
  }

  /**
   * Creates the text matrix.
   *
   * @param m the m
   * @return the annotation matrix
   */
  public static DataFrame createTextMatrix(DataFrame m) {
    DataFrame ret = createTextMatrix(m.getRows(), m.getCols());

    copyAnnotations(m, ret);

    return ret;
  }

  /**
   * Creates the dynamic matrix.
   *
   * @return the annotation matrix
   */
  public static DataFrame createWorksheet(int rows, int columns) {
    return createDataFrame(new MixedWorksheet(rows, columns));
  }

  /**
   * Creates the dynamic matrix.
   *
   * @param m the m
   * @return the annotation matrix
   */
  public static DataFrame createWorksheet(DataFrame m) {
    DataFrame ret = createWorksheet(m.getRows(), m.getCols());

    copyAnnotations(m, ret);

    return ret;
  }

  /**
   * Create an annotatable matrix from a matrix.
   *
   * @param m the m
   * @return the annotation matrix
   */
  public static DataFrame createDataFrame(Matrix m) {
    return new DataFrame(m);
  }

  /**
   * Creates the annotatable matrix from columns.
   *
   * @param m the m
   * @param columns the columns
   * @return the annotation matrix
   */
  public static DataFrame createAnnotatableMatrixFromCols(DataFrame m,
      List<Integer> columns) {
    DataFrame ret = createDataFrame(m.getRows(), columns.size());

    DataFrame.copyIndex(m, ret);

    for (int i = 0; i < columns.size(); ++i) {
      ret.copyColumn(m, columns.get(i), i);
    }

    return ret;
  }

  /**
   * Creates the annotatable matrix from rows.
   *
   * @param m the m
   * @param rows the rows
   * @return the annotation matrix
   */
  public static DataFrame createAnnotatableMatrixFromRows(DataFrame m,
      List<Integer> rows) {
    DataFrame ret = createDataFrame(rows.size(), m.getCols());

    DataFrame.copyColumnHeaders(m, ret);

    for (int i = 0; i < rows.size(); ++i) {
      ret.copyRow(m, rows.get(i), i);
    }

    return ret;
  }

  //
  // Static methods
  //

  /**
   * Write a simple expression matrix in GCT format.
   *
   * @param matrix the matrix
   * @param file the file
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void writeEstMatrixV1(DataFrame matrix, Path file)
      throws IOException {
    BufferedWriter writer = FileUtils.newBufferedWriter(file);
    
    Join join = Join.onTab();

    try {
      writer.write(EST_VERSION_1);
      writer.newLine();

      writer.write(EST_ROWS);
      writer.write(TextUtils.TAB_DELIMITER);
      writer.write(Integer.toString(matrix.getRows()));
      writer.newLine();

      writer.write(EST_COLUMNS);
      writer.write(TextUtils.TAB_DELIMITER);
      writer.write(Integer.toString(matrix.getCols()));
      writer.newLine();

      List<String> groups = new ArrayList<String>();

      // for (Group group : matrix.getColumnGroups()) {
      // groups.add(group);
      // }

      writer.write(EST_ANNOTATION_GROUPS);
      writer.write(TextUtils.TAB_DELIMITER);
      writer.write(Integer.toString(groups.size()));

      if (groups.size() > 0) {
        writer.write(TextUtils.TAB_DELIMITER);
        writer.write(join.toString(groups));
      }

      writer.newLine();

      List<String> rowAnnotationNames = CollectionUtils
          .head(matrix.getIndex().getNames(), 1);

      writer.write(EST_ANNOTATION_ROWS);

      writer.write(TextUtils.TAB_DELIMITER);

      // Since the first annotation is the row name and we are already
      // writing that, ignore the first row annotation
      writer.write(Integer.toString(rowAnnotationNames.size()));

      if (rowAnnotationNames.size() > 0) {
        writer.write(TextUtils.TAB_DELIMITER);
        writer.write(join.toString(rowAnnotationNames));
      }

      writer.newLine();

      List<String> columnAnnotationNames = CollectionUtils
          .head(matrix.getColumnHeader().getNames(), 1);

      writer.write(EST_ANNOTATION_COLUMNS);

      writer.write(TextUtils.TAB_DELIMITER);
      writer.write(Integer.toString(columnAnnotationNames.size()));

      if (columnAnnotationNames.size() > 0) {
        writer.write(TextUtils.TAB_DELIMITER);
        writer.write(join.toString(columnAnnotationNames));
      }

      writer.newLine();

      // column names

      // The first annotation row is the Id and column names
      writer.write(ROW_NAMES);
      writer.write(TextUtils.TAB_DELIMITER);

      if (rowAnnotationNames.size() > 1) {
        writer.write(join.toString(rowAnnotationNames));
        writer.write(TextUtils.TAB_DELIMITER);
      }

      writer.write(join.toString(matrix.getColumnNames()));
      writer.newLine();

      for (String name : columnAnnotationNames) {
        writer.write(name);
        writer.write(TextUtils.repeat(TextUtils.TAB_DELIMITER,
            rowAnnotationNames.size() + 1));

        writer.write(join.toString(matrix.getColumnHeader().getAnnotation(name)));
        writer.newLine();
      }

      for (int i = 0; i < matrix.getRows(); ++i) {
        writer.write(matrix.getRowName(i));

        for (String name : rowAnnotationNames) {
          writer.write(TextUtils.TAB_DELIMITER);
          writer.write(matrix.getIndex().getAnnotation(name, i).toString());
        }

        for (int j = 0; j < matrix.getCols(); ++j) {
          writer.write(TextUtils.TAB_DELIMITER);
          writer.write(formatValue(matrix.getValue(i, j)));
        }

        writer.newLine();
      }
    } finally {
      writer.close();
    }
  }

  /**
   * Write a simple expression matrix in GCT format.
   *
   * @param matrix the matrix
   * @param file the file
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void writeEstMatrixV2(DataFrame matrix, Path file)
      throws IOException {
    BufferedWriter writer = FileUtils.newBufferedWriter(file);

    Join join = Join.onTab();
    
    try {
      writer.write(EST_VERSION_2);
      writer.newLine();

      writer.write(EST_ROWS);
      writer.write(TextUtils.TAB_DELIMITER);
      writer.write(Integer.toString(matrix.getRows()));
      writer.newLine();

      writer.write(EST_COLUMNS);
      writer.write(TextUtils.TAB_DELIMITER);
      writer.write(Integer.toString(matrix.getCols()));
      writer.newLine();

      List<String> groups = new ArrayList<String>();

      // for (Group group : matrix.getColumnGroups()) {
      // groups.add(group);
      // }

      writer.write(EST_ANNOTATION_GROUPS);
      writer.write(TextUtils.TAB_DELIMITER);
      writer.write(Integer.toString(groups.size()));

      if (groups.size() > 0) {
        writer.write(TextUtils.TAB_DELIMITER);
        writer.write(join.toString(groups));
      }

      writer.newLine();

      //
      // Write out the row annotations
      //

      writer.write(EST_ANNOTATION_ROWS);

      writer.write(TextUtils.TAB_DELIMITER);
      writer.write(Integer.toString(matrix.getIndex().getNames().size()));

      if (matrix.getIndex().getNames().size() > 0) {
        writer.write(TextUtils.TAB_DELIMITER);
        writer.write(join.toString(matrix.getIndex().getNames()));
      }

      writer.newLine();

      for (String name : matrix.getIndex().getNames()) {
        writer.write(EST_ANNOTATION_ROW);
        writer.write(TextUtils.TAB_DELIMITER);
        writer.write(name);
        writer.write(TextUtils.TAB_DELIMITER);
        writer.write(join.toString(matrix.getIndex().getText(name)));
        writer.newLine();
      }

      //
      // Write out the column annotations
      //

      writer.write(EST_ANNOTATION_COLUMNS);

      writer.write(TextUtils.TAB_DELIMITER);
      writer.write(Integer.toString(matrix.getColumnHeader().getNames().size()));

      if (matrix.getColumnHeader().getNames().size() > 0) {
        writer.write(TextUtils.TAB_DELIMITER);
        writer.write(
            join.toString(matrix.getColumnHeader().getNames()));
      }

      writer.newLine();

      for (String name : matrix.getColumnHeader().getNames()) {
        writer.write(EST_ANNOTATION_COLUMN);
        writer.write(TextUtils.TAB_DELIMITER);
        writer.write(name);
        writer.write(TextUtils.TAB_DELIMITER);
        writer.write(join.toString(matrix.getColumnHeader().getAnnotation(name)));
        writer.newLine();
      }

      //
      // Write out the data
      //

      writer.write(EST_MATRIX);
      writer.newLine();

      for (int i = 0; i < matrix.getRows(); ++i) {
        for (int j = 0; j < matrix.getCols(); ++j) {
          writer.write(formatValue(matrix.getValue(i, j)));

          if (j < matrix.getCols() - 1) {
            writer.write(TextUtils.TAB_DELIMITER);
          }
        }

        writer.newLine();
      }
    } finally {
      writer.close();
    }
  }

  /**
   * Write a simple expression matrix in tab delimited text format.
   *
   * @param <T> the generic type
   * @param matrix the matrix
   * @param file the file
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static <T> void writeDataFrame(DataFrame matrix, Path file)
      throws IOException {
    BufferedWriter writer = FileUtils.newBufferedWriter(file);

    Join join = Join.onTab();
    
    try {
      boolean hasHeader = false;

      if (matrix.getIndex().getNames() != null
          && matrix.getIndex().getNames().size() > 0) {
        writer.write(join.toString(matrix.getIndex().getNames()));

        writer.write(TextUtils.TAB_DELIMITER);

        hasHeader = true;
      }

      if (matrix.getColumnNames() != null
          && matrix.getColumnHeader().size() > 0) {
        writer.write(join.toString(matrix.getColumnNames()));

        hasHeader = true;
      } else {
        // If there are row annotation names, then a header must be
        // present. If there are no column names, fill in row with
        // empty tabs
        if (hasHeader) {
          // pad with empty cells if there are not column name

          writer.write(TextUtils.emptyCells(matrix.getCols()));
        }
      }

      if (hasHeader) {
        writer.newLine();
      }

      for (int i = 0; i < matrix.getRows(); ++i) {
        for (String name : matrix.getIndex().getNames()) {
          // System.err.println("aha " + name + " " + i);

          writer.write(matrix.getIndex().getText(name, i));
          writer.write(TextUtils.TAB_DELIMITER);
        }

        for (int j = 0; j < matrix.getCols(); ++j) {

          writer.write(formatValue(matrix.getText(i, j)));

          if (j < matrix.getCols() - 1) {
            writer.write(TextUtils.TAB_DELIMITER);
          }
        }

        writer.newLine();
      }
    } finally {
      writer.close();
    }
  }

  /**
   * Format value.
   *
   * @param <T> the generic type
   * @param value the value
   * @return the string
   */
  public static <T> String formatValue(T value) {
    if (value != null) {
      return value.toString();
    } else {
      return TextUtils.EMPTY_STRING;
    }
  }

  /**
   * Search a matrix row annotations and return matching rows. This method is
   * case insensitive.
   *
   * @param matrix the matrix
   * @param text the text
   * @return the list
   */
  public static List<Integer> findRows(DataFrame matrix, String text) {
    List<Integer> ret = new ArrayList<Integer>();

    String ls = text.toLowerCase();

    for (int i = 0; i < matrix.getRows(); ++i) {
      for (String name : matrix.getIndex().getNames()) {
        if (matrix.getIndex().getText(name, i).toLowerCase().contains(ls)) {
          ret.add(i);
          break;
        }
      }
    }

    return ret;
  }

  /**
   * Match rows.
   *
   * @param m the m
   * @param rowAnnotation the row annotation
   * @param regex the regex
   * @return the list
   */
  public static List<Integer> matchRows(DataFrame m,
      String rowAnnotation,
      String regex) {
    return matchRows(m, rowAnnotation, regex, true);
  }

  /**
   * Match rows.
   *
   * @param m the m
   * @param rowAnnotation the row annotation
   * @param regex the regex
   * @return the list
   */
  public static List<Integer> matchRows(DataFrame m,
      String rowAnnotation,
      Pattern regex) {
    return matchRows(m, rowAnnotation, regex, true);
  }

  /**
   * Matches row annotation by regex and returns the indices of rows.
   *
   * @param m the m
   * @param rowAnnotation the row annotation
   * @param regex the regex
   * @param keep the keep
   * @return the list
   */
  public static List<Integer> matchRows(DataFrame m,
      String rowAnnotation,
      String regex,
      boolean keep) {
    return matchRows(m, rowAnnotation, Pattern.compile(regex), keep);
  }

  /**
   * Matches row annotation by regex and returns the indices of rows.
   *
   * @param m the m
   * @param rowAnnotation the row annotation
   * @param regex the regex
   * @param keep the keep
   * @return the list
   */
  public static List<Integer> matchRows(DataFrame m,
      String rowAnnotation,
      Pattern regex,
      boolean keep) {
    List<Integer> ret = new ArrayList<Integer>();

    String[] annotations = m.getIndex().getText(rowAnnotation);

    if (keep) {
      for (int i = 0; i < m.getRows(); ++i) {
        if (regex.matcher(annotations[i]).matches()) {
          ret.add(i);
        }
      }
    } else {
      for (int i = 0; i < m.getRows(); ++i) {
        if (!regex.matcher(annotations[i]).matches()) {
          ret.add(i);
        }
      }
    }

    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#copyRow(org.abh.common.math.matrix.
   * Matrix, int, int)
   */
  @Override
  public void copyRow(final Matrix from, int row, int toRow) {
    getMatrix().copyRow(from, row, toRow);
  }

  /**
   * Copy a row from one matrix to another allowing the row from the source to
   * be different from the target.
   *
   * @param from the from
   * @param row the row
   * @param toRow the to row
   */
  public void copyRow(final DataFrame from, int row, int toRow) {

    if (row >= 0) {
      getMatrix().copyRow(from.getMatrix(), row, toRow);
      copyIndex(from, row, this, toRow);
    } else {
      // co >= 0
      // We are copying the row annotation of the from matrix to a
      // column in the to matrix
      //Object[] values = from.getColumns().getAnnotation(row).rowAsList(0);

      Object[] values = new Object[from.getCols()];
      from.getColumnHeader().getAnnotation(row).rowToObject(0, values);
      getMatrix().setRow(toRow, values);
    }
  }

  /**
   * Copy rows.
   *
   * @param from the from
   * @param row the row
   * @param toRow the to row
   */
  public void copyRows(final DataFrame from, int row, int toRow) {
    copyRows(from, row, toRow, 0);
  }

  /**
   * Copy rows.
   *
   * @param from the from
   * @param row the row
   * @param toRow the to row
   * @param toOffset the to offset
   */
  public void copyRows(final DataFrame from, int row, int toRow, int toOffset) {

    for (int i = row; i <= toRow; ++i) {
      copyRow(from, i, i + toOffset);
    }
  }

  /**
   * Copy row annotation.
   *
   * @param from the from
   * @param to the to
   * @param row the row
   */
  public static void copyIndex(final DataFrame from,
      DataFrame to,
      int row) {
    copyIndex(from, row, to, row);
  }

  /**
   * Copy row annotation.
   *
   * @param from the from
   * @param fromRow the from row
   * @param to the to
   * @param toRow the to row
   */
  public static void copyIndex(final DataFrame from,
      int fromRow,
      DataFrame to,
      int toRow) {
    for (String name : from.getIndex().getNames()) {
      to.getIndex().setAnnotation(name, toRow, from.getIndex().getAnnotation(name, fromRow));
    }
  }

  /**
   * Copy row annotations.
   *
   * @param from the from
   * @param to the to
   */
  public static void copyIndex(final DataFrame from, DataFrame to) {
    for (String name : from.getIndex().getNames()) {
      copyIndex(from, name, to);
    }
  }

  /**
   * Copy row annotations.
   *
   * @param from the from
   * @param name the name
   * @param to the to
   */
  public static void copyIndex(final DataFrame from,
      String name,
      DataFrame to) {
    copyIndex(from, name, true, to);
  }
  
  public static void copyIndex(final DataFrame from,
      String name,      
      boolean shallow,
      DataFrame to) {
    if (shallow) {
      to.getIndex().setAnnotation(name, from.getIndex().getAnnotation(name));
    } else {
      to.getIndex().setAnnotation(name, from.getIndex().getAnnotation(name).copy());
    }
  }

  /**
   * Copy row annotations.
   *
   * @param from the from
   * @param to the to
   * @param rows the rows
   */
  public static void copyIndex(final DataFrame from,
      DataFrame to,
      int... rows) {
    
    for (String name : from.getIndex().getNames()) {
      
      switch (from.getIndex().getAnnotation(name).getType()) {
      case NUMBER:
        double[] v = from.getIndex().getAnnotation(name).rowToDouble(0);

        double[] subAnnotations = ArrayUtils.subList(v, rows);

        to.getIndex().setAnnotation(name, subAnnotations);
        break;
      case TEXT:
        String[] ta = from.getIndex().getAnnotation(name).rowToText(0);

        String[] ts = ArrayUtils.subList(ta, rows);

        to.getIndex().setAnnotation(name, ts);
        break;
      default:
        Object[] oa = from.getIndex().getAnnotation(name).rowToObject(0);

        Object[] ls = ArrayUtils.subList(oa, rows);

        to.getIndex().setAnnotation(name, ls);

        break;
      }
    }
  }

  /**
   * Copy row annotations.
   *
   * @param from the from
   * @param to the to
   * @param rows the rows
   */
  public static void copyIndex(final DataFrame from,
      DataFrame to,
      Collection<Integer> rows) {
    
    for (String name : from.getIndex().getNames()) {
  
      switch (from.getIndex().getAnnotation(name).getType()) {
      case NUMBER:
        double[] va = from.getIndex().getAnnotation(name).rowToDouble(0);
        double[] subAnnotations = CollectionUtils.subList(va, rows);
        to.getIndex().setAnnotation(name, subAnnotations);
        break;
      case TEXT:
        String[] ta = from.getIndex().getAnnotation(name).rowToText(0);
        String[] ts = CollectionUtils.subList(ta, rows);
        to.getIndex().setAnnotation(name, ts);
        break;
      default:
        // Mixed
        Object[] o = from.getIndex().getAnnotation(name).rowToObject(0);
        Object[] ls = CollectionUtils.subList(o, rows);
        to.getIndex().setAnnotation(name, ls);
        break;
      }
    }
  }

  /**
   * Copy row annotations using an indexed list.
   *
   * @param <V> the value type
   * @param from the from
   * @param to the to
   * @param rows the rows
   */
  public static <V extends Comparable<? super V>> void copyIndexFromIndexed(
      final DataFrame from,
      DataFrame to,
      List<Indexed<Integer, V>> rows) {
    for (String name : from.getIndex().getNames()) {
      switch (from.getIndex().getAnnotation(name).getType()) {
      case NUMBER:
        double[] va = from.getIndex().getAnnotation(name).rowToDouble(0);

        double[] ls = CollectionUtils.subListIndexed(va, rows);

        to.getIndex().setAnnotation(name, ls);
        break;
      case TEXT:
        String[] ta = from.getIndex().getAnnotation(name).rowToText(0);
        String[] ts = CollectionUtils.subListIndexed(ta, rows);

        to.getIndex().setAnnotation(name, ts);
        break;
      default:
        Object[] oa = from.getIndex().getAnnotation(name).rowToObject(0);

        Object[] os = CollectionUtils.subListIndexed(oa, rows);

        to.getIndex().setAnnotation(name, os);
        break;
      }

      /*
       * List<Object> annotations = from.getIndex().getAnnotation(name).rowAsList(0);
       * 
       * List<Object> subAnnotations =
       * CollectionUtils.subListIndexed(annotations, rows);
       * 
       * to.getIndex().setAnnotation(name, subAnnotations);
       */
    }
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
    if (from instanceof DataFrame) {
      CopyColumnHeader((DataFrame) from, column, this, toColumn);
    }

    super.copyColumn(from, column, toColumn); // getInnerMatrix().copyColumn(from,
                                              // column, toColumn);
  }

  /**
   * Copy column.
   *
   * @param from the from
   * @param column the column
   */
  public void copyColumn(final DataFrame from, int column) {
    copyColumn(from, column, column);
  }

  /**
   * Copy the column from one matrix to another.
   *
   * @param from the from
   * @param column the column
   * @param toColumn the offset
   */
  public void copyColumn(final DataFrame from, int column, int toColumn) {

    if (column >= 0) {
      CopyColumnHeader(from, column, this, toColumn);

      super.copyColumn(from, column, toColumn);
    } else {
      // co >= 0
      // We are copying the row annotation of the from matrix to a
      // column in the to matrix
      
      Object[] values = new Object[getCols()];
      from.getIndex().getAnnotation(column).rowToObject(0, values);

      setColumnName(toColumn, from.getIndex().getName(column));
      setColumn(toColumn, values);
    }
  }

  /**
   * Copy column annotation.
   *
   * @param from the from
   * @param column the column
   * @param to the to
   * @param offset the offset
   */
  public static void CopyColumnHeader(final DataFrame from,
      int column,
      DataFrame to,
      int offset) {

    for (String name : from.getColumnHeader().getNames()) {
      to.getColumnHeader().setAnnotation(name,
          offset,
          from.getColumnHeader().getAnnotation(name, column));
    }
  }

  /**
   * Copy column annotations.
   *
   * @param from the from
   * @param to the to
   */
  public static void copyColumnHeaders(final DataFrame from, DataFrame to) {
    for (String name : from.getColumnHeader().getNames()) {
      to.getColumnHeader().setAnnotation(name, from.getColumnHeader().getAnnotation(name));
    }
  }

  /**
   * Copy column annotations.
   *
   * @param from the from
   * @param to the to
   * @param columns the columns
   */
  public static void copyColumnHeaders(final DataFrame from,
      DataFrame to,
      Collection<Integer> columns) {

    int c = from.getCols();
    
    for (String name : from.getColumnHeader().getNames()) {
      // List<Object> annotations =
      // from.getColumns().getAnnotation(name).rowAsList(0);
      // List<Object> subAnnotations = CollectionUtils.subList(annotations,
      // columns);
      // to.getColumns().setAnnotation(name, subAnnotations);

      switch (from.getColumnHeader().getAnnotation(name).getType()) {
      case NUMBER:
        double[] va = new double[c];
        from.getColumnHeader().getAnnotation(name).rowToDouble(0, va);
        double[] ns = CollectionUtils.subList(va, columns);
        to.getColumnHeader().setAnnotation(name, ns);
        break;
      case TEXT:
        String[] ta = new String[c];
        from.getColumnHeader().getAnnotation(name).rowToText(0, ta);
        String[] ts = CollectionUtils.subList(ta, columns);
        to.getColumnHeader().setAnnotation(name, ts);
        break;
      default:
        String[] oa = new String[c];
        from.getColumnHeader().getAnnotation(name).rowToObject(0, oa);
        Object[] os = CollectionUtils.subList(oa, columns);
        to.getColumnHeader().setAnnotation(name, os);
        break;
      }

    }
  }

  /**
   * Copy column annotations.
   *
   * @param from the from
   * @param fromStart the from start
   * @param fromEnd the from end
   * @param to the to
   * @param toStart the to start
   */
  public static void copyColumnHeaders(final DataFrame from,
      int fromStart,
      int fromEnd,
      DataFrame to,
      int toStart) {
    
    Object[] values = new Object[from.getCols()];
    
    for (String name : from.getColumnHeader().getNames()) {
      from.getColumnHeader().getAnnotation(name).rowToObject(0, values);

      Object[] subAnnotations = CollectionUtils
          .subList(values, fromStart, fromEnd - fromStart + 1);

      int s = toStart;

      for (Object m : subAnnotations) {
        to.getColumnHeader().setAnnotation(name, s++, m);
      }
    }
  }

  /**
   * Copy column annotations.
   *
   * @param from the from
   * @param to the to
   * @param toStart the to start
   */
  public static void copyColumnHeaders(final DataFrame from,
      DataFrame to,
      int toStart) {
    Object[] values = new Object[from.getCols()];
    
    for (String name : from.getColumnHeader().getNames()) {
      from.getColumnHeader().getAnnotation(name).rowToObject(0, values);

      int s = toStart;

      for (Object m : values) {
        to.getColumnHeader().setAnnotation(name, s++, m);
      }
    }
  }

  /**
   * Copy the row and column annotations from matrix to another.
   *
   * @param from the from
   * @param to the to
   */
  public static void copyAnnotations(final DataFrame from, DataFrame to) {
    to.setName(from.getName());

    copyIndex(from, to);
    copyColumnHeaders(from, to);
  }

  /**
   * Copy column annotations indexed.
   *
   * @param <V> the value type
   * @param from the from
   * @param to the to
   * @param columns the columns
   */
  public static <V extends Comparable<? super V>> void copyColumnHeadersIndexed(
      final DataFrame from,
      DataFrame to,
      List<Indexed<Integer, V>> columns) {
    for (String name : from.getColumnHeader().getNames()) {
      // List<Object> annotations =
      // from.getColumns().getAnnotation(name).rowAsList(0);
      // List<Object> subAnnotations =
      // CollectionUtils.subListIndexed(annotations, columns);
      // to.getColumns().setAnnotation(name, subAnnotations);

      switch (from.getColumnHeader().getAnnotation(name).getType()) {
      case NUMBER:
        double[] v = new double[from.getCols()];
        from.getColumnHeader().getAnnotation(name).rowToDouble(0, v);

        double[] subAnnotations = CollectionUtils.subListIndexed(v,
            columns);

        to.getColumnHeader().setAnnotation(name, subAnnotations);
        break;
      case TEXT:
        double[] t = new double[from.getCols()];
        from.getColumnHeader().getAnnotation(name).rowToDouble(0, t);
        double[] ts = CollectionUtils.subListIndexed(t, columns);
        to.getColumnHeader().setAnnotation(name, ts);
        break;
      default:
        Object[] o = new Object[from.getCols()];
        from.getColumnHeader().getAnnotation(name).rowToObject(0, o);
        Object[] os = CollectionUtils.subListIndexed(o, columns);
        to.getColumnHeader().setAnnotation(name, os);
        break;
      }
    }
  }

  /**
   * Set the row annotation of multiple rows.
   *
   * @param m the m
   * @param name the name
   * @param rows the rows
   * @param value the value
   */
  public static void setAnnotation(DataFrame m,
      String name,
      List<Integer> rows,
      double value) {
    for (int i : rows) {
      m.getIndex().setAnnotation(name, i, value);
    }
  }

  /**
   * Sets the annotation.
   *
   * @param m the m
   * @param name the name
   * @param rows the rows
   * @param value the value
   */
  public static void setAnnotation(DataFrame m,
      String name,
      List<Integer> rows,
      String value) {
    for (int i : rows) {
      m.getIndex().setAnnotation(name, i, value);
    }
  }

  /**
   * Sets the annotation.
   *
   * @param m the m
   * @param name the name
   * @param rows the rows
   * @param value the value
   */
  public static void setAnnotation(DataFrame m,
      String name,
      List<Integer> rows,
      MatrixCell value) {
    for (int i : rows) {
      m.getIndex().setAnnotation(name, i, value);
    }
  }

  /**
   * Copy columns.
   *
   * @param from the from
   * @param fromColstart the from column start
   * @param fromColumnEnd the from column end
   * @param to the to
   */
  public static void copyColumns(DataFrame from,
      int fromColstart,
      int fromColumnEnd,
      DataFrame to) {
    copyColumns(from, fromColstart, fromColumnEnd, to, 0);
  }

  /**
   * Copy columns.
   *
   * @param from the from
   * @param to the to
   * @param toColOffset the to col offset
   */
  public static void copyColumns(DataFrame from,
      DataFrame to,
      int toColOffset) {
    copyColumns(from, 0, to, toColOffset);
  }

  /**
   * Copy columns from one matrix starting at a given offset to the beginning of
   * the to matrix.
   *
   * @param from the from
   * @param fromColstart the from column start
   * @param to the to
   */
  public static void copyColumns(DataFrame from,
      int fromColstart,
      DataFrame to) {
    copyColumns(from, fromColstart, to, 0);
  }

  /**
   * Copy a range of columns from one matrix to another with the ability to
   * offset the copy in the to matrix.
   *
   * @param from the from
   * @param fromColstart the from column start
   * @param to the to
   * @param toColOffset the to col offset
   */
  public static void copyColumns(DataFrame from,
      int fromColstart,
      DataFrame to,
      int toColOffset) {
    copyColumns(from, fromColstart, from.getCols() - 1, to, toColOffset);
  }

  /**
   * Copy columns.
   *
   * @param from The from matrix.
   * @param fromColStart The from column start column
   * @param fromColumnEnd the from column end
   * @param to the to
   * @param toColOffset the to col offset
   */
  public static void copyColumns(DataFrame from,
      int fromColStart,
      int fromColumnEnd,
      DataFrame to,
      int toColOffset) {
    int cols = fromColumnEnd - fromColStart + 1;

    for (int j = 0; j < cols; ++j) {
      to.copyColumn(from, j + fromColStart, j + toColOffset);
    }
  }

  /**
   * Copy columns.
   *
   * @param m the m
   * @param iter the iter
   * @param to the to
   */
  public static void copyColumns(final DataFrame m,
      final Iterable<? extends MatrixGroup> iter,
      DataFrame to) {
    copyColumns(m, iter, to, 0);
  }

  /**
   * Copy columns.
   *
   * @param m the m
   * @param iter the iter
   * @param to the to
   * @param offset the offset
   */
  public static void copyColumns(final DataFrame m,
      final Iterable<? extends MatrixGroup> iter,
      DataFrame to,
      int offset) {
    int c = offset;

    for (MatrixGroup g : iter) {
      for (int i : MatrixGroup.findColumnIndices(m, g)) {
        to.copyColumn(m, i, c);

        ++c;
      }
    }
  }

  /**
   * Copy columns.
   *
   * @param from the from
   * @param indices the indices
   * @param to the to
   */
  public static void copyColumns(final DataFrame from,
      final Collection<Integer> indices,
      DataFrame to) {
    copyColumns(from, indices, to, 0);
  }

  /**
   * Copy columns.
   *
   * @param from the from
   * @param indices the indices
   * @param to the to
   * @param offset the offset
   */
  public static void copyColumns(final DataFrame from,
      final Collection<Integer> indices,
      DataFrame to,
      int offset) {
    int c = offset;

    for (int i : indices) {
      to.copyColumn(from, i, c);

      ++c;
    }
  }

  /**
   * Copy.
   *
   * @param from the from
   * @param to the to
   */
  public static void copy(DataFrame from, DataFrame to) {
    copy(from, 0, 0, to, 0, 0);
  }

  /**
   * Copy.
   *
   * @param from the from
   * @param fromRowOffset the from row offset
   * @param fromColOffset the from col offset
   * @param to the to
   * @param toRowOffset the to row offset
   * @param toColOffset the to col offset
   */
  public static void copy(DataFrame from,
      int fromRowOffset,
      int fromColOffset,
      DataFrame to,
      int toRowOffset,
      int toColOffset) {
    int fromRows = from.getRows() - fromRowOffset;
    int fromCols = from.getCols() - fromColOffset;

    int toRows = to.getRows() - toRowOffset;
    int toCols = to.getCols() - toColOffset;

    int rows = Math.min(fromRows, toRows);
    int cols = Math.min(fromCols, toCols);

    for (int j = 0; j < cols; ++j) {
      int cFrom = j + fromColOffset;
      int cTo = j + toColOffset;

      to.setColumnName(cTo, from.getColumnName(cFrom));
    }

    List<String> names = from.getIndex().getNames();

    for (int i = 0; i < rows; ++i) {
      int rFrom = i + fromRowOffset;
      int rTo = i + toRowOffset;

      for (String name : names) {
        to.getIndex().setAnnotation(name, rTo, from.getIndex().getAnnotation(name, rFrom));
      }
    }

    for (int i = 0; i < rows; ++i) {
      int rFrom = i + fromRowOffset;
      int rTo = i + toRowOffset;

      for (int j = 0; j < cols; ++j) {
        int cFrom = j + fromColOffset;
        int cTo = j + toColOffset;

        to.set(rTo, cTo, from.get(rFrom, cFrom));
      }
    }
  }

  /**
   * Write row.
   *
   * @param m the m
   * @param writer the writer
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void writeHeader(DataFrame m, BufferedWriter writer)
      throws IOException {
    if (m.getIndex().getNames().size() > 0) {
      writer.write(TextUtils.tabJoin(m.getIndex().getNames()));
      writer.write(TextUtils.TAB_DELIMITER);
    }

    writer.write(TextUtils.tabJoin(m.getColumnNames()));
    writer.newLine();
  }

  /**
   * Write row.
   *
   * @param m the m
   * @param row the row
   * @param writer the writer
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void writeRow(DataFrame m, int row, BufferedWriter writer)
      throws IOException {
    if (m.getIndex().getNames().size() > 0) {
      writer.write(TextUtils.tabJoin(m.getIndex().getText(row)));
      writer.write(TextUtils.TAB_DELIMITER);
    }

    writer.write(Join.onTab().values(m.rowToText(row)).toString());
    writer.newLine();
  }

  /**
   * Find the first column header containing text (case insensitive).
   *
   * @param m the m
   * @param terms the terms
   * @return the column index or -1 if a column is not found
   */
  public static int findColumn(DataFrame m, String... terms) {
    List<String> names = m.getIndex().getNames();
    
    int index = TextUtils.findFirst(names, terms);
    
    if (index != -1) {
      // In the extended column names so return as negative index
      
      return index - names.size();
    } else {
      return TextUtils.findFirst(m.getColumnNames(), terms);
    }
  }

  /**
   * For each term, find the first column matching that term. Each term can be
   * composed of multiple keyswords separated by pipe | if a column is expected
   * to contain variations of a name.
   *
   * @param m the m
   * @param terms the terms
   * @return the map
   */
  public static Map<String, Integer> findColumns(DataFrame m, String... terms) {
    Map<String, Integer> indexMap = new HashMap<String, Integer>();

    for (String term : terms) {
      List<String> alts = Splitter.on('|').text(term);

      indexMap.put(term, TextUtils.findFirst(m.getColumnNames(), alts));
    }

    return indexMap;
  }

  public static List<Integer> findAllColumns(DataFrame m, String... terms) {
    List<Integer> ret = new ArrayList<Integer>(terms.length);

    String[] names = m.getColumnNames();

    for (String term : terms) {
      List<String> alts = Splitter.on('|').text(term);

      int index = TextUtils.findFirst(names, alts);

      if (index != -1) {
        ret.add(index);
      }
    }

    return ret;
  }

  public static List<Integer> findAllColumns(DataFrame m,
      Collection<String> terms) {
    List<Integer> ret = new ArrayList<Integer>(terms.size());

    String[] names = m.getColumnNames();

    for (String term : terms) {
      List<String> alts = Splitter.on('|').text(term);

      int index = TextUtils.findFirst(names, alts);

      if (index != -1) {
        ret.add(index);
      }
    }

    return ret;
  }

  /**
   * Find.
   *
   * @param m the m
   * @param text the text
   * @param wholeCell the whole cell
   * @param caseSensitive the case sensitive
   * @return the matrix cell
   */
  public static MatrixCellRef find(DataFrame m,
      String text,
      boolean wholeCell,
      boolean caseSensitive) {
    return find(m, text, wholeCell, caseSensitive, START_CELL);
  }

  /**
   * Search for a string in a matrix and return the cell reference if found.
   *
   * @param m the m
   * @param text the text
   * @param wholeCell the whole cell
   * @param caseSensitive the case sensitive
   * @param startCell the start cell
   * @return the matrix cell
   */
  public static MatrixCellRef find(DataFrame m,
      String text,
      boolean wholeCell,
      boolean caseSensitive,
      MatrixCellRef startCell) {
    String t;

    int index;
    int columnIndex;

    Pattern pattern;

    if (wholeCell) {
      if (caseSensitive) {
        pattern = Pattern.compile("^" + text + "$");
      } else {
        pattern = Pattern.compile("^" + text + "$", Pattern.CASE_INSENSITIVE);
      }
    } else {
      if (caseSensitive) {
        pattern = Pattern.compile(text);
      } else {
        pattern = Pattern.compile(text, Pattern.CASE_INSENSITIVE);
      }
    }

    Matcher matcher = pattern.matcher(TextUtils.EMPTY_STRING);

    int r = -1;

    for (String name : m.getIndex().getNames()) {
      for (int i = 0; i < m.getRows(); ++i) {
        index = (i + startCell.row) % m.getRows();

        t = m.getIndex().getText(name, index);

        if (t != null) {
          matcher.reset(t);

          if (matcher.find()) {
            return new MatrixCellRef(index, r);
          }
        }
      }

      --r;
    }

    r = -1;

    for (String name : m.getColumnHeader().getNames()) {
      for (int i = 0; i < m.getCols(); ++i) {
        index = (i + startCell.column) % m.getCols();

        t = m.getColumnHeader().getText(name, index);

        if (t != null) {
          matcher.reset(t);

          if (matcher.find()) {
            return new MatrixCellRef(r, index);
          }
        }
      }

      --r;
    }

    for (int i = 0; i < m.getRows(); ++i) {
      index = (i + startCell.row) % m.getRows();

      for (int j = 0; j < m.getCols(); ++j) {
        columnIndex = (j + startCell.column) % m.getCols();

        t = m.getText(index, columnIndex);

        if (t != null) {
          matcher.reset(t);

          if (matcher.find()) {
            return new MatrixCellRef(index, columnIndex);
          }
        }
      }
    }

    return null;
  }

  /**
   * Find all.
   *
   * @param m the m
   * @param text the text
   * @param wholeCell the whole cell
   * @param caseSensitive the case sensitive
   * @param startCell the start cell
   * @return the list
   */
  public static List<MatrixCellRef> findAll(DataFrame m,
      String text,
      boolean wholeCell,
      boolean caseSensitive,
      MatrixCellRef startCell) {
    String t;

    int index;
    int columnIndex;

    Pattern pattern;

    if (wholeCell) {
      if (caseSensitive) {
        pattern = Pattern.compile("^" + text + "$");
      } else {
        pattern = Pattern.compile("^" + text + "$", Pattern.CASE_INSENSITIVE);
      }
    } else {
      if (caseSensitive) {
        pattern = Pattern.compile(text);
      } else {
        pattern = Pattern.compile(text, Pattern.CASE_INSENSITIVE);
      }
    }

    List<MatrixCellRef> cells = new ArrayList<MatrixCellRef>();

    Matcher matcher = pattern.matcher(TextUtils.EMPTY_STRING);

    int r = -1;

    for (String name : m.getIndex().getNames()) {
      for (int i = 0; i < m.getRows(); ++i) {
        index = (i + startCell.row) % m.getRows();

        t = m.getIndex().getText(name, index);

        if (t != null) {
          matcher.reset(t);

          if (matcher.find()) {
            cells.add(new MatrixCellRef(index, r));
          }
        }
      }

      --r;
    }

    r = -1;

    for (String name : m.getColumnHeader().getNames()) {
      for (int i = 0; i < m.getCols(); ++i) {
        index = (i + startCell.column) % m.getCols();

        t = m.getColumnHeader().getText(name, index);

        if (t != null) {
          matcher.reset(t);

          if (matcher.find()) {
            cells.add(new MatrixCellRef(r, index));
          }
        }
      }

      --r;
    }

    for (int i = 0; i < m.getRows(); ++i) {
      index = (i + startCell.row) % m.getRows();

      for (int j = 0; j < m.getCols(); ++j) {
        columnIndex = (j + startCell.column) % m.getCols();

        t = m.getText(index, columnIndex);

        if (t != null) {
          matcher.reset(t);

          if (matcher.find()) {
            cells.add(new MatrixCellRef(index, columnIndex));
          }
        }
      }
    }

    return cells;
  }

  /**
   * Parses the txt matrix.
   *
   * @param file the file
   * @param hasHeader the has header
   * @param rowAnnotations the row annotations
   * @return the annotation matrix
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static DataFrame parseTxtMatrix(Path file,
      int headers,
      int rowAnnotations) throws IOException {
    return parseTxtMatrix(file,
        headers,
        TextUtils.EMPTY_LIST,
        rowAnnotations,
        TextUtils.TAB_DELIMITER);
  }

  /**
   * Parses the txt matrix.
   *
   * @param file the file
   * @param hasHeader the has header
   * @param skipMatches the skip matches
   * @param rowAnnotations the row annotations
   * @param delimiter the delimiter
   * @return the annotation matrix
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static DataFrame parseTxtMatrix(Path file,
      int headers,
      List<String> skipMatches,
      int rowAnnotations,
      String delimiter) throws IOException {
    if (headers > 0) {
      return new MixedMatrixParser(headers, skipMatches, rowAnnotations,
          delimiter).parse(file);
    } else {
      return parseWorksheet(file, headers, skipMatches, rowAnnotations, delimiter);
    }
  }

  /**
   * Parses the csv matrix.
   *
   * @param file the file
   * @param hasHeader the has header
   * @param skipMatches the skip matches
   * @param rowAnnotations the row annotations
   * @return the annotation matrix
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static DataFrame parseCsvMatrix(Path file,
      int headers,
      List<String> skipMatches,
      int rowAnnotations) throws IOException {
    if (headers > 0) {
      return new CsvMatrixParser(true, rowAnnotations).parse(file);
    } else {
      return parseWorksheet(file,
          headers,
          skipMatches,
          rowAnnotations,
          TextUtils.COMMA_DELIMITER);
    }
  }

  /**
   * Parses the dynamic matrix.
   *
   * @param file the file
   * @param hasHeader the has header
   * @param skipMatches the skip matches
   * @param rowAnnotations the row annotations
   * @param delimiter the delimiter
   * @return the annotation matrix
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static DataFrame parseWorksheet(Path file,
      int headers, 
      List<String> skipMatches,
      int rowAnnotations,
      String delimiter) throws IOException {
    return new MixedWorksheetParser(headers, skipMatches, rowAnnotations, delimiter)
        .parse(file);
  }

  /**
   * Sets the column.
   *
   * @param <T> the generic type
   * @param column the column
   * @param value the value
   * @param m the m
   */
  public static <T> void setColumn(int column, T value, Matrix m) {
    for (int r = 0; r < m.getRows(); ++r) {
      m.set(r, column, value);
    }
  }

  /**
   * Copy a number of rows from one matrix to another.
   *
   * @param m the m
   * @param rows the rows
   * @param ret the ret
   */
  public static void copyRows(final DataFrame m,
      final List<Integer> rows,
      DataFrame ret) {
    copyRows(m, rows, ret, 0);
  }

  /**
   * Copy a number of rows from matrix to another where each row that is copied
   * is copied consecutively into the new array.
   *
   * @param m the m
   * @param rows the rows
   * @param ret the ret
   * @param offset the offset
   */
  public static void copyRows(final DataFrame m,
      final List<Integer> rows,
      DataFrame ret,
      int offset) {

    for (int row : rows) {
      ret.copyRow(m, row, offset++);
    }
  }

  /**
   * Loads the first.
   *
   * @param file the file
   * @param skipHeader the skip header
   * @return the list
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static List<String> firstColAsList(Path file, boolean skipHeader)
      throws IOException {
    return colAsList(file, skipHeader, 0);
  }

  /**
   * Parses a matrix file and extracts a column as a list.
   *
   * @param file the file
   * @param skipHeader the skip header
   * @param col the col
   * @return the list
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static List<String> colAsList(Path file, boolean skipHeader, int col)
      throws IOException {
    // LOG.info("Load list from {}, {}...", file, skipHeader);

    BufferedReader reader = FileUtils.newBufferedReader(file);

    String line;

    List<String> rows = new ArrayList<String>();

    Splitter splitter = Splitter.onTab();

    try {

      if (skipHeader) {
        reader.readLine();
      }

      while ((line = reader.readLine()) != null) {
        rows.add(splitter.text(line).get(col));
      }
    } finally {
      reader.close();
    }

    return rows;
  }

  /**
   * Copy column names.
   *
   * @param from the from
   * @param to the to
   */
  public static void copyColumnNames(DataFrame from, DataFrame to) {
    copyColumnNames(from, to, 0);
  }

  /**
   * Copy column names.
   *
   * @param from the from
   * @param to the to
   * @param offset the offset
   */
  public static void copyColumnNames(DataFrame from, DataFrame to, int offset) {
    int c = Math.min(from.getCols(), to.getCols());

    for (int i = 0; i < c; ++i) {
      to.setColumnName(i + offset, from.getColumnName(i));
    }
  }

  /**
   * Returns a list of column names given a list of indices.
   *
   * @param mMatrix the m matrix
   * @param columns the columns
   * @return the list
   */
  public static List<String> columnNames(DataFrame mMatrix,
      Collection<Integer> columns) {
    List<String> ret = new ArrayList<String>(columns.size());

    for (int c : columns) {
      ret.add(mMatrix.getColumnName(c));
    }

    return ret;
  }

  /**
   * Row names.
   *
   * @param mMatrix the m matrix
   * @param rows the rows
   * @return the list
   */
  public static List<String> rowNames(DataFrame mMatrix,
      Collection<Integer> rows) {
    List<String> ret = new ArrayList<String>(rows.size());

    for (int r : rows) {
      ret.add(mMatrix.getRowName(r));
    }

    return ret;
  }

  /**
   * Copy annotations for lists of rows and columns.
   *
   * @param rows the rows
   * @param columns the columns
   * @param m the m
   * @param ret the ret
   */
  public static void copyAnnotations(List<Integer> rows,
      List<Integer> columns,
      final DataFrame m,
      DataFrame ret) {

    for (int i = 0; i < columns.size(); ++i) {
      CopyColumnHeader(m, columns.get(i), ret, i);
    }

    for (int i = 0; i < rows.size(); ++i) {
      copyIndex(m, rows.get(i), ret, i);
    }
  }

  /**
   * Alt index modulo.
   *
   * @param i the i
   * @param size the size
   * @return the int
   */
  public static int altIndexModulo(int i, int size) {
    return (size + i) % size;
  }

  
}
