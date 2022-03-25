/**
 * Copyright 2017 Antony Holmes
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

package org.jebtk.math.matrix.utils;

import java.util.ArrayList;
import java.util.List;

import org.jebtk.core.Mathematics;
import org.jebtk.math.matrix.DataFrame;
import org.jebtk.math.matrix.DoubleMatrix;
import org.jebtk.math.matrix.IndexRowMatrix;
import org.jebtk.math.matrix.Matrix;
import org.jebtk.math.matrix.MatrixGroup;
import org.jebtk.math.statistics.Statistics;
import org.jebtk.math.statistics.TTest;

/**
 * The Class MatrixUtils.
 */
public class MatrixUtils {

  /**
   * Instantiates a new matrix utils.
   */
  private MatrixUtils() {

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
    int r = m.getRows();
    int c = m.getCols();

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
   * Row means.
   *
   * @param m the m
   * @return the double[]
   */
  public static double[] rowMeans(Matrix m) {
    if (m instanceof DoubleMatrix) {
      return rowMeans((DoubleMatrix) m);
    } else if (m instanceof IndexRowMatrix) {
      return rowMeans((IndexRowMatrix) m);
    } else {
      int r = m.getRows();
      int c = m.getCols();

      double[] means = new double[r];

      for (int i = 0; i < r; ++i) {
        double[] values = new double[c];

        for (int j = 0; j < c; ++j) {
          values[j] = m.getValue(i, j);
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
  public static double[] rowMeans(IndexRowMatrix m) {
    int r = m.getRows();
    int c = m.getCols();

    double[] means = new double[r];

    for (int i = 0; i < r; ++i) {
      double[] values = new double[c];

      int index = i * c;

      for (int j = 0; j < c; ++j) {
        values[j] = m.getValue(index);

        ++index;
      }

      double mean = Statistics.mean(values);

      means[i] = mean;
    }

    return means;
  }

  /**
   * Row means.
   *
   * @param m the m
   * @return the double[]
   */
  public static double[] rowMeans(DoubleMatrix m) {
    double[] means = new double[m.mDim.mRows];

    for (int i = 0; i < m.mDim.mRows; ++i) {
      double[] values = new double[m.mDim.mCols];

      int index = i * m.mDim.mCols;

      for (int j = 0; j < m.mDim.mCols; ++j) {
        values[j] = m.mData[index];

        ++index;
      }

      double mean = Statistics.mean(values);

      means[i] = mean;
    }

    return means;
  }

  /**
   * Column rows.
   *
   * @param m the m
   * @return the double[]
   */
  public static double[] columnRows(DoubleMatrix m) {
    int r = m.getRows();
    int c = m.getCols();

    double[] means = new double[r];

    for (int i = 0; i < r; ++i) {
      double[] values = new double[c];

      int index = i * c;

      for (int j = 0; j < r; ++j) {
        values[j] = m.mData[index];

        ++index;
      }

      double mean = Statistics.mean(values);

      means[i] = mean;
    }

    return means;
  }

  /**
   * Max row sum.
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
   * Max col sum.
   *
   * @param m the m
   * @return the double
   */
  public static double maxColSum(DataFrame m) {
    double max = Double.MIN_VALUE;

    for (int i = 0; i < m.getCols(); ++i) {
      double sum = 0;

      for (int j = 0; j < m.getRows(); ++j) {
        double v = m.getValue(j, i);

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
   * T test.
   *
   * @param m the m
   * @param g1 the g 1
   * @param g2 the g 2
   * @param equalVariance the equal variance
   * @return the list
   */
  public static List<Double> tTest(DataFrame m,
      MatrixGroup g1,
      MatrixGroup g2,
      boolean equalVariance) {
    List<Double> pvalues = new ArrayList<Double>(m.getRows());

    List<Integer> g11 = MatrixGroup.findColumnIndices(m, g1);
    List<Integer> g22 = MatrixGroup.findColumnIndices(m, g2);

    for (int i = 0; i < m.getRows(); ++i) {
      List<Double> p1 = new ArrayList<Double>(g11.size());

      for (int c : g11) {
        p1.add(m.getValue(i, c));
      }

      List<Double> p2 = new ArrayList<Double>(g22.size());

      for (int c : g22) {
        p2.add(m.getValue(i, c));
      }

      double p;

      if (equalVariance) {
        p = TTest.twoTailEqualVarianceTTest(p1, p2);
      } else {
        p = TTest.twoTailUnequalVarianceTTest(p1, p2);
      }

      // Set strange values to NaN
      if (Mathematics.isInvalidNumber(p)) {
        p = 1; // Double.NaN;
      }

      pvalues.add(p);
    }

    return pvalues;
  }

  /**
   * Log fold change.
   *
   * @param matrix the matrix
   * @param g1 the g 1
   * @param g2 the g 2
   * @return the list
   */
  public static List<Double> logFoldChange(DataFrame matrix,
      MatrixGroup g1,
      MatrixGroup g2) {
    List<Integer> g11 = MatrixGroup.findColumnIndices(matrix, g1);
    List<Integer> g22 = MatrixGroup.findColumnIndices(matrix, g2);

    Matrix im = matrix.getMatrix();

    List<Double> foldChanges = new ArrayList<Double>(im.getRows());

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

      foldChanges.add(foldChange);
    }

    return foldChanges;
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
}
