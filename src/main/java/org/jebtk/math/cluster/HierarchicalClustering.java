/**
 * Copyright (C) 2016, Antony Holmes
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. Neither the name of copyright holder nor the names of its contributors 
 *     may be used to endorse or promote products derived from this software 
 *     without specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.jebtk.math.cluster;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jebtk.math.matrix.Matrix;

/**
 * The class HierarchicalClustering.
 */
public class HierarchicalClustering {

  /** The flips. */
  private static boolean[] FLIPS = { true, false };

  /**
   * Creates the row distance matrix.
   *
   * @param m the m
   * @param d the d
   * @return the distance matrix
   */
  public static DistanceMatrix createRowDistanceMatrix(Matrix m,
      DistanceMetric d) {
    // to account for the new clusters
    int c = m.getRows();

    int s = 2 * c - 1;

    DistanceMatrix distance = new DistanceMatrix(s); // DoubleMatrix(s, s);

    System.err.println("Creating row distance matrix " + s + "...");

    // all pair wise distances
    for (int i = 0; i < c; ++i) {
      for (int j = 0; j < c; ++j) {
        distance.set(i, j, d.rowDistance(m, i, j));
      }
    }

    System.err.println("Finished creating row distance matrix .");

    return distance;
  }

  /**
   * Creates the column distance matrix.
   *
   * @param m the m
   * @param d the d
   * @return the distance matrix
   */
  public static DistanceMatrix createColumnDistanceMatrix(Matrix m,
      DistanceMetric d) {
    int c = m.getCols();

    // The total number of clusters we will create
    int s = 2 * c - 1;

    DistanceMatrix distance = new DistanceMatrix(s); // DoubleMatrix(s, s);

    System.err.println("Creating distance matrix " + c);

    for (int i = 0; i < c; ++i) {
      for (int j = 0; j < c; ++j) {
        distance.set(i, j, (double) d.columnDistance(m, i, j));
      }
    }

    return distance;
  }

  /**
   * Row cluster.
   *
   * @param m the m
   * @param l the l
   * @param distanceMetric the distance metric
   * @param optimalLeafOrder the optimal leaf order
   * @return the cluster
   */
  public static Cluster rowCluster(final Matrix m,
      final Linkage l,
      DistanceMetric distanceMetric,
      boolean optimalLeafOrder) {
    DistanceMatrix distance = createRowDistanceMatrix(m, distanceMetric);

    return cluster(l, m.getRows(), optimalLeafOrder, distance);
  }

  /**
   * Column cluster.
   *
   * @param m the m
   * @param l the l
   * @param distanceMetric the distance metric
   * @param optimalLeafOrder the optimal leaf order
   * @return the cluster
   */
  public static Cluster columnCluster(final Matrix m,
      final Linkage l,
      final DistanceMetric distanceMetric,
      boolean optimalLeafOrder) {

    DistanceMatrix distance = createColumnDistanceMatrix(m, distanceMetric);

    return cluster(l, m.getCols(), optimalLeafOrder, distance);
  }
  
  public static Cluster columnCluster(final Matrix m,
      final Linkage l,
      boolean optimalLeafOrder,
      final DistanceMatrix mDist) {

    return cluster(l, m.getCols(), optimalLeafOrder, mDist);
  }

