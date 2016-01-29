package nl.valori.dashboard.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import nl.valori.space.SpaceRef;

public class ValueSet extends AbstractPersistentEntity {

    private float weightFactor;
    private SpaceRef<KpiVariable> kpiVariable;
    private Set<DatedValue> datedValues;

    public ValueSet() {
	kpiVariable = new SpaceRef<KpiVariable>();
    }

    public float getWeightFactor() {
	return this.weightFactor;
    }

    public void setWeightFactor(float weightFactor) {
	this.weightFactor = weightFactor;
    }

    public KpiVariable getKpiVariable() {
	return kpiVariable.get();
    }

    public void setKpiVariable(KpiVariable kpiVariable) {
	this.kpiVariable.set(kpiVariable);
    }

    public Set<DatedValue> getDatedValues() {
	if (datedValues == null) {
	    datedValues = new HashSet<DatedValue>();
	}
	return datedValues;
    }

    public void setDatedValues(Set<DatedValue> datedValues) {
	this.datedValues = datedValues;
    }

    public void addDatedValues(DatedValue datedValues) {
	getDatedValues().add(datedValues);
    }

    public void removeDatedValues(DatedValue datedValues) {
	getDatedValues().remove(datedValues);
    }

    @Override
    public String toString() {
	return " weightFactor=" + weightFactor;
    }

    public Period getFullPeriod() {
	Period fullPeriod = null;
	for (DatedValue datedValue : getDatedValues()) {
	    if (fullPeriod == null) {
		fullPeriod = new Period(datedValue.getBeginDate(), datedValue.getEndDate());
	    } else {
		fullPeriod.span(datedValue.getBeginDate());
		fullPeriod.span(datedValue.getEndDate());
	    }
	}
	return fullPeriod;
    }

    public float getMinimum(Period period) {
	if (period == null) {
	    return Float.NaN;
	}
	// TODO - implement getAccumulatedMinimum() properly (only positive values are OK now)
	return 0;
    }

    public float getMaximum(Period period) {
	if (period == null) {
	    return Float.NaN;
	}
	if (getKpiVariable().isAccumulated()) {
	    return getAccumulatedValueAt(period.end);
	}

	Set<Date> dates = new HashSet<Date>();
	dates.add(period.begin);
	dates.add(period.end);

	for (DatedValue datedValue : getDatedValues()) {
	    if (period.spans(datedValue.getBeginDate())) {
		dates.add(datedValue.getBeginDate());
	    }
	    // TODO - implement getMaximum() properly (check endDate == null)
	    if (period.spans(datedValue.getEndDate())) {
		dates.add(datedValue.getEndDate());
	    }
	}

	float maximum = 0;
	for (Date date : dates) {
	    float value = getValueAt(date);
	    if (!Float.isNaN(value)) {
		maximum = Math.max(maximum, value);
	    }
	}
	return maximum;
    }

    private float getAccumulatedValueAt(java.util.Date date) {
	if (date == null) {
	    return Float.NaN;
	}
	float value = Float.NaN;
	boolean interpolate = getKpiVariable().isInterpolated();
	for (DatedValue datedValue : getDatedValues()) {
	    if (date.getTime() >= datedValue.getBeginDate().getTime()) {
		if (Float.isNaN(value)) {
		    value = 0;
		}
		if (interpolate) {
		    value += datedValue.interpolateValueAt(date);
		} else if (datedValue.getEndDate().getTime() < date.getTime()) {
		    value += datedValue.getValue();
		}
	    }
	}
	return value;
    }

    public float getValueAt(java.util.Date date) {
	if (date == null) {
	    return Float.NaN;
	}
	if (getKpiVariable().isAccumulated()) {
	    return getAccumulatedValueAt(date);
	}

	boolean isInterpolated = getKpiVariable().isInterpolated();
	float value = Float.NaN;
	for (DatedValue datedValue : getDatedValues()) {
	    if (new Period(datedValue.getBeginDate(), datedValue.getEndDate()).spans(date)) {
		if (Float.isNaN(value)) {
		    value = 0;
		}
		if (isInterpolated) {
		    value += datedValue.interpolateValueAt(date);
		} else {
		    value += datedValue.getValue();
		}
	    }
	}
	return value;
    }

