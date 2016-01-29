package nl.valori.space.remote;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Set;

import nl.valori.space.SpaceFiller;
import nl.valori.space.SpaceId;
import nl.valori.space.serialize.unmarshall.Unmarshaller;

public class RemoteSpaceFiller implements SpaceFiller {

    // TODO - make server-URL configurable
    protected static final String REMOTE_SPACE_URL = "http://localhost:8080/dashboard/server";
    protected static final Charset CHARSET = Charset.forName("UTF8");

    public void getObjects(SpaceId... spaceIds) {
	try {
	    StringBuilder sb = new StringBuilder();
	    for (SpaceId spaceId : spaceIds) {
		if (sb.length() == 0) {
		    sb.append("?");
		} else {
		    sb.append("&");
		}
		sb.append("id=" + spaceId);
	    }

	    URL url = new URL(REMOTE_SPACE_URL + sb.toString());
	    // TODO - remove debug logging request
	    System.err.println("Request: " + url.toExternalForm());
	    InputStream is = url.openStream();
	    Reader reader = new InputStreamReader(is, CHARSET);
	    // Fetch the objects.
	    Set<SpaceId> deserializedSpaceIds = new Unmarshaller().unmarshall(reader);
	    is.close();

	    // Check the delivery.
	    for (SpaceId spaceId : spaceIds) {
		if (!deserializedSpaceIds.remove(spaceId)) {
		    throw new RuntimeException("Received a SpaceId that wasn't requested: " + spaceId);
		}
	    }
	    for (SpaceId spaceId : deserializedSpaceIds) {
		if (!spaceId.isTemporary()) {
		    // TODO - throw new RuntimeException("Additional SpaceId's have been fetched: " +
		    // deserializedSpaceIds);
		}
	    }
	} catch (MalformedURLException e) {
	    throw new RuntimeException(e);
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }
}