  /**
   * Cluster.
   *
   * @param l A linkage function.
   * @param numberOfSamples How many samples are in the matrix.
   * @param optimalLeafOrdering Whether to try and reorder the tree to minimize
   *          the distance between nodes.
   * @param distanceMatrix The distance matrix.
   * 
   * @return the cluster
   */
  public static Cluster cluster(final Linkage l,
      int numberOfSamples,
      boolean optimalLeafOrdering,
      DistanceMatrix distanceMatrix) {

    // Start by putting every index in a cluster

    List<Cluster> allClusters = new ArrayList<Cluster>();
    List<Cluster> clusters = new ArrayList<Cluster>();

    // keep track of the clusters created
    int clusterId = 0;

    for (int i = 0; i < numberOfSamples; ++i) {
      Cluster cluster = new Cluster(clusterId, 0);

      // cluster.add(i);

      allClusters.add(cluster);
      clusters.add(cluster);

      ++clusterId;
    }

    // System.err.println("Clustering " + clusters.size() + " " +
    // distanceMatrix.getColumnCount() + " " + clusterId);

    double minDistance = Double.MAX_VALUE;

    int minCluster1;
    int minCluster2;
    double distance;

    // Set<Integer> used = new HashSet<Integer>();

    while (clusters.size() > 1) {
      // see which two are the closest

      minDistance = Double.MAX_VALUE;
      minCluster1 = -1;
      minCluster2 = -1;

      for (int i = 0; i < clusters.size(); ++i) {
        // if (used.contains(i)) {
        // continue;
        // }

        Cluster c1 = clusters.get(i);

        for (int j = i + 1; j < clusters.size(); ++j) {
          // if (used.contains(j)) {
          // continue;
          // }

          Cluster c2 = clusters.get(j);

          distance = l.getLinkage(distanceMatrix, c1, c2);

          if (distance < minDistance) {
            minCluster1 = i;
            minCluster2 = j;
            minDistance = distance;
          }
        }
      }

      // now we have the two closest clusters
      // so create a new batch of clusters

      Cluster mergeCluster = new Cluster(clusterId, minDistance,
          clusters.get(minCluster1), clusters.get(minCluster2));

      allClusters.add(mergeCluster);

      ++clusterId;

      // add the ids from the two clusters
      // for (int index : minCluster1) {
      // mergeCluster.add(index);

      // System.err.println(ids[i]);
      // }

      // for (int index : minCluster2) {
      // mergeCluster.add(index);

      // System.err.println(ids[i]);
      // }

      // Update the distance matrix of the new cluster to every other cluster

      // distanceMatrix.set(mergeCluster.getUid(), mergeCluster.getUid(), 0);

      for (int i = 0; i < clusterId; ++i) {
        Cluster c1 = allClusters.get(i);

        distance = (double) l.getLinkage(distanceMatrix, mergeCluster, c1);

        distanceMatrix.set(mergeCluster.getId(), c1.getId(), (double) distance);
      }

      // Create a new batch of clusters removing
      // the two that have just been merged

      // List<Cluster> newClusters = new ArrayList<Cluster>();

      // Remove the the clusters we just merged
      clusters.remove(Math.max(minCluster1, minCluster2));
      clusters.remove(Math.min(minCluster1, minCluster2));

      // used.add(minCluster1);
      // used.add(minCluster2);

      clusters.add(mergeCluster);

      /**
       * for (Cluster c1 : clusters) { if (c1.equals(minCluster1)) { continue; }
       * 
       * if (c1.equals(minCluster2)) { continue; }
       * 
       * newClusters.add(c1);
       * 
       * //System.err.println("adding " + c1.getUid()); }
       * 
       * // Get rid of the old set of clusters clusterStack.pop();
       * 
       * // Push on the new set of clusters clusterStack.push(newClusters);
       */
    }

    // The last cluster is the single cluster
    // from which all sub clusters are
    // derived.
    Cluster rootCluster = clusters.get(0);

    // reorder by id
    // orderById(distanceMatrix, rootCluster);

    if (optimalLeafOrdering) {
      rootCluster = optimalLeafOrder(numberOfSamples,
          distanceMatrix,
          rootCluster);
    }

    return rootCluster;
  }

