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
import java.util.Deque;
import java.util.List;

import org.jebtk.core.IdObject;

/**
 * Clusters of rows or columns. Clusters with no children are leaves
 * representing actual indexes. Clusters with children are collections of
 * clusters. The tree must be explored to the leaves to determine which are the
 * actual indices in the tree.
 * 
 * @author Antony Holmes
 *
 */
public class Cluster extends IdObject {

  /**
   * The member level.
   */
  private double mLevel;

  /**
   * The member size.
   */
  private int mSize = 1;

  /**
   * The member c1.
   */
  private Cluster mC1;

  /**
   * The member c2.
   */
  private Cluster mC2;

  /**
   * The member is parent.
   */
  private boolean mIsParent = false;

  /**
   * Instantiates a new cluster.
   *
   * @param id the id
   * @param level the level
   */
  public Cluster(int id, double level) {
    super(id);

    mLevel = level;
  }

  /**
   * Instantiates a new cluster.
   *
   * @param id the id
   * @param level the level
   * @param c1 the c1
   * @param c2 the c2
   */
  public Cluster(int id, double level, Cluster c1, Cluster c2) {
    super(id);

    mLevel = level;

    mC1 = c1;
    mC2 = c2;

    mSize = c1.getCumulativeChildCount() + c2.getCumulativeChildCount();

    mIsParent = true;
  }

  /**
   * Instantiates a new cluster.
   *
   * @param cluster the cluster
   */
  public Cluster(Cluster cluster) {
    super(cluster.getId());

    mLevel = cluster.mLevel;

    if (mC1 != null) {
      mC1 = new Cluster(cluster.mC1);
    }

    if (cluster.mC2 != null) {
      mC2 = new Cluster(cluster.mC2);
    }

    mSize = cluster.mSize;

    mIsParent = cluster.mIsParent;
  }

  /**
   * Checks if is parent.
   *
   * @return true, if is parent
   */
  public boolean isParent() {
    return mIsParent;
  }

  /**
   * Gets the level.
   *
   * @return the level
   */
  public double getLevel() {
    return mLevel;
  }

  /**
   * Gets the child1.
   *
   * @return the child1
   */
  public Cluster getChild1() {
    return mC1;
  }

  /**
   * Gets the child2.
   *
   * @return the child2
   */
  public Cluster getChild2() {
    return mC2;
  }

  /**
   * Returns the total number of elements that have been placed under this
   * cluster. Returns either 1 if this cluster has no children, or the running
   * sum of children.
   * 
   * cluster
   *
   * @return the cumulative child count
   */
  public int getCumulativeChildCount() {
    return mSize;
  }

  /**
   * Sets the child1.
   *
   * @param c1 the new child1
   */
  public void setChild1(Cluster c1) {
    mC1 = c1;

    mIsParent = true;
  }

  /**
   * Sets the child2.
   *
   * @param c2 the new child2
   */
  public void setChild2(Cluster c2) {
    mC2 = c2;

    mIsParent = true;
  }

  /**
   * Sets the children.
   *
   * @param c1 the c 1
   * @param c2 the c 2
   */
  public void setChildren(Cluster c1, Cluster c2) {
    mC1 = c1;
    mC2 = c2;

    mIsParent = true;
  }

  /**
   * Swap children.
   */
  public void swapChildren() {
    Cluster t = mC1;

    mC1 = mC2;
    mC2 = t;
  }

  /**
   * Return the ordered list of ids in the cluster.
   *
   * @param root the root
   * @return the ordered ids
   */
  public static List<Integer> getLeafOrderedIds(Cluster root) {
    List<Integer> ret = new ArrayList<Integer>(100);

    orderedLeafIds(root, ret);

    return ret;
  }

  /**
   * Adds the cluster leaf ids to the ids array in the order they appear in the
   * cluster tree using depth first search.
   *
   * @param root the root
   * @param ids the ids
   */
  public static void orderedLeafIds(final Cluster root, List<Integer> ids) {
    Deque<Cluster> stack = new ArrayDeque<Cluster>();

    stack.push(root);

    while (!stack.isEmpty()) {
      Cluster c = stack.pop();

      if (c.isParent()) {
        stack.push(c.getChild2());
        stack.push(c.getChild1());
      } else {
        ids.add(c.getId());
      }
    }
  }
}
