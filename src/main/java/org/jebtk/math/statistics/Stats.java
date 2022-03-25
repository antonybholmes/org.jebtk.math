/**
 * Copyright 2017 Antony Holmes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jebtk.math.statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.jebtk.core.Indexed;
import org.jebtk.core.IndexedInt;
import org.jebtk.core.Mathematics;
import org.jebtk.core.collections.CollectionUtils;
import org.jebtk.core.collections.CountMap;
import org.jebtk.core.sys.SysUtils;

/**
 * In memory stats calculations on an array. The array is guaranteed to be
 * sorted.
 * 
 * @author Antony Holmes
 *
 */
public class Stats {

  /** The m values. */
  public final double[] mData;

  /** The m N. */
  private final int mN;

  /** The m EI. */
  private final int mEI;

  /**
   * Instantiates a new stats.
   *
   * @param values the values
   */
  public Stats(List<Double> values) {
    this(CollectionUtils.toArray(values));
  }

  /**
   * Instantiates a new stats.
   *
   * @param values the values
   */
  public Stats(double[] values) {
    this(values, 0, values.length);
  }

  public Stats(double[] values, int s, int l) {
    mData = new double[l];

    System.arraycopy(values, s, mData, 0, l);

    // Ensure array is sorted for stats that assumed ordered data
    Arrays.sort(mData);

    mN = mData.length;
    mEI = mN - 1;
  }

  public Stats(double[] values, int s, int skip, int l) {
    mData = new double[l];

    SysUtils.arraycopy(values, s, skip, mData, 0, l);

    // Ensure array is sorted for stats that assumed ordered data
    Arrays.sort(mData);

    mN = mData.length;
    mEI = mN - 1;
  }

  /**
   * Returns the inter-quartile range of the values.
   *
   * @return the double
   */
  public double iqr() {
    return q3() - q1();
  }

  /**
   * Q 1.
   *
   * @return the double
   */
  private double q1() {
    return percentile(25);
  }

  /**
   * Q 3.
   *
   * @return the double
   */
  private double q3() {
    return percentile(75);
  }

  /**
   * Median.
   *
   * @return the double
   */
  public double median() {
    return percentile(50);
  }

  /**
   * Calculates the percentile in a ordered list using the third method
   * recommended by NIST and as used in Apache Math
   * 
   * https://en.wikipedia.org/wiki/Percentile
   *
   * @param percentile the percentile
   * @return the double
   */
  public double percentile(int percentile) {
    if (mN == 1) {
      return mData[0];
    }

    // int p = Mathematics.bound(percentile, 1, 100);

    double rank = percentile * (mN + 1) / 100.0;

    if (rank < 1) {
      return mData[0];
    } else if (rank >= mN) {
      return mData[mEI];
    } else {
      int fp = (int) rank;
      double d = rank - fp;

      int i = fp - 1;

      double lower = mData[i];
      double upper = mData[i + 1];

      // System.err.println(rank + " " + fp + " " + d + " " + lower + " " +
      // upper + " " + Arrays.toString(mValues));

      return lower + d * (upper - lower);
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
   * Quart coeff disp.
   *
   * @return the double
   */
  public double quartCoeffDisp() {
    double q1 = q1();
    double q3 = q3();

    double d = (q3 + q1);

    if (d != 0) {
      return (q3 - q1) / d;
    } else {
      return 0;
    }
  }

  /**
   * Returns the sum of the numbers.
   *
   * @return the double
   */
  public double sum() {
    double sum = 0;

    for (int i = 0; i < mN; ++i) {
      sum += mData[i];
    }

    return sum;
  }

  /**
   * Mean.
   *
   * @return the double
   */
  public double mean() {
    return sum() / mN;
  }

  public double geometricMean() {
    int c = 0;
    double sum = 1;
    double v;

    for (int i = 0; i < mN; ++i) {
      v = mData[i];

      if (v > 0) {
        sum *= v;
        ++c;
      }
    }

    return Mathematics.nthRoot(sum, c);
  }

  /**
   * Pop var.
   *
   * @return the double
   */
  public double popVar() {
    double mean = mean();

    double variance = 0;

    double d;

    for (int i = 0; i < mN; ++i) {
      double v = mData[i];

      d = v - mean;

      // square
      d *= d;

      variance += d;
    }

    variance /= mN;

    return variance;
  }

  /**
   * Pop std dev.
   *
   * @return the double
   */
  public double popStdDev() {
    return Math.sqrt(popVar());
  }

  /**
   * Sample var.
   *
   * @return the double
   */
  public double sampleVar() {
    double mean = mean();

    double variance = 0;

    double d;

    for (int i = 0; i < mN; ++i) {
      double v = mData[i];

      d = v - mean;

      // square
      d *= d;

      variance += d;
    }

    variance /= (mN - 1);

    return variance;
  }

  /**
   * Sample std dev.
   *
   * @return the double
   */
  public double sampleStdDev() {
    return Math.sqrt(sampleVar());
  }

  /**
   * Returns the modes of a the data set (the values that occur most
   * frequently).
   *
   * @return the list
   */
  public List<Double> mode() {
    CountMap<Double> occurences = CountMap.create();

    for (double v : mData) {
      occurences.put(v);
    }

    List<Double> modes = new ArrayList<Double>();

    for (Entry<Double, Integer> entry : occurences.entrySet()) {
      if (entry.getValue() == occurences.getMaxC()) {
        modes.add(entry.getKey());
      }
    }

    return modes;
  }

  /**
   * Benjamini hochberg.
   *
   * @return the double[]
   */
  public double[] benjaminiHochberg() {

    // Sort the values and adjust to get the fdf
    // see http://en.wikipedia.org/wiki/False_discovery_rate
    //

    // First index the list so we can keep track of which p-value
    // gets which ranking
    List<Indexed<Integer, Double>> indexedP = IndexedInt.index(mData);

    double[] fdrs = new double[mN];

    double fdr;

    int rank = 1;

    for (int i = 0; i < mN; ++i) {
      double v = indexedP.get(i).getValue();

      fdr = v * ((double) mN / rank);

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

      // System.err.println("p " + indexedP.get(i).getValue() + " " + fdr + " "
      // + rank + " " + n + " r " + ((double)n / rank));

      // We bound the p-value to reprevent rounding
      // errors
      fdrs[i] = Mathematics.bound(fdr, 0, 1.0);

      ++rank;
    }

    // Now lets correct the fdrs using the cumulative minimum to ensure
    // that fdrs only increase

    double[] fdrC = Statistics.cumMinRev(fdrs);

    // Now create a return list that maps the fdrs
    // back to their original index in the list

    double[] ret = Mathematics.zerosArray(mN);

    for (int i = 0; i < mN; ++i) {
      ret[indexedP.get(i).getIndex()] = fdrC[i];
    }

    // System.err.println("p " + sorted.toString());
    // System.err.println("bh " + ret.toString());

    return ret;
  }

  /**
   * Compute the median absolute deviation of a set of values.
   *
   * @return the double
   */
  public double mad() {
    double[] a = new double[mData.length];

    double m = median();

    for (int i = 0; i < mN; ++i) {
      a[i] = Math.abs(mData[i] - m);
    }

    return Statistics.median(a);
  }

  /**
   * Mad std dev.
   *
   * @return the double
   */
  public double madStdDev() {
    return 1.4826 * mad();
  }

  public double[] data() {
    return mData;
  }

  public double correlation(Stats s2) {
    return Statistics.correlation(mData, s2.mData);
  }

  public double spearmanCorrelation(Stats s2) {
    return Statistics.spearmanCorrelation(mData, s2.mData);
  }
}
