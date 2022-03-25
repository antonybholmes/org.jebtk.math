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

import java.util.List;

/**
 * Parses a text file and creates a matrix from it.
 * 
 * @author Antony Holmes
 */
public class TextMatrixParser extends MixedMatrixParser {

  
  public TextMatrixParser(int headers, 
      int rowAnnotations,
      String delimiter) {
    super(headers, rowAnnotations, delimiter);
  }
  
  /**
   * Instantiates a new text matrix parser.
   *
   * @param hasHeader the has header
   * @param skipMatches can be used to define rows/columns that should be
   *          skipped
   * @param rowAnnotations the row annotations
   * @param delimiter the delimiter
   */
  public TextMatrixParser(int headers, List<String> skipMatches,
      int rowAnnotations, String delimiter) {
    super(headers, skipMatches, rowAnnotations, delimiter);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.MixedMatrixParser#createMatrix(int, int)
   */
  @Override
  public DataFrame createMatrix(int rows, int columns) {
    return DataFrame.createTextMatrix(rows, columns);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.jebtk.math.matrix.MixedMatrixParser#set(org.jebtk.math.matrix.Matrix,
   * int, int, java.lang.String)
   */
  @Override
  protected void set(Matrix matrix, int row, int column, String value) {
    // We are only interested in strings.
    matrix.update(row, column, value);
  }
}
