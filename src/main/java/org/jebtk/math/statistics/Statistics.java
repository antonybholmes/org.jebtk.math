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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.jebtk.core.Indexed;
import org.jebtk.core.IndexedInt;
import org.jebtk.core.Mathematics;
import org.jebtk.core.collections.CollectionUtils;
import org.jebtk.core.collections.CountMap;
import org.jebtk.core.collections.DefaultTreeMap;

/**
 * Provides statistical functions for Java structures.
 *
 * @author Antony Holmes
 *
 */
public class Statistics {

  /**
   * Instantiates a new statistics.
   */
  private Statistics() {
    // do nothing
  }

  /**
   * Zscore.
   *
   * @param values the values
   * @return the double[]
   */
  public static final double[] zscore(double[] values) {
    double m = mean(values);
    double sd = popStdDev(values);

    return zscore(values, m, sd);
  }

  /**
   * Zscore.
   *
   * @param values the values
   * @param m the m
   * @param sd the sd
   * @return the double[]
   */
  public static final double[] zscore(double[] values, double m, double sd) {
    double[] zscores = new double[values.length];

    for (int i = 0; i < values.length; ++i) {
      zscores[i] = zscore(values[i], m, sd);
    }

    return zscores;
  }

  /**
   * Zscore.
   *
   * @param values the values
   * @param m the m
   * @param sd the sd
   * @return the list
   */
  public static final List<Double> zscore(List<Double> values,
      double m,
      double sd) {
    List<Double> zscores = new ArrayList<Double>(values.size());

    for (double v : values) {
      zscores.add(zscore(v, m, sd));
    }

    return zscores;
  }

  /**
   * Zscore.
   *
   * @param x the x
   * @param m the m
   * @param sd the sd
   * @return the double
   */
  public static final double zscore(double x, double m, double sd) {
    return (x - m) / sd;
  }

  /**
   * Returns the population standard deviation of a list of numbers.
   *
   * @param values the values
   * @return the double
   */
  public static final double popStdDev(List<Double> values) {
    return Math.sqrt(populationVariance(values));
  }

  /**
   * Returns the variance of a list of numbers with a given mean. This function
   * is designed for the case when you want the mean and standard deviation
   * since it does not require the mean to be recalculated. If the supplied mean
   * does not match the mean of the list of values, the returned value is
   * meaningless.
   *
   * @param values the values
   * @return the double
   */
  public static final double populationVariance(List<Double> values) {
    if (CollectionUtils.isNullOrEmpty(values)) {
      return 0;
    }

    double mean = mean(values);

    double variance = 0;
    int size = 0;

    double d;

    for (double v : values) {
      if (Mathematics.isValidNumber(v)) {
        // substract mean
        d = v - mean;

        // square
        d *= d;

        variance += d;
        ++size;
      }
    }

    if (size > 0) {
      return variance / size;
    } else {
      return 0;
    }
  }

  /**
   * Population standard deviation.
   *
   * @param values the values
   * @return the double
   */
  public static final double popStdDev(double[] values) {
    return popStdDev(values, 0, values.length - 1);
  }

  /**
   * Population standard deviation.
   *
   * @param values the values
   * @param startIndex the start index
   * @param endIndex the end index
   * @return the double
   */
  public static final double popStdDev(double[] values,
      int startIndex,
      int endIndex) {
    return Math.sqrt(populationVariance(values, startIndex, endIndex));
  }

  /**
   * Population variance.
   *
   * @param values the values
   * @return the double
   */
  public static final double populationVariance(double[] values) {
    return populationVariance(values, 0, values.length - 1);
  }

  /**
   * Calculates the population variance of an array.
   *
   * @param values the values
   * @param startIndex the start index
   * @param endIndex the end index
   * @return the double
   */
  public static final double populationVariance(double[] values,
      int startIndex,
      int endIndex) {
    double mean = mean(values, startIndex, endIndex);

    double variance = 0;

    double d;

    int size = 0;

    for (int i = startIndex; i <= endIndex; ++i) {
      double v = values[i];

      if (Mathematics.isValidNumber(v)) {
        // substract mean
        d = v - mean;

        // square
        d *= d;

        variance += d;
        ++size;
      }
    }

    variance /= size;

    return variance;
  }

  /**
   * Returns the standard deviation of a list of numbers.
   *
   * @param values the values
   * @return the double
   */
  public static final double sampleStandardDeviation(List<Double> values) {
    return sampleStandardDeviation(values, 0, values.size() - 1);
  }

