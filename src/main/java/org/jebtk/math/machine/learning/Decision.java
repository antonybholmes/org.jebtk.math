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

import java.util.Collection;
import java.util.Iterator;

/**
 * The Class Decision.
 */
public class Decision implements Iterable<String> {

  /** The m att idx. */
  private int mAttIdx;

  /** The m type. */
  private DecisionType mType;

  /** The m values. */
  private Collection<String> mValues = null;

  /** The m pivot. */
  private double mPivot = Double.NaN;

  /**
   * Should return the tree branch to follow based on the value.
   *
   * @param attIdx the att idx
   * @param pivot the pivot
   */
  public Decision(int attIdx, double pivot) {
    mAttIdx = attIdx;
    mPivot = pivot;
    mType = DecisionType.NUMERICAL;
  }

  /**
   * Instantiates a new decision.
   *
   * @param attIdx the att idx
   * @param values the values
   */
  public Decision(int attIdx, Collection<String> values) {
    mAttIdx = attIdx;
    mValues = values;
    mType = DecisionType.TEXT;
  }

  /**
   * Returns the attribute index in the list of values that will be processed.
   * Samples
   *
   * @return the att idx
   */
  public int getAttIdx() {
    return mAttIdx;
  }

  /**
   * Gets the type.
   *
   * @return the type
   */
  public DecisionType getType() {
    return mType;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Iterable#iterator()
   */
  @Override
  public Iterator<String> iterator() {
    return mValues.iterator();
  }

  /**
   * Returns the test pivot value.
   *
   * @return the pivot
   */
  public double getPivot() {
    return mPivot;
  }
}
