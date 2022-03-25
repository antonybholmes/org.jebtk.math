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
package org.jebtk.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jebtk.core.Indexed;
import org.jebtk.core.Mathematics;
import org.jebtk.core.sys.SysUtils;
import org.jebtk.math.test.Condition;
import org.jebtk.math.test.GreaterThanCondition;
import org.jebtk.math.test.GreaterThanEqualToCondition;
import org.jebtk.math.test.LessThanCondition;
import org.jebtk.math.test.LessThanEqualToCondition;

/**
 * The Class MathUtils.
 */
public class MathUtils {

  /**
   * Instantiates a new math utils.
   */
  private MathUtils() {
    // Do nothing
  }

  /**
   * Divide a list of doubles by a number.
   *
   * @param <T> the generic type
   * @param values the values
   * @param x the x
   * @return the list
   */
  public static <T extends Number> List<Double> multiply(List<T> values,
      double x) {
    List<Double> ret = new ArrayList<Double>(values.size());

    for (T v : values) {
      ret.add(v.doubleValue() * x);
    }

    return ret;
  }

  /**
   * Adds the.
   *
   * @param values the values
   * @param i the i
   * @return the list
   */
  public static List<Integer> add(Collection<Integer> values, int i) {
    List<Integer> ret = new ArrayList<Integer>(values.size());

    for (int v : values) {
      ret.add(v + 1);
    }

    return ret;
  }

  /**
   * Divide a list of numbers by a number.
   *
   * @param <T> the generic type
   * @param values the values
   * @param x the x
   * @return the list
   */
  public static <T extends Number> List<Double> divide(List<T> values,
      double x) {
    List<Double> ret = new ArrayList<Double>(values.size());

    for (T v : values) {
      ret.add(v.doubleValue() / x);
    }

    return ret;
  }

  /**
   * Return an array of a value divided by each element in an array.
   * 
   * @param d
   * @param values
   * @return
   */
  public static double[] divided(double d, final double[] values) {
    int n = values.length;

    double[] ret = new double[n];

    for (int i = 0; i < n; ++i) {
      ret[i] = d / values[i];
    }

    return ret;
  }

  /**
   * Modify an array to d / x where x is an element in the array.
   * 
   * @param d
   * @param values
   */
  public static void divide(double d, double[] values) {
    divide(d, values, values);
  }

  public static void divide(double d, double[] values, double[] ret) {
    for (int i = 0; i < values.length; ++i) {
      ret[i] = d / values[i];
    }
  }

  public static void divide(double[] values, double d) {
    divide(values, d, values);
  }

  /**
   * Divide values by d and put the results into ret.
   * 
   * @param values
   * @param d
   * @param ret
   */
  public static void divide(double[] values, double d, double[] ret) {
    for (int i = 0; i < values.length; ++i) {
      ret[i] = values[i] / d;
    }
  }

  /**
   * Find the indices of indexed values whose value is greater than x.
   *
   * @param values the values
   * @param x the x
   * @return the list
   */
  public static List<Integer> gt(Collection<Indexed<Integer, Double>> values,
      double x) {
    return findInIndexedList(values, new GreaterThanCondition(x));
  }

  /**
   * Find all items greater than or equal to x in a list and return their
   * indices.
   *
   * @param values the values
   * @param x the x
   * @return the list
   */
  public static List<Integer> ge(Collection<Indexed<Integer, Double>> values,
      double x) {
    return findInIndexedList(values, new GreaterThanEqualToCondition(x));
  }

  /**
   * Lt indexed list.
   *
   * @param values the values
   * @param x the x
   * @return the list
   */
  public static List<Integer> lt(Collection<Indexed<Integer, Double>> values,
      double x) {
    return findInIndexedList(values, new LessThanCondition(x));
  }

  /**
   * Le.
   *
   * @param values the values
   * @param x the x
   * @return the list
   */
  public static List<Integer> le(Collection<Indexed<Integer, Double>> values,
      double x) {
    return findInIndexedList(values, new LessThanEqualToCondition(x));
  }