  /**
   * Sample standard deviation.
   *
   * @param values the values
   * @param startIndex the start index
   * @param endIndex the end index
   * @return the double
   */
  public static final double sampleStandardDeviation(List<Double> values,
      int startIndex,
      int endIndex) {
    return Math.sqrt(sampleVariance(values, startIndex, endIndex));
  }

  /**
   * Sample variance.
   *
   * @param values the values
   * @return the double
   */
  public static final double sampleVariance(List<Double> values) {
    return sampleVariance(values, 0, values.size() - 1);
  }

  /**
   * Returns the variance of a list of numbers with a given mean. This function
   * is designed for the case when you want the mean and standard deviation
   * since it does not require the mean to be recalculated. If the supplied mean
   * does not match the mean of the list of values, the returned value is
   * meaningless.
   *
   * @param values the values
   * @param startIndex the start index
   * @param endIndex the end index
   * @return the double
   */
  public static final double sampleVariance(List<Double> values,
      int startIndex,
      int endIndex) {
    double mean = mean(values, startIndex, endIndex);

    double variance = 0;

    double d;

    double size = endIndex - startIndex + 1;

    // System.err.println(size + " " + startIndex + " " + endIndex);

    for (int i = startIndex; i <= endIndex; ++i) {
      // substract mean
      d = values.get(i) - mean;

      // square
      d *= d;

      variance += d;
    }

    variance /= (size - 1);

    return variance;
  }

  /**
   * Sample standard deviation.
   *
   * @param values the values
   * @return the double
   */
  public static final double sampleStdDev(double[] values) {
    return sampleStdDev(values, 0, values.length - 1);
  }

  /**
   * Sample standard deviation.
   *
   * @param values the values
   * @param startIndex the start index
   * @param endIndex the end index
   * @return the double
   */
  public static final double sampleStdDev(double[] values,
      int startIndex,
      int endIndex) {
    return Math.sqrt(sampleVariance(values, startIndex, endIndex));
  }

  /**
   * Sample variance.
   *
   * @param values the values
   * @return the double
   */
  public static final double sampleVariance(double[] values) {
    return sampleVariance(values, 0, values.length - 1);
  }

  /**
   * Returns the variance of a list of numbers with a given mean. This function
   * is designed for the case when you want the mean and standard deviation
   * since it does not require the mean to be recalculated. If the supplied mean
   * does not match the mean of the list of values, the returned value is
   * meaningless.
   *
   * @param values the values
   * @param startIndex the start index
   * @param endIndex the end index
   * @return the double
   */
  public static final double sampleVariance(double[] values,
      int startIndex,
      int endIndex) {
    double mean = mean(values, startIndex, endIndex);

    double variance = 0;

    double d;

    double size = endIndex - startIndex + 1;

    for (int i = startIndex; i <= endIndex; ++i) {
      // substract mean
      d = values[i] - mean;

      // square
      d *= d;

      variance += d;
    }

    variance /= (size - 1);

    return variance;
  }

  /**
   * Calculate the mean of a set of numbers, returns 0 if the list is empty.
   *
   * @param values the values
   * @return the double
   */
  public static final double mean(List<Double> values) {
    return mean(values, 0, values.size() - 1);
  }

  /**
   * Returns the mean of a list of numbers.
   *
   * @param values the values
   * @param startIndex the start index
   * @param endIndex the end index
   * @return the double
   */
  public static final double mean(List<Double> values,
      int startIndex,
      int endIndex) {
    if (CollectionUtils.isNullOrEmpty(values)) {
      return 0;
    }

    double mean = 0;
    int size = 0;

    for (int i = startIndex; i <= endIndex; ++i) {
      double v = values.get(i);

      if (Mathematics.isValidNumber(v)) {
        mean += v;
        ++size;
      }
    }

    if (size > 0) {
      return mean / size;
    } else {
      return 0;
    }
  }

  /**
   * Mean.
   *
   * @param values the values
   * @return the double
   */
  public static final double mean(double[] values) {
    return mean(values, 0, values.length);
  }

  /**
   * Mean.
   *
   * @param values the values
   * @param startIndex the start index
   * @param endIndex the end index
   * @return the double
   */
  public static final double mean(double[] values, int startIndex, int l) {
    double mean = 0;
    int size = 0;

    for (int i = 0; i < l; ++i) {
      double v = values[startIndex++];

      if (Mathematics.isValidNumber(v)) {
        mean += v;
        ++size;
      }
    }

    return mean / size;
  }
  