    public float getDelta(Period period) {
	// TODO - compute getDelta() properly!
	if ((period == null) || (period.begin == null) || (period.end == null)) {
	    return Float.NaN;
	} else {
	    return getValueAt(period.end) - getValueAt(period.begin);
	}
    }

    public float getDeltaMinimum(Period period) {
	// TODO - compute getDeltaMinimum() properly!
	return 0;
    }

    public float getDeltaMaximum(Period period) {
	// TODO - compute getDeltaMaximum() properly!
	// SortedSet<Date> sortedDates = new TreeSet<Date>();
	// for (DatedValue datedValue : getDatedValues()) {
	// if (period.spans(datedValue.getBegin())) {
	// sortedDates.add(datedValue.getBegin());
	// }
	// if (period.spans(datedValue.getEnd())) {
	// sortedDates.add(datedValue.getEnd());
	// }
	// }
	// float maximum = 0;
	// for (Date date : sortedDates) {
	// maximum = Math.max(maximum, getAccumulatedValueAt(date));
	// }
	// return maximum;
	if (getFullPeriod() == null) {
	    return Float.NaN;
	}
	float total = getValueAt(getFullPeriod().end);
	return total * period.getNumDays() / getFullPeriod().getNumDays() * 10;
    }

    public AccumulatedValue[] getAccumulatedValues(Period period) {
	if (period == null) {
	    return new AccumulatedValue[0];
	}
	// Get a sorted set with all relevant dates.
	SortedSet<Date> sortedDates = new TreeSet<Date>();
	for (DatedValue datedValue : getDatedValues()) {
	    if (period.spans(datedValue.getBeginDate())) {
		sortedDates.add(datedValue.getBeginDate());
	    }
	    if (period.spans(datedValue.getEndDate())) {
		sortedDates.add(datedValue.getEndDate());
	    }
	}
	// Create AccumulatedValue objects.
	List<AccumulatedValue> result = new ArrayList<AccumulatedValue>();
	for (Date date : sortedDates) {
	    AccumulatedValue accumulatedValue = new AccumulatedValue();
	    accumulatedValue.date = date;
	    accumulatedValue.value = getAccumulatedValueAt(date);
	    result.add(accumulatedValue);
	}
	return result.toArray(new AccumulatedValue[0]);
    }

    public AccumulatedValue[] getAccumulatedDiffs(Period period, ValueSet subtractValueSet) {
	if (period == null) {
	    return new AccumulatedValue[0];
	}
	if (subtractValueSet == null) {
	    return getAccumulatedValues(period);
	}
	// Get a sorted set with all relevant dates.
	SortedSet<Date> sortedDates = new TreeSet<Date>();
	for (DatedValue datedValue : getDatedValues()) {
	    if (period.spans(datedValue.getBeginDate())) {
		sortedDates.add(datedValue.getBeginDate());
	    }
	    if (period.spans(datedValue.getEndDate())) {
		sortedDates.add(datedValue.getEndDate());
	    }
	}
	for (DatedValue datedValue : subtractValueSet.getDatedValues()) {
	    if (period.spans(datedValue.getBeginDate())) {
		sortedDates.add(datedValue.getBeginDate());
	    }
	    if (period.spans(datedValue.getEndDate())) {
		sortedDates.add(datedValue.getEndDate());
	    }
	}
	// Create AccumulatedValue objects.
	List<AccumulatedValue> result = new ArrayList<AccumulatedValue>();
	for (Date date : sortedDates) {
	    AccumulatedValue accumulatedValue = new AccumulatedValue();
	    accumulatedValue.date = date;
	    accumulatedValue.value = getAccumulatedValueAt(date) - subtractValueSet.getAccumulatedValueAt(date);
	    result.add(accumulatedValue);
	}
	return result.toArray(new AccumulatedValue[0]);
    }
}