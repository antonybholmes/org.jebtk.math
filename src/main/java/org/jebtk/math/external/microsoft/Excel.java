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
package org.jebtk.math.external.microsoft;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jebtk.core.Mathematics;
import org.jebtk.core.collections.ArrayUtils;
import org.jebtk.core.io.FileUtils;
import org.jebtk.core.io.Io;
import org.jebtk.core.io.PathUtils;
import org.jebtk.core.text.TextUtils;
import org.jebtk.math.matrix.DataFrame;

/**
 * Functions for reading and writing Excel files.
 * 
 * @author Antony Holmes
 *
 */
public class Excel {

  /**
   * The constant XLS_EXTENSION.
   */
  public static final String XLS_EXTENSION = "xls";

  /**
   * The constant XLSX_EXTENSION.
   */
  public static final String XLSX_EXTENSION = "xlsx";

  /**
   * The constant BLACK.
   */
  public static final XSSFColor BLACK = new XSSFColor(new Color(1, 1, 1));

  /**
   * Instantiates a new excel.
   */
  private Excel() {
    // Do nothing
  }

  /**
   * Convert to matrix.
   *
   * @param file the file
   * @param hasHeader the has header
   * @param skipMatches the skip matches
   * @param rowAnnotations the row annotations
   * @param delimiter the delimiter
   * @return the annotation matrix
   * @throws InvalidFormatException the invalid format exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static DataFrame convertToMatrix(Path file,
      int headers,
      List<String> skipMatches,
      int rowAnnotations,
      String delimiter) throws InvalidFormatException, IOException {
    if (ExcelPathUtils.ext().xlsx().test(file)) {
      return convertXlsxToMatrix(file, headers > 0, rowAnnotations);
    } else if (ExcelPathUtils.ext().xls().test(file)) {
      return convertXlsToMatrix(file, headers > 0, rowAnnotations);
    } else if (PathUtils.ext().csv().test(file)) {
      return DataFrame
          .parseCsvMatrix(file, headers, skipMatches, rowAnnotations);
    } else {
      return DataFrame.parseTxtMatrix(file,
          headers,
          skipMatches,
          rowAnnotations,
          delimiter);
    }
  }

  /**
   * Creates the xls workbook.
   *
   * @param file the file
   * @return the HSSF workbook
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static HSSFWorkbook createXlsWorkbook(Path file) throws IOException {
    return new HSSFWorkbook(FileUtils.newBufferedInputStream(file));
  }

  /**
   * Creates the xlsx workbook.
   *
   * @param file the file
   * @return the XSSF workbook
   * @throws InvalidFormatException the invalid format exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static XSSFWorkbook createXlsxWorkbook(Path file)
      throws InvalidFormatException, IOException {
    return new XSSFWorkbook(OPCPackage.open(file.toFile()));
  }

  // public static XLSXMetaData xLSXMetaData(Path file) throws IOException,
  // OpenXML4JException, SAXException {
  // return new XLSXMetaData(file);
  // }

  /**
   * Convert xlsx to matrix.
   *
   * @param file the file
   * @param hasHeader the has header
   * @param rowAnnotations the row annotations
   * @return the annotation matrix
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws InvalidFormatException the invalid format exception
   */
  public static DataFrame convertXlsxToMatrix(Path file,
      boolean hasHeader,
      int rowAnnotations) throws IOException, InvalidFormatException {
    XSSFWorkbook workbook = createXlsxWorkbook(file);

    DataFrame ret = convertXlsxToMatrix(workbook, hasHeader, rowAnnotations);

    workbook.close();

    return ret;
  }