  public static final double mean(int[] values, int start, int l) {
    double mean = 0;
    
    for (int i = 0; i < l; ++i) {
      mean += values[start++];
    }

    return mean / l;
  }

  /**
   * Returns the mode of a list of numbers.
   *
   * @param values the values
   * @return the list
   */
  public static final List<Double> mode(List<Double> values) {
    int max = -1;
    int count;

    Map<Double, Integer> occurences = new HashMap<Double, Integer>();

    for (double v : values) {
      if (!occurences.containsKey(v)) {
        occurences.put(v, 1);
      } else {
        occurences.put(v, occurences.get(v) + 1);
      }

      count = occurences.get(v);

      if (count > max) {
        max = count;
      }
    }

    List<Double> modes = new ArrayList<Double>();

    for (Entry<Double, Integer> entry : occurences.entrySet()) {
      if (entry.getValue() == max) {
        modes.add(entry.getKey());
      }
    }

    return modes;
  }

  /**
   * Returns the median of a list of numbers. Values must be sorted prior to
   * running this function.
   *
   * @param values the values
   * @return the double
   */
  public static final double median(final List<Double> values) {
    if (CollectionUtils.isNullOrEmpty(values)) {
      return 0;
    }

    List<Double> sv = CollectionUtils.sort(values);

    if (sv.size() % 2 == 0) {
      // the mid point between the two middle values in the case
      // of a list with an even number of elements
      // subtract 1 since m represents an index in a zero based
      // array whilst m assumes 1 based

      // m is the index of the lower index
      int m = sv.size() / 2 - 1;

      return (sv.get(m) + sv.get(m + 1)) / 2.0;
    } else {
      // odd size so middle value is exact

      return sv.get(sv.size() / 2);
    }
  }

  /**
   * Median.
   *
   * @param values the values
   * @return the double
   */
  public static double median(final double[] values) {
    return percentile(values, 50);
  }

  /**
   * Returns the mode of a series of numbers.
   * 
   * @param values
   * @return
   */
  public static double mode(final double[] values) {
    CountMap<Double> countMap = CountMap.create();

    for (double v : values) {
      countMap.put(v);
    }

    return countMap.getMaxK();
  }

  /**
   * Calculates the percentile in a ordered list using the third method
   * recommended by NIST and as used in Apache Math
   * 
   * https://en.wikipedia.org/wiki/Percentile
   *
   * @param values the values
   * @param percentile the percentile
   * @return the double
   */
  public static double percentile(final double[] values, int percentile) {
    int n = values.length;

    switch (n) {
    case 0:
      return 0;
    case 1:
      return values[0];
    default:
      // int p = Mathematics.bound(percentile, 1, 100);

      double rank = percentile * (n + 1) / 100.0;

      if (rank < 1) {
        return values[0];
      } else if (rank >= n) {
        return values[n - 1];
      } else {
        int fp = (int) rank;
        double d = rank - fp;

        int i = fp - 1;

        double lower = values[i];
        double upper = values[i + 1];

        // System.err.println(rank + " " + fp + " " + d + " " + lower + " " +
        // upper + " " + Arrays.toString(mValues));

        return lower + d * (upper - lower);
      }
    }

    /*
     * Let n be the length of the (sorted) array and 0 < p <= 100 be the desired
     * percentile. If n = 1 return the unique array element (regardless of the
     * value of p); otherwise Compute the estimated percentile position pos = p
     * * (n + 1) / 100 and the difference, d between pos and floor(pos) (i.e.
     * the fractional part of pos). If pos < 1 return the smallest element in
     * the array. Else if pos >= n return the largest element in the array. Else
     * let lower be the element in position floor(pos) in the array and let
     * upper be the next element in the array. Return lower + d * (upper -
     * lower)
     */
  }

  /**
   * Return the first quartile of a data set.
   *
   * @param values the values
   * @return the double
   */
  public static final double q1(final List<Double> values) {
    return median(CollectionUtils.head(values, values.size() / 2));

    /*
     * if (values.size() % 2 == 0) { return median(CollectionUtils.head(values,
     * values.size() / 2)); } else { return median(CollectionUtils.head(values,
     * values.size() / 2 + 1)); }
     */
  }

  /**
   * Returns the third quartile (upper 25% of a sorted set of numbers). Values
   * must be sorted prior to running this function.
   *
   * @param values the values
   * @return the double
   */
  public static final double q3(final List<Double> values) {
    return median(CollectionUtils.end(values, values.size() / 2));

    // StatUtils.mean(values.);

    /*
     * if (values.size() % 2 == 0) { return median(CollectionUtils.end(values,
     * values.size() / 2)); } else { return median(CollectionUtils.end(values,
     * values.size() / 2 + 1)); }
     */
  }

