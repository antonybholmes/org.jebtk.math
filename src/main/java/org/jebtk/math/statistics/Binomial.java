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

import java.util.HashMap;
import java.util.Map;

import org.jebtk.core.text.TextUtils;

/**
 * Fast implementation of the hypergeometric function using the sum of logs.
 *
 * @author Antony Holmes
 *
 */
public class Binomial {

  /**
   * The member cached factorial.
   */
  private Map<Integer, Double> mCachedFactorial = new HashMap<Integer, Double>();

  /**
   * The member cached binomial.
   */
  private Map<String, Double> mCachedBinomial = new HashMap<String, Double>();

  /**
   * The member cached log binomial.
   */
  private Map<String, Double> mCachedLogBinomial = new HashMap<String, Double>();

  /**
   * Cache some log values to speed up execution.
   */
  public final void cache() {
    for (int i = 0; i < 10000; ++i) {
      logFactorial(i);

      // for (int j = 0; j < 10000; ++j) {
      // logBinomial(i, j);
      // }
    }
  }

  /**
   * Log gamma.
   *
   * @param n the n
   * @return the double
   */
  public final double logGamma(int n) {
    return logFactorial(n - 1);
  }

  /**
   * Gamma.
   *
   * @param n the n
   * @return the int
   */
  public final int gamma(int n) {
    return factorial(n - 1);
  }

  /**
   * Factorial.
   *
   * @param n the n
   * @return the int
   */
  public final int factorial(int n) {
    return (int) Math.exp(logFactorial(n));
  }

  /**
   * Return log n!.
   *
   * @param n the n
   * @return log n!
   */
  public double logFactorial(int n) {
    Double ret = mCachedFactorial.get(n);

    if (ret != null) {
      return ret;
    }

    double f = 0.0;

    for (int i = 1; i <= n; i++) {
      f += Math.log(i);
    }

    mCachedFactorial.put(n, f);

    return f;
  }

  /**
   * Return the binomial coefficient n choose k.
   *
   * @param n the n
   * @param k the k
   * @return the double
   */
  public final double binomial(int n, int k) {
    return binomial(n, k, getKey(n, k));
  }

  /**
   * Return the binomial coefficient n choose k.
   *
   * @param n the n
   * @param k the k
   * @param key the key
   * @return the double
   */
  public final double binomial(int n, int k, String key) {
    Double ret = mCachedBinomial.get(key);

    if (ret != null) {
      // System.err.println("cached pdf " + hkey + " " + ret);

      return ret;
    }

    double logBinomial = logBinomial(n, k);

    double binomial = Math.exp(logBinomial);

    // cachedBinomial.put(key, binomial);

    return binomial;
  }

  /**
   * Log binomial.
   *
   * @param n the n
   * @param k the k
   * @return the double
   */
  public final double logBinomial(int n, int k) {
    String key = getKey(n, k);

    Double ret = mCachedLogBinomial.get(key);

    if (ret != null) {
      return ret;
    }

    double binomial = logFactorial(n) - logFactorial(k) - logFactorial(n - k);

    mCachedLogBinomial.put(key, binomial);

    return binomial;
  }

  /**
   * Gets the key.
   *
   * @param n the n
   * @param k the k
   * @return the key
   */
  private static final String getKey(int n, int k) {
    return new StringBuilder().append(n).append(TextUtils.COLON_DELIMITER)
        .append(k).toString(); // k + ":" + N + ":" + m + ":" + n;

    // System.err.println(k + " " + N + " " + m + " " + n + " " + key);
  }

  /**
   * Clear the cached hypergeometric intermediates. The Object will aggressively
   * cache intermediate parts of calculations for reuse so a pdf/cdf calculation
   * is done at most once, however this may impact memory usage.
   */
  public void clear() {
    mCachedFactorial.clear();

    mCachedBinomial.clear();
    mCachedLogBinomial.clear();
  }
}
