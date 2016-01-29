package nl.valori.dashboard.model;

import java.util.Date;

public class Threshold extends AbstractPersistentEntity {

    private String name;
    private int alertLevelIfAbove;
    private int alertLevelIfAt;
    private int alertLevelIfBelow;
    private float value;
    private KpiVariable fractionOfKpiVariable;
    private KpiVariable relativeToKpiVariable;

    public String getName() {
	return this.name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public int getAlertLevelIfAbove() {
	return this.alertLevelIfAbove;
    }

    public void setAlertLevelIfAbove(int alertLevelIfAbove) {
	this.alertLevelIfAbove = alertLevelIfAbove;
    }

    public int getAlertLevelIfAt() {
	return this.alertLevelIfAt;
    }

    public void setAlertLevelIfAt(int alertLevelIfAt) {
	this.alertLevelIfAt = alertLevelIfAt;
    }

    public int getAlertLevelIfBelow() {
	return this.alertLevelIfBelow;
    }

    public void setAlertLevelIfBelow(int alertLevelIfBelow) {
	this.alertLevelIfBelow = alertLevelIfBelow;
    }

    public float getValue() {
	return this.value;
    }

    public void setValue(float value) {
	this.value = value;
    }

    public KpiVariable getRelativeToKpiVariable() {
	return relativeToKpiVariable;
    }

    public void setRelativeToKpiVariable(KpiVariable relativeToKpiVariable) {
	this.relativeToKpiVariable = relativeToKpiVariable;
    }

    public KpiVariable getFractionOfKpiVariable() {
	return fractionOfKpiVariable;
    }

    public void setFractionOfKpiVariable(KpiVariable fractionOfKpiVariable) {
	this.fractionOfKpiVariable = fractionOfKpiVariable;
    }

    @Override
    public String toString() {
	return " alertLevelIfAbove=" + alertLevelIfAbove + " alertLevelIfAt=" + alertLevelIfAt + " alertLevelIfBelow="
		+ alertLevelIfBelow + " value=" + value + " fractionOfKpiVariableId=" + fractionOfKpiVariable
		+ " relativeToKpiVariableId=" + relativeToKpiVariable + " name=" + name;
    }

    public int getAlertLevel(float kpiValue, KpiHolder kpiHolder, Date date) {
	if (Float.isNaN(kpiValue)) {
	    // TODO - how to deal with thresholds if no data is available?
	    return 0;
	}
	float thresholdValue = getThreshold(kpiHolder, date);
	if (Float.isNaN(thresholdValue)) {
	    // TODO - how to deal with alerts if no threshold is available?
	    return 0;
	} else if (kpiValue > thresholdValue) {
	    return alertLevelIfAbove;
	} else if (kpiValue == thresholdValue) {
	    return alertLevelIfAt;
	} else {
	    return alertLevelIfBelow;
	}
    }

    public float getThreshold(KpiHolder kpiHolder, Date date) {
	float thresholdValue = value;
	KpiVariable fractionOfKpiVariable = getFractionOfKpiVariable();
	if (fractionOfKpiVariable != null) {
	    ValueSet fractionOfValueSet = kpiHolder.getValueSet(fractionOfKpiVariable);
	    if (fractionOfValueSet == null) {
		return Float.NaN;
	    }
	    float fractionOfValue = fractionOfValueSet.getValueAt(date);
	    if (!Float.isNaN(fractionOfValue)) {
		thresholdValue = value * fractionOfValue;
	    }
	}
	KpiVariable relativeToKpiVariable = getRelativeToKpiVariable();
	if (relativeToKpiVariable != null) {
	    ValueSet relativeToValueSet = kpiHolder.getValueSet(relativeToKpiVariable);
	    if (relativeToValueSet == null) {
		return Float.NaN;
	    }
	    float relativeToValue = relativeToValueSet.getValueAt(date);
	    if (!Float.isNaN(relativeToValue)) {
		thresholdValue += relativeToValue;
	    }
	}
	return thresholdValue;
    }
}