  /**
   * Returns the list of indices where a condition is met.
   *
   * @param values the values
   * @param condition the condition
   * @return the list
   */
  public static List<Integer> findInIndexedList(
      Collection<Indexed<Integer, Double>> values,
      Condition condition) {
    List<Integer> indices = new ArrayList<Integer>();

    for (Indexed<Integer, Double> index : values) {
      if (condition.test(index.getValue())) {
        indices.add(index.getIndex());
      }
    }

    return indices;
  }

  /**
   * Returns the indices of items of who are greater than the min.
   *
   * @param items the items
   * @param min the min
   * @return the list
   */
  public static List<Indexed<Integer, Double>> min(
      List<Indexed<Integer, Double>> items,
      double min) {

    List<Indexed<Integer, Double>> ret = new ArrayList<Indexed<Integer, Double>>(
        items.size());

    for (Indexed<Integer, Double> index : items) {
      // System.err.println("filter sd " + i + " " + sd[i] + " " + min);

      if (index.getValue() >= min) {
        ret.add(index);
      }
    }

    return ret;
  }

  public static void multiply(double[] num, double[] denum) {
    multiply(num, denum, num);
  }

  public static void multiply(double[] num, double[] denum, double[] res) {
    multiply(num, 0, 1, denum, 0, 1, res, 0, 1, num.length);
  }

  public static void multiply(double[] num,
      int ns,
      int nk,
      double[] denum,
      int ds,
      int dk,
      int l) {
    multiply(num, ns, nk, denum, ds, dk, num, num.length);
  }

  /**
   * Divide num by denum and put results in res.
   * 
   * @param num
   * @param ns
   * @param nk
   * @param denum
   * @param ds
   * @param dk
   * @param l
   * @param res
   */
  public static void multiply(final double[] num,
      int ns,
      int nk,
      final double[] denum,
      int ds,
      int dk,
      double[] res,
      int l) {
    multiply(num, ns, nk, denum, ds, dk, res, ns, nk, num.length);
  }

  public static void multiply(final double[] num,
      int ns,
      int nk,
      final double[] denum,
      int ds,
      int dk,
      double[] res,
      int rs,
      int rk,
      int l) {
    for (int i = 0; i < l; ++i) {
      res[rs] = num[ns] * denum[ds];

      rs += rk;
      ns += nk;
      ds += dk;
    }
  }

  public static void divide(double[] num, double[] denum) {
    divide(num, denum, num);
  }

  public static void divide(double[] num, double[] denum, double[] res) {
    divide(num, 0, 1, denum, 0, 1, res, 0, 1, num.length);
  }

  public static void divide(double[] num,
      int ns,
      int nk,
      double[] denum,
      int ds,
      int dk,
      int l) {
    divide(num, ns, nk, denum, ds, dk, num, num.length);
  }

  /**
   * Divide num by denum and put results in res.
   * 
   * @param num
   * @param ns
   * @param nk
   * @param denum
   * @param ds
   * @param dk
   * @param l
   * @param res
   */
  public static void divide(double[] num,
      int ns,
      int nk,
      double[] denum,
      int ds,
      int dk,
      double[] res,
      int l) {
    divide(num, ns, nk, denum, ds, dk, res, ns, nk, num.length);
  }

  public static void divide(double[] num,
      int ns,
      int nk,
      double[] denum,
      int ds,
      int dk,
      double[] res,
      int rs,
      int rk,
      int l) {
    for (int i = 0; i < l; ++i) {
      res[rs] = num[ns] / denum[ds];

      rs += rk;
      ns += nk;
      ds += dk;
    }
  }

  /**
   * Linearly interpret v on the x scale and map to y scale.
   * 
   * @param v
   * @param x
   * @param y
   * @return
   */
  public static double linearInterpolation(double v,
      double[] x,
      double[] y) {

    SysUtils.err().println("x", Arrays.toString(x), Arrays.toString(y));
    
    if (v <= x[0]) {
      return y[0];
    }
    
    int xn = x.length;
    int xni = xn - 1;
    
    if (v >= x[xni]) {
      return y[xni];
    }
    
    for (int i = 0; i < xni; ++i) {
      if (v >= x[i] && v < x[i + 1]) {
        double vx = (v - x[i]) / (x[i + 1] - x[i]);
        
        double yx = y[i] + (y[i + 1] - y[i]) * vx;
            
        return Mathematics.bound(yx, y[0], y[xni]);
      }
    }
    
    return 0;
  }
}
