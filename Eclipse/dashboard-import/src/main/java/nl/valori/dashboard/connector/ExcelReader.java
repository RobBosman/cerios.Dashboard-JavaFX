/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.valori.dashboard.connector;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * @author Rob
 */
public class ExcelReader implements Reader {

    public static int getColumn(String rowCol) {
	rowCol = rowCol.toUpperCase();
	int col = 0;
	for (int i = 0; i < rowCol.length(); i++) {
	    char c = rowCol.charAt(i);
	    if (Character.isLetter(c)) {
		col = col * 26 + Character.getNumericValue(c) - Character.getNumericValue('A') + 1;
	    } else {
		break;
	    }
	}
	return col - 1;
    }

    public static int[] getColumns(String[] rowCols) {
	int[] columns = new int[rowCols.length];
	for (int i = 0; i < rowCols.length; i++) {
	    columns[i] = getColumn(rowCols[i]);
	}
	return columns;
    }

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String getCellName(HSSFCell cell) {
	StringBuffer columnName = new StringBuffer();
	int columnIndex = cell.getColumnIndex();
	while (columnIndex > 0) {
	    columnName.append(ALPHABET.charAt(columnIndex % 26));
	    columnIndex /= 26;
	}
	columnName.append(cell.getRowIndex() + 1);
	return columnName.toString();
    }

    public static int[] getRowColumn(String rowCol) {
	rowCol = rowCol.toUpperCase();
	int row = 0;
	int col = 0;
	for (int i = 0; i < rowCol.length(); i++) {
	    char c = rowCol.charAt(i);
	    if (Character.isLetter(c)) {
		col = col * 26 + Character.getNumericValue(c) - Character.getNumericValue('A') + 1;
	    } else if (Character.isDigit(c)) {
		row = row * 10 + Character.digit(c, 10);
	    }
	}
	return new int[] { row - 1, col - 1 };
    }

    public HSSFWorkbook getWorkbook(String fileName) {
	try {
	    InputStream myxls = null;
	    try {
		myxls = new FileInputStream(fileName);
		return new HSSFWorkbook(myxls);
	    } finally {
		if (myxls != null) {
		    myxls.close();
		}
	    }
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    public Date getDate(String datumLocation) {
	return getCell(datumLocation).getDateCellValue();
    }

    public String getValue(String waardeLocation) {
	HSSFCell cell = getCell(waardeLocation);
	return String.valueOf(cell.getNumericCellValue());
    }

    private HSSFCell getCell(String location) {
	// [test.xls]Blad1!$B$2
	int i = location.indexOf("]");
	String fileName = location.substring(1, i);
	String cellLocation = location.substring(i + 1);
	return getCell(getWorkbook(fileName), cellLocation);
    }

    private HSSFCell getCell(HSSFWorkbook wb, String location) {
	if (location.contains("!")) {
	    String[] cellLocation = location.split("\\!");
	    if (cellLocation.length == 2) {
		HSSFSheet sheet = wb.getSheet(cellLocation[0]);
		if (sheet != null) {
		    return getCell(sheet, cellLocation[1].toUpperCase());
		}
	    }
	}
	return getCell(wb.getSheetAt(0), location.toUpperCase());
    }

    private HSSFCell getCell(HSSFSheet sheet, String rowCol) {
	int[] rc = getRowColumn(rowCol);
	return sheet.getRow(rc[0]).getCell(rc[1]);
    }
}
