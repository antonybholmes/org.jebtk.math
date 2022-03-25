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

/**
 * Maximum distance metric.
 *
 * @author Antony Holmes
 */
public class MaximumDistanceMetric extends DistanceMetric {

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.cluster.DistanceMetric#distance(double[], double[])
   */
  @Override
  public double distance(double[] d1, double[] d2) {
    double d = Double.MIN_VALUE;
    double x;

    for (int i = 0; i < d1.length; ++i) {
      x = Math.abs(d1[i] - d2[i]);

      if (x > d) {
        d = x;
      }
    }

    return d;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.cluster.DistanceMetric#distance(java.util.List,
   * java.util.List)
   */
  @Override
  public double distance(List<Double> d1, List<Double> d2) {
    double d = Double.MIN_VALUE;
    double x;

    for (int i = 0; i < d1.size(); ++i) {
      x = Math.abs(d1.get(i) - d2.get(i));

      if (x > d) {
        d = x;
      }
    }

    return d;
  }
}
