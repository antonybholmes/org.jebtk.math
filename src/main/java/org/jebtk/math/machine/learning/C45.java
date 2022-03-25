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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jebtk.core.DblIdx;
import org.jebtk.core.Mathematics;
import org.jebtk.core.collections.CollectionUtils;
import org.jebtk.core.collections.CountMap;
import org.jebtk.core.tree.TreeNode;
import org.jebtk.math.matrix.DataFrame;
import org.jebtk.math.matrix.MatrixGroup;

/**
 * The Class C45.
 */
public class C45 {

  /**
   * Instantiates a new c45.
   */
  private C45() {
    // Do nothing
  }

  /**
   * Parses the double.
   *
   * @param m the m
   * @param groups the groups
   * @return the decision tree
   */
  public static DecisionTree parseDouble(DataFrame m,
      List<? extends MatrixGroup> groups) {

    List<String> labels = new ArrayList<String>(m.getCols());

    Map<Integer, List<MatrixGroup>> indexMap = MatrixGroup
        .arrangeGroupsByIndex(m, groups);

    for (int index : CollectionUtils.sortKeys(indexMap)) {
      labels.add(indexMap.get(index).get(0).getName());
    }

    String[] attributes = m.getRowNames();

    Set<Integer> usedAttributes = new HashSet<Integer>();

    Deque<List<Integer>> sQueue = new ArrayDeque<List<Integer>>();
    Deque<DecisionTree> tQueue = new ArrayDeque<DecisionTree>();

    // Start will all the columns (S)
    sQueue.push(Mathematics.sequence(0, m.getCols() - 1));

    DecisionTree root = null;

    while (!sQueue.isEmpty()) {
      // The samples list we are going to subdivide
      List<Integer> s = sQueue.pop();

      // Get the parent (if one exists)
      DecisionTree node = null;

      if (!tQueue.isEmpty()) {
        node = tQueue.pop();
      }

      if (s.size() == 1) {
        // Leaf so there is nothing to split on

        // The leaf just contains the label and no decision object
        // so we know when we have finished parsing the tree
        TreeNode<Decision> child = new TreeNode<Decision>(labels.get(s.get(0)));

        node.addChild(child);

        continue;
      }

      // Handle the pivot case where we find the best attribute to
      // split the sample set on.

      int bestAttIdx = -1;
      int bestAttPivot = -1;
      double maxAttIG = Double.MIN_VALUE;
      DblIdx[] bestSortedValues = null;
      List<Integer> bestSSorted = null;

      // Find the best attribute
      for (int attIdx = 0; attIdx < attributes.length; ++attIdx) {
        if (usedAttributes.contains(attIdx)) {
          continue;
        }

        // Extract the values for the samples of interest
        double[] values = new double[s.size()];

        for (int i = 0; i < s.size(); ++i) {
          values[i] = m.getValue(attIdx, s.get(i));
        }

        // Index them
        DblIdx[] sortedValues = DblIdx.index(values);

        // Sort them
        Arrays.sort(sortedValues);

        // Now we need to reorder s to match the value order

        List<Integer> sSorted = new ArrayList<Integer>(s.size());

        for (DblIdx index : sortedValues) {
          sSorted.add(index.getIndex());
        }

        // Test each pivot point

        int bestPivot = -1;

        double maxIG = Double.MIN_VALUE;

        double entropy = entropy(labels, sSorted);

        for (int pivot = 1; pivot < values.length - 1; ++pivot) {
          double ig = infGain(labels, sSorted, pivot, entropy);

          // Find the split with the max information gain
          if (ig > maxIG) {
            bestPivot = pivot;
            maxIG = ig;
          }
        }

        // Keep track of the best attribute splitting values
        if (maxIG > maxAttIG) {
          bestAttIdx = attIdx;
          bestAttPivot = bestPivot;
          maxAttIG = maxIG;
          bestSortedValues = sortedValues;
          bestSSorted = sSorted;
        }
      }

      // Use the midpoint value of the two points surrounding the pivot
      // to get the numerical point around which we decide which
      // cluster to be in.
      double pivot = (bestSortedValues[bestAttPivot - 1].getValue()
          + bestSortedValues[bestAttPivot].getValue()) / 2;

      Decision d = new Decision(bestAttIdx, pivot);

      DecisionTree child = new DecisionTree(
          attributes[bestAttIdx] + " <= " + pivot, d);

      // Create the root if it doesn't exist
      if (root == null) {
        root = child;
      }

      // If there is a parent, make ourselves the child of it
      if (node != null) {
        node.addChild(child);
      }

      List<Integer> s1 = CollectionUtils.head(bestSSorted, bestAttPivot);

      List<Integer> s2 = CollectionUtils.subList(bestSSorted, bestAttPivot);

      // Process depth first so we always want to process s1 before s2
      sQueue.push(s2);
      sQueue.push(s1);

      // Both will share the node as a parent
      tQueue.push(child);
      tQueue.push(child);

      // We can only check an attribute once
      usedAttributes.add(bestAttIdx);
    }

    return root;
  }

  /**
   * Entropy.
   *
   * @param labels the labels
   * @param s the s
   * @return the double
   */
  private static double entropy(final List<String> labels,
      final List<Integer> s) {
    CountMap<String> countMap = new CountMap<String>();

    // We need to know the frequencies of each class
    for (int i : s) {
      countMap.inc(labels.get(i));
    }

    double n = s.size();

    double entropy = 0;

    for (Entry<String, Integer> x : countMap) {
      double px = x.getValue() / n;

      entropy -= px * Mathematics.log2(px);
    }

    return entropy;
  }

  /**
   * Inf gain.
   *
   * @param labels the labels
   * @param s the s
   * @param pivot the pivot
   * @param hs the hs
   * @return the double
   */
  private static double infGain(final List<String> labels,
      final List<Integer> s,
      int pivot,
      double hs) {

    // Create partitions at the pivot point
    List<Integer> s1 = CollectionUtils.head(s, pivot);
    List<Integer> s2 = CollectionUtils.subList(s, pivot);

    double n = s.size();

    double p1 = s1.size() / n;
    double p2 = s2.size() / n;

    double ig = hs - (p1 * entropy(labels, s1)) - (p2 * entropy(labels, s2));

    return ig;
  }
}
