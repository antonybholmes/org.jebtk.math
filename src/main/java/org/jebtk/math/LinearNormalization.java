package org.jebtk.math;

public class LinearNormalization implements Normalization {

  private double[] mX;
  
  private static final double[] Y = {0, 0.5, 1};

  public LinearNormalization(double max) {
    this(0, max);
  }
  
  public LinearNormalization(double min, double max) {
    this(min, (min + max) / 2, max);
  }
  
  public LinearNormalization(double min, double mid, double max) {
    mX = new double[]{min, mid, max};
  }
  
  @Override
  public double norm(double v) {
    //return (v - mMin) / mRange;
    
    return MathUtils.linearInterpolation(v, mX, Y);
  }
}
