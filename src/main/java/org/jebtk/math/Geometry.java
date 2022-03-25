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
package org.jebtk.math;

import org.jebtk.core.geom.IntPos2D;

/**
 * The Class Geometry.
 */
public class Geometry {

  /**
   * Instantiates a new geometry.
   */
  private Geometry() {

  }

  /**
   * Overlap.
   *
   * @param p1 the p 1
   * @param p2 the p 2
   * @param w the w
   * @return true, if successful
   */
  public static boolean overlap(IntPos2D p1, IntPos2D p2, int w) {
    return overlap(p1, p2, w, w);
  }

  /**
   * Overlap.
   *
   * @param p1 the p 1
   * @param p2 the p 2
   * @param w the w
   * @param h the h
   * @return true, if successful
   */
  public static boolean overlap(IntPos2D p1, IntPos2D p2, int w, int h) {
    return overlap(p1.getX(), p1.getY(), w, h, p2.getX(), p2.getY(), w, h);
  }

  /**
   * Overlap.
   *
   * @param x1 the x 1
   * @param y1 the y 1
   * @param w1 the w 1
   * @param h1 the h 1
   * @param x2 the x 2
   * @param y2 the y 2
   * @param w2 the w 2
   * @param h2 the h 2
   * @return true, if successful
   */
  public static boolean overlap(int x1,
      int y1,
      int w1,
      int h1,
      int x2,
      int y2,
      int w2,
      int h2) {

    int x12 = x1 + w1;
    int y12 = y1 + h1;

    int x22 = x2 + w2;
    int y22 = y2 + h2;

    return (x1 >= x2 && x1 <= x22 || x12 >= x2 && x12 <= x22)
        && (y1 >= y2 && y1 <= y22 || y12 >= y2 && y12 <= y22);
  }
}
