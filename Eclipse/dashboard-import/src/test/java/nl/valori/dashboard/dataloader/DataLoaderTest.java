package nl.valori.dashboard.dataloader;

import java.util.Collection;

import junit.framework.TestCase;
import nl.valori.dashboard.dao.DashboardDAO;
import nl.valori.dashboard.model.KpiHolder;

public class DataLoaderTest extends TestCase {

    public void testSchemaDropCreate() throws Exception {
	DataLoaderTestCase dataLoaderTestCase = new DataLoaderTestCase();
	// TODO - Verify clearDatabase
	dataLoaderTestCase.clearDatabase();
    }

    public void testDataLoaderTestCase() throws Exception {
	DataLoaderTestCase dataLoaderTestCase = new DataLoaderTestCase();
	// TODO - Verify loadXml
	dataLoaderTestCase.loadXml(getClass().getResourceAsStream("dataLoaderTest.xml"));

	Collection<KpiHolder> roots = new DashboardDAO().getRootKpiHolders();
	assertEquals("KpiHolder.getRoots() is not correct", 1, roots.size());
    }
}
