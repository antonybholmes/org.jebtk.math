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
package org.abh.common.math.test;

import java.util.ArrayList;
import java.util.List;

import org.jebtk.math.statistics.Stats;
import org.jebtk.math.statistics.TwoSampleTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * The Class StatsTest.
 */
public class StatsTest {

  /**
   * Percentile test.
   */
  @Test
  public void percentileTest() {
    double[] values = { 20, 15, 40, 35, 50 };

    double p25 = new Stats(values).percentile(25);

    System.err.println("p " + p25);

    Assert.assertEquals("Percentile 0.25 = 17.5", p25, 17.5, 0);
  }

  /**
   * Median test.
   */
  @Test
  public void medianTest() {
    double[] values = { 1, 3, 3, 6, 7, 8, 9 };

    double m = new Stats(values).median();

    System.err.println("m " + m);

    Assert.assertEquals("median == 6", m, 6, 0);
  }

  /**
   * Quart coeff dist test.
   */
  @Test
  public void quartCoeffDistTest() {
    double[] values = { 1.8, 2, 2.1, 2.4, 2.6, 2.9, 3 };

    double m = new Stats(values).quartCoeffDisp();

    System.err.println("quartCoeffDist " + m);

    Assert.assertEquals("quartCoeffDist == 0.18", m, 0.18, 0.01);
  }

  @Test
  public void TTest() {
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

    TwoSampleTest test = TwoSampleTest.create(v1, v2);

    System.err.println(test.twoTailUnequalVarianceTTest());
  }

  @Test
  public void mannWhitneyTest() {
    double[] v1 = { 8, 7, 6, 2, 5, 8, 7, 3 };

    double[] v2 = { 9, 9, 7, 8, 10, 9, 6 };

    TwoSampleTest test = TwoSampleTest.create(v1, v2);

    System.err.println("MU " + test.mannWhitneyU());
  }

}
