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

import java.util.Map;

import org.apache.commons.math3.distribution.HypergeometricDistribution;
import org.jebtk.core.AgeMap;

/**
 * Fast implementation of the hypergeometric function using the sum of logs.
 *
 * @author Antony Holmes
 *
 */
public class Hypergeometric {

  /**
   * The member cached pdf.
   */
  private Map<String, Double> mCachedPdf = new AgeMap<String, Double>(10000);

  /**
   * The member cached cdf.
   */
  private Map<String, Double> mCachedCdf = new AgeMap<String, Double>(10000);

  /**
   * The member two tail cdf.
   */
  private Map<String, Double> mTwoTailCdf = new AgeMap<String, Double>(10000);

  /**
   * The member one tail cdf.
   */
  private Map<String, Double> mOneTailCdf = new AgeMap<String, Double>(10000);

  /**
   * Gets the key.
   *
   * @param successes the successes
   * @param sampleSize the sample size
   * @param populationSuccesses the population successes
   * @param populationSize the population size
   * @return the key
   */
  private static String getKey(int successes,
      int sampleSize,
      int populationSuccesses,
      int populationSize) {
    return successes + "." + sampleSize + "." + populationSuccesses + "."
        + populationSize;
  }

  /**
   * Lookup.
   *
   * @param successes the successes
   * @param sampleSize the sample size
   * @param populationSuccesses the population successes
   * @param populationSize the population size
   * @param cache the cache
   * @return the double
   */
  private static double lookup(int successes,
      int sampleSize,
      int populationSuccesses,
      int populationSize,
      Map<String, Double> cache) {

    String key = getKey(successes,
        sampleSize,
        populationSuccesses,
        populationSize);

    if (cache.containsKey(key)) {
      return cache.get(key);
    } else {
      return -1;
    }
  }

  /**
   * Store.
   *
   * @param p the p
   * @param successes the successes
   * @param sampleSize the sample size
   * @param populationSuccesses the population successes
   * @param populationSize the population size
   * @param cache the cache
   */
  private static void store(double p,
      int successes,
      int sampleSize,
      int populationSuccesses,
      int populationSize,
      Map<String, Double> cache) {

    String key = getKey(successes,
        sampleSize,
        populationSuccesses,
        populationSize);

    cache.put(key, p);
  }

  /**
   * Compute the hypergeometric PDF.
   *
   * @param successes the successes
   * @param sampleSize the sample size
   * @param populationSuccesses the population successes
   * @param populationSize the population size
   * @return the double
   */
  public final double pdf(int successes,
      int sampleSize,
      int populationSuccesses,
      int populationSize) {
    double pdf = lookup(successes,
        sampleSize,
        populationSuccesses,
        populationSize,
        mCachedPdf);

    if (pdf != -1) {
      return pdf;
    }

    HypergeometricDistribution d = new HypergeometricDistribution(null,
        populationSize, populationSuccesses, sampleSize);

    pdf = d.probability(successes);

    store(pdf,
        successes,
        sampleSize,
        populationSuccesses,
        populationSize,
        mCachedPdf);

    return pdf;
  }

  /**
   * Compute the hypergeometric CDF.
   *
   * @param successes the successes
   * @param sampleSize the sample size
   * @param populationSuccesses the population successes
   * @param populationSize the population size
   * @return the double
   */
  public final double cdf(int successes,
      int sampleSize,
      int populationSuccesses,
      int populationSize) {
    double cdf = lookup(successes,
        sampleSize,
        populationSuccesses,
        populationSize,
        mCachedCdf);

    if (cdf != -1) {
      return cdf;
    }

    HypergeometricDistribution d = new HypergeometricDistribution(null,
        populationSize, populationSuccesses, sampleSize);

    cdf = d.cumulativeProbability(successes);

    store(cdf,
        successes,
        sampleSize,
        populationSuccesses,
        populationSize,
        mCachedCdf);

    return cdf;
  }

  /**
   * Cdf two tail.
   *
   * @param successes the successes
   * @param sampleSize the sample size
   * @param populationSuccesses the population successes
   * @param populationSize the population size
   * @return the double
   */
  public final double cdfTwoTail(int successes,
      int sampleSize,
      int populationSuccesses,
      int populationSize) {
    double cdf = lookup(successes,
        sampleSize,
        populationSuccesses,
        populationSize,
        mTwoTailCdf);

    if (cdf != -1) {
      return cdf;
    }

    double cdf0 = pdf(successes,
        sampleSize,
        populationSuccesses,
        populationSize);

    cdf = 0;

    for (int i = 0; i <= sampleSize; ++i) {
      double c = pdf(i, sampleSize, populationSuccesses, populationSize);

      if (c <= cdf0) {
        // System.err.println("cdf " + c);
        cdf += c;
      }

    }

    cdf = Math.min(1.0, cdf);

    store(cdf,
        successes,
        sampleSize,
        populationSuccesses,
        populationSize,
        mTwoTailCdf);

    return cdf;
  }

  /**
   * The probability of seeing this number of successes or more (up to sample
   * size).
   *
   * @param successes the successes
   * @param sampleSize the sample size
   * @param populationSuccesses the population successes
   * @param populationSize the population size
   * @return the double
   */
  public final double cdfOneTail(int successes,
      int sampleSize,
      int populationSuccesses,
      int populationSize) {
    double cdf = lookup(successes,
        sampleSize,
        populationSuccesses,
        populationSize,
        mOneTailCdf);

    if (cdf != -1) {
      return cdf;
    }

    HypergeometricDistribution d = new HypergeometricDistribution(null,
        populationSize, populationSuccesses, sampleSize);

    cdf = d.upperCumulativeProbability(successes);

    store(cdf,
        successes,
        sampleSize,
        populationSuccesses,
        populationSize,
        mOneTailCdf);

    return cdf;
  }

  /**
   * The main method.
   *
   * @param args the arguments
   */
  public static void main(String[] args) {
    Hypergeometric g = new Hypergeometric();

    System.err.println(g.cdfOneTail(128, 250, 186, 500));

    HypergeometricDistribution d = new HypergeometricDistribution(500, 186,
        250);

    System.err.println(d.upperCumulativeProbability(128));
  }
}
