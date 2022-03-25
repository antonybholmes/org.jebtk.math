package org.jebtk.math.matrix;

public interface CellFunction {
  /**
   * Apply a function to a matrix cell.
   * 
   * @param row
   * @param col
   * @param value
   * @return
   */
  public double f(int row, int col, double x, double... y);
}
