package nl.valori.dashboard.loader.excel;

import nl.valori.dashboard.connector.ExcelReader;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;

public class ExcelObtainerName {

    private int[] columnNumbers;
    private String separator;

    public ExcelObtainerName(String columnNames, String separator) {
	this.columnNumbers = ExcelReader.getColumns(columnNames.split(" "));
	if (separator == null) {
	    this.separator = "";
	} else {
	    this.separator = separator;
	}
    }

    @SuppressWarnings("deprecation")
    public String getName(HSSFRow row) {
	String name = "";
	for (int columnNumber : columnNumbers) {
	    String namePart;
	    HSSFCell cell = row.getCell(columnNumber);
	    if (cell == null) {
		namePart = "";
	    } else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
		namePart = String.valueOf(Math.round(cell.getNumericCellValue()));
	    } else if (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
		namePart = cell.getStringCellValue();
	    } else {
		namePart = cell.toString().trim();
	    }
	    if (namePart.length() != 0) {
		if (name.length() > 0) {
		    name += separator;
		}
		name += namePart;
	    }
	}
	return name;
    }
}