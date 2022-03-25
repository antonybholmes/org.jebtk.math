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

import java.awt.geom.Point2D;

import org.jebtk.core.geom.DoublePos2D;

/**
 * Evaluate a point on a cubic bezier curve.
 * 
 * @author antony
 *
 */
public class CubicBezier {

  /** The Constant P0. */
  private static final DoublePos2D P0 = new DoublePos2D(0, 0);

  /** The Constant P3. */
  private static final DoublePos2D P3 = new DoublePos2D(1, 1);

  /** The m X. */
  private double[] mX = new double[4];

  /** The m Y. */
  private double[] mY = new double[4];

  /**
   * Instantiates a new cubic bezier.
   *
   * @param p0 the p 0
   * @param p1 the p 1
   * @param p2 the p 2
   * @param p3 the p 3
   */
  public CubicBezier(DoublePos2D p0, DoublePos2D p1, DoublePos2D p2,
      DoublePos2D p3) {
    mX[0] = p0.getX();
    mX[1] = p1.getX();
    mX[2] = p2.getX();
    mX[3] = p3.getX();

    mY[0] = p0.getY();
    mY[1] = p1.getY();
    mY[2] = p2.getY();
    mY[3] = p3.getY();
  }

  /**
   * Instantiates a new cubic bezier.
   *
   * @param p0 the p 0
   * @param p1 the p 1
   * @param p2 the p 2
   * @param p3 the p 3
   */
  public CubicBezier(Point2D.Double p0, Point2D.Double p1, Point2D.Double p2,
      Point2D.Double p3) {
    mX[0] = p0.x;
    mX[1] = p1.x;
    mX[2] = p2.x;
    mX[3] = p3.x;

    mY[0] = p0.y;
    mY[1] = p1.y;
    mY[2] = p2.y;
    mY[3] = p3.y;
  }

  /**
   * Instantiates a new cubic bezier.
   *
   * @param x0 the x 0
   * @param y0 the y 0
   * @param x1 the x 1
   * @param y1 the y 1
   * @param x2 the x 2
   * @param y2 the y 2
   * @param x3 the x 3
   * @param y3 the y 3
   */
  public CubicBezier(double x0, double y0, double x1, double y1, double x2,
      double y2, double x3, double y3) {
    mX[0] = x0;
    mX[1] = x1;
    mX[2] = x2;
    mX[3] = x3;

    mY[0] = y0;
    mY[1] = y1;
    mY[2] = y2;
    mY[3] = y3;
  }

  /**
   * Eval.
   *
   * @param t the t
   * @return the double pos 2 D
   */
  public DoublePos2D eval(double t) {
    double u = 1 - t;
    double tt = t * t;
    double uu = u * u;
    double uuu = uu * u;
    double ttt = tt * t;

    return new DoublePos2D(eval(t, u, tt, uu, uuu, ttt, mX),
        eval(t, u, tt, uu, uuu, ttt, mY));
  }

  /*
  private double evalX(double t,
      double u,
      double tt,
      double uu,
      double uuu,
      double ttt) {
    double x = uuu * mX[0]; // first term
    x += 3 * uu * t * mX[1]; // second term
    x += 3 * u * tt * mX[2]; // third term
    x += ttt * mX[3]; // fourth term

    return x;
  }

  private double evalY(double t,
      double u,
      double tt,
      double uu,
      double uuu,
      double ttt) {
    double y = uuu * mY[0]; // first term
    y += 3 * uu * t * mY[1]; // second term
    y += 3 * u * tt * mY[2]; // third term
    y += ttt * mY[3]; // fourth term

    return y;
  }
  */
  
  public double evalX(double t) {
    return eval(t, mX);
  }
  
  public double evalY(double t) {
    return eval(t, mY);
  }
  
  private static double eval(double t, double[] p) {
    double u = 1 - t;
    double tt = t * t;
    double uu = u * u;
    double uuu = uu * u;
    double ttt = tt * t;

    return eval(t, u, tt, uu, uuu, ttt, p);
  }
  
  /**
   * Explicit form of cubic, see 
   * https://en.wikipedia.org/wiki/B%C3%A9zier_curve.
   * 
   * @param t
   * @param u
   * @param tt
   * @param uu
   * @param uuu
   * @param ttt
   * @param p
   * @return
   */
  private static double eval(double t,
      double u,
      double tt,
      double uu,
      double uuu,
      double ttt,
      double[] p) {
    double x = uuu * p[0]; // first term
    x += 3 * uu * t * p[1]; // second term
    x += 3 * u * tt * p[2]; // third term
    x += ttt * p[3]; // fourth term

    return x;
  }

  /**
   * Return a normalized Cubic Bezier where the start and end points are fixed
   * at (0, 0) and (1, 1) respectively.
   *
   * @param p1 the p 1
   * @param p2 the p 2
   * @return the cubic bezier
   */
  public static CubicBezier normCubicBezier(DoublePos2D p1, DoublePos2D p2) {
    return new CubicBezier(P0, p1, p2, P3);
  }

  /**
   * Norm cubic bezier.
   *
   * @param x1 the x 1
   * @param y1 the y 1
   * @param x2 the x 2
   * @param y2 the y 2
   * @return the cubic bezier
   */
  public static CubicBezier normCubicBezier(double x1,
      double y1,
      double x2,
      double y2) {
    return new CubicBezier(0, 0, x1, y1, x2, y2, 1, 1);
  }
}
