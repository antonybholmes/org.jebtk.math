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

import org.jebtk.core.Mathematics;
import org.jebtk.math.statistics.Statistics;

/**
 * Pearson correlation distance metric.
 *
 * @author Antony Holmes
 */
public class PearsonDistanceMetric extends DistanceMetric {

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.cluster.DistanceMetric#distance(double[], double[])
   */
  @Override
  public double distance(double[] d1, double[] d2) {
    double m1 = Statistics.mean(d1);
    double m2 = Statistics.mean(d2);

    double sd1 = Statistics.popStdDev(d1);
    double sd2 = Statistics.popStdDev(d2);

    double[] dd1 = Statistics.zscore(d1, m1, sd1);
    double[] dd2 = Statistics.zscore(d2, m2, sd2);

    double d = 0;

    for (int i = 0; i < d1.length; ++i) {
      d += dd1[i] * dd2[i];
    }

    // average d
    d /= d1.length;

    return Mathematics.bound(1.0 - d, 0, 2);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.cluster.DistanceMetric#distance(java.util.List,
   * java.util.List)
   */
  @Override
  public double distance(List<Double> d1, List<Double> d2) {
    double m1 = Statistics.mean(d1);
    double m2 = Statistics.mean(d2);

    double sd1 = Statistics.popStdDev(d1);
    double sd2 = Statistics.popStdDev(d2);

    List<Double> dd1 = Statistics.zscore(d1, m1, sd1);
    List<Double> dd2 = Statistics.zscore(d2, m2, sd2);

    double d = 0;

    for (int i = 0; i < d1.size(); ++i) {
      d += dd1.get(i) * dd2.get(i);
    }

    // average d
    d /= d1.size();

    // In case of rounding errors ensure that the distance cannot be
    // less than zero. This corrects situations such as -1E-15 which
    // should be zero.
    return Mathematics.boundMin(1.0 - d, 0);
  }
}
