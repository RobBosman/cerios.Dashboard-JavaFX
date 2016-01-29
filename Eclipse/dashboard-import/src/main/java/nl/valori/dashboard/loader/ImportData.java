package nl.valori.dashboard.loader;

import java.util.Date;

import nl.valori.dashboard.model.Period;

public class ImportData {

    private String kpiVariableName;
    private String kpiHolderName;
    private Date beginDate;
    private Date endDate;
    private float value;

    public ImportData() {
	value = Float.NaN;
    }

    public String getKpiVariableName() {
	return kpiVariableName;
    }

    public void setKpiVariableName(String kpiVariableName) {
	if ((kpiVariableName != null) && (kpiVariableName.length() == 0)) {
	    return;
	}
	ensureUniqueDefinition("kpiVariableName", this.kpiVariableName, kpiVariableName);
	this.kpiVariableName = kpiVariableName;
    }

    public String getKpiHolderName() {
	return kpiHolderName;
    }

    public void setKpiHolderName(String kpiHolderName) {
	if ((kpiHolderName != null) && (kpiHolderName.length() == 0)) {
	    return;
	}
	ensureUniqueDefinition("kpiHolderName", this.kpiHolderName, kpiHolderName);
	this.kpiHolderName = kpiHolderName;
    }

    public Date getBeginDate() {
	return beginDate;
    }

    public void setBeginDate(Date beginDate) {
	ensureUniqueDefinition("beginDate", this.beginDate, beginDate);
	this.beginDate = beginDate;
    }

    public Date getEndDate() {
	return endDate;
    }

    public void setEndDate(Date endDate) {
	ensureUniqueDefinition("endDate", this.endDate, endDate);
	this.endDate = endDate;
    }

    public Period getPeriod() {
	return new Period(getBeginDate(), getEndDate());
    }

    public float getValue() {
	return value;
    }

    public void setValue(float value) {
	ensureUniqueDefinition("value", this.value, value);
	this.value = value;
    }

    private void ensureUniqueDefinition(String fieldName, Object existingDef, Object newDef) {
	if ((newDef != null) && (existingDef != null) && (!newDef.equals(existingDef))) {
	    throw new RuntimeException("Multiple definitions of " + fieldName);
	}
    }

    private void ensureUniqueDefinition(String fieldName, float existingDef, float newDef) {
	if ((!Float.isNaN(newDef)) && (!Float.isNaN(existingDef)) && (newDef != existingDef)) {
	    throw new RuntimeException("Multiple definitions of " + fieldName);
	}
    }
}