package nl.valori.dashboard.tsystems;

import java.util.Date;

import nl.valori.dashboard.connector.ExcelReader;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;

public class ImportExcelObtainerDate {

    private String column;

    public ImportExcelObtainerDate(String column) {
	this.column = column;
    }

    public ImportExcelObtainerDate(String column, int year) {
	this.column = column;
    }

    public Date getDate(HSSFRow row) {
	int cellNum = ExcelReader.getColumn(column);
	HSSFCell cell = row.getCell(cellNum);
	try {
	    return cell.getDateCellValue();
	} catch (Throwable t) {
	    throw new RuntimeException("Cell " + ExcelReader.getCellName(cell) + ": " + t.getMessage(), t);
	}
    }
}