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
package org.jebtk.math.statistics;

// TODO: Auto-generated Javadoc
/**
 * The class GaussianKernelDensity.
 */
public class NormKernelDensity extends KernelDensity {

  /**
   * The constant GAUSSIAN_BANDWIDTH_FACTOR.
   */
  private static final double GAUSSIAN_BANDWIDTH_FACTOR = 0.2; // 1.0 / 5.0;

  /**
   * The constant KERNEL.
   */
  private static final Kernel KERNEL = new GaussianKernel();

  /** The Constant FACTOR. */
  private static final double FACTOR = Math.pow(4.0 / 3.0, 0.2);

  /** The Constant POWER_MAP. */
  // Cache powers that we keep using
  private static final double[] POWER_MAP = new double[100];

  static {
    // Precache some likely values to speed things up a bit.

    for (int i = 0; i < 100; ++i) {
      POWER_MAP[i] = Math.pow(i, -GAUSSIAN_BANDWIDTH_FACTOR);
    }
  }

  /**
   * Instantiates a new norm kernel density.
   *
   * @param dist the dist
   */
  public NormKernelDensity(double[] dist) {
    super(dist, KERNEL);
  }

  /**
   * Bandwidth estimate. See
   * https://en.wikipedia.org/wiki/Kernel_density_estimation
   *
   * @param dist the dist
   * @return the double
   */
  @Override
  public double bandwidthEstimate(double[] dist) {
    Stats stats = new Stats(dist);

    if (stats.sum() == 0) {
      return 1;
    }

    int n = dist.length;

    // Use the MATLAB method
    double sd = stats.madStdDev(); // Statistics.sampleStandardDeviation(dist);

    // System.err.println("sd " + sd + " " + Math.pow(n,
    // -GAUSSIAN_BANDWIDTH_FACTOR) + " " +Arrays.toString(dist));

    if (sd > 0) {
      if (n < 100) {
        // Use the cache
        return FACTOR * sd * POWER_MAP[n];
      } else {
        // Do the calculation each time, but we are assuming that
        // it will be rare to get 100 samples per phenotype
        return FACTOR * sd * Math.pow(n, -GAUSSIAN_BANDWIDTH_FACTOR);
      }

      // sd * Math.pow(4.0 / (3.0 * n), GAUSSIAN_BANDWIDTH_FACTOR); //0.1157;
      // //1.06 * sd * Math.pow(dist.size(), GAUSSIAN_BANDWIDTH_FACTOR);
      // return sd * Math.pow(4.0 / (3.0 * n), GAUSSIAN_BANDWIDTH_FACTOR);
    } else {
      return 1;
    }
  }
}