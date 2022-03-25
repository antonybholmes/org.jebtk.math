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
import java.util.Deque;

import org.jebtk.math.matrix.Matrix;

/**
 * The class CompleteLinkage.
 */
public class CompleteLinkage implements Linkage {

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.abh.lib.math.cluster.Linkage#getLinkage(org.abh.lib.math.matrix.Matrix,
   * org.abh.lib.math.cluster.Cluster, org.abh.lib.math.cluster.Cluster)
   */
  @Override
  public double getLinkage(final Matrix distanceMatrix,
      final Cluster c1,
      final Cluster c2) {
    double d = Double.MIN_VALUE;

    /*
     * for (int i1 : c1) { for (int i2 : c2) { if (distanceMatrix.get(i1, i2) >
     * d) { d = distanceMatrix.get(i1, i2); } } }
     */

    if (!c1.isParent() && !c2.isParent()) {
      // both nodes are leaves so compare to each other

      d = distanceMatrix.getValue(c1.getId(), c2.getId());
    } else if (!c1.isParent() && c2.isParent()) {
      Deque<Cluster> stack = new ArrayDeque<Cluster>();

      stack.push(c2);

      while (!stack.isEmpty()) {
        Cluster c = stack.pop();

        if (c.isParent()) {
          stack.push(c.getChild2());
          stack.push(c.getChild1());
        } else {
          double dt = distanceMatrix.getValue(c1.getId(), c.getId());

          if (dt > d) {
            d = dt;
          }
        }
      }
    } else if (c1.isParent() && !c2.isParent()) {
      Deque<Cluster> stack = new ArrayDeque<Cluster>();

      stack.push(c1);

      while (!stack.isEmpty()) {
        Cluster c = stack.pop();

        if (c.isParent()) {
          stack.push(c.getChild2());
          stack.push(c.getChild1());
        } else {
          double dt = distanceMatrix.getValue(c2.getId(), c.getId());

          if (dt > d) {
            d = dt;
          }
        }
      }
    } else {
      Deque<Cluster> stack1 = new ArrayDeque<Cluster>();

      stack1.push(c1);

      while (!stack1.isEmpty()) {
        Cluster cp1 = stack1.pop();

        if (cp1.isParent()) {
          stack1.push(cp1.getChild2());
          stack1.push(cp1.getChild1());
        } else {
          Deque<Cluster> stack2 = new ArrayDeque<Cluster>();

          stack2.push(c2);

          while (!stack2.isEmpty()) {
            Cluster cp2 = stack2.pop();

            if (cp2.isParent()) {
              stack2.push(cp2.getChild2());
              stack2.push(cp2.getChild1());

            } else {
              double dt = distanceMatrix.getValue(cp1.getId(), cp2.getId());

              if (dt > d) {
                d = dt;
              }
            }
          }
        }
      }
    }

    return d;
  }
}