  /**
   * Convert xlsx to matrix.
   *
   * @param workbook the workbook
   * @param hasHeader the has header
   * @param rowAnnotations the row annotations
   * @return the annotation matrix
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws InvalidFormatException the invalid format exception
   */
  public static DataFrame convertXlsxToMatrix(XSSFWorkbook workbook,
      boolean hasHeader,
      int rowAnnotations) throws IOException, InvalidFormatException {
    hasHeader = hasHeader || rowAnnotations > 0;

    // We use the evaluator to evaluate formulas to values before
    // parsing the values into memory
    FormulaEvaluator evaluator = workbook.getCreationHelper()
        .createFormulaEvaluator();

    XSSFSheet sheet = workbook.getSheetAt(0);

    int rows = sheet.getPhysicalNumberOfRows() - (hasHeader ? 1 : 0);

    int cols = sheet.getRow(0).getPhysicalNumberOfCells() - rowAnnotations;

    DataFrame matrix = DataFrame.createMixedMatrix(rows, cols); // new
                                                                // MixedSparseMatrix(r,
                                                                // c);

    // lets create some row headings

    List<String> rowHeadings = new ArrayList<String>();

    if (hasHeader) {
      System.err.println("Excel file has header");

      for (int i = 0; i < rowAnnotations; ++i) {
        XSSFCell cell = sheet.getRow(0).getCell(i);

        if (evaluator.evaluateInCell(cell)
            .getCellType() == Cell.CELL_TYPE_NUMERIC) {
          rowHeadings.add(Double.toString(cell.getNumericCellValue()));
        } else {
          rowHeadings.add(cell.getStringCellValue());
        }
      }

      for (int i = 0; i < cols; ++i) {
        XSSFCell cell = sheet.getRow(0).getCell(rowAnnotations + i);

        if (cell != null) {
          if (evaluator.evaluateInCell(cell)
              .getCellType() == Cell.CELL_TYPE_NUMERIC) {
            matrix.setColumnName(i,
                Double.toString(cell.getNumericCellValue()));
          } else {
            matrix.setColumnName(i, cell.getStringCellValue());
          }
        }
      }
    }

    for (int i = 0; i < rows; ++i) {
      // if there is a header, we must read from the next row
      int r = i + (hasHeader ? 1 : 0);

      if (hasHeader) {
        // We can't have row annotations unless there is a header
        for (int j = 0; j < rowAnnotations; ++j) {
          XSSFCell cell = sheet.getRow(r).getCell(j);

          if (cell != null) {
            if (evaluator.evaluateInCell(cell)
                .getCellType() == Cell.CELL_TYPE_NUMERIC) {
              matrix.getIndex().setAnnotation(rowHeadings.get(j),
                  i,
                  Double.toString(cell.getNumericCellValue()));
            } else {
              matrix.getIndex().setAnnotation(rowHeadings.get(j),
                  i,
                  cell.getStringCellValue());
            }
          }
        }
      }

      for (int j = 0; j < cols; ++j) {
        XSSFCell cell = sheet.getRow(r).getCell(rowAnnotations + j);

        if (cell != null) {
          if (evaluator.evaluateInCell(cell)
              .getCellType() == Cell.CELL_TYPE_NUMERIC) {
            matrix.set(i, j, cell.getNumericCellValue());
          } else {
            matrix.set(i, j, cell.getStringCellValue());
          }
        } else {
          matrix.set(i, j, TextUtils.EMPTY_STRING);
        }
      }
    }

    return matrix;
  }

  /**
   * Convert xlsx to matrix.
   *
   * @param file the file
   * @param hasHeader the has header
   * @param rowAnnotations the row annotations
   * @return the annotation matrix
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws InvalidFormatException the invalid format exception
   */
  public static DataFrame convertXlsToMatrix(Path file,
      boolean hasHeader,
      int rowAnnotations) throws IOException, InvalidFormatException {
    HSSFWorkbook workbook = createXlsWorkbook(file);

    DataFrame ret = convertXlsToMatrix(workbook, hasHeader, rowAnnotations);

    workbook.close();

    return ret;
  }

