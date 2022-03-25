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

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.jebtk.core.collections.CollectionUtils;
import org.jebtk.core.io.FileUtils;
import org.jebtk.core.io.Io;
import org.jebtk.core.text.TextUtils;

/**
 * Parses a text file and creates a matrix from it.
 * 
 * @author Antony Holmes
 */
public class CsvMatrixParser implements MatrixParser {

  /**
   * The member row annotations.
   */
  protected int mRowAnnotations = -1;

  /**
   * The member has header.
   */
  protected boolean mHasHeader = false;

  /**
   * Instantiates a new text matrix parser.
   */
  public CsvMatrixParser() {
    this(true, 0);
  }

  /**
   * Instantiates a new text matrix parser.
   *
   * @param rowAnnotations the row annotations
   */
  public CsvMatrixParser(int rowAnnotations) {
    this(true, rowAnnotations);
  }

  /**
   * Instantiates a new text matrix parser.
   *
   * @param hasHeader the has header
   */
  public CsvMatrixParser(boolean hasHeader) {
    this(hasHeader, 0);
  }

  /**
   * Instantiates a new text matrix parser.
   *
   * @param hasHeader the has header
   * @param rowAnnotations the row annotations
   */
  public CsvMatrixParser(boolean hasHeader, int rowAnnotations) {
    // If row annotations >0, you must have a header otherwise the file
    // is garbage
    mHasHeader = hasHeader || rowAnnotations > 0;
    mRowAnnotations = rowAnnotations;
  }

  /**
   * Sets the.
   *
   * @param matrix the matrix
   * @param row the row
   * @param column the column
   * @param value the value
   */
  protected void set(Matrix matrix, int row, int column, String value) {
    if (TextUtils.isNumber(value)) {
      matrix.set(row, column, Double.parseDouble(value));
    } else {
      matrix.set(row, column, value);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.MatrixParser#parse(java.io.Path)
   */
  @Override
  public DataFrame parse(Path file) throws IOException {
    DataFrame matrix = null;

    BufferedReader reader = FileUtils.newBufferedReader(file);

    String line;

    List<String> tokens;

    int rows = mHasHeader ? 0 : 1;
    int columns = -1;

    try {
      line = reader.readLine();
      tokens = TextUtils.parseCSVLine(line); // ImmutableList.copyOf(Splitter.on(TextUtils.TAB_DELIMITER).split(line));
                                             // //TextUtils.tabSplit(line);

      columns = tokens.size() - mRowAnnotations;

      while ((line = reader.readLine()) != null) {
        if (Io.isEmptyLine(line)) {
          continue;
        }

        ++rows;
      }
    } finally {
      reader.close();
    }

    matrix = createMatrix(rows, columns);

    reader = FileUtils.newBufferedReader(file);

    List<String> rowAnnotationNames = null;

    try {
      if (mHasHeader) {
        // add column names
        line = reader.readLine();
        tokens = TextUtils.parseCSVLine(line);

        matrix.setColumnNames(CollectionUtils.subList(tokens, mRowAnnotations));

        rowAnnotationNames = CollectionUtils
            .subList(tokens, 0, mRowAnnotations);
      }

      int row = 0;

      while ((line = reader.readLine()) != null) {
        if (Io.isEmptyLine(line)) {
          continue;
        }

        tokens = TextUtils.parseCSVLine(line);

        if (mHasHeader) {
          for (int i = 0; i < mRowAnnotations; ++i) {
            matrix.getIndex().setAnnotation(rowAnnotationNames.get(i),
                row,
                tokens.get(i));
          }
        }

        // the first token is the column name so ignore it
        for (int i = mRowAnnotations; i < tokens.size(); ++i) {
          set(matrix, row, i - mRowAnnotations, tokens.get(i));
        }

        ++row;
      }
    } finally {
      reader.close();
    }

    return matrix;
  }

  /**
   * Creates the matrix.
   *
   * @param rows the rows
   * @param columns the columns
   * @return the annotation matrix
   */
  public DataFrame createMatrix(int rows, int columns) {
    return DataFrame.createDataFrame(rows, columns);
  }
}
