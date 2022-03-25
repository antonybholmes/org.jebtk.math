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
package org.jebtk.math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.jebtk.core.collections.CollectionUtils;

// TODO: Auto-generated Javadoc
/**
 * Generates vectors of evenly spaced numbers.
 * 
 * @author Antony Holmes
 *
 */
public class Linspace {

  /**
   * Generate 100 evenly spaced points between start and end.
   *
   * @param start the start
   * @param end the end
   * @return the list
   */
  public static List<Double> generate(double start, double end) {
    return generate(start, end, 100);

  }

  /**
   * Generate n points starting at start and ending at end.
   *
   * @param start the start
   * @param end the end
   * @param n the n
   * @return the list
   */
  public static List<Double> generate(double start, double end, int n) {
    List<Double> values = new ArrayList<Double>(n);

    double d = (end - start) / (n - 1);

    double s = start;

    for (int i = 0; i < n; ++i) {
      values.add(s);

      s += d;
    }

    return values;
  }

  /**
   * Generate2.
   *
   * @param start the start
   * @param end the end
   * @return the double[]
   */
  public static double[] generate2(double start, double end) {
    return genArray(start, end, 100);

  }

  /**
   * Generate2.
   *
   * @param start the start
   * @param end the end
   * @param n the n
   * @return the double[]
   */
  public static double[] genArray(double start, double end, int n) {
    double[] values = new double[n];

    double d = (end - start) / (n - 1);

    double s = start;

    for (int i = 0; i < n; ++i) {
      values[i] = s;

      s += d;
    }

    return values;
  }

  /**
   * Returns the start and end as an array.
   *
   * @param start the start
   * @param end the end
   * @return the list
   */
  public static List<Double> range(double start, double end) {
    List<Double> values = new ArrayList<Double>();

    values.add(start);
    values.add(end);

    return values;
  }

  /**
   * Evenly spaced.
   *
   * @param start the start
   * @param end the end
   * @return the list
   */
  public static List<Double> evenlySpaced(double start, double end) {
    return evenlySpaced(start, end, 1);
  }

  /**
   * Returns a list of evenly spaced, increasing numbers from start to the
   * closest number to end that does not exceed end, given the increment.
   *
   * @param start The starting value.
   * @param end The end value; should be greater than start.
   * @param inc a positive increment value.
   * @return the list
   */
  public static List<Double> evenlySpaced(double start,
      double end,
      double inc) {
    // System.err.println("lin space " + start + " " + end + " " + inc);

    // if (inc <= 0 || end < start) {
    // return Collections.emptyList();
    // }

    List<Double> values = new ArrayList<Double>();

    double s = start;

    while (s <= end) {
      values.add(s);

      s += inc;
    }

    return values;
  }

  /**
   * Evenly spaced exclusive.
   *
   * @param start the start
   * @param end the end
   * @param inc the inc
   * @return the list
   */
  public static List<Double> evenlySpacedExclusive(double start,
      double end,
      double inc) {
    if (inc <= 0 || end < start) {
      return Collections.emptyList();
    }

    List<Double> values = new ArrayList<Double>();

    double s = start;

    while (s < end) {
      if (s > start) {
        values.add(s);
      }

      s += inc;
    }

    return values;
  }

  /**
   * Generate a list of random numbers between a min (inclusive) and a max
   * (exclusive).
   *
   * @param min the min
   * @param max the max
   * @param n the n
   * @return the list
   */
  public static List<Double> random(double min, double max, int n) {
    Random random = new Random();

    List<Double> values = new ArrayList<Double>(n);

    double r = max - min;

    while (values.size() < n) {
      values.add(random.nextDouble() * r + min);
    }

    return values;
  }

  /**
   * Return an a list of numbers which are evenly spaced subdivisions.
   *
   * @param ticks the ticks
   * @param subdivisions the subdivisions
   * @return the list
   */
  public static List<Double> subDivide(List<Double> ticks, int subdivisions) {
    if (CollectionUtils.isNullOrEmpty(ticks) || subdivisions < 1) {
      return Collections.emptyList();
    }

    List<Double> values = new ArrayList<Double>(
        (ticks.size() - 1) * subdivisions);

    int s = subdivisions - 1;

    for (int i = 0; i < ticks.size() - 1; ++i) {
      double inc = (ticks.get(i + 1) - ticks.get(i)) / s;

      values.addAll(evenlySpaced(ticks.get(i), ticks.get(i + 1), inc));
    }

    return values;
  }
}