  /**
   * Convert xls to matrix.
   *
   * @param workbook the workbook
   * @param hasHeader the has header
   * @param rowAnnotations the row annotations
   * @return the annotation matrix
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws InvalidFormatException the invalid format exception
   */
  public static DataFrame convertXlsToMatrix(HSSFWorkbook workbook,
      boolean hasHeader,
      int rowAnnotations) throws IOException, InvalidFormatException {
    hasHeader = hasHeader || rowAnnotations > 0;

    // We use the evaluator to evaluate formulas to values before
    // parsing the values into memory
    FormulaEvaluator evaluator = workbook.getCreationHelper()
        .createFormulaEvaluator();

    HSSFSheet sheet = workbook.getSheetAt(0);

    int rows = sheet.getPhysicalNumberOfRows() - (hasHeader ? 1 : 0);

    int cols = sheet.getRow(0).getPhysicalNumberOfCells() - rowAnnotations;

    DataFrame matrix = DataFrame.createWorksheet(rows, cols); // .createMatrix(rows,
    // cols); //new
    // MixedSparseMatrix(r,
    // c);

    // lets create some row headings

    List<String> rowHeadings = new ArrayList<String>();

    if (hasHeader) {
      System.err.println("Excel file has header");

      for (int i = 0; i < rowAnnotations; ++i) {
        HSSFCell cell = sheet.getRow(0).getCell(i);

        if (evaluator.evaluateInCell(cell)
            .getCellType() == Cell.CELL_TYPE_NUMERIC) {
          rowHeadings.add(Double.toString(cell.getNumericCellValue()));
        } else {
          rowHeadings.add(cell.getStringCellValue());
        }
      }

      for (int i = 0; i < cols; ++i) {
        HSSFCell cell = sheet.getRow(0).getCell(rowAnnotations + i);

        if (cell != null) {
          if (evaluator.evaluateInCell(cell)
              .getCellType() == Cell.CELL_TYPE_NUMERIC) {
            matrix.setColumnName(i,
                Double.toString(cell.getNumericCellValue()));
          } else {
            matrix.setColumnName(i, cell.getStringCellValue());
          }
        }
      }
    }

    for (int i = 0; i < rows; ++i) {
      // if there is a header, we must read from the next row
      int r = i + (hasHeader ? 1 : 0);

      if (hasHeader) {
        // We can't have row annotations unless there is a header
        for (int j = 0; j < rowAnnotations; ++j) {
          HSSFCell cell = sheet.getRow(r).getCell(j);

          if (cell != null) {
            if (evaluator.evaluateInCell(cell)
                .getCellType() == Cell.CELL_TYPE_NUMERIC) {
              matrix.getIndex().setAnnotation(rowHeadings.get(j),
                  i,
                  Double.toString(cell.getNumericCellValue()));
            } else {
              matrix.getIndex().setAnnotation(rowHeadings.get(j),
                  i,
                  cell.getStringCellValue());
            }
          }
        }
      }

      for (int j = 0; j < cols; ++j) {
        HSSFCell cell = sheet.getRow(r).getCell(rowAnnotations + j);

        if (cell != null) {
          if (evaluator.evaluateInCell(cell)
              .getCellType() == Cell.CELL_TYPE_NUMERIC) {
            matrix.set(i, j, cell.getNumericCellValue());
          } else {
            matrix.set(i, j, cell.getStringCellValue());
          }
        } else {
          matrix.set(i, j, TextUtils.EMPTY_STRING);
        }
      }
    }

    return matrix;
  }

  /**
   * Create a workbook from a matrix.
   *
   * @param matrix the matrix
   * @param file the file
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void writeXlsx(DataFrame matrix, Path file) throws IOException {
    XSSFWorkbook workbook = createWorkbook(matrix);

    writeXlsx(workbook, file);
  }

  /**
   * Write xlsx.
   *
   * @param workbook the workbook
   * @param file the file
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void writeXlsx(XSSFWorkbook workbook, Path file)
      throws IOException {

    // Also ensure the file has the correct file extension
    OutputStream out = Files
        .newOutputStream(PathUtils.addExtension(file, XLSX_EXTENSION));

    try {
      workbook.write(out);
    } finally {
      out.close();
    }
  }

  /**
   * Write xls.
   *
   * @param matrix the matrix
   * @param file the file
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void writeXls(DataFrame matrix, Path file) throws IOException {
    HSSFWorkbook workbook = createXlsWorkbook(matrix);

    writeXls(workbook, file);
  }

  /**
   * Write xls.
   *
   * @param workbook the workbook
   * @param file the file
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void writeXls(HSSFWorkbook workbook, Path file)
      throws IOException {

    // Also ensure the file has the correct file extension
    OutputStream out = Files
        .newOutputStream(PathUtils.addExtension(file, XLS_EXTENSION));

    try {
      workbook.write(out);
    } finally {
      out.close();
    }
  }

  /**
   * Write xlsx locked.
   *
   * @param workbook the workbook
   * @param file the file
   * @param password the password
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void writeXlsxLocked(XSSFWorkbook workbook,
      Path file,
      String password) throws IOException {

    for (int i = 0; i < workbook.getNumberOfSheets(); ++i) {
      XSSFSheet sheet = workbook.getSheetAt(i);

      sheet.protectSheet(password);
    }

    // String password = "tmwrnj"; //"abcd";

    /*
     * byte[] pwdBytes = null;
     * 
     * 
     * try { pwdBytes = Hex.decodeHex(password.toCharArray()); } catch
     * (DecoderException e) { e.printStackTrace(); }
     * 
     * 
     * sheet.lockDeleteColumns(); sheet.lockDeleteRows();
     * sheet.lockFormatCells(); sheet.lockFormatColumns();
     * sheet.lockFormatRows(); sheet.lockInsertColumns();
     * sheet.lockInsertRows();
     * 
     * 
     * 
     * CTSheetProtection sheetProtection =
     * sheet.getCTWorksheet().getSheetProtection();
     * 
     * 
     * System.err.println("sdfds " + (sheetProtection == null) + " " +
     * pwdBytes);
     * 
     * sheetProtection.setPassword(pwdBytes);
     * 
     * sheet.enableLocking();
     * 
     * workbook.lockStructure();
     */

