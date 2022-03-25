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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.apache.commons.math3.stat.inference.TestUtils;
import org.jebtk.core.collections.CollectionUtils;

/**
 * In memory stats calculations on an array. The array is guaranteed to be
 * sorted.
 * 
 * @author Antony Holmes
 *
 */
public class TwoSampleTest {

  private double[] mS1;
  private double[] mS2;

  MannWhitneyUTest mMw = null;

  /**
   * Instantiates a new test.
   */
  public TwoSampleTest(double[] s1, double[] s2) {
    mS1 = new double[s1.length];
    System.arraycopy(s1, 0, mS1, 0, s1.length);
    Arrays.sort(mS1);

    mS2 = new double[s2.length];
    System.arraycopy(s2, 0, mS2, 0, s2.length);
    Arrays.sort(mS2);

  }

  /**
   * Returns the t-statistic for two samples.
   * 
   * @return the double
   */
  public double tStat() {
    return TestUtils.t(mS1, mS2);
  }

  /**
   * TTest unequal variance, heteroscedastic.
   *
   * @return the double
   */
  public double twoTailUnequalVarianceTTest() {
    return TestUtils.tTest(mS1, mS2);
  }

  /**
   * TTest equal variance, heteroscedastic.
   *
   * @return the double
   */
  public double twoTailEqualVarianceTTest() {
    return TestUtils.homoscedasticTTest(mS1, mS2);
  }

  public double mannWhitneyU() {
    return getMW().mannWhitneyU(mS1, mS2);
  }

  public double mannWhitney() {
    return getMW().mannWhitneyUTest(mS1, mS2);
  }

  private MannWhitneyUTest getMW() {
    if (mMw == null) {
      mMw = new MannWhitneyUTest();
    }

    return mMw;
  }

  public static TwoSampleTest create(List<Double> v1, List<Double> v2) {
    return new TwoSampleTest(CollectionUtils.toArray(v1),
        CollectionUtils.toArray(v2));
  }

  public static TwoSampleTest create(double[] v1, double[] v2) {
    return new TwoSampleTest(v1, v2);
  }

  public static TwoSampleTest create(List<Double> v1, double[] v2) {
    return new TwoSampleTest(CollectionUtils.toArray(v1), v2);
  }

  public static TwoSampleTest create(double[] v1, List<Double> v2) {
    return new TwoSampleTest(v1, CollectionUtils.toArray(v2));
  }
}