  /**
   * Iqr.
   *
   * @param values the values
   * @return the double
   */
  public static double iqr(final List<Double> values) {
    List<Double> sv = CollectionUtils.sort(values);

    return q3(sv) - q1(sv);
  }

  /**
   * Returns the quartile coefficient of dispersion, i.e. the IQR divided by the
   *
   * @param values the values
   * @return the double
   */
  public static double quartileCoeffDisp(final List<Double> values) {
    List<Double> sv = CollectionUtils.sort(values);

    double q1 = q1(sv);
    double q3 = q3(sv);

    double d = (q3 + q1);

    if (d != 0) {
      return (q3 - q1) / d;
    } else {
      return 0;
    }
  }

  /**
   * Quantile.
   *
   * @param values the values
   * @param q the q
   * @return the double
   */
  public static final double quantile(final List<Double> values, int q) {
    if (CollectionUtils.isNullOrEmpty(values)) {
      return Double.NaN;
    }

    if (values.size() == 1) {
      return values.get(0);
    }

    if (values.size() == 2) {
      return (values.get(0) + values.get(1)) / 2.0;
    }

    // for calculating the median we need a shallow copy of the array because
    // otherwise
    // collections will reorder it and so the probe values will be wrong
    List<Double> sv = CollectionUtils.sort(values);

    if (sv.size() % 2 == 0) {
      // the mid point between the two middle values in the case
      // of a list with an even number of elements
      // subtract 1 since m represents an index in a zero based
      // array whilst m assumes 1 based

      // m is the index of the lower index
      int m = sv.size() * q / 100 - 1;

      return (sv.get(m) + sv.get(m + 1)) / 2.0;
    } else {
      // odd size so middle value is exact

      return sv.get(values.size() * q / 100);
    }
  }

  /**
   * Compute the standard deviation using medians.
   *
   * @param dist the dist
   * @return the double
   */
  public static double madStandardDeviation(List<Double> dist) {
    return 1.4826 * mad(dist);
  }

  /**
   * Compute the median absolute deviation of a set of values.
   *
   * @param values the values
   * @return the double
   */
  public static double mad(List<Double> values) {
    List<Double> a = new ArrayList<Double>(values.size());

    double m = median(values);

    for (double value : values) {
      a.add(Math.abs(value - m));
    }

    return median(a);
  }

  /**
   * Mad std dev.
   *
   * @param dist the dist
   * @return the double
   */
  public static double madStdDev(final double[] dist) {
    return 1.4826 * mad(dist);
  }

  /**
   * Compute the median absolute deviation of a set of values.
   *
   * @param values the values
   * @return the double
   */
  public static double mad(final double[] values) {
    int n = values.length;

    double[] a = new double[n];

    double m = median(values);

    for (int i = 0; i < n; ++i) {
      a[i] = Math.abs(values[i] - m);
    }

    return median(a);
  }

  /**
   * Fdr.
   *
   * @param pvalues the pvalues
   * @return the list
   */
  public static List<Double> fdr(List<Double> pvalues) {
    return fdr(pvalues, FDRType.BENJAMINI_HOCHBERG);
  }

  /**
   * Returns the false discovery rate adjusted q-values for a set of p-values.
   *
   * @param pvalues the pvalues
   * @param type the type
   * @return the list
   */
  public static List<Double> fdr(final List<Double> pvalues, FDRType type) {
    switch (type) {
    case BENJAMINI_HOCHBERG:
      return benjaminiHochbergCorrection(pvalues);
    case BONFERRONI:
      return bonferroniCorrection(pvalues);
    default:
      return pvalues;
    }
  }

  /**
   * Fdr.
   *
   * @param pvalues the pvalues
   * @param type the type
   * @return the double[]
   */
  public static double[] fdr(final double[] pvalues, FDRType type) {
    switch (type) {
    case BENJAMINI_HOCHBERG:
      return benjaminiHochbergCorrection(pvalues);
    case BONFERRONI:
      return bonferroniCorrection(pvalues);
    default:
      return pvalues;
    }
  }

  /**
   * Corrects p-values using the Bonferroni method.
   *
   * @param pvalues the pvalues
   * @return the list
   */
  public static List<Double> bonferroniCorrection(List<Double> pvalues) {
    int n = pvalues.size();

    List<Double> ret = new ArrayList<Double>(n);

    for (double value : pvalues) {
      ret.add(value * n);
    }

    return ret;
  }

