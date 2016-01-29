package nl.valori.dashboard.model;

import java.util.Collection;
import java.util.Date;

import nl.valori.dashboard.dao.DashboardDAO;
import nl.valori.dashboard.dataloader.DataLoaderTestCase;

public class ModelTest extends DataLoaderTestCase {

    @SuppressWarnings("unused")
    public void test() throws Exception {
	DashboardDAO dao = new DashboardDAO();

	clearDatabase();
	loadXml(getClass().getResourceAsStream("modelTest.xml"));

	Collection<KpiHolder> roots = dao.getRootKpiHolders();
	assertEquals("KpiHolder.getRoots() is not correct", 1, roots.size());
	KpiHolder kpiHolder = roots.iterator().next();

	GuiLayout guiLayout = kpiHolder.getGuiLayout();
	GuiComponent guiComponent = guiLayout.getGuiComponents().iterator().next();
	GuiElement guiElementTarget = guiComponent.getGuiElement("TARGET");
	KpiVariable kpiVariableTarget = guiElementTarget.getKpiVariable();
	ValueSet valueSetTarget = kpiHolder.getValueSet(kpiVariableTarget);
	GuiElement guiElementActual = guiComponent.getGuiElement("ACTUAL");
	KpiVariable kpiVariableActual = guiElementActual.getKpiVariable();
	ValueSet valueSetActual = kpiHolder.getValueSet(kpiVariableActual);

	Period periodTarget = valueSetTarget.getFullPeriod();
	Period periodActual = valueSetActual.getFullPeriod();
	Period periodKpiHolder = kpiHolder.getFullPeriod();

	Date date = toDate("2009-01-12");
	float accumulatedValueTarget = valueSetTarget.getValueAt(date);
	float accumulatedValueActual = valueSetActual.getValueAt(date);

	Date beginDate = toDate("2009-01-01");
	Date endDate = toDate("2009-12-31");
	AccumulatedValue[] accumulatedValues = valueSetActual.getAccumulatedValues(new Period(beginDate, endDate));

	Threshold threshold = kpiVariableActual.getThreshold("MINIMUM");
	float kpiValue = valueSetActual.getValueAt(date);
	float thresholdValue = threshold.getThreshold(kpiHolder, date);
	int alertLevel = threshold.getAlertLevel(kpiValue, kpiHolder, date);

	date = toDate("2009-02-13");
	kpiValue = valueSetActual.getValueAt(date);
	thresholdValue = threshold.getThreshold(kpiHolder, date);
	alertLevel = threshold.getAlertLevel(kpiValue, kpiHolder, date);
	return;
    }
}