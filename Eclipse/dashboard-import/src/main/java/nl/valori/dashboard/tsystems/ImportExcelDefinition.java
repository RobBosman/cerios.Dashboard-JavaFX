package nl.valori.dashboard.tsystems;

import java.util.Date;

import nl.valori.dashboard.model.Period;

import org.apache.poi.hssf.usermodel.HSSFRow;

public class ImportExcelDefinition {

    private ImportExcelObtainerName kpiHolderNameObtainer;
    private ImportExcelObtainerName kpiHolderParentNameObtainer;
    private ImportExcelObtainerDate beginDateObtainer;
    private ImportExcelObtainerDate endDateObtainer;
    private ImportExcelObtainerValue valueObtainer;
    private ImportExcelObtainerPeriod periodObtainer;

    public void setKpiHolderNameObtainer(ImportExcelObtainerName kpiHolderNameObtainer) {
	this.kpiHolderNameObtainer = kpiHolderNameObtainer;
    }

    public void setKpiHolderParentNameObtainer(ImportExcelObtainerName kpiHolderParentNameObtainer) {
	this.kpiHolderParentNameObtainer = kpiHolderParentNameObtainer;
    }

    public void setBeginDateObtainer(ImportExcelObtainerDate beginDateObtainer) {
	this.beginDateObtainer = beginDateObtainer;
    }

    public void setEndDateObtainer(ImportExcelObtainerDate endDateObtainer) {
	this.endDateObtainer = endDateObtainer;
    }

    public void setValueObtainer(ImportExcelObtainerValue valueObtainer) {
	this.valueObtainer = valueObtainer;
    }

    public void setPeriodObtainer(ImportExcelObtainerPeriod periodObtainer) {
	this.periodObtainer = periodObtainer;
    }

    public String getKpiHolderName(HSSFRow row) {
	return kpiHolderNameObtainer.getName(row);
    }

    public String getKpiHolderParentName(HSSFRow row) {
	return kpiHolderParentNameObtainer.getName(row);
    }

    public Period getPeriod(HSSFRow row) {
	if (periodObtainer != null) {
	    return periodObtainer.getPeriod(row);
	} else {
	    Date begin = beginDateObtainer.getDate(row);
	    Date end = null;
	    if (endDateObtainer != null) {
		end = endDateObtainer.getDate(row);
	    }
	    return new Period(begin, end);
	}
    }

    public float getValue(HSSFRow row) {
	return valueObtainer.getValue(row);
    }
}