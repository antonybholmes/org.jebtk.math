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
package org.jebtk.math.machine.learning;

import org.jebtk.core.tree.TreeNode;

/**
 * The Class DecisionTree.
 */
public class DecisionTree extends TreeNode<Decision> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new decision tree.
   *
   * @param name the name
   */
  public DecisionTree(String name) {
    super(name);
  }

  /**
   * Instantiates a new decision tree.
   *
   * @param name the name
   * @param d the d
   */
  public DecisionTree(String name, Decision d) {
    super(name, d);
  }

  /**
   * Classify.
   *
   * @param values the values
   * @return the string
   */
  public String classify(double[] values) {
    return classify(this, values);
  }

  // public String classify(String[] values) {
  // return classify(this, values);
  // }

  /**
   * Classify some data based on a decision tree.
   *
   * @param root the root
   * @param values the values
   * @return the string
   */
  public static String classify(TreeNode<Decision> root, double[] values) {

    TreeNode<Decision> current = root;

    while (current.getValue() != null) {
      Decision decision = current.getValue();

      int attIdx = decision.getAttIdx();

      double v = values[attIdx];

      int child;

      if (v <= decision.getPivot()) {
        child = 0;
      } else {
        child = 1;
      }

      // Move along to the next decision node.
      current = current.getChild(child);
    }

    return current.getName();
  }
}