  /**
   * Bonferroni correction.
   *
   * @param pvalues the pvalues
   * @return the double[]
   */
  public static double[] bonferroniCorrection(final double[] pvalues) {
    int n = pvalues.length;

    double[] ret = new double[pvalues.length];

    for (int i = 0; i < n; ++i) {
      ret[i] = pvalues[i] * n;
    }

    return ret;
  }

  /**
   * Returns the p-values corrected and thresholded so they are less or equal to
   * than alpha. ValueIndex contains the p value plus the index the the input
   * list corresponding to the pvalues.
   *
   * @param pvalues the pvalues
   * @param alpha the alpha
   * @return the list
   */
  public static List<Indexed<Integer, Double>> bonferroniCorrection(
      List<Double> pvalues,
      double alpha) {
    List<Double> corrected = bonferroniCorrection(pvalues);

    return threshold(corrected, alpha);
  }

  /**
   * Benjamini-Hochberg Correction of p-values. Returns a q-value list equal in
   * length to the p-values.
   *
   * @param pvalues the pvalues
   * @return the list
   */
  public static List<Double> benjaminiHochbergCorrection(List<Double> pvalues) {
    // Sort the values and adjust to get the fdf
    // see http://en.wikipedia.org/wiki/False_discovery_rate
    //

    // First index the list so we can keep track of which p-value
    // gets which ranking
    List<Indexed<Integer, Double>> indexedP = IndexedInt.index(pvalues);

    // Now they are in rank order
    Collections.sort(indexedP);

    int n = pvalues.size();
    double validN = countValidNumbers(pvalues);

    List<Double> fdrs = new ArrayList<Double>(pvalues.size());

    double fdr;

    int rank = 1;

    for (int i = 0; i < n; ++i) {
      double v = indexedP.get(i).getValue();

      if (Mathematics.isValidNumber(v)) {
        fdr = v * validN / rank;

        // Bound it
        // fdr = Math.max(0, Math.min(fdr, 1.0));

        // if the fdr is less than the previously ranked
        // entry, set it to the higher value. The FDR
        // cannot alter the ranking of each entity, only
        // control whether they are classified as significant
        // or not

        // if (i > 0) {
        // fdr = Math.max(fdr, fdrs.get(i - 1));
        // }

        // The q-value is limited to be between
        // 0 and the q-value of the element one
        // rank above this one
        // ret.add(Math.min(ret.get(i - 1), fdr));

        // System.err.println("p " + indexedP.get(i).getValue() + " " + fdr + "
        // " + rank + " " + n + " r " + ((double)n / rank));

        // We bound the p-value to reprevent rounding
        // errors
        fdrs.add(Mathematics.bound(fdr, 0, 1.0));

        ++rank;
      } else {
        fdrs.add(v);
      }
    }

    // Now lets correct the fdrs using the cumulative minimum to ensure
    // that fdrs only increase

    List<Double> fdrC = cumMinRev(fdrs);

    // Now create a return list that maps the fdrs
    // back to their original index in the list

    List<Double> ret = Mathematics.zeros(n);

    for (int i = 0; i < n; ++i) {
      ret.set(indexedP.get(i).getIndex(), fdrC.get(i));
    }

    // System.err.println("p " + sorted.toString());
    // System.err.println("bh " + ret.toString());

    return ret;
  }

  /**
   * Benjamini hochberg correction.
   *
   * @param pvalues the pvalues
   * @return the double[]
   */
  public static double[] benjaminiHochbergCorrection(final double[] pvalues) {
    // Sort the values and adjust to get the fdf
    // see http://en.wikipedia.org/wiki/False_discovery_rate
    //

    // First index the list so we can keep track of which p-value
    // gets which ranking
    List<Indexed<Integer, Double>> indexedP = IndexedInt.index(pvalues);

    // Now they are in rank order
    Collections.sort(indexedP);

    int n = pvalues.length;
    double validN = countValidNumbers(pvalues);

    double[] fdrs = new double[n];

    double fdr;

    int rank = 1;

    for (int i = 0; i < n; ++i) {
      double v = indexedP.get(i).getValue();

      if (Mathematics.isValidNumber(v)) {
        fdr = v * validN / rank;

        // We bound the p-value to reprevent rounding
        // errors
        fdrs[i] = Mathematics.bound(fdr, 0, 1.0);

        ++rank;
      } else {
        fdrs[i] = v;
      }
    }

    // Now lets correct the fdrs using the cumulative minimum to ensure
    // that fdrs only increase

    double[] fdrC = cumMinRev(fdrs);

    // Now create a return list that maps the fdrs
    // back to their original index in the list

    double[] ret = Mathematics.zerosArray(n);

    for (int i = 0; i < n; ++i) {
      ret[indexedP.get(i).getIndex()] = fdrC[i];
    }

    // System.err.println("p " + sorted.toString());
    // System.err.println("bh " + ret.toString());

    return ret;
  }

