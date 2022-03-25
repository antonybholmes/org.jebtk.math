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
 * The class EstMatrixParser.
 *
 * @param <T> the generic type
 */
public abstract class EstMatrixParser<T> implements MatrixParser {

  /**
   * The constant EST_VERSION_2.
   */
  private static final String EST_VERSION_2 = "#2.0";

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.math.matrix.MatrixParser#parse(java.io.Path)
   */
  @Override
  public DataFrame parse(Path file) throws IOException {
    DataFrame matrix = null;

    if (!FileUtils.exists(file)) {
      return null;
    }

    BufferedReader reader = FileUtils.newBufferedReader(file);

    String line;

    List<String> tokens;

    try {

      line = reader.readLine();

      tokens = TextUtils.fastSplit(line, TextUtils.TAB_DELIMITER);

      if (tokens.get(0).equals(EST_VERSION_2)) {
        matrix = parseV2(reader);
      } else {
        // parse version 1
        matrix = parseV1(reader);
      }
    } finally {
      reader.close();
    }

    return matrix;
  }

  /**
   * Parses the v1.
   *
   * @param reader the reader
   * @return the annotation matrix
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private DataFrame parseV1(BufferedReader reader) throws IOException {
    DataFrame matrix = null;

    int r = 0;
    int c = 0;
    int size;

    List<String> tokens;

    String line;

    // rows
    line = reader.readLine();
    tokens = TextUtils.fastSplit(line, TextUtils.TAB_DELIMITER);
    r = Integer.parseInt(tokens.get(1));

    // columns
    line = reader.readLine();
    tokens = TextUtils.fastSplit(line, TextUtils.TAB_DELIMITER);
    c = Integer.parseInt(tokens.get(1));

    // Create the matrix
    matrix = createMatrix(r, c);

    // groups
    line = reader.readLine();
    tokens = TextUtils.fastSplit(line, TextUtils.TAB_DELIMITER);
    size = Integer.parseInt(tokens.get(1));
    // List<String> groupDefinitions = ArrayUtils.subList(tokens, 2, size);

    // for (String groupDefinition : groupDefinitions) {
    // matrix.addColumnGroup(Group.parse(groupDefinition));
    // }

    // row annotations
    line = reader.readLine();
    tokens = TextUtils.fastSplit(line, TextUtils.TAB_DELIMITER);
    size = Integer.parseInt(tokens.get(1));
    List<String> rowNames = CollectionUtils.subList(tokens, 2, size);

    // column annotations
    line = reader.readLine();
    tokens = TextUtils.fastSplit(line, TextUtils.TAB_DELIMITER);
    size = Integer.parseInt(tokens.get(1));
    List<String> columnNames = CollectionUtils.subList(tokens, 2, size);

    // add column names
    line = reader.readLine();
    tokens = TextUtils.fastSplit(line, TextUtils.TAB_DELIMITER);

    System.err.println("num c ids " + line + " " + rowNames.size() + " "
        + CollectionUtils.head(tokens, rowNames.size() + 1).toString());

    matrix.setColumnNames(CollectionUtils.head(tokens, rowNames.size() + 1));

    // add the existing column annotation
    for (String name : columnNames) {
      line = reader.readLine();
      tokens = TextUtils.fastSplit(line, TextUtils.TAB_DELIMITER);
      matrix.getColumnHeader().setAnnotation(name,
          CollectionUtils.head(tokens, rowNames.size() + 1).toArray());
    }

    int row = 0;

    while ((line = reader.readLine()) != null) {
      if (Io.isEmptyLine(line)) {
        continue;
      }

      tokens = TextUtils.fastSplit(line, TextUtils.TAB_DELIMITER);

      matrix.setRowName(row, tokens.get(0));

      // add row annotations
      for (int i = 0; i < rowNames.size(); ++i) {
        matrix.getIndex().setAnnotation(rowNames.get(i), row, tokens.get(i + 1));
      }

      // the first token is the column name so ignore it
      for (int i = rowNames.size() + 1; i < tokens.size(); ++i) {
        set(matrix, row, i - rowNames.size() - 1, tokens.get(i));
      }

      ++row;
    }

    return matrix;
  }

  /**
   * Parses the v2.
   *
   * @param reader the reader
   * @return the annotation matrix
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private DataFrame parseV2(BufferedReader reader) throws IOException {
    DataFrame matrix = null;

    int rows = 0;
    int columns = 0;
    int size;

    List<String> tokens;

    String line;

    System.err.println("Parsing V2");

    // rows
    line = reader.readLine();
    tokens = TextUtils.fastSplit(line, TextUtils.TAB_DELIMITER);
    rows = Integer.parseInt(tokens.get(1));

    // columns
    line = reader.readLine();
    tokens = TextUtils.fastSplit(line, TextUtils.TAB_DELIMITER);
    columns = Integer.parseInt(tokens.get(1));

    // Create the matrix
    matrix = createMatrix(rows, columns);

    // groups
    line = reader.readLine();
    tokens = TextUtils.fastSplit(line, TextUtils.TAB_DELIMITER);
    size = Integer.parseInt(tokens.get(1));
    // List<String> groupDefinitions = ArrayUtils.subList(tokens, 2, size);

    // for (String groupDefinition : groupDefinitions) {
    // matrix.addColumnGroup(Group.parse(groupDefinition));
    // }

    //
    // row annotations
    //

    line = reader.readLine();
    tokens = TextUtils.fastSplit(line, TextUtils.TAB_DELIMITER);
    size = Integer.parseInt(tokens.get(1));

    for (int i = 0; i < size; ++i) {
      line = reader.readLine();
      tokens = TextUtils.fastSplit(line, TextUtils.TAB_DELIMITER);

      String name = tokens.get(1);

      List<String> values = CollectionUtils.subList(tokens, 2, rows);

      matrix.getIndex().setAnnotation(name, values.toArray());
    }

    //
    // column annotations
    //

    line = reader.readLine();
    tokens = TextUtils.fastSplit(line, TextUtils.TAB_DELIMITER);
    size = Integer.parseInt(tokens.get(1));

    for (int i = 0; i < size; ++i) {
      line = reader.readLine();
      tokens = TextUtils.fastSplit(line, TextUtils.TAB_DELIMITER);

      String name = tokens.get(1);

      // The number of values must match the number of columns
      List<String> values = CollectionUtils.subList(tokens, 2, columns);

      matrix.getColumnHeader().setAnnotation(name, values.toArray());
    }

    // Skip #MATRIX
    reader.readLine();

    for (int r = 0; r < rows; ++r) {
      line = reader.readLine();

      if (Io.isEmptyLine(line)) {
        continue;
      }

      tokens = TextUtils.fastSplit(line, TextUtils.TAB_DELIMITER);

      for (int c = 0; c < columns; ++c) {
        set(matrix, r, c, tokens.get(c));
      }
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
  protected abstract DataFrame createMatrix(int rows, int columns);

  /**
   * Convert the string value into one suitable for an array of type T.
   *
   * @param matrix the matrix
   * @param row the row
   * @param column the column
   * @param value the value
   */
  protected abstract void set(Matrix matrix, int row, int column, String value);
}
