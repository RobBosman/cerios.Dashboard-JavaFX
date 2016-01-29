package nl.valori.dashboard.loader.excel;

import java.util.Calendar;

import nl.valori.dashboard.connector.ExcelReader;
import nl.valori.dashboard.model.Period;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;

public class ExcelObtainerPeriod {

    private String column;
    private int year;

    public ExcelObtainerPeriod(String column, int year) {
	this.column = column;
	this.year = year;
    }

    /**
     * format: "CW 01 (29.12.-04.01.)" => 04-01-2009
     */
    public Period getPeriod(HSSFRow row) {
	int cellNum = ExcelReader.getColumn(column);
	HSSFCell cell = row.getCell(cellNum);

	String content = cell.toString();
	content = content.replaceAll(".*\\(", "");
	content = content.replaceAll("\\).*", "");
	content = content.replaceAll("[^0-9]+", "_");
	String[] numbers = content.split("_");
	if (numbers.length != 4) {
	    throw new RuntimeException("Cell " + ExcelReader.getCellName(cell) + ": cannot read begin and end date.");
	}
	Calendar calBegin = Calendar.getInstance();
	calBegin.set(Calendar.DAY_OF_MONTH, Integer.parseInt(numbers[0]));
	calBegin.set(Calendar.MONTH, Integer.parseInt(numbers[1]));
	calBegin.set(Calendar.YEAR, year);
	Calendar calEnd = Calendar.getInstance();
	calEnd.set(Calendar.DAY_OF_MONTH, Integer.parseInt(numbers[2]));
	calEnd.set(Calendar.MONTH, Integer.parseInt(numbers[3]));
	calEnd.set(Calendar.YEAR, year);
	return new Period(calBegin.getTime(), calEnd.getTime());
    }
}