  /**
   * Could the number of valid numbers in a list (i.e. exclude NaNs and
   * infinites).
   *
   * @param values the values
   * @return the int
   */
  public static int countValidNumbers(List<Double> values) {
    int ret = 0;

    for (double v : values) {
      if (Mathematics.isValidNumber(v)) {
        ++ret;
      }
    }

    return ret;
  }

  /**
   * Count valid numbers.
   *
   * @param values the values
   * @return the int
   */
  public static int countValidNumbers(final double[] values) {
    int ret = 0;

    for (double v : values) {
      if (Mathematics.isValidNumber(v)) {
        ++ret;
      }
    }

    return ret;
  }

  /**
   * Return the minimum cumulative values of an array.
   *
   * @param values the values
   * @return the list
   */
  public static List<Double> cumMinRev(List<Double> values) {
    List<Double> ret = Mathematics.zeros(values.size());

    int start = values.size() - 1;

    double min = Double.MAX_VALUE;
    double v;

    // Loop from the end until we find the first non NaN number.

    while (start >= 0) {
      v = values.get(start);

      ret.set(start, v);

      --start;

      if (Mathematics.isValidNumber(v)) {
        min = v;
        break;
      }
    }

    // System.err.println("cum min " + min);

    // Use the running min to set groups of values, skip NaNs
    for (int i = start; i >= 0; --i) {
      v = values.get(i);

      // System.err.println("v " + i + " " + v + " " + min);

      if (Mathematics.isValidNumber(v)) {
        min = Math.min(min, values.get(i));
        ret.set(i, min);
      } else {
        ret.set(i, v);
      }
    }

    return ret;
  }

  /**
   * Cum min rev.
   *
   * @param values the values
   * @return the double[]
   */
  public static double[] cumMinRev(final double[] values) {
    double[] ret = Mathematics.zerosArray(values.length);

    int start = values.length - 1;

    double min = Double.MAX_VALUE;
    double v;

    // Loop from the end until we find the first non NaN number.

    while (start >= 0) {
      v = values[start];

      ret[start] = v;

      --start;

      if (Mathematics.isValidNumber(v)) {
        min = v;
        break;
      }
    }

    // Use the running min to set groups of values, skip NaNs
    for (int i = start; i >= 0; --i) {
      v = values[i];

      if (Mathematics.isValidNumber(v)) {
        min = Math.min(min, values[i]);
        ret[i] = min;
      } else {
        ret[i] = v;
      }
    }

    return ret;
  }

  /**
   * Returns the p-values corrected and thresholded so they are less or equal to
   * than alpha. ValueIndex contains the p value plus the index the the input
   * list corresponding to the pvalue
   *
   * @param pvalues the pvalues
   * @param alpha the alpha
   * @return the list
   */
  public static List<Indexed<Integer, Double>> benjaminiHochbergCorrection(
      List<Double> pvalues,
      double alpha) {
    List<Double> corrected = benjaminiHochbergCorrection(pvalues);

    return threshold(corrected, alpha);
  }

  /**
   * Returns the p-values corrected and thresholded so they are less or equal to
   * than alpha. ValueIndex contains the p value plus the index the the input
   * list corresponding to the pvalue
   *
   * @param values the values
   * @param max the max
   * @return the list
   */
  public static List<Indexed<Integer, Double>> threshold(
      final List<Double> values,
      double max) {
    List<Indexed<Integer, Double>> indexed = IndexedInt.index(values);

    List<Indexed<Integer, Double>> ret = new ArrayList<Indexed<Integer, Double>>(
        values.size());

    for (Indexed<Integer, Double> item : indexed) {
      double v = item.getValue();

      if (Mathematics.isInvalidNumber(v) || v > max) {
        System.err.println("in " + v + " " + item.getIndex());
        continue;
      }

      ret.add(item);
    }

    return ret;
  }

