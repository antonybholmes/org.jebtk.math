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
package org.jebtk.math.matrix;

import org.jebtk.core.event.ChangeEvent;
import org.jebtk.core.event.EventProducer;

/**
 * Listen for when an underlying matrix changes.
 *
 * @author Antony Holmes
 */
public class MatrixEventListeners extends EventProducer<MatrixEventListener>
    implements MatrixEventProducer {

  /**
   * The constant serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.abh.lib.math.matrix.MatrixEventProducer#addMatrixListener(org.abh.lib.
   * math.matrix.MatrixEventListener)
   */
  @Override
  public void addMatrixListener(MatrixEventListener l) {
    mListeners.add(l);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.abh.lib.math.matrix.MatrixEventProducer#removeMatrixListener(org.abh.
   * lib.math.matrix.MatrixEventListener)
   */
  @Override
  public void removeMatrixListener(MatrixEventListener l) {
    mListeners.remove(l);
  }

  /**
   * Should be fired when the matrix is updated.
   */
  public void fireMatrixChanged() {
    fireMatrixChanged(new ChangeEvent(this, MATRIX_CHANGED_EVENT));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.abh.lib.math.matrix.MatrixEventProducer#fireMatrixChanged(org.abh.lib.
   * event.ChangeEvent)
   */
  @Override
  public void fireMatrixChanged(ChangeEvent e) {
    for (MatrixEventListener l : mListeners) {
      l.matrixChanged(e);
    }
  }
}