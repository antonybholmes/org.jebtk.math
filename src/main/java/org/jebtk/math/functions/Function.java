package org.jebtk.math.functions;

public interface Function {
  /**
   * Apply a function to the value in a cell.
   * 
   * @param a value 1.
   * @param b value 2.
   * @return The value of the binary operation such as add.
   */
  public double f(double a, double b);
}
