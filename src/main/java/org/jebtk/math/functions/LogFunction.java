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
package org.jebtk.math.functions;

import org.jebtk.core.Mathematics;
import org.jebtk.math.matrix.CellFunction;

/**
 * The Class LogFunction.
 */
public class LogFunction implements CellFunction {

  /** The m base. */
  private int mBase;

  /**
   * Instantiates a new log function.
   *
   * @param base the base
   */
  public LogFunction(int base) {
    mBase = base;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.functions.Function#apply(double)
   */
  @Override
  public double f(int r, int c, double x, double... y) {
    return Mathematics.log(x, mBase);
  }

}
