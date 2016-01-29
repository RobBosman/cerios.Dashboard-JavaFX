package nl.valori.dashboard.remote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;
import nl.valori.dashboard.model.KpiHolder;
import nl.valori.space.Space;

public class RemoteSpaceTest extends TestCase {

    public void test() throws Exception {
	DashboardRemoteSpace remoteSpace = new DashboardRemoteSpace();
	Space.getInstance().setFiller(remoteSpace);
	List<KpiHolder> kpiHolders = remoteSpace.getRootKpiHolders();

	KpiHolder kpiHolder = kpiHolders.get(0);
	java.util.List<KpiHolder> children = new ArrayList<KpiHolder>(kpiHolder.getChildren());
	Collections.sort(children, KpiHolder.COMPARATOR_NAME);

	assertEquals("Number of KpiHolders is not correct.", 1, kpiHolders.size());
    }
}