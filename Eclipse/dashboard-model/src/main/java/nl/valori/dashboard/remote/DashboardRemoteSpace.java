package nl.valori.dashboard.remote;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import nl.valori.dashboard.model.BootstrapQueries;
import nl.valori.dashboard.model.KpiHolder;
import nl.valori.space.Space;
import nl.valori.space.SpaceId;
import nl.valori.space.remote.RemoteSpaceFiller;
import nl.valori.space.serialize.unmarshall.Unmarshaller;

public class DashboardRemoteSpace extends RemoteSpaceFiller implements BootstrapQueries {

    public List<KpiHolder> getRootKpiHolders() {
	try {
	    URL url = new URL(REMOTE_SPACE_URL + "?getRootKpiHolders");
	    InputStream is = url.openStream();
	    Reader reader = new InputStreamReader(is, CHARSET);
	    Set<SpaceId> deserializedSpaceIds = new Unmarshaller().unmarshall(reader);
	    is.close();

	    List<KpiHolder> kpiHolders = new ArrayList<KpiHolder>();
	    Space space = Space.getInstance();
	    for (SpaceId spaceId : deserializedSpaceIds) {
		Object object = space.read(spaceId);
		if (object instanceof KpiHolder) {
		    kpiHolders.add((KpiHolder) object);
		}
	    }
	    return kpiHolders;
	} catch (MalformedURLException e) {
	    throw new RuntimeException(e);
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }
}