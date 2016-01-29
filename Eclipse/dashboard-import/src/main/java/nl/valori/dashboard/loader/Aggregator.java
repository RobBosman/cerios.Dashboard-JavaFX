package nl.valori.dashboard.loader;

import java.util.HashMap;
import java.util.Map;

import nl.valori.dashboard.dao.DAO;
import nl.valori.dashboard.model.DatedValue;
import nl.valori.dashboard.model.KpiHolder;
import nl.valori.dashboard.model.KpiVariable;
import nl.valori.dashboard.model.ValueSet;

public class Aggregator {

    private DAO dao;

    public Aggregator(DAO dao) {
	this.dao = dao;
    }

    public void aggregate(KpiHolder kpiHolder) {
	// Aggregate all my children and determine the total weight factor per KpiVariable.
	Map<KpiVariable, Float> weightTotals = new HashMap<KpiVariable, Float>();
	for (KpiHolder child : kpiHolder.getChildren()) {
	    // Aggregate the child.
	    aggregate(child);
	    // Determine the total weight factor per KpiVariable.
	    for (ValueSet otherValueSet : child.getValueSets()) {
		KpiVariable kpiVariable = otherValueSet.getKpiVariable();
		float weightTotal = otherValueSet.getWeightFactor();
		if (weightTotals.containsKey(kpiVariable)) {
		    weightTotal += weightTotals.get(kpiVariable);
		}
		weightTotals.put(kpiVariable, weightTotal);
	    }
	}
	// Now aggregate the data of all children into myself.
	for (KpiHolder child : kpiHolder.getChildren()) {
	    for (ValueSet otherValueSet : child.getValueSets()) {
		KpiVariable kpiVariable = otherValueSet.getKpiVariable();
		ValueSet myValueSet = kpiHolder.getValueSet(kpiVariable);
		if (myValueSet == null) {
		    myValueSet = new ValueSet();
		    myValueSet.setKpiVariable(otherValueSet.getKpiVariable());
		    myValueSet.setWeightFactor(1);
		    kpiHolder.getValueSets().add(myValueSet);
		    // Register the new entity as a persistent object.
		    dao.save(myValueSet);
		}
		aggregate(myValueSet, otherValueSet, weightTotals.get(kpiVariable));
	    }
	}
    }

    private void aggregate(ValueSet valueSet, ValueSet otherValueSet, float weightTotal) {
	// Aggregate (= duplicate) all values
	boolean isWeightedAggregation = valueSet.getKpiVariable().isWeightedAggregation();
	for (DatedValue otherDatedValue : otherValueSet.getDatedValues()) {
	    DatedValue myDatedValue = new DatedValue();
	    float otherValue = otherDatedValue.getValue();
	    if (isWeightedAggregation) {
		otherValue *= valueSet.getWeightFactor() / weightTotal;
	    }
	    myDatedValue.setValue(otherValue);
	    myDatedValue.setBeginDate(otherDatedValue.getBeginDate());
	    myDatedValue.setEndDate(otherDatedValue.getEndDate());
	    valueSet.addDatedValues(myDatedValue);
	    // Register the new entity as a persistent object.
	    dao.save(myDatedValue);
	}
    }
}