  /**
   * Keep values less or equal to some maximum.
   *
   * @param values the values
   * @param max the max
   * @return the list
   */
  public static List<Indexed<Integer, Double>> threshold(final double[] values,
      double max) {
    List<Indexed<Integer, Double>> indexed = IndexedInt.index(values);

    List<Indexed<Integer, Double>> ret = new ArrayList<Indexed<Integer, Double>>(
        values.length);

    for (Indexed<Integer, Double> item : indexed) {
      double v = item.getValue();

      if (Mathematics.isValidNumber(v) && v <= max) {
        ret.add(item);
      }
    }

    return ret;
  }

  /**
   * Out of range.
   *
   * @param values the values
   * @param min the min
   * @param max the max
   * @return the list
   */
  public static List<Indexed<Integer, Double>> outOfRange(double[] values,
      double min,
      double max) {
    List<Indexed<Integer, Double>> indexed = IndexedInt.index(values);

    List<Indexed<Integer, Double>> ret = new ArrayList<Indexed<Integer, Double>>();

    for (Indexed<Integer, Double> item : indexed) {
      if (item.getValue() <= min || item.getValue() >= max) {
        ret.add(item);
      }
    }

    return ret;
  }

  /**
   * Returns the rank of numbers with tied ranks.
   *
   * @param values the values
   * @return A list ranked values, or null if the values list is null or empty.
   */
  public static List<Double> tiedRank(List<Double> values) {
    if (values == null || values.size() == 0) {
      return null;
    }

    CountMap<Double> countMap = new CountMap<Double>();

    // Count the values
    countMap.inc(values);

    List<Indexed<Integer, Double>> sorted = IndexedInt.index(values);

    // Items are now rank sorted
    Collections.sort(sorted);

    double rank = 1.0;

    double currentValue = Double.MIN_VALUE;

    double v;

    Map<Integer, Double> ranks = new HashMap<Integer, Double>();

    // ranks.put(sorted.get(0).getIndex(), rank /
    // countMap.get(sorted.get(0).getValue()));

    for (int i = 0; i < sorted.size(); ++i) {
      v = sorted.get(i).getValue();

      if (v > currentValue) {
        // We have moved into a new block so calculate the fractional
        // rank for this block

        // Since we are using zero based indices, the rank is the
        // number of items in the group (since for each index we
        // must add 1 to get the one based index) plus the zero
        // based ranks of each group member.

        rank = 1; // countMap.get(v);

        for (int j = 0; j < countMap.get(v); ++j) {
          rank += i + j;
        }

        rank /= countMap.get(v);

        // increase the rank by the number of previous items
        // rank += i; //countMap.get(sorted.get(i - 1).getValue());
      }

      currentValue = v;

      ranks.put(sorted.get(i).getIndex(), rank);
    }

    List<Double> ret = new ArrayList<Double>(values.size());

    for (int i = 0; i < values.size(); ++i) {
      ret.add(ranks.get(i));
    }

    return ret;
  }

  /**
   * Returns the ratio of non-zero values to the number of values.
   *
   * @param scores the scores
   * @return the double
   */
  public static double pNonZero(List<Double> scores) {
    double ret = 0;

    for (double score : scores) {
      if (score != 0) {
        ++ret;
      }
    }

    ret /= (double) scores.size();

    return ret;
  }

  /**
   * Returns the -log10(p). Useful for converting very small p values to more
   * manageable numbers.
   *
   * @param p the p
   * @return the double
   */
  public static double minusLog10P(double p) {
    return -Mathematics.log10(p);
  }

  /**
   * Histogram.
   *
   * @param values the values
   * @param binWidth the bin width
   * @return the list
   */
  public static List<HistBin> histogram(List<Double> values, double binWidth) {
    return histogram(values,
        Mathematics.min(values),
        Mathematics.max(values),
        binWidth);
  }

