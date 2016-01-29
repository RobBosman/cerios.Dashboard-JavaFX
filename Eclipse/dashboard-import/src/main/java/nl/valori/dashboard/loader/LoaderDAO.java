package nl.valori.dashboard.loader;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.valori.dashboard.dao.DashboardDAO;
import nl.valori.dashboard.model.DatedValue;
import nl.valori.dashboard.model.GuiLayout;
import nl.valori.dashboard.model.ImportHistory;
import nl.valori.dashboard.model.KpiHolder;
import nl.valori.dashboard.model.KpiVariable;
import nl.valori.dashboard.model.Period;
import nl.valori.dashboard.model.ValueSet;

import org.hibernate.criterion.Restrictions;

public class LoaderDAO extends DashboardDAO {

    // TODO - Remove DATE_LIMIT.
    private static final Date DATE_LIMIT;

    static {
	Calendar cal = Calendar.getInstance();
	cal.set(2009, 6, 1); // 01-07-2009
	DATE_LIMIT = cal.getTime();
    }

    private ImportHistory importHistory;

    public void setImportHistoryId(ImportHistory importHistory) {
	this.importHistory = importHistory;
    }

    public KpiHolder getKpiHolderByName(String name) {
	return (KpiHolder) getSession().createCriteria(KpiHolder.class).add(Restrictions.like("name", name))
		.uniqueResult();
    }

    public GuiLayout getGuiLayoutByName(String name) {
	return (GuiLayout) getSession().createCriteria(GuiLayout.class).add(Restrictions.eq("name", name))
		.uniqueResult();
    }

    public KpiVariable getKpiVariableByName(String name) {
	return (KpiVariable) getSession().createCriteria(KpiVariable.class).add(Restrictions.like("name", name))
		.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public Map<String, KpiHolder> getKpiHolderLeaves() {
	List<KpiHolder> kpiHolders = (List<KpiHolder>) getSession().createCriteria(KpiHolder.class).list();
	Set<KpiHolder> parents = new HashSet<KpiHolder>();
	for (KpiHolder kpiHolder : kpiHolders) {
	    if (kpiHolder.getParent() != null) {
		parents.add(kpiHolder.getParent());
	    }
	}
	kpiHolders.removeAll(parents);
	Map<String, KpiHolder> result = new HashMap<String, KpiHolder>();
	for (KpiHolder kpiHolder : kpiHolders) {
	    result.put(kpiHolder.getCode(), kpiHolder);
	}
	return result;
    }

    public KpiHolder getOrCreateKpiHolder(String name, GuiLayout guiLayout, KpiHolder parent) {
	KpiHolder kpiHolder = getKpiHolderByName(name);
	if (kpiHolder == null) {
	    kpiHolder = new KpiHolder();
	    kpiHolder.setName(name);
	    kpiHolder.setGuiLayout(guiLayout);
	    kpiHolder.setParent(parent);
	    if (parent != null) {
		parent.getChildren().add(kpiHolder);
	    }
	    getSession().save(kpiHolder);
	}
	return kpiHolder;
    }

    public ValueSet getOrCreateValueSet(KpiHolder kpiHolder, KpiVariable kpiVariable) {
	// Find existing object
	for (ValueSet valueSet : kpiHolder.getValueSets()) {
	    if (valueSet.getKpiVariable().getName().equals(kpiVariable.getName())) {
		return valueSet;
	    }
	}
	return createValueSet(kpiHolder, kpiVariable);
    }

    public ValueSet createValueSet(KpiHolder kpiHolder, KpiVariable kpiVariable) {
	ValueSet valueSet = new ValueSet();
	valueSet.setKpiVariable(kpiVariable);
	valueSet.setWeightFactor(1.0F);
	kpiHolder.getValueSets().add(valueSet);
	getSession().save(valueSet);
	return valueSet;
    }

    public DatedValue addDatedValue(KpiHolder kpiHolder, KpiVariable kpiVariable, float value, Period period) {
	ValueSet valueSet = getOrCreateValueSet(kpiHolder, kpiVariable);
	// If the values of this KpiVariable cannot be accumulated...
	if (!kpiVariable.isAccumulated()) {
	    // ...then find any unterminated DatedValue (i.e. with no end date) and terminate it.
	    for (DatedValue datedValue : valueSet.getDatedValues()) {
		if (datedValue.getEndDate() == null) {
		    if (datedValue.getBeginDate().getTime() >= period.begin.getTime()) {
			throw new RuntimeException("Cannot add value for period [" + period
				+ "], because there is already a value that begins at " + datedValue.getBeginDate()
				+ ".");
		    } else {
			// Terminate it to the new begin.
			datedValue.setEndDate(period.begin);
		    }
		}
	    }
	}
	return createDatedValue(valueSet, value, period.begin, period.end);
    }

    public DatedValue createDatedValue(ValueSet valueSet, float value, Date begin, Date end) {
	if (begin == null) {
	    throw new RuntimeException("Cannot create DatedValue object without begin date.");
	}
	if ((DATE_LIMIT != null) && (begin.after(DATE_LIMIT))) {
	    throw new RuntimeException("This is a DEMO version that should not be used after " + DATE_LIMIT + ".");
	}
	DatedValue datedValue = new DatedValue();
	datedValue.setValue(value);
	datedValue.setBeginDate(begin);
	datedValue.setEndDate(end);
	datedValue.setImportHistory(importHistory);
	valueSet.addDatedValues(datedValue);
	getSession().save(datedValue);
	return datedValue;
    }
}
