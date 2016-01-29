package nl.valori.dashboard.model;

import java.util.HashSet;
import java.util.Set;

public class KpiVariable extends AbstractPersistentEntity {

    private String name;
    private boolean interpolated;
    private boolean weightedAggregation;
    private boolean accumulated;
    private Set<Threshold> thresholds;

    public String getName() {
	return this.name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public boolean isInterpolated() {
	return this.interpolated;
    }

    public void setInterpolated(boolean interpolated) {
	this.interpolated = interpolated;
    }

    public boolean isWeightedAggregation() {
	return this.weightedAggregation;
    }

    public void setWeightedAggregation(boolean weightedAggregation) {
	this.weightedAggregation = weightedAggregation;
    }

    public boolean isAccumulated() {
	return this.accumulated;
    }

    public void setAccumulated(boolean accumulated) {
	this.accumulated = accumulated;
    }

    public Set<Threshold> getThresholds() {
	if (thresholds == null) {
	    thresholds = new HashSet<Threshold>();
	}
	return thresholds;
    }

    public void setThresholds(Set<Threshold> thresholds) {
	this.thresholds = thresholds;
    }

    public void addThresholds(Threshold thresholds) {
	getThresholds().add(thresholds);
    }

    public void removeThresholds(Threshold thresholds) {
	getThresholds().remove(thresholds);
    }

    public Threshold getThreshold(String name) {
	for (Threshold threshold : getThresholds()) {
	    if (threshold.getName().equals(name)) {
		return threshold;
	    }
	}
	return null;
    }

    @Override
    public String toString() {
	return " name=" + name + " interpolated=" + interpolated + " weightedAggregation=" + weightedAggregation
		+ " accumulated=" + accumulated;
    }
}