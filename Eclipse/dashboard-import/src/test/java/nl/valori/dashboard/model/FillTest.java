package nl.valori.dashboard.model;

import java.util.Collection;

import nl.valori.dashboard.dataloader.DataLoaderTestCase;
import nl.valori.dashboard.loader.LoaderDAO;

public class FillTest extends DataLoaderTestCase {

    public void testDataLoader() throws Exception {
	LoaderDAO dao = new LoaderDAO();

	clearDatabase();
	loadXml(getClass().getResourceAsStream("fillTest.xml"));

	Collection<KpiHolder> roots = dao.getRootKpiHolders();
	assertEquals("KpiHolder.getRoots() is not correct", 1, roots.size());
	KpiHolder root = roots.iterator().next();

	assertEquals("getName() is not correct", "PORTFOLIO", root.getName());
	assertEquals("getGuiLayout() is not correct", "default", root.getGuiLayout().getName());

	KpiVariable kpiVariable = dao.getKpiVariableByName("budget.spent");
	ValueSet valueSet = root.getValueSet(kpiVariable);
	assertEquals("ValueSet.kpi is not correct", kpiVariable.getName(), valueSet.getKpiVariable().getName());

	Period period = root.getFullPeriod();
	assertTrue("Period is not correct", period.begin.before(period.end));
	assertEquals("Period.begin is not correct", toDate("2009-01-12"), period.begin);
	assertEquals("Period.end is not correct", toDate("2009-03-31"), period.end);
	assertEquals("Period.getNumDays() is not correct", 78, period.getNumDays());
	Period subPeriod = period.getPeriodTill(toDate("2009-02-08"));
	assertEquals("Period.getPeriodTill() is not correct", toDate("2009-02-01"), subPeriod.begin);
	assertEquals("Period.getPeriodTill() is not correct", toDate("2009-02-08"), subPeriod.end);
	assertEquals("Period.getPeriodTill() is not correct", 7, subPeriod.getNumDays());

	Collection<KpiHolder> children = root.getChildren();
	assertEquals("getChildren() is not correct", 1, children.size());
	KpiHolder child = children.iterator().next();
	assertEquals("Programme is not correct", "PROGRAMME A", child.getName());
    }
}