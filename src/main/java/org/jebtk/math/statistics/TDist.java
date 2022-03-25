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

import java.util.ArrayList;
import java.util.List;

/**
 * The class TDist.
 */
public class TDist {

  /**
   * The main method.
   *
   * @param args the arguments
   */
  public static void main(String[] args) {
    System.err.println(tDist(0, 2));

    System.err.println((twoTailTDist(1.95, 60)));

    List<Double> v1 = new ArrayList<Double>();

    /*
     * v1.add(3.0); v1.add(4.0); v1.add(5.0); v1.add(8.0); v1.add(9.0);
     * v1.add(1.0); v1.add(2.0); v1.add(4.0); v1.add(5.0);
     */

    v1.add(30.02);
    v1.add(29.99);
    v1.add(30.11);
    v1.add(29.97);
    v1.add(30.01);
    v1.add(29.99);

    List<Double> v2 = new ArrayList<Double>();

    /*
     * v2.add(6.0); v2.add(19.0); v2.add(3.0); v2.add(2.0); v2.add(14.0);
     * v2.add(4.0); v2.add(5.0); v2.add(17.0); v2.add(1.0);
     */

    v2.add(29.89);
    v2.add(29.93);
    v2.add(29.72);
    v2.add(29.98);
    v2.add(30.02);
    v2.add(29.98);

    System.err.println(twoTailTTest(v1, v2));

    System.err.println(gamma(3.0 / 2.0));
  }

  /**
   * Instantiates a new t dist.
   */
  private TDist() {
    // do nothing
  }

  /**
   * One sample.
   *
   * @param values the values
   * @param mean the mean
   * @return the double
   */
  public static double oneSample(List<Double> values, double mean) {
    int n = values.size();

    return (Statistics.mean(values) - mean)
        / (Statistics.popStdDev(values) * Math.sqrt(n));
  }

  /**
   * Independent two sample.
   *
   * @param values1 the values1
   * @param values2 the values2
   * @return the double
   */
  public static double independentTwoSample(List<Double> values1,
      List<Double> values2) {

    int n1 = values1.size();
    int n2 = values2.size();

    // double sx1x2 = Math.sqrt(0.5 * (Statistics.sampleVariance(values1) +
    // Statistics.sampleVariance(values2)));

    // return (Statistics.mean(values1) - Statistics.mean(values2)) / (sx1x2 *
    // Math.sqrt(2.0 / n));

    double sx1x2 = Math.sqrt(Statistics.populationVariance(values1) / n1
        + Statistics.populationVariance(values2) / n2);

    double t = (Statistics.mean(values1) - Statistics.mean(values2)) / sx1x2;

    System.err.println("sx " + sx1x2 + " " + t + " "
        + (Statistics.mean(values1) - Statistics.mean(values2)));

    return t;
  }

  /**
   * Df.
   *
   * @param values1 the values1
   * @param values2 the values2
   * @return the double
   */
  public static double df(List<Double> values1, List<Double> values2) {

    int n1 = values1.size();
    int n2 = values2.size();

    double s1 = Statistics.populationVariance(values1) / n1;
    double s2 = Statistics.populationVariance(values2) / n2;

    double num = s1 + s2;

    num *= num;

    double denom = (s1 * s1) / (n1 - 1) + (s2 * s2) / (n2 - 1);

    return num / denom;
  }

  /**
   * Two tail t test.
   *
   * @param values1 the values1
   * @param values2 the values2
   * @return the double
   */
  public static double twoTailTTest(List<Double> values1,
      List<Double> values2) {
    double t = independentTwoSample(values1, values2);

    double v = df(values1, values2); // 2 * n - 1;

    System.err.println("v" + v);

    return twoTailTDist(t, v);
  }

  /**
   * Incomplete beta.
   *
   * @param x the x
   * @param a the a
   * @param b the b
   * @return the double
   */
  public static double incompleteBeta(double x, int a, int b) {
    Binomial binomial = new Binomial();

    double ret = 0;

    int max = a + b - 1;

    for (int i = a; i <= max; ++i) {
      ret += binomial.binomial(max, i) * Math.pow(x, i)
          * Math.pow((1 - x), max - i);
    }

    return ret;
  }

  /**
   * Cum t dist.
   *
   * @param x the x
   * @param v the v
   * @return the double
   */
  public static double cumTDist(double x, int v) {

    double s = 0;

    double d = (x + 5) / 100.0;

    double d2 = d / 2.0;

    double t = -5;

    List<Double> p = new ArrayList<Double>(100);

    for (int i = 0; i < 100; ++i) {
      p.add(tDist(t, v));

      t += d;
    }

    for (int i = 0; i < 99; ++i) {
      s += d2 * (p.get(i) + p.get(i + 1));
    }

    return s;
  }

  /**
   * Ln gamma.
   *
   * @param z the z
   * @return the double
   */
  public static double lnGamma(double z) {
    // return (z - 0.5) * Math.log(z) - z + 0.5 * Math.log(2 * Math.PI);

    // from wiki
    return z * Math.log(z) - z - 0.5 * Math.log(z / (2.0 * Math.PI))
        + (1.0 / (12.0 * z)) - (1.0 / (360.0 * Math.pow(z, 3)))
        + (1.0 / (1260.0 * Math.pow(z, 5)));
  }

  /**
   * Gamma.
   *
   * @param z the z
   * @return the double
   */
  public static double gamma(double z) {
    return Math.exp(lnGamma(z));
  }

  /**
   * Beta.
   *
   * @param x the x
   * @param y the y
   * @return the double
   */
  public static double beta(double x, double y) {
    return Math.exp(lnGamma(x) + lnGamma(y) - lnGamma(x + y));
  }

  /**
   * T dist.
   *
   * @param t the t
   * @param v the v
   * @return the double
   */
  public static double tDist(double t, double v) {

    double vv = (v + 1.0) / 2.0;
    double v2 = v / 2.0;

    // System.err.println(vv + " " + gamma(vv));

    // return 1.0 / Math.sqrt(v * Math.PI) * gamma(vv) / gamma(v2) * Math.pow((1
    // + (t * t) / v), -v2);

    return 1.0 / (Math.sqrt(v) * beta(v2, 0.5)) * Math.pow(v / (v + t * t), vv);
  }

  /**
   * Two tail t dist.
   *
   * @param t the t
   * @param v the v
   * @return the double
   */
  public static double twoTailTDist(double t, double v) {
    return tDist(t, v);
  }

  /**
   * One tail t dist.
   *
   * @param t the t
   * @param v the v
   * @return the double
   */
  public static double oneTailTDist(double t, double v) {
    return tDist(t, v) / 2.0;
  }
}