  /**
   * Reorders the cluster tree by swapping branches so adjacent nodes are as
   * close as possible based on the distance matrix. This operation swaps right
   * to left.
   *
   * @param distanceMatrix the distance matrix
   * @param rootCluster the root cluster
   */
  public static void orderClustersOptimally(final DistanceMatrix distanceMatrix,
      Cluster rootCluster) {
    Deque<Cluster> stack = new ArrayDeque<Cluster>();

    stack.push(rootCluster);

    Cluster c1;
    Cluster c2;
    Cluster c3;
    Cluster c4;

    // sum total distances

    Map<Integer, Double> cumDistanceMap = new HashMap<Integer, Double>();

    for (int i = 0; i < distanceMatrix.getRows(); ++i) {
      double sum = 0;

      for (int j = i + 1; j < distanceMatrix.getRows(); ++j) {
        sum += distanceMatrix.getValue(i, j);
      }

      cumDistanceMap.put(i, sum);
    }

    while (stack.size() > 0) {
      Cluster cluster = stack.pop();

      if (!cluster.isParent()) {
        continue;
      }

      // Determine the child of the parent that is not this cluster

      c1 = cluster.getChild1();
      c2 = cluster.getChild2();

      // System.err.println("reorder " + cluster.getUid() + " " +
      // c1.getCumulativeChildCount() + " " + c2.getCumulativeChildCount());

      // if c2 contains children, see which is closer to
      // c1 and swap them if necessary

      if (c2.isParent()) {
        c3 = c2.getChild1();
        c4 = c2.getChild2();

        double d1 = distanceMatrix.get(c1, c3);
        double d2 = distanceMatrix.get(c1, c4);

        // swap the clusters so the closest is always leftmost
        if (d2 < d1) {
          // System.err.println("Swap " + c1.getUid() + " " + c2.getUid());

          c2.setChild1(c4);
          c2.setChild2(c3);
        }
      }

      // Place children with fewest children on the right

      if (cumDistanceMap.get(c2.getId()) < cumDistanceMap.get(c1.getId())) {
        cluster.setChild1(c2);
        cluster.setChild2(c1);
      }

      // repeat on the children
      stack.push(c2);
      stack.push(c1);
    }
  }

  /**
   * Attempts to maximize the sum of the distances between pairs of nodes by
   * flipping internal nodes to rearrange the tree whilst preserving its
   * structure.
   *
   * @param numberOfSamples the number of samples
   * @param distanceMatrix the distance matrix
   * @param rootCluster the root cluster
   * @return the cluster
   */
  public static Cluster optimalLeafOrder(int numberOfSamples,
      final DistanceMatrix distanceMatrix,
      final Cluster rootCluster) {

    int n = distanceMatrix.getCols();

    Cluster optCluster = null;

    // double minD = Double.MAX_VALUE;
    double maxD = 0;

    int total = 0;

    // We don't need to flip leaves since that will accomplish nothing
    // so concentrate on parents only. The parents come after the leaves
    // by id so we can start the counters at the number of samples

    Cluster c;
    Cluster cn;
    Cluster c1;
    Cluster c2;
    Cluster cn1;
    Cluster cn2;
    Deque<Cluster> stack;
    Deque<Cluster> newTreeStack;
    Cluster newRoot;
    List<Integer> nodes = new ArrayList<Integer>(numberOfSamples);
    int id1;
    int id2;

    // We start at numberOfSamples because the leaves are numbered
    // [0, (number of samples -1)]. Therefore any node with an id
    // greater than or equal to numberOfSamples must be a parent and
    // considered for flipping

    for (int c1id = numberOfSamples; c1id < n; ++c1id) {
      for (boolean c1flip : FLIPS) {
        for (int c2id = numberOfSamples; c2id < n; ++c2id) {
          // No point generating a tree where we are altering the
          // node itself
          if (c2id == c1id) {
            continue;
          }

          for (boolean c2flip : FLIPS) {

            // Build the new tree

            stack = new ArrayDeque<Cluster>();
            stack.push(rootCluster);

            newRoot = new Cluster(rootCluster.getId(), rootCluster.getLevel());

            // Build a new tree where we might be swapping nodes
            // around
            newTreeStack = new ArrayDeque<Cluster>();
            newTreeStack.push(newRoot);

            while (!stack.isEmpty()) {
              c = stack.pop();
              cn = newTreeStack.pop();

              if (c.isParent()) {
                // System.err.println("c " + c.getId() + " " + c.getLevel() + "
                // " + c.isParent());

                c1 = c.getChild1();
                c2 = c.getChild2();

                id1 = c1.getId();
                id2 = c2.getId();

                // If a node says flip, do so
                if ((id1 == c1id && c1flip) || (id2 == c2id && c2flip)) {
                  cn1 = new Cluster(id2, c2.getLevel());
                  cn2 = new Cluster(id1, c1.getLevel());

                  // explore right first
                  stack.push(c1);
                  stack.push(c2);

                } else {
                  cn1 = new Cluster(id1, c1.getLevel());
                  cn2 = new Cluster(id2, c2.getLevel());

                  stack.push(c2);
                  stack.push(c1);
                }

                cn.setChildren(cn1, cn2);

                // The new stack always has its children
                // pushed on in the same order (of course
                // they have been flipped above)
                newTreeStack.push(cn2);
                newTreeStack.push(cn1);
              }
            }

            // Now we have a new tree, get the list of nodes in
            // the order they now appear

            // Stores the nodes in the order they are found in
            // the tree
            nodes.clear();

            // Add the ids to nodes
            Cluster.orderedLeafIds(newRoot, nodes);

            // Now we need to calculate the total distance between
            // all pairs

            double d = sumDistPairs(nodes, distanceMatrix);

            /*
             * if (d < minD) { optCluster = newRoot; optNodes = nodes;
             * 
             * minD = d; }
             */

            if (d > maxD) {
              optCluster = newRoot;

              maxD = d;
            }

            ++total;
          }
        }
      }
    }

    System.err.println("opt nodes " + Cluster.getLeafOrderedIds(optCluster)
        + " " + maxD + " " + total);

    return optCluster;
  }

