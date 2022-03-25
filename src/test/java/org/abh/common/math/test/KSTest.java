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

import java.util.Arrays;

import org.jebtk.math.statistics.KernelDensity;
import org.jebtk.math.statistics.NormKernelDensity;
import org.junit.Assert;
import org.junit.Test;

/**
 * The Class KSTest.
 */
public class KSTest {

  /**
   * Ks test.
   */
  @Test
  public void ksTest() {
    double[] v = { -2.1000, -1.3000, -0.4000, 1.9000, 5.1000, 6.2000 };

    KernelDensity density = new NormKernelDensity(v);

    // double[] v = {0, 10};

    // List<Double> a = CollectionUtils.toList(v);

    // List<Double> p = Linspace.generate(-3, 3, 100);

    double[] p1y = density.cdf(v);

    System.err.println(Arrays.toString(p1y));

    Assert.assertEquals("pl1 0.25 = 17.5", 1, 1, 0);
  }
}
