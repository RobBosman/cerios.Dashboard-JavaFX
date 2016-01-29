package nl.valori.dashboard.model;

import java.io.StringWriter;
import java.util.List;

import junit.framework.TestCase;
import nl.valori.dashboard.dao.DashboardDAO;
import nl.valori.space.serialize.marshall.Marshaller;

public class Test extends TestCase {

    public void test() throws Exception {
	List<KpiHolder> roots = new DashboardDAO().getRootKpiHolders();
	KpiHolder kpiHolder = roots.iterator().next();
	StringWriter writer = new StringWriter();
	new Marshaller().marshall(writer, kpiHolder);

	System.out.println(writer.toString());
    }
}