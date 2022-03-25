/**
 * Copyright 2017 Antony Holmes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jebtk.math.external.microsoft;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jebtk.core.text.TextUtils;
import org.jebtk.math.matrix.CellType;
import org.jebtk.math.matrix.DataFrame;
import org.jebtk.math.matrix.Matrix;
import org.jebtk.math.matrix.MatrixType;
import org.jebtk.math.matrix.MixedMatrix;
import org.jebtk.math.matrix.RegularMatrix;

/**
 * Ecapsulates an Excel worksheet inside a matrix.
 * 
 * @author Antony Holmes
 *
 */
public class ExcelMatrix extends RegularMatrix {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The m sheet. */
  private XSSFSheet mSheet;

  /** The m has header. */
  private boolean mHasHeader;

  /** The m row annotations. */
  private int mRowAnnotations;

  /** The m evaluator. */
  private FormulaEvaluator mEvaluator;

  /**
   * Instantiates a new excel matrix.
   *
   * @param sheet the sheet
   * @param evaluator the evaluator
   * @param hasHeader the has header
   * @param rowAnnotations the row annotations
   */
  private ExcelMatrix(XSSFSheet sheet, FormulaEvaluator evaluator,
      boolean hasHeader, int rowAnnotations) {
    super(getRows(sheet, hasHeader), getCols(sheet, rowAnnotations));

    mSheet = sheet;
    mEvaluator = evaluator;
    mHasHeader = hasHeader;
    mRowAnnotations = rowAnnotations;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#getType()
   */
  @Override
  public MatrixType getType() {
    return MatrixType.MIXED;
  }

  @Override
  public Matrix transpose() {
    return this;
  }

  @Override
  public Matrix ofSameType(int rows, int cols) {
    // In this instance we used a mixed matrix in place of the Excel matrix
    // since the Excel matrix is tied
    // to the underlying sheet object.
    return new MixedMatrix(rows, cols);
  }

  @Override
  public Matrix copy() {
    Matrix ret = ofSameType();

    ret.update(this);

    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#getCellType(int, int)
   */
  @Override
  public CellType getCellType(int row, int col) {
    int r = row + (mHasHeader ? 1 : 0);

    XSSFCell cell = mSheet.getRow(r).getCell(mRowAnnotations + col);

    if (cell != null) {
      if (mEvaluator.evaluateInCell(cell)
          .getCellType() == Cell.CELL_TYPE_NUMERIC) {
        return CellType.NUMBER;
      } else {
        return CellType.TEXT;
      }
    } else {
      return CellType.TEXT;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#getValue(int, int)
   */
  @Override
  public double getValue(int row, int col) {
    int r = row + (mHasHeader ? 1 : 0);

    XSSFCell cell = mSheet.getRow(r).getCell(mRowAnnotations + col);

    if (cell != null) {
      if (mEvaluator.evaluateInCell(cell)
          .getCellType() == Cell.CELL_TYPE_NUMERIC) {
        return cell.getNumericCellValue();
      } else {
        return 0;
      }
    } else {
      return 0;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.math.matrix.Matrix#getText(int, int)
   */
  @Override
  public String getText(int row, int col) {
    int r = row + (mHasHeader ? 1 : 0);

    XSSFCell cell = mSheet.getRow(r).getCell(mRowAnnotations + col);

    if (cell != null) {
      switch (mEvaluator.evaluateInCell(cell).getCellType()) {
      case Cell.CELL_TYPE_STRING:
        return cell.getStringCellValue();
      case Cell.CELL_TYPE_NUMERIC:
        return Double.toString(cell.getNumericCellValue());
      default:
        return TextUtils.EMPTY_STRING;
      }
    } else {
      return TextUtils.EMPTY_STRING;
    }
  }

  //
  // Static methods
  //

  /**
   * Gets the rows.
   *
   * @param sheet the sheet
   * @param hasHeader the has header
   * @return the rows
   */
  private static int getRows(XSSFSheet sheet, boolean hasHeader) {
    return sheet.getPhysicalNumberOfRows() - (hasHeader ? 1 : 0);
  }

  /**
   * Gets the cols.
   *
   * @param sheet the sheet
   * @param rowAnnotations the row annotations
   * @return the cols
   */
  private static int getCols(XSSFSheet sheet, int rowAnnotations) {
    return sheet.getRow(0).getPhysicalNumberOfCells() - rowAnnotations;
  }

  /**
   * Xlsx as matrix.
   *
   * @param file the file
   * @param hasHeader the has header
   * @param rowAnnotations the row annotations
   * @return the annotation matrix
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws InvalidFormatException the invalid format exception
   */
  public static DataFrame xlsxAsMatrix(Path file,
      boolean hasHeader,
      int rowAnnotations) throws IOException, InvalidFormatException {
    XSSFWorkbook workbook = Excel.createXlsxWorkbook(file);

    DataFrame ret = xlsxAsMatrix(workbook, hasHeader, rowAnnotations);

    workbook.close();

    return ret;
  }

  /**
   * Wrap an excel xlsx file object in a matrix for a consistent data view and
   * inter-operability with other matrices.
   *
   * @param workbook the workbook
   * @param hasHeader the has header
   * @param rowAnnotations the row annotations
   * @return the annotation matrix
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws InvalidFormatException the invalid format exception
   */
  public static DataFrame xlsxAsMatrix(XSSFWorkbook workbook,
      boolean hasHeader,
      int rowAnnotations) throws IOException, InvalidFormatException {
    hasHeader = hasHeader || rowAnnotations > 0;

    // We use the evaluator to evaluate formulas to values before
    // parsing the values into memory
    FormulaEvaluator evaluator = workbook.getCreationHelper()
        .createFormulaEvaluator();

    XSSFSheet sheet = workbook.getSheetAt(0);

    // int rows = sheet.getPhysicalNumberOfRows() - (hasHeader ? 1 : 0);

    // int cols = sheet.getRow(0).getPhysicalNumberOfCells() - rowAnnotations;

    ExcelMatrix excelMatrix = new ExcelMatrix(sheet, evaluator, hasHeader,
        rowAnnotations); // new MixedSparseMatrix(r, c);

    DataFrame m = DataFrame.createDataFrame(excelMatrix);

    //
    // Create the headings
    //

    List<String> rowHeadings = new ArrayList<String>();

    if (hasHeader) {
      for (int i = 0; i < rowAnnotations; ++i) {
        XSSFCell cell = sheet.getRow(0).getCell(i);

        if (evaluator.evaluateInCell(cell)
            .getCellType() == Cell.CELL_TYPE_NUMERIC) {
          rowHeadings.add(Double.toString(cell.getNumericCellValue()));
        } else {
          rowHeadings.add(cell.getStringCellValue());
        }
      }

      for (int i = 0; i < m.getCols(); ++i) {
        XSSFCell cell = sheet.getRow(0).getCell(rowAnnotations + i);

        if (cell != null) {
          if (evaluator.evaluateInCell(cell)
              .getCellType() == Cell.CELL_TYPE_NUMERIC) {
            m.setColumnName(i, Double.toString(cell.getNumericCellValue()));
          } else {
            m.setColumnName(i, cell.getStringCellValue());
          }
        }
      }
    }

    for (int i = 0; i < m.getRows(); ++i) {
      // if there is a header, we must read from the next row
      int r = i + (hasHeader ? 1 : 0);

      if (hasHeader) {
        // We can't have row annotations unless there is a header
        for (int j = 0; j < rowAnnotations; ++j) {
          XSSFCell cell = sheet.getRow(r).getCell(j);

          if (cell != null) {
            if (evaluator.evaluateInCell(cell)
                .getCellType() == Cell.CELL_TYPE_NUMERIC) {
              m.getIndex().setAnnotation(rowHeadings.get(j),
                  i,
                  Double.toString(cell.getNumericCellValue()));
            } else {
              m.getIndex().setAnnotation(rowHeadings.get(j),
                  i,
                  cell.getStringCellValue());
            }
          }
        }
      }
    }

    return m;
  }
}
