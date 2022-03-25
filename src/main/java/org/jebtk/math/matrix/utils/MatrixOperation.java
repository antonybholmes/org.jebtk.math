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
package org.jebtk.math.matrix.utils;

import org.jebtk.math.matrix.DataFrame;

/**
 * The class MatrixOperation.
 */
public abstract class MatrixOperation {

  /**
   * The member previous op.
   */
  private MatrixOperation mPreviousOp;

  /**
   * Instantiates a new matrix operation.
   *
   * @param child the child
   */
  public MatrixOperation(MatrixOperation child) {
    mPreviousOp = child;
  }

  /**
   * Apply a series of operations to a matrix and return the resulting new
   * matrix.
   *
   * @param m the m
   * @return the annotation matrix
   */
  public DataFrame to(DataFrame m) {
    return op(mPreviousOp.to(m));
  }

  /**
   * Op should be overridden to perform the actual matrix transformation.
   *
   * @param m the m
   * @return the annotation matrix
   */
  public abstract DataFrame op(DataFrame m);

  /**
   * Does nothing to a matrix. Designed to start a transformation pipeline. This
   * should never be inserted arbitrarily into a pipeline.
   *
   */
  private static class NullMatrixOperation extends MatrixOperation {

    /**
     * Instantiates a new null matrix operation.
     */
    public NullMatrixOperation() {
      super(null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abh.lib.math.matrix.MatrixOperation#to(org.abh.lib.math.matrix.
     * DataFrame)
     */
    @Override
    public DataFrame to(DataFrame m) {
      return m;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abh.lib.math.matrix.MatrixOperation#op(org.abh.lib.math.matrix.
     * DataFrame)
     */
    @Override
    public DataFrame op(DataFrame m) {
      return null;
    }
  }

  /**
   * Initialize a matrix transformation pipeline to operate on a matrix in a
   * functional manner.
   *
   * @return the matrix operation
   */
  public static MatrixOperation transform() {
    return new NullMatrixOperation();
  }

  /**
   * The Class MinMatrixOperation.
   */
  private static class MinMatrixOperation extends MatrixOperation {

    /**
     * The member x.
     */
    private double mX;

    /**
     * Instantiates a new adds the matrix operation.
     *
     * @param mo the mo
     * @param x the x
     */
    public MinMatrixOperation(MatrixOperation mo, double x) {
      super(mo);

      mX = x;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abh.lib.math.matrix.MatrixOperation#op(org.abh.lib.math.matrix.
     * DataFrame)
     */
    @Override
    public DataFrame op(DataFrame m) {
      return MatrixOperations.min(m, mX);
    }
  }

  /**
   * Min.
   *
   * @param x the x
   * @return the matrix operation
   */
  public MatrixOperation min(double x) {
    return new MinMatrixOperation(this, x);
  }

  //
  // Log functions
  //

  /**
   * The class Log2MatrixOperation.
   */
  private static class Log2MatrixOperation extends MatrixOperation {

    /**
     * Instantiates a new log2 matrix operation.
     *
     * @param mo the mo
     */
    public Log2MatrixOperation(MatrixOperation mo) {
      super(mo);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abh.lib.math.matrix.MatrixOperation#op(org.abh.lib.math.matrix.
     * DataFrame)
     */
    @Override
    public DataFrame op(DataFrame m) {
      return MatrixOperations.log2(m);
    }
  }

  /**
   * Log2.
   *
   * @return the matrix operation
   */
  public MatrixOperation log2() {
    return new Log2MatrixOperation(this);
  }

  /**
   * The class Log10MatrixOperation.
   */
  private static class Log10MatrixOperation extends MatrixOperation {

    /**
     * Instantiates a new log10 matrix operation.
     *
     * @param mo the mo
     */
    public Log10MatrixOperation(MatrixOperation mo) {
      super(mo);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abh.lib.math.matrix.MatrixOperation#op(org.abh.lib.math.matrix.
     * DataFrame)
     */
    @Override
    public DataFrame op(DataFrame m) {
      return MatrixOperations.log10(m);
    }
  }

  /**
   * Log10.
   *
   * @return the matrix operation
   */
  public MatrixOperation log10() {
    return new Log10MatrixOperation(this);
  }

  /**
   * The class Log10MatrixOperation.
   */
  private static class LnMatrixOperation extends MatrixOperation {

    /**
     * Instantiates a new log10 matrix operation.
     *
     * @param mo the mo
     */
    public LnMatrixOperation(MatrixOperation mo) {
      super(mo);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.abh.lib.math.matrix.MatrixOperation#op(org.abh.lib.math.matrix.
     * DataFrame)
     */
    @Override
    public DataFrame op(DataFrame m) {
      return MatrixOperations.ln(m);
    }
  }

  /**
   * Log10.
   *
   * @return the matrix operation
   */
  public MatrixOperation ln() {
    return new LnMatrixOperation(this);
  }
}
