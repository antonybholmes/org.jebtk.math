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

import org.jebtk.math.matrix.DataFrame;
import org.jebtk.math.matrix.Matrix;
import org.jebtk.math.matrix.MatrixDimFunction;

/**
 * Inline functions that perform arithmetic on a matrix and modify it in place.
 * Use MatrixOperations to modify a copy of a matrix and leave the original
 * alone.
 * 
 * @author antony
 *
 */
public class MatrixArithmetic {

  private static abstract class ArithDimFunc implements MatrixDimFunction {

    /** The m V. */
    protected double mV;

    /**
     * Instantiates a new arith func.
     *
     * @param v the v
     */
    public ArithDimFunc(double v) {
      mV = v;
    }
  }

  private static class DivideDimFunc extends ArithDimFunc {

    /**
     * Instantiates a new divide func.
     *
     * @param v the v
     */
    public DivideDimFunc(double v) {
      super(v);
    }

    @Override
    public void apply(int index, double[] data, double[] ret) {
      for (int i = 0; i < data.length; ++i) {
        ret[i] = data[i] / mV;
      }
    }
  }

  /**
   * Adds x to the matrix.
   *
   * @param x the x
   * @param m the m
   */
  public static void add(double x, Matrix m) {
    m.apply(Matrix.ADD_FUNCTION, x);
  }

  /**
   * Copy a matrix and add x to each cell.
   * 
   * @param m
   * @param x
   * @return
   */
  public static Matrix add(DataFrame m, double x) {
    return new DataFrame(m, add(m.getMatrix(), x));
  }

  public static Matrix add(Matrix m, double x) {
    return m.add(x);
  }

  /**
   * Subtract.
   *
   * @param x the x
   * @param m the m
   */
  public static void subtract(double x, Matrix m) {
    add(-x, m);
  }

  /**
   * Multiply.
   *
   * @param x the x
   * @param m the m
   */
  public static void multiply(double x, Matrix m) {
    m.apply(Matrix.MULT_FUNCTION, x);
  }

  /**
   * Divide.
   *
   * @param x the x
   * @param m the m
   */
  public static void divide(double x, Matrix m) {
    m.apply(Matrix.DIV_FUNCTION, m);
  }

  public static void divide(int row, double x, Matrix m) {
    m.rowApply(new DivideDimFunc(x), row);
  }
}
