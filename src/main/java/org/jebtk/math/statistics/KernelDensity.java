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

import java.util.Arrays;

import org.jebtk.core.Mathematics;
import org.jebtk.math.Linspace;

// TODO: Auto-generated Javadoc
/**
 * The class KernelDensity.
 */
public abstract class KernelDensity {

  /** The Constant DEFAULT_POINTS. */
  private static final int DEFAULT_POINTS = 100;

  /** The m dist. */
  private double[] mDist;

  /** The m lx. */
  private double[] mLx;

  /** The m cdf. */
  private double[] mCdf;

  /**
   * Instantiates a new kernel density.
   *
   * @param dist the dist
   * @param kernel the kernel
   */
  public KernelDensity(double[] dist, Kernel kernel) {
    mDist = new double[dist.length];

    // Clone the dist since we are going to modify it
    System.arraycopy(dist, 0, mDist, 0, dist.length);

    // Ensure array is sorted for stats that assumed ordered data
    Arrays.sort(mDist);

    // evaluation points to create a smooth function
    mLx = evalPoints(dist);

    double bandwidth = bandwidthEstimate(dist);

    double[] pdf = kde(mLx, dist, bandwidth, kernel);

    mCdf = cdf(mLx, pdf);

    // System.err.println("lx " + Arrays.toString(mLx));
    // System.err.println("pdf " + Arrays.toString(pdf));
    // System.err.println("cdf " + Arrays.toString(mCdf));
  }

  /**
   * Cdf.
   *
   * @param xp the xp
   * @return the double[]
   */
  public double[] cdf(double[] xp) {
    double[] ret = new double[xp.length];

    for (int i = 0; i < xp.length; ++i) {
      double x = xp[i];

      int ci = 0;

      // integrate f(x) dx
      if (x <= mLx[0]) {
        // x is before the the points we are evaluating so just use
        // the first point as an estimate of the pdf
        ci = 0;
      } else if (x >= mLx[mLx.length - 1]) {
        // comes after the cdf finishes so use the maximum cdf to
        // approximate the point
        ci = mLx.length - 1;
      } else {
        // Search until we hit the first point equal to or greater
        // than x. If the cdf has enough points, this will give a
        // reasonably close approximation of the cdf for x.
        for (int j = 1; j < mCdf.length - 1; ++j) {
          if (mLx[j] >= x) {
            // System.err.println("sum " + j + " " + x + " " +
            // cdfPoints.get(j));

            break;
          }

          ++ci;

          // Trapezoid rule
          // sum += pdf.get(j) + pdf.get(j + 1); // * (evalPoints.get(j + 1) -
          // evalPoints.get(j));

        }
      }

      // sum *= h;

      // System.err.println("sumd " + x + " " + i + " " + ci + " " +
      // cdf.get(ci));
      // System.err.println("eh " + cdf);

      ret[i] = mCdf[ci];
    }

    // PrintUtils.out().println("=====");
    // PrintUtils.out().columns(cdfPoints, pdf);
    // PrintUtils.out().columns(cdfPoints, cdf);
    // PrintUtils.out().columns(dist);
    // PrintUtils.out().println(bandwidth);
    // PrintUtils.out().println("=====");

    // System.exit(0);

    // System.err.println("cdf " + Arrays.toString(ret));

    return ret;
  }

  /**
   * Bandwidth estimate.
   *
   * @param dist the dist
   * @return the double
   */
  public abstract double bandwidthEstimate(double[] dist);

  //
  // Static methods
  //

  /**
   * Integrate the pdf to make a cdf using the evaluation points to form a
   * distribution.
   *
   * @param cdfPoints Used to determine the x gap only.
   * @param pdf y at each cdfPoint/x.
   * @return the double[]
   */
  private static double[] cdf(final double[] cdfPoints, final double[] pdf) {

    // Assume points are evenly distributed along x
    double h = (cdfPoints[1] - cdfPoints[0]) / 2.0;

    double sum = pdf[0] * h;

    double[] ret = new double[cdfPoints.length];

    ret[0] = Mathematics.bound(sum, 0, 1);

    // System.err.println("pdf " + pdf.length + " " + cdfPoints.length);

    for (int i = 1; i < pdf.length; ++i) {
      sum += h * (pdf[i - 1] + pdf[i]); // * (evalPoints.get(j + 1) -
                                        // evalPoints.get(j));

      // Bound sum between 0 and 1 for cdf
      ret[i] = sum; // Mathematics.bound(sum, 0, 1);
    }

    return ret;
  }

  /**
   * For integration purposes, we need a range of points in the distribution we
   * can evaluate the pdf at.
   *
   * @param dist the dist
   * @return the list
   */
  public static double[] evalPoints(double[] dist) {
    return evalPoints(dist, DEFAULT_POINTS);
  }

  /**
   * Eval points.
   *
   * @param dist the dist
   * @param n the n
   * @return the list
   */
  public static double[] evalPoints(double[] dist, int n) {
    double min;
    double max;

    if (Mathematics.sum(dist) == 0) {
      min = -3;
      max = 3;
    } else {
      Stats stats = new Stats(dist);

      // Use Sample STDEV, as per MATLAB
      double sd = stats.sampleStdDev();
      double m = stats.mean();
      double d = sd * 4;

      min = m - d;
      max = m + d;
    }

    // double min = dist[0];
    // max = dist.get(dist.length - 1);

    return evalPoints(min, max, n);
  }

  /**
   * Eval points.
   *
   * @param min the min
   * @param max the max
   * @param n the n
   * @return the list
   */
  public static double[] evalPoints(double min, double max, int n) {
    double[] values = Linspace.genArray(min, max, n);

    /*
     * double[] values = new double[](n);
     * 
     * double x = min; //Mathematics.min(dist);
     * 
     * double d = (max - min) / (n - 1);
     * 
     * for (int i = 0; i < n; ++i) { values.add(x);
     * 
     * x += d; }
     */

    // System.err.println(Arrays.toString(values));

    return values;
  }

  /**
   * Evaluate the points of xp relative to dist. Thus given a point x we can
   * estimate its density in the distribution dist which is a finite number of
   * points in an unknown distribution.
   *
   * @param xp the xp
   * @param dist the dist
   * @param bandwidth the bandwidth
   * @param kernel the kernel
   * @return the list
   */
  public static double[] kde(final double[] xp,
      double[] dist,
      double bandwidth,
      Kernel kernel) {
    int n = xp.length;

    double[] values = new double[n];

    for (int i = 0; i < n; ++i) {
      values[i] = kde(xp[i], dist, bandwidth, kernel);
    }

    return values;
  }

  /**
   * The Kernel Density estimator (KDE).
   *
   * @param x the x
   * @param dist the dist
   * @param bandwidth the bandwidth
   * @param kernel the kernel
   * @return the double
   */
  public static double kde(double x,
      double[] dist,
      double bandwidth,
      Kernel kernel) {
    double n = dist.length;

    double f = 1.0 / (bandwidth * n);

    double sum = 0;

    // System.err.println("kde " + x + " " + dist);

    for (double xi : dist) {
      sum += kernel.evaluate((x - xi) / bandwidth);
    }

    // System.err.println("kde " + x + " " + Arrays.toString(dist) + " " +
    // bandwidth);

    return f * sum;
  }

}
