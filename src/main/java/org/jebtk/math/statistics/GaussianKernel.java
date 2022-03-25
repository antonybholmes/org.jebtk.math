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
package org.jebtk.math.statistics;

// TODO: Auto-generated Javadoc
/**
 * The class GaussianKernel.
 */
public class GaussianKernel implements Kernel {

  /**
   * The factor.
   */
  private double mFactor = -1;

  /** The m mean. */
  private double mMean;

  /** The m var. */
  private double mVar;

  /**
   * Instantiates a new gaussian kernel.
   */
  public GaussianKernel() {
    this(0, 1);
  }

  /**
   * Instantiates a new gaussian kernel.
   *
   * @param sd the sd
   */
  public GaussianKernel(double sd) {
    this(0, sd);
  }

  /**
   * Instantiates a new gaussian kernel.
   *
   * @param mean the mean
   * @param sd the sd
   */
  public GaussianKernel(double mean, double sd) {
    mMean = mean;
    mVar = sd * sd;

    mFactor = 1.0 / (sd * Math.sqrt(2.0 * Math.PI));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.statistics.Kernel#evaluate(double)
   */
  @Override
  public double evaluate(double x) {
    double num = x - mMean;

    num *= num;

    double denom = 2 * mVar;

    return mFactor * Math.exp(-num / denom);
  }

}