  /**
   * Create a histogram of values between the start and end. This can be used to
   * assess the counts of values in a given range.
   *
   * @param values the values
   * @param start the start
   * @param end the end
   * @param binWidth the bin width
   * @return the list
   */
  public static List<HistBin> histogram(List<Double> values,
      double start,
      double end,
      double binWidth) {
    int n = (int) ((end - start) / binWidth);
    int ni = n - 1;
    
    // If there are 3 bins, there are two divisions between them. The distance
    // between divisions allows us to normalize which bin we are in. e.g.
    // 3 bins => w = 1 / (3 - 1) = 0.5 so that < 0.5 = b1, < 1 = b2 and < 2 = b3
    double w = (end - start) / ni;

    int[] bins = Mathematics.zerosIntArray(n);
    
    int b;
    
    for (Double v : values) {
      // Skip values outside of range.
      if (v < start || v > end) {
        continue;
      }

      b = (int)((v - start) / w); //(int)((v - start) / binWidth);

      //System.err.println("hist " + v + " " + b + " " + start + " " + binWidth);
      
      bins[b] += 1;
    }

    List<HistBin> histBins = new ArrayList<HistBin>(n);

    for (int c : bins) {
      histBins.add(new HistBin(start, binWidth, c));

      //System.err.println("hhh " + start + " " + c);

      start += binWidth;
    }

    return histBins;
  }

  /**
   * Histogram.
   *
   * @param values the values
   * @param binWidth the bin width
   * @return the hist bin[]
   */
  public static HistBin[] histogram(double[] values, double binWidth) {
    return histogram(values,
        Mathematics.min(values),
        Mathematics.max(values),
        binWidth);
  }

  /**
   * Create a histogram of values between the start and end. This can be used to
   * assess the counts of values in a given range.
   *
   * @param values the values
   * @param start the start
   * @param end the end
   * @param binWidth the bin width
   * @return the list
   */
  public static HistBin[] histogram(double[] values,
      double start,
      double end,
      double binWidth) {
    int b = (int) ((end - start) / binWidth) + 1;

    int[] bins = Mathematics.zerosIntArray(b);

    for (Double v : values) {
      // Skip values outside of range.
      if (v < start || v > end) {
        continue;
      }

      b = (int) Math.ceil((v - start) / binWidth) - 1;

      if (b >= 0) {
        ++bins[b]; // bins.set(b, bins.get(b) + 1);
      }
    }

    HistBin[] histBins = new HistBin[bins.length];

    for (int i = 0; i < bins.length; ++i) {
      int c = bins[i];

      histBins[i] = new HistBin(start, binWidth, c);

      // System.err.println(start + " " + c);

      start += binWidth;
    }

    return histBins;
  }

  /**
   * Takes a list of integers and returns an ordered list of counts of the
   * integers in bins.
   *
   * @param values the values
   * @param binSize the bin size
   * @return the list
   */
  public static List<Integer> binCounts(final List<Integer> values,
      int binSize) {
    Map<Integer, Integer> map = DefaultTreeMap.create(0);

    for (int read : values) {
      int bin = read / binSize;

      map.put(bin, map.get(bin) + 1);
    }

    List<Integer> ret = new ArrayList<Integer>(map.size());

    for (int bin : map.keySet()) {
      ret.add(map.get(bin));
    }

    return ret;
  }

  /**
   * Bin values between a given start and end creating empty bins to fill gaps
   * as necessary.
   *
   * @param values the values
   * @param start the start
   * @param end the end
   * @param binSize the bin size
   * @return the list
   */
  public static List<Integer> binCounts(final List<Integer> values,
      int start,
      int end,
      int binSize) {
    int s = start / binSize;
    int e = end / binSize;
    int l = e - s + 1;

    System.err.println(start + " " + end + " " + s + " " + e + " " + l);

    Map<Integer, Integer> map = DefaultTreeMap.create(0);

    for (int rs : values) {
      int bin = (rs - start) / binSize;

      System.err.println(rs + " " + (rs - start) + " " + bin);

      if (bin >= 0) {
        map.put(bin, map.get(bin) + 1);
      }
    }

    List<Integer> ret = Mathematics.intZeros(l);

    for (int bin : map.keySet()) {
      ret.set(bin, map.get(bin));
    }

    return ret;
  }

  public static double geometricMean(double[] values) {
    return geometricMean(values, 0, values.length);
  }

  /**
   * Calculate the geometric mean of a number of values in an array at the
   * specified offset.
   * 
   * @param values
   * @param offset
   * @param l
   * @return
   */
  public static double geometricMean(double[] values, int offset, int l) {

    int c = 0;
    double sum = 1;
    double v;
    int s = offset;

    for (int i = 0; i < l; ++i) {
      v = values[s++];

      if (v > 0) {
        sum *= v;
        ++c;
      }
    }

    return Mathematics.nthRoot(sum, c);
  }

  public static double correlation(double[] v1, double[] v2) {
    return new PearsonsCorrelation().correlation(v1, v2);
  }

  public static double spearmanCorrelation(double[] v1, double[] v2) {
    return new SpearmansCorrelation().correlation(v1, v2);
  }
}
