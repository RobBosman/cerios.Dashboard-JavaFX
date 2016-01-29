package nl.valori.space.serialize.marshall;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import nl.valori.space.Space;
import nl.valori.space.SpaceId;
import nl.valori.space.serialize.Converter;
import nl.valori.space.serialize.SerializeContext;
import nl.valori.space.serialize.marshall.writers.DefaultWriter;

public class Marshaller {

    private SerializeContext serializeContext;
    private Map<Object, Class<?>> objectClazzMap;
    private Set<SpaceId> spaceIdsToBeSerialized;
    private Set<SpaceId> temporarySpaceIds;

    public Marshaller() {
	serializeContext = new SerializeContext();
	objectClazzMap = new HashMap<Object, Class<?>>();
	spaceIdsToBeSerialized = new HashSet<SpaceId>();
	temporarySpaceIds = new TreeSet<SpaceId>(SpaceId.COMPARATOR);
    }

    public SerializeContext getSerializeContext() {
	return serializeContext;
    }

    public void marshall(Writer writer, Object... objects) throws IOException {
	marshall(new DefaultWriter(writer), objects);
    }

    public void marshall(ChainableMarshallWriter chainableMarshallWriter, Object... objects) throws IOException {
	if ((objects == null) || (objects.length == 0)) {
	    return;
	}

	spaceIdsToBeSerialized.clear();
	temporarySpaceIds.clear();
	objectClazzMap.clear();

	// Mark all objects to be serialized.
	for (Object object : objects) {
	    registerSpaceIdToBeSerialized(Space.getInstance().put(object));
	}

	// Analyze all objectsToBeSerialized by determining the required converters and detecting circular references.
	ProcessorFirstPhase firstPhase = new ProcessorFirstPhase(this);
	for (Object object : objects) {
	    firstPhase.writeSpaceObject(object);
	}

	// Now serialize everything, starting with the circular references...
	ProcessorSecondPhase secondPhase = new ProcessorSecondPhase(this, chainableMarshallWriter);
	for (SpaceId spaceId : temporarySpaceIds) {
	    secondPhase.writeSpaceId(spaceId);
	}
	// ...and then the rest.
	spaceIdsToBeSerialized.removeAll(temporarySpaceIds);
	for (SpaceId spaceId : spaceIdsToBeSerialized) {
	    secondPhase.writeSpaceId(spaceId);
	}

	chainableMarshallWriter.close();

	spaceIdsToBeSerialized.clear();
	temporarySpaceIds.clear();
	objectClazzMap.clear();
    }

    public Class<?> getSerializableClass(Object object) {
	Class<?> serializableClass = objectClazzMap.get(object);
	if (serializableClass == null) {
	    serializableClass = serializeContext.determineSerializableClass(object);
	    if ((Converter.isReferable(serializableClass))) {
		objectClazzMap.put(object, serializableClass);
	    }
	}
	return serializableClass;
    }

    public void registerSpaceIdToBeSerialized(SpaceId spaceId) {
	spaceIdsToBeSerialized.add(spaceId);
    }

    public void registerNonLazyReference(Object object) {
	SpaceId spaceId = Space.getInstance().getId(object);
	if (spaceId != null) {
	    registerSpaceIdToBeSerialized(spaceId);
	} else {
	    spaceId = Space.getInstance().putTemporary(object);
	    temporarySpaceIds.add(spaceId);
	}
    }

    public boolean isRegisteredToBeSerialized(SpaceId spaceId) {
	return (spaceIdsToBeSerialized.contains(spaceId) || temporarySpaceIds.contains(spaceId));
    }

    public boolean isTemporarySpaceId(SpaceId spaceId) {
	return temporarySpaceIds.contains(spaceId);
    }
}
