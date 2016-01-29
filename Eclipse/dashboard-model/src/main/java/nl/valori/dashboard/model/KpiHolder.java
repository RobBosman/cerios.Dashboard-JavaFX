package nl.valori.dashboard.model;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

import nl.valori.space.SpaceRef;
import nl.valori.space.SpaceSet;

public class KpiHolder extends AbstractPersistentEntity {

    public static final Comparator<KpiHolder> COMPARATOR_NAME = new Comparator<KpiHolder>() {

	public int compare(KpiHolder kpiHolder1, KpiHolder kpiHolder2) {
	    return kpiHolder1.getName().compareTo(kpiHolder2.getName());
	}
    };

    private String name;
    private SpaceRef<GuiLayout> guiLayout;
    private SpaceRef<KpiHolder> parent;
    private SpaceSet<KpiHolder> children;
    private SpaceSet<ValueSet> valueSets;

    public KpiHolder() {
	guiLayout = new SpaceRef<GuiLayout>();
	parent = new SpaceRef<KpiHolder>();
	children = new SpaceSet<KpiHolder>();
	valueSets = new SpaceSet<ValueSet>();
    }

    public String getName() {
	return this.name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public GuiLayout getGuiLayout() {
	return guiLayout.get();
    }

    public void setGuiLayout(GuiLayout guiLayout) {
	this.guiLayout.set(guiLayout);
    }

    public KpiHolder getParent() {
	return parent.get();
    }

    public void setParent(KpiHolder parent) {
	this.parent.set(parent);
    }

    public Set<KpiHolder> getChildren() {
	return children;
    }

    public void setChildren(Set<KpiHolder> children) {
	this.children.set(children);
    }

    public Set<ValueSet> getValueSets() {
	return valueSets;
    }

    public void setValueSets(Set<ValueSet> valueSets) {
	this.valueSets.set(valueSets);
    }

    @Override
    public String toString() {
	return " id=" + getId() + " name=" + name;
    }

    public ValueSet getValueSet(KpiVariable kpiVariable) {
	if (kpiVariable == null) {
	    return null;
	}
	for (ValueSet valueSet : getValueSets()) {
	    if (valueSet.getKpiVariable().getName().equals(kpiVariable.getName())) {
		return valueSet;
	    }
	}
	return null;
    }

    public Period getFullPeriod() {
	Period fullPeriod = null;
	for (ValueSet valueSet : getValueSets()) {
	    Period valueSetPeriod = valueSet.getFullPeriod();
	    if (valueSetPeriod != null) {
		if (fullPeriod == null) {
		    fullPeriod = new Period(valueSetPeriod.begin, valueSetPeriod.end);
		} else {
		    fullPeriod.span(valueSetPeriod);
		}
	    }
	}
	return fullPeriod;
    }

    public int getAlertLevelAt(KpiVariable kpiVariable, java.util.Date date) {
	int alertLevel = 0;
	ValueSet valueSet = getValueSet(kpiVariable);
	if (valueSet == null) {
	    return 0;
	}
	Collection<Threshold> thresholds = kpiVariable.getThresholds();
	if (!thresholds.isEmpty()) {
	    float kpiValue = valueSet.getValueAt(date);
	    if (Float.isNaN(kpiValue)) {
		return 0;
	    }
	    for (Threshold threshold : thresholds) {
		alertLevel = Math.max(alertLevel, threshold.getAlertLevel(kpiValue, this, date));
	    }
	}
	return alertLevel;
    }

    // TODO - Solve KpiHolder.code!
    public String getCode() {
	return getName().replaceAll("#.*", "");
    }
}