  /**
   * Sums the distance between adjacent pairs of leaves.
   *
   * @param leaves the leaves
   * @param distanceMatrix the distance matrix
   * @return the double
   */
  private static double sumDistPairs(List<Integer> leaves,
      DistanceMatrix distanceMatrix) {
    double d = 0;

    for (int i = 0; i < leaves.size() - 1; ++i) {
      d += distanceMatrix.getValue(leaves.get(i), leaves.get(i + 1));
    }

    return d;
  }

  /**
   * Order.
   *
   * @param distanceMatrix the distance matrix
   * @param rootCluster the root cluster
   */
  public static void order(final DistanceMatrix distanceMatrix,
      Cluster rootCluster) {
    Deque<Cluster> stack = new ArrayDeque<Cluster>();
    List<Cluster> parents = new ArrayList<Cluster>();

    stack.push(rootCluster);

    Map<Integer, Double> minDistanceMap = new HashMap<Integer, Double>();
    // Map<Integer, double> maxDistanceMap = new HashMap<Integer, double>();

    while (!stack.isEmpty()) {
      Cluster cluster = stack.pop();

      if (cluster.isParent()) {
        parents.add(cluster);

        stack.push(cluster.getChild2());
        stack.push(cluster.getChild1());

        minDistanceMap.put(cluster.getId(),
            distanceMatrix.get(cluster.getChild1(), cluster.getChild2()));
        // maxDistanceMap.put(cluster.getUid(), double.MAX_VALUE);
      } else {
        // Since we encounter the leaf clusters in the order
        // they appear in the tree, we can store their
        // x position in order

        minDistanceMap.put(cluster.getId(), Double.MAX_VALUE);

        // maxDistanceMap.put(cluster.getUid(), cumDistance(distanceMatrix,
        // cluster.getUid()));
      }
    }

    Collections.reverse(parents);

    double d1;
    double d2;
    // double minD;

    for (Cluster cluster : parents) {

      // Determine which child is closest to all
      // the other nodes
      Cluster c1 = cluster.getChild1();
      Cluster c2 = cluster.getChild2();

      d1 = minDistanceMap.get(c1.getId());
      d2 = minDistanceMap.get(c2.getId());

      // minD = Math.min(d1, d2); //d1 + d2; // + Math.min(d1, d2);

      // Update the mid point of this cluster

      // minDistanceMap.put(cluster.getUid(), minD);
      // maxDistanceMap.put(cluster.getUid(), Math.max(d1, d2));

      // swap if necessary
      if (d2 < d1) {
        cluster.setChild1(c2);
        cluster.setChild2(c1);
      }
    }

    // At this point we know the nodes are left ordered smaller
    // distances appear towards the left

  }

  /**
   * Order nodes so that they appear smallest to largest to try and preserve the
   * layout of the original matrix as much as possible.
   *
   * @param distanceMatrix the distance matrix
   * @param rootCluster the root cluster
   */
  public static void orderById(final DistanceMatrix distanceMatrix,
      Cluster rootCluster) {
    Deque<Cluster> stack = new ArrayDeque<Cluster>();
    List<Cluster> parents = new ArrayList<Cluster>();

    stack.push(rootCluster);

    Map<Integer, Integer> orderMap = new HashMap<Integer, Integer>();
    // Map<Integer, double> maxDistanceMap = new HashMap<Integer, double>();

    while (!stack.isEmpty()) {
      Cluster cluster = stack.pop();

      if (cluster.isParent()) {
        parents.add(cluster);

        stack.push(cluster.getChild2());
        stack.push(cluster.getChild1());

        orderMap.put(cluster.getId(), -1);
        // maxDistanceMap.put(cluster.getUid(), double.MAX_VALUE);
      } else {
        // Since we encounter the leaf clusters in the order
        // they appear in the tree, we can store their
        // x position in order

        orderMap.put(cluster.getId(), cluster.getId());

        // maxDistanceMap.put(cluster.getUid(), cumDistance(distanceMatrix,
        // cluster.getUid()));
      }
    }

    Collections.reverse(parents);

    int o1;
    int o2;
    // double minD;

    for (Cluster cluster : parents) {

      // Determine which child is closest to all
      // the other nodes
      Cluster c1 = cluster.getChild1();
      Cluster c2 = cluster.getChild2();

      o1 = orderMap.get(c1.getId());
      o2 = orderMap.get(c2.getId());

      orderMap.put(cluster.getId(), Math.min(o1, o2));

      // Update the mid point of this cluster

      // minDistanceMap.put(cluster.getUid(), minD);
      // maxDistanceMap.put(cluster.getUid(), Math.max(d1, d2));

      // swap if necessary
      if (o2 < o1) {
        cluster.swapChildren();
      }
    }

    // At this point we know the nodes are left ordered smaller
    // distances appear towards the left

  }

  /*
   * private static double cumDistance(final DistanceMatrix distanceMatrix, int
   * index) { double sum = 0; double d;
   * 
   * for (int i = index + 1; i < distanceMatrix.getRowCount(); ++i) { d =
   * distanceMatrix.get(index, i);
   * 
   * d *= d;
   * 
   * sum += d; }
   * 
   * sum = (double)Math.sqrt(sum);
   * 
   * return sum; }
   */

  /*
   * private static double minDistance(final DistanceMatrix distanceMatrix, int
   * index) { double d = double.MAX_VALUE;
   * 
   * for (int i = index + 1; i < distanceMatrix.getRowCount(); ++i) { d =
   * Math.min(d, distanceMatrix.get(index, i));
   * 
   * }
   * 
   * return d; }
   */

  /*
   * private static DistanceMatrix getSimilarityMatrix(DistanceMatrix
   * distanceMatrix) { DistanceMatrix similarity = new
   * DistanceMatrix(distanceMatrix.getRowCount());
   * 
   * double d; double sum;
   * 
   * for (int i = 0; i < distanceMatrix.getRowCount(); ++i) { sum = 0;
   * 
   * //d1 =
   * 
   * for (int j = i + 1; j < distanceMatrix.getRowCount(); ++j) { d =
   * distanceMatrix.get(i, j);
   * 
   * d *= d;
   * 
   * sum += d; }
   * 
   * sum = (double)Math.sqrt(sum); }
   * 
   * r }
   */
}
