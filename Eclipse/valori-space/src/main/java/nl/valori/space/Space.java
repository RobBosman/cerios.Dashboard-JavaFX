package nl.valori.space;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;

public class Space {

    private static final Space singleton = new Space();

    public static final Space getInstance() {
	return singleton;
    }

    private Map<SpaceId, Object> objectMap;
    private long latestPermId;
    private int latestTempId;
    private SpaceFiller spaceFiller;

    private Space() {
	objectMap = new WeakHashMap<SpaceId, Object>();
	// TODO - objectMap = new ConcurrentHashMap<SpaceId, Object>();
    }

    public void setFiller(SpaceFiller spaceFiller) {
	this.spaceFiller = spaceFiller;
    }

    public SpaceId getId(Object object) {
	return obtainId(object, false, false, null);
    }

    public SpaceId put(Object object) {
	return obtainId(object, false, true, null);
    }

    public SpaceId putTemporary(Object object) {
	return obtainId(object, true, true, null);
    }

    public SpaceId putAt(SpaceId spaceId, Object object) {
	return obtainId(object, false, true, spaceId);
    }

    public Object read(SpaceId spaceId) {
	Object object = objectMap.get(spaceId);
	// If the object was found or if it's a temporary SpaceId, then return the object.
	if ((object != null) || (spaceId.isTemporary())) {
	    return object;
	}
	// If the object has never been added to the Space...
	if (!objectMap.containsKey(spaceId)) {
	    // ...then throw an exception.
	    throw new RuntimeException("Cannot read SpaceId `" + spaceId
		    + "`, because it has never been added to the Space.");
	}
	// If a SpaceFiller is not available...
	if (spaceFiller == null) {
	    // ...then return null.
	    // TODO - (Probably an Unmarshaller is busy filling the space.)
	    return null;
	}
	// Fetch the object with the SpaceFiller.
	// TODO - introduce fetch groups
	spaceFiller.getObjects(spaceId);
	return objectMap.get(spaceId);
    }

    public Object take(SpaceId spaceId) {
	return objectMap.remove(spaceId);
    }

    public SpaceId getInternalSpaceId(SpaceId spaceId) {
	for (SpaceId key : objectMap.keySet()) {
	    if (key.equals(spaceId)) {
		return key;
	    }
	}
	objectMap.put(spaceId, null);
	return spaceId;
    }

    public void clear() {
	objectMap.clear();
	latestPermId = 0;
	latestTempId = 0;
    }

    private SpaceId obtainId(Object object, boolean isTemporary, boolean putIfAbsent, SpaceId preferredSpaceId) {
	if (object == null) {
	    if (putIfAbsent) {
		throw new RuntimeException("Sorry, null objects are not added to the Space.");
	    }
	    return null;
	}

	// If a certain spaceId is preferred...
	if (preferredSpaceId != null) {
	    // ...then see if the object is already present at that spaceId.
	    Object existingObject = objectMap.get(preferredSpaceId);
	    if ((!preferredSpaceId.isTemporary()) && (existingObject != null)) {
		// Throw an exception if another spaceObject is already present at the preferred spaceId.
		checkIfEquals(preferredSpaceId, object, existingObject);
	    }
	}

	// Find the object in the Space.
	for (Map.Entry<SpaceId, Object> entry : objectMap.entrySet()) {
	    // If it is present...
	    if (object.equals(entry.getValue())) {
		// ...then return its spaceId.
		// However, if a preferred SpaceId was specified and if it differs from the key that was found...
		if ((preferredSpaceId != null) && (!preferredSpaceId.equals(entry.getKey()))) {
		    // ...then throw an exception.
		    throw new RuntimeException("An object of class " + object.getClass().getName()
			    + " cannot be added at preferred SpaceId `" + preferredSpaceId
			    + "`, because it is already present at SpaceId `" + entry.getKey() + "`.");
		}
		return entry.getKey();
	    }
	}

	// The object is not in the Space.
	if (putIfAbsent) {
	    // Create a new unique spaceId...
	    if (preferredSpaceId == null) {
		preferredSpaceId = generateUniqueSpaceId(isTemporary);
	    }
	    // ...and add the object to the space.
	    objectMap.put(preferredSpaceId, object);
	    return preferredSpaceId;
	} else {
	    return null;
	}
    }

    private SpaceId generateUniqueSpaceId(boolean isTemporary) {
	SpaceId newId;
	do {
	    newId = new SpaceId(String.valueOf(isTemporary ? ++latestTempId : ++latestPermId), isTemporary);
	} while (objectMap.containsKey(newId));
	return newId;
    }

    /**
     * Checks if two objects are equal.
     * 
     * @param object1
     * @param object2
     * @return
     */
    private static void checkIfEquals(SpaceId preferredSpaceId, Object object1, Object object2) {
	// Throw an exception if another spaceObject is already present at the preferred spaceId.
	if (object1 == object2) {
	    return;
	} else if ((object1 != null) && (object2 != null) && (object1.equals(object2))) {
	    return;
	} else if (!object1.getClass().equals(object2.getClass())) {
	    throw new RuntimeException("Preferred SpaceId `" + preferredSpaceId
		    + "` is already occupied by an object of class " + object2.getClass().getName()
		    + " and will not be replaced by an object of class " + object1.getClass().getName());
	} else {
	    try {
		// Compare each getter property.
		for (Method getter : object1.getClass().getDeclaredMethods()) {
		    if ((Modifier.isPublic(getter.getModifiers())) && (getter.getParameterTypes().length == 0)
			    && (getter.getName().startsWith("get"))) {
			Object p1 = getter.invoke(object1, (Object[]) null);
			Object p2 = getter.invoke(object2, (Object[]) null);
			if ((p1 != null) && (!p1.equals(p2))) {
			    throw new RuntimeException("Preferred SpaceId `" + preferredSpaceId
				    + "` is already occupied by an object of class " + object1.getClass().getName()
				    + " that differs in property '" + getter.getName() + "': " + p1 + " != " + p2);
			}
		    }
		}
	    } catch (IllegalArgumentException e) {
		throw new RuntimeException(e);
	    } catch (IllegalAccessException e) {
		throw new RuntimeException(e);
	    } catch (InvocationTargetException e) {
		throw new RuntimeException(e);
	    }
	}
    }

    @Override
    public String toString() {
	String[] printLines = new String[objectMap.keySet().size()];
	int i = 0;
	for (Map.Entry<SpaceId, Object> entry : objectMap.entrySet()) {
	    Object object = entry.getValue();
	    if (object == null) {
		printLines[i++] = String.format("%5s(?)\tnull", ("`" + entry.getKey() + "`"));
	    } else {
		printLines[i++] = String.format("%5s(%s)\t%s", ("`" + entry.getKey() + "`"), object.getClass()
			.getName(), object);
	    }
	}
	// Due to garbage collection some keys may have gone in the mean time. So we have to fill in the blanks.
	while (i < printLines.length) {
	    printLines[i++] = "";
	}

	// Sort the output in increasing order.
	Arrays.sort(printLines);

	StringBuilder sb = new StringBuilder();
	for (String printLine : printLines) {
	    if (sb.length() > 0) {
		sb.append("\n");
	    }
	    sb.append(printLine);
	}
	return sb.toString();
    }
}
