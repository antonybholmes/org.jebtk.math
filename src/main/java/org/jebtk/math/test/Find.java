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

import java.util.ArrayList;
import java.util.List;

/**
 * The class Find.
 */
public class Find {

  /**
   * Instantiates a new find.
   */
  private Find() {
    // Do nothing
  }

  /**
   * Finds the indices of values less than x.
   *
   * @param values the values
   * @param x the x
   * @return the list
   */
  public static List<Integer> lt(List<Double> values, double x) {
    return find(values, new LessThanCondition(x));
  }

  /**
   * Ge.
   *
   * @param values the values
   * @param x the x
   * @return the list
   */
  public static List<Integer> ge(List<Double> values, double x) {
    return find(values, new GreaterThanEqualToCondition(x));
  }

  /**
   * Returns the list of indices where a condition is met.
   *
   * @param values the values
   * @param condition the condition
   * @return the list
   */
  public static List<Integer> find(List<Double> values, Condition condition) {
    List<Integer> indices = new ArrayList<Integer>();

    for (int i = 0; i < values.size(); ++i) {
      if (condition.test(values.get(i))) {
        indices.add(i);
      }
    }

    return indices;
  }
}
