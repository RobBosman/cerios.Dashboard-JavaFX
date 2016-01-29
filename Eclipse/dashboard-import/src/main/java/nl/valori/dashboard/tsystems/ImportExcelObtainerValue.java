package nl.valori.dashboard.tsystems;

import nl.valori.dashboard.connector.ExcelReader;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;

public class ImportExcelObtainerValue {

    private int[] columnNumbers;

    public ImportExcelObtainerValue(String columnNames) {
	this.columnNumbers = ExcelReader.getColumns(columnNames.split(" "));
    }

    public float getValue(HSSFRow row) {
	double sum = 0;
	for (int columnNumber : columnNumbers) {
	    sum += cellValue(row, columnNumber);
	}
	return (float) sum;
    }

    private double cellValue(HSSFRow row, int columnIndex) {
	HSSFCell cell = row.getCell(columnIndex);
	try {
	    return cell.getNumericCellValue();
	} catch (Throwable t) {
	    String content = cell.toString().trim();
	    if (content.length() == 0) {
		return 0;
	    }
	    try {
		return Double.parseDouble(content);
	    } catch (Exception e1) {
		// ignore exceptions here
	    }
	    throw new RuntimeException("Cell " + ExcelReader.getCellName(cell) + ": " + t.getMessage(), t);
	}
    }
}