package nl.valori.dashboard.loader.excel;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import nl.valori.dashboard.loader.ImportDefinition;
import nl.valori.dashboard.model.Period;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelSource {

    private ImportDefinition importDefinition;
    private String fileName;
    private String sheetName;
    private int firstRow;
    private int lastRow;

    private ExcelObtainerName kpiVariableNameObtainer;
    private ExcelObtainerName kpiHolderNameObtainer;
    private ExcelObtainerDate beginDateObtainer;
    private ExcelObtainerDate endDateObtainer;
    private ExcelObtainerPeriod periodObtainer;
    private ExcelObtainerValue valueObtainer;

    private HSSFSheet sheet;

    public ExcelSource(ImportDefinition importDefinition, String fileName, String sheetName, int firstRow, int lastRow) {
	this.importDefinition = importDefinition;
	this.fileName = fileName;
	this.sheetName = sheetName;
	this.firstRow = firstRow;
	this.lastRow = lastRow;
    }

    public String getFileName() {
	return fileName;
    }

    public String getSheetName() {
	return sheetName;
    }

    public int getFirstRow() {
	return firstRow;
    }

    public int getLastRow() {
	return lastRow;
    }

    public void setKpiVariableNameObtainer(ExcelObtainerName kpiVariableNameObtainer) {
	this.kpiVariableNameObtainer = kpiVariableNameObtainer;
    }

    public void setKpiHolderNameObtainer(ExcelObtainerName kpiHolderNameObtainer) {
	this.kpiHolderNameObtainer = kpiHolderNameObtainer;
    }

    public void setBeginDateObtainer(ExcelObtainerDate beginDateObtainer) {
	this.beginDateObtainer = beginDateObtainer;
    }

    public void setEndDateObtainer(ExcelObtainerDate endDateObtainer) {
	this.endDateObtainer = endDateObtainer;
    }

    public void setPeriodObtainer(ExcelObtainerPeriod periodObtainer) {
	this.periodObtainer = periodObtainer;
    }

    public void setValueObtainer(ExcelObtainerValue valueObtainer) {
	this.valueObtainer = valueObtainer;
    }

    private HSSFRow getRow(int row) {
	return getSheet().getRow(row - 1);
    }

    private HSSFSheet getSheet() {
	if (sheet == null) {
	    try {
		sheet = new HSSFWorkbook(new FileInputStream(fileName)).getSheet(sheetName);
	    } catch (IOException e) {
		throw new RuntimeException(e);
	    }
	}
	return sheet;
    }

    public String getKpiVariableName(int row) {
	if (kpiVariableNameObtainer != null) {
	    return kpiVariableNameObtainer.getName(getRow(row));
	} else if (importDefinition != null) {
	    return importDefinition.getKpiVariableName();
	} else {
	    return null;
	}
    }

    public String getKpiHolderName(int row) {
	if (kpiHolderNameObtainer != null) {
	    return kpiHolderNameObtainer.getName(getRow(row));
	} else if (importDefinition != null) {
	    return importDefinition.getKpiHolderName();
	} else {
	    return null;
	}
    }

    public Date getBeginDate(int row) {
	if (beginDateObtainer != null) {
	    return beginDateObtainer.getDate(getRow(row));
	} else if (importDefinition != null) {
	    return importDefinition.getBeginDate();
	} else {
	    return null;
	}
    }

    public Date getEndDate(int row) {
	if (endDateObtainer != null) {
	    return endDateObtainer.getDate(getRow(row));
	} else if (importDefinition != null) {
	    return importDefinition.getEndDate();
	} else {
	    return null;
	}
    }

    public Period getPeriod(int row) {
	if (periodObtainer != null) {
	    return periodObtainer.getPeriod(getRow(row));
	} else {
	    return new Period(getBeginDate(row), getEndDate(row));
	}
    }

    public float getValue(int row) {
	if (valueObtainer != null) {
	    return valueObtainer.getValue(getRow(row));
	} else if (importDefinition != null) {
	    return importDefinition.getValue();
	} else {
	    return Float.NaN;
	}
    }
}