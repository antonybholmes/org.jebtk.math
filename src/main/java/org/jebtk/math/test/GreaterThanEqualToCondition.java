/**
 * Copyright 2016 Antony Holmes
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
package org.jebtk.math.test;

/**
 * The class GreaterThanEqualToCondition.
 */
public class GreaterThanEqualToCondition implements Condition {

  /**
   * The v.
   */
  private double v;

  /**
   * Instantiates a new greater than equal to condition.
   *
   * @param v the v
   */
  public GreaterThanEqualToCondition(double v) {
    this.v = v;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.find.Condition#test(double)
   */
  @Override
  public boolean test(double x) {
    return x >= v;
  }

}
