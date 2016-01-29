package nl.valori.dashboard.model;

import java.util.HashSet;
import java.util.Set;

import nl.valori.space.SpaceRef;

public class GuiElement extends PropertyHolder {

    private SpaceRef<KpiVariable> kpiVariable;
    private String label;
    private String represents;
    private String valueFormat;
    private String unit;
    private Set<Property> properties;

    public GuiElement() {
	kpiVariable = new SpaceRef<KpiVariable>();
    }

    public KpiVariable getKpiVariable() {
	return kpiVariable.get();
    }

    public void setKpiVariable(KpiVariable kpiVariable) {
	this.kpiVariable.set(kpiVariable);
    }

    public String getLabel() {
	return this.label;
    }

    public void setLabel(String label) {
	this.label = label;
    }

    public String getRepresents() {
	return this.represents;
    }

    public void setRepresents(String represents) {
	this.represents = represents;
    }

    public String getValueFormat() {
	return this.valueFormat;
    }

    public void setValueFormat(String valueFormat) {
	this.valueFormat = valueFormat;
    }

    public String getUnit() {
	return this.unit;
    }

    public void setUnit(String unit) {
	this.unit = unit;
    }

    public Set<Property> getProperties() {
	if (properties == null) {
	    properties = new HashSet<Property>();
	}
	return properties;
    }

    public void setProperties(Set<Property> properties) {
	this.properties = properties;
    }

    @Override
    public String toString() {
	return " represents=" + represents + " label=" + label + " valueFormat=" + valueFormat + " unit=" + unit;
    }

    public String format(float value) {
	if (Float.isNaN(value)) {
	    value = 0;
	}
	return String.format(valueFormat, value);
    }
}