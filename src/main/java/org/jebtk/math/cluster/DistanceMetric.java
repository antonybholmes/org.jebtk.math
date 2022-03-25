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
package org.jebtk.math.cluster;

import java.util.List;

import org.jebtk.math.matrix.Matrix;

/**
 * Calculate the distance between features.
 * 
 * @author Antony Holmes
 *
 */
public abstract class DistanceMetric {

  /**
   * Column distance.
   *
   * @param m the m
   * @param c1 the c1
   * @param c2 the c2
   * @return the double
   */
  public double columnDistance(final Matrix m, int c1, int c2) {
    double[] d1 = new double[m.getRows()];
    double[] d2 = new double[m.getRows()];

    for (int i = 0; i < m.getRows(); ++i) {
      d1[i] = m.getValue(i, c1);
      d2[i] = m.getValue(i, c2);
    }

    return distance(d1, d2);
  }

  /**
   * Row distance.
   *
   * @param m the m
   * @param r1 the r1
   * @param r2 the r2
   * @return the double
   */
  public double rowDistance(final Matrix m, int r1, int r2) {
    int c = m.getCols();

    double[] d1 = new double[c];
    double[] d2 = new double[c];

    for (int i = 0; i < c; ++i) {
      d1[i] = m.getValue(r1, i);
      d2[i] = m.getValue(r2, i);
    }

    return distance(d1, d2);
  }

  /**
   * Measures the distance between two rows/columns. The arrays must be the same
   * length.
   * 
   * @param d1 Array 1
   * @param d2 Array 2
   * 
   * @return The distance between the two arrays.
   */
  public abstract double distance(final double[] d1, final double[] d2);

  /**
   * Distance.
   *
   * @param d1 the d 1
   * @param d2 the d 2
   * @return the double
   */
  public abstract double distance(final List<Double> d1, final List<Double> d2);
}
