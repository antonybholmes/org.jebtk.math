package org.jebtk.math.external.microsoft;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.extractor.XSSFEventBasedExcelExtractor;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.jebtk.core.collections.CollectionUtils;
import org.jebtk.core.text.RegexUtils;
import org.jebtk.core.text.TextUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * A rudimentary XLSX -> CSV processor modeled on the POI sample program
 * XLS2CSVmra from the package org.apache.poi.hssf.eventusermodel.examples. As
 * with the HSSF version, this tries to spot missing rows and cells, and output
 * empty entries for them.
 * <p>
 * Data sheets are read using a SAX parser to keep the memory footprint
 * relatively small, so this should be able to read enormous workbooks. The
 * styles table and the shared-string table must be kept in memory. The standard
 * POI styles table class is used, but a custom (read-only) class is used for
 * the shared string table because the standard POI SharedStringsTable grows
 * very quickly with the number of unique strings.
 * <p>
 * For a more advanced implementation of SAX event parsing of XLSX files, see
 * {@link XSSFEventBasedExcelExtractor} and {@link XSSFSheetXMLHandler}. Note
 * that for many cases, it may be possible to simply use those with a custom
 * {@link SheetContentsHandler} and no SAX code needed of your own!
 */
public class XLSXMetaData {
  private List<String> mRow = new ArrayList<String>(100);

  /**
   * Uses the XSSF Event SAX helpers to do most of the work of parsing the Sheet
   * XML, and outputs the contents as a (basic) CSV.
   */
  private class SheetFirstRows implements SheetContentsHandler {
    private boolean mSecondRow;
    private int mCurrentRow;
    private int mCurrentCol;

    @Override
    public void startRow(int rowNum) {
      if (rowNum == 1) {
        mSecondRow = true;
        mCurrentRow = rowNum;
        mCurrentCol = -1;
      }
    }

    @Override
    public void endRow(int rowNum) {
      mSecondRow = false;
    }

    @Override
    public void cell(String cellReference,
        String formattedValue,
        XSSFComment comment) {
      if (!mSecondRow) {
        return;
      }

      CellReference address;

      if (cellReference != null) {
        address = new CellReference(cellReference);
      } else {
        address = new CellReference(mCurrentRow, mCurrentCol);
      }

      mRow.add(formattedValue);

      int thisCol = address.getCol();

      mCurrentCol = thisCol;
    }

    @Override
    public void headerFooter(String text, boolean isHeader, String tagName) {
      // Skip, no headers or footers in CSV
    }
  }

  public XLSXMetaData(Path file) {
    // The package open is instantaneous, as it should be.
    OPCPackage xlsxPackage;

    try {
      xlsxPackage = OPCPackage.open(file.toFile(), PackageAccess.READ);

      try {
        try {
          process(xlsxPackage);
        } catch (IOException | OpenXML4JException | SAXException e) {
          e.printStackTrace();
        }
      } finally {
        try {
          xlsxPackage.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    } catch (InvalidFormatException e) {
      e.printStackTrace();
    }
  }

  /**
   * Initiates the processing of the XLS workbook file to CSV.
   *
   * @throws IOException If reading the data from the package fails.
   * @throws SAXException if parsing the XML data fails.
   */
  public void process(OPCPackage xlsxPackage)
      throws IOException, OpenXML4JException, SAXException {
    ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(
        xlsxPackage);

    XSSFReader xssfReader = new XSSFReader(xlsxPackage);

    StylesTable styles = xssfReader.getStylesTable();

    XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader
        .getSheetsData();

    // First sheet
    InputStream inputStream = iter.next();
    // String sheetName = iter.getSheetName();

    try {
      processSheet(styles, strings, new SheetFirstRows(), inputStream);
    } finally {
      inputStream.close();
    }
  }

  /**
   * Parses and shows the content of one sheet using the specified styles and
   * shared-strings tables.
   *
   * @param styles The table of styles that may be referenced by cells in the
   *          sheet
   * @param strings The table of strings that may be referenced by cells in the
   *          sheet
   * @param sheetInputStream The stream to read the sheet-data from.
   * 
   * @exception java.io.IOException An IO exception from the parser, possibly
   *              from a byte stream or character stream supplied by the
   *              application.
   * @throws SAXException if parsing the XML data fails.
   */
  private void processSheet(StylesTable styles,
      ReadOnlySharedStringsTable strings,
      SheetContentsHandler sheetHandler,
      InputStream sheetInputStream) throws IOException, SAXException {
    DataFormatter formatter = new DataFormatter();
    InputSource sheetSource = new InputSource(sheetInputStream);
    try {
      XMLReader sheetParser = SAXHelper.newXMLReader();
      ContentHandler handler = new XSSFSheetXMLHandler(styles, null, strings,
          sheetHandler, formatter, false);
      sheetParser.setContentHandler(handler);
      sheetParser.parse(sheetSource);
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(
          "SAX parser appears to be broken - " + e.getMessage());
    }
  }

  public boolean isNumerical(int rowAnnotations) {
    return RegexUtils.matches(TextUtils.NUMBER_PATTERN,
        CollectionUtils.tail(mRow, rowAnnotations));

  }

  public int estimateIndexCols() {
    return estimateRowAnnotations(mRow);
  }

  public static int estimateRowAnnotations(List<String> data) {
    int ret = 0;

    while (ret < data.size() - 1) {
      if (TextUtils.areNumbers(CollectionUtils.tail(data, ret))) {
        break;
      }

      ++ret;
    }
    return ret;
  }

}