    // Also ensure the file has the correct file extension

    OutputStream out = Files.newOutputStream(file);

    try {
      workbook.write(out);
    } finally {
      out.close();
    }
  }

  /**
   * Create a workbook from a model.
   *
   * @param m the m
   * @return the XSSF workbook
   */
  public static XSSFWorkbook createWorkbook(DataFrame m) {
    XSSFWorkbook workbook = new XSSFWorkbook();

    createWorkSheet(m, workbook);

    return workbook;
  }

  /**
   * Creates the work sheet.
   *
   * @param m the m
   * @param workbook the workbook
   * @return the XSSF workbook
   */
  public static XSSFWorkbook createWorkSheet(DataFrame m,
      XSSFWorkbook workbook) {
    Sheet sheet = workbook
        .createSheet("Sheet" + (workbook.getNumberOfSheets() + 1));

    // Keep track of how many rows we have created.
    int r = 0;
    int c = 0;

    // All cells get a default style

    XSSFFont font = workbook.createFont();
    font.setFontHeightInPoints((short) 11);
    font.setFontName("Arial");

    // Because of some stupid bug in POI, black appears as white
    // in the Excel file, so we pick a color very close to black
    // and use that instead
    // font.setColor(new XSSFColor(new Color(1, 1, 1)));

    XSSFCellStyle headerStyle = workbook.createCellStyle();
    headerStyle.setFont(font);
    headerStyle.setWrapText(true);

    XSSFCellStyle defaultStyle = workbook.createCellStyle();
    defaultStyle.setFont(font);
    defaultStyle.setWrapText(true);

    XSSFRow row;
    XSSFCell cell;

    //
    // Create the header
    //

    List<String> names = m.getColumnHeader().getNames();
    List<String> rowHeadings = m.getIndex().getNames();

    for (int i = 0; i < names.size() - 1; ++i) {
      row = (XSSFRow) sheet.createRow(r++);

      createBlankCells(row, rowHeadings.size());

      String name = names.get(i);

      for (int j = 0; j < m.getCols(); ++j) {
        cell = row.createCell(i);

        cell.setCellStyle(headerStyle);
        cell.setCellValue(m.getColumnHeader().getText(name, j));
      }
    }

    // deal with the last header by also adding in the row headers
    if (names.size() > 0) {
      c = 0;

      row = (XSSFRow) sheet.createRow(r++);

      for (String name : rowHeadings) {
        cell = row.createCell(c++);

        cell.setCellStyle(headerStyle);
        cell.setCellValue(name);
      }

      for (int j = 0; j < m.getCols(); ++j) {
        cell = row.createCell(c++);

        cell.setCellStyle(headerStyle);
        cell.setCellValue(
            m.getColumnHeader().getText(names.get(names.size() - 1), j));
      }
    }

    for (int i = 0; i < m.getRows(); ++i) {
      c = 0;

      row = (XSSFRow) sheet.createRow(r++);

      for (String name : rowHeadings) {
        cell = row.createCell(c++);

        cell.setCellStyle(defaultStyle);

        Object v = m.getIndex().getAnnotation(name, i);

        if (v instanceof Number) {
          cell.setCellValue(((Number) v).doubleValue());
        } else {
          cell.setCellValue(v.toString());
        }
      }

      for (int j = 0; j < m.getCols(); ++j) {
        cell = row.createCell(c++);
        cell.setCellStyle(defaultStyle);

        Object v = m.get(i, j);

        if (v instanceof Number) {
          cell.setCellValue(((Number) v).doubleValue());
        } else {
          // String value = m.getText(i, j);

          // if (value != null) {
          cell.setCellValue(m.getText(i, j));
          // }
        }
      }
    }

    // Auto size all the columns
    for (int i = 0; i < sheet.getRow(0).getPhysicalNumberOfCells(); i++) {
      // sheet.autoSizeColumn(i);

      sheet.setColumnWidth(i, 256 * 30);
    }

    return workbook;
  }

  /**
   * Create a workbook from a model.
   *
   * @param m the m
   * @return the XSSF workbook
   */
  public static HSSFWorkbook createXlsWorkbook(DataFrame m) {
    HSSFWorkbook workbook = new HSSFWorkbook();

    createXlsWorkSheet(m, workbook);

    return workbook;
  }

  /**
   * Creates the work sheet.
   *
   * @param m the m
   * @param workbook the workbook
   * @return the XSSF workbook
   */
  public static HSSFWorkbook createXlsWorkSheet(DataFrame m,
      HSSFWorkbook workbook) {
    Sheet sheet = workbook
        .createSheet("Sheet" + (workbook.getNumberOfSheets() + 1));

    // Keep track of how many rows we have created.
    int r = 0;
    int c = 0;

    // All cells get a default style

    HSSFFont font = workbook.createFont();
    font.setFontHeightInPoints((short) 11);
    font.setFontName("Arial");

    // Because of some stupid bug in POI, black appears as white
    // in the Excel file, so we pick a color very close to black
    // and use that instead
    // font.setColor(new XSSFColor(new Color(1, 1, 1)));

    HSSFCellStyle headerStyle = workbook.createCellStyle();
    headerStyle.setFont(font);
    headerStyle.setWrapText(true);

    HSSFCellStyle defaultStyle = workbook.createCellStyle();
    defaultStyle.setFont(font);
    defaultStyle.setWrapText(true);

    XSSFRow row;
    XSSFCell cell;

    //
    // Create the header
    //

    List<String> names = m.getColumnHeader().getNames();
    List<String> rowHeadings = m.getIndex().getNames();

    for (int i = 0; i < names.size() - 1; ++i) {
      row = (XSSFRow) sheet.createRow(r++);

      createBlankCells(row, rowHeadings.size());

      String name = names.get(i);

      for (int j = 0; j < m.getCols(); ++j) {
        cell = row.createCell(i);

        cell.setCellStyle(headerStyle);
        cell.setCellValue(m.getColumnHeader().getText(name, j));
      }
    }

    // deal with the last header by also adding in the row headers
    if (names.size() > 0) {
      c = 0;

      row = (XSSFRow) sheet.createRow(r++);

      for (String name : rowHeadings) {
        cell = row.createCell(c++);

        cell.setCellStyle(headerStyle);
        cell.setCellValue(name);
      }

      for (int j = 0; j < m.getCols(); ++j) {
        cell = row.createCell(c++);

        cell.setCellStyle(headerStyle);
        cell.setCellValue(
            m.getColumnHeader().getText(names.get(names.size() - 1), j));
      }
    }

    for (int i = 0; i < m.getRows(); ++i) {
      c = 0;

      row = (XSSFRow) sheet.createRow(r++);

      for (String name : rowHeadings) {
        cell = row.createCell(c++);

        cell.setCellStyle(defaultStyle);

        Object v = m.getIndex().getAnnotation(name, i);

        if (v instanceof Number) {
          cell.setCellValue(((Number) v).doubleValue());
        } else {
          cell.setCellValue(v.toString());
        }
      }

      for (int j = 0; j < m.getCols(); ++j) {
        cell = row.createCell(c++);
        cell.setCellStyle(defaultStyle);

        double v = m.getValue(i, j);

        if (Mathematics.isValidNumber(v)) {
          cell.setCellValue(v);
        } else {
          String value = m.getText(i, j);

          if (value != null) {
            cell.setCellValue(value);
          }
        }
      }
    }

    // Auto size all the columns
    for (int i = 0; i < sheet.getRow(0).getPhysicalNumberOfCells(); i++) {
      // sheet.autoSizeColumn(i);

      sheet.setColumnWidth(i, 256 * 30);
    }

    return workbook;
  }

  /**
   * Create blank cells en mass.
   *
   * @param row the row
   * @param size the size
   */
  private static void createBlankCells(XSSFRow row, int size) {
    for (int i = 0; i < size; ++i) {
      row.createCell(i);
    }
  }

  /**
   * Returns a list of the sheets in a workbook.
   *
   * @param file the file
   * @return the sheet names
   * @throws FileNotFoundException the file not found exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static List<String> getSheetNames(Path file)
      throws FileNotFoundException, IOException {
    String ext = PathUtils.getFileExt(file);

    List<String> ret = new ArrayList<String>();

    if (ext.equals(Excel.XLS_EXTENSION)) {
      HSSFWorkbook workbook = new HSSFWorkbook(
          FileUtils.newBufferedInputStream(file));

      for (int i = 0; i < workbook.getNumberOfSheets(); ++i) {
        ret.add(workbook.getSheetName(i));
      }

      workbook.close();
    } else if (ext.equals(Excel.XLSX_EXTENSION)) {
      XSSFWorkbook workbook = new XSSFWorkbook(
          FileUtils.newBufferedInputStream(file));

      for (int i = 0; i < workbook.getNumberOfSheets(); ++i) {
        ret.add(workbook.getSheetName(i));
      }

      workbook.close();
    } else {
      // do nothing
    }

    return ret;
  }

  /**
   * Gets the text from file.
   *
   * @param file the file
   * @param skipHeader the skip header
   * @return the text from file
   * @throws InvalidFormatException the invalid format exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static String[] getTextFromFile(Path file, boolean skipHeader)
      throws InvalidFormatException, IOException {
    if (file == null) {
      return ArrayUtils.EMPTY_STRING_ARRAY;
    }

    String ext = PathUtils.getFileExt(file);

    if (ext.equals(Excel.XLS_EXTENSION)) {
      return loadXls(file, skipHeader);
    } else if (ext.equals(Excel.XLSX_EXTENSION)) {
      return loadXlsx(file, skipHeader);
    } else if (ext.equals(Io.FILE_EXT_CSV)) {
      return Io.loadCSVList(file, skipHeader);
    } else {
      return TextUtils.firstColAsList(file, skipHeader);
    }
  }

  /**
   * Gets the header.
   *
   * @param file the file
   * @return the header
   * @throws InvalidFormatException the invalid format exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static List<String> getHeader(Path file)
      throws InvalidFormatException, IOException {
    if (file == null) {
      return Collections.emptyList();
    }

    String ext = PathUtils.getFileExt(file);

    if (ext.equals(Excel.XLS_EXTENSION)) {
      return getXlsHeader(file);
    } else if (ext.equals(Excel.XLSX_EXTENSION)) {
      return getXlsxHeader(file);
    } else if (ext.equals(Io.FILE_EXT_CSV)) {
      return Io.getCSVHeader(file);
    } else {
      return Io.getHeader(file);
    }
  }

  /**
   * Create a simple workbook from a list of lines.
   *
   * @param lines the lines
   * @return the XSSF workbook
   */
  public static XSSFWorkbook create(List<String> lines) {
    XSSFWorkbook workbook = new XSSFWorkbook();

    Sheet sheet = workbook.createSheet("Sheet1");

    // Keep track of how many rows we have created.
    int r = 0;

    XSSFFont font = workbook.createFont();
    font.setFontHeightInPoints((short) 11);
    font.setFontName("Arial");

    XSSFCellStyle defaultStyle = workbook.createCellStyle();
    defaultStyle.setFont(font);
    defaultStyle.setWrapText(true);

    XSSFRow row;
    XSSFCell cell;

    for (String line : lines) {
      row = (XSSFRow) sheet.createRow(r);

      cell = row.createCell(0);
      cell.setCellStyle(defaultStyle);
      cell.setCellValue(new XSSFRichTextString(line));

      ++r;
    }

    return workbook;
  }

  /**
   * Save xlsx.
   *
   * @param lines the lines
   * @param file the file
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void saveXlsx(List<String> lines, Path file)
      throws IOException {
    writeXlsx(create(lines), file);
  }

  /**
   * Load.
   *
   * @param file the file
   * @param skipHeader the skip header
   * @return the list
   * @throws InvalidFormatException the invalid format exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static String[] load(Path file, boolean skipHeader)
      throws InvalidFormatException, IOException {
    if (file == null) {
      return ArrayUtils.EMPTY_STRING_ARRAY;
    }

    String ext = PathUtils.getFileExt(file);

    if (ext.equals(Excel.XLSX_EXTENSION)) {
      return loadXlsx(file, skipHeader);
    } else {
      return Io.getColumn(file, skipHeader);
    }
  }

  /**
   * Convert an excel file into a string list.
   *
   * @param file the file
   * @param skipHeader the skip header
   * @return the list
   * @throws InvalidFormatException the invalid format exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static String[] loadXlsx(Path file, boolean skipHeader)
      throws InvalidFormatException, IOException {
    XSSFWorkbook workbook = new XSSFWorkbook(OPCPackage.open(file.toFile()));

    XSSFSheet sheet = workbook.getSheetAt(0);

    String[] ret = new String[sheet.getPhysicalNumberOfRows()];

    for (int i = skipHeader ? 1 : 0; i < sheet.getPhysicalNumberOfRows(); ++i) {
      ret[i] = sheet.getRow(i).getCell(0).getStringCellValue();
    }

    workbook.close();

    return ret;
  }

  /**
   * Load xls.
   *
   * @param file the file
   * @param skipHeader the skip header
   * @return the list
   * @throws InvalidFormatException the invalid format exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static String[] loadXls(Path file, boolean skipHeader)
      throws InvalidFormatException, IOException {
    HSSFWorkbook workbook = new HSSFWorkbook(
        FileUtils.newBufferedInputStream(file));

    HSSFSheet sheet = workbook.getSheetAt(0);

    String[] ret = new String[sheet.getPhysicalNumberOfRows()];

    for (int i = skipHeader ? 1 : 0; i < sheet.getPhysicalNumberOfRows(); ++i) {
      ret[i] = sheet.getRow(i).getCell(0).getStringCellValue();
    }

    workbook.close();

    return ret;
  }

  /**
   * Gets the xlsx header.
   *
   * @param file the file
   * @return the xlsx header
   * @throws InvalidFormatException the invalid format exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static List<String> getXlsxHeader(Path file)
      throws InvalidFormatException, IOException {
    XSSFWorkbook workbook = new XSSFWorkbook(OPCPackage.open(file.toFile()));

    XSSFSheet sheet = workbook.getSheetAt(0);

    XSSFRow row = sheet.getRow(0);

    List<String> ret = new ArrayList<String>();

    for (int i = 0; i < row.getPhysicalNumberOfCells(); ++i) {
      ret.add(row.getCell(i).getStringCellValue());
    }

    workbook.close();

    return ret;
  }

  /**
   * Gets the xls header.
   *
   * @param file the file
   * @return the xls header
   * @throws InvalidFormatException the invalid format exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static List<String> getXlsHeader(Path file)
      throws InvalidFormatException, IOException {
    HSSFWorkbook workbook = new HSSFWorkbook(
        FileUtils.newBufferedInputStream(file));

    HSSFSheet sheet = workbook.getSheetAt(0);

    HSSFRow row = sheet.getRow(0);

    List<String> ret = new ArrayList<String>();

    for (int i = 0; i < row.getPhysicalNumberOfCells(); ++i) {
      ret.add(row.getCell(i).getStringCellValue());
    }

    workbook.close();

    return ret;
  }

  /**
   * Create some blank cells in a spreadsheet.
   *
   * @param n the n
   * @param row the row
   */
  public static void createEmptyColumns(int n, XSSFRow row) {
    int s = row.getLastCellNum();

    for (int i = 0; i < n; ++i) {
      row.createCell(s++);
    }
  }
}
