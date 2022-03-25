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

import org.jebtk.math.CubicBezier;
import org.junit.Test;

/**
 * The Class MathTest.
 */
public class MathTest {

  /**
   * Norm cubic bezier.
   */
  @Test
  public void normCubicBezier() {
    CubicBezier c = CubicBezier.normCubicBezier(0.4, 0.0, 0.2, 1);

    for (double i = 0; i <= 1; i += 0.1) {
      System.err.println("bezier " + c.eval(i));
    }
  }
}
