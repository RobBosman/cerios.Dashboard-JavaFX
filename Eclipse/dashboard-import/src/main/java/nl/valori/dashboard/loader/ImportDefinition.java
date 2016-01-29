package nl.valori.dashboard.loader;

import java.util.Date;

public class ImportDefinition extends ImportData {

    private ImportDefinition parentImportDefinition;

    public ImportDefinition(ImportDefinition parentImportDefinition) {
	this.parentImportDefinition = parentImportDefinition;
    }

    @Override
    public String getKpiVariableName() {
	String kpiVariableName = super.getKpiVariableName();
	if ((kpiVariableName == null) && (parentImportDefinition != null)) {
	    return parentImportDefinition.getKpiVariableName();
	} else {
	    return kpiVariableName;
	}
    }

    @Override
    public String getKpiHolderName() {
	String kpiHolderName = super.getKpiHolderName();
	if ((kpiHolderName == null) && (parentImportDefinition != null)) {
	    return parentImportDefinition.getKpiHolderName();
	} else {
	    return kpiHolderName;
	}
    }

    @Override
    public Date getBeginDate() {
	Date beginDate = super.getBeginDate();
	if ((beginDate == null) && (parentImportDefinition != null)) {
	    return parentImportDefinition.getBeginDate();
	} else {
	    return beginDate;
	}
    }

    @Override
    public Date getEndDate() {
	Date endDate = super.getEndDate();
	if ((endDate == null) && (parentImportDefinition != null)) {
	    return parentImportDefinition.getEndDate();
	} else {
	    return endDate;
	}
    }

    @Override
    public float getValue() {
	float value = super.getValue();
	if ((Float.isNaN(value)) && (parentImportDefinition != null)) {
	    return parentImportDefinition.getValue();
	} else {
	    return value;
	}
    }
}