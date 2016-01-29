package nl.valori.space;

public class SpaceRef<T> {

    private static final ThreadLocal<SpaceId> spaceIdToBeSet = new ThreadLocal<SpaceId>();

    public static void setValue(SpaceId spaceId) {
	spaceIdToBeSet.set(spaceId);
    }

    private SpaceId spaceId;

    public SpaceId getId() {
	return spaceId;
    }

    public void setId(SpaceId spaceId) {
	this.spaceId = spaceId;
    }

    @SuppressWarnings("unchecked")
    public T get() {
	if (spaceId == null) {
	    return null;
	} else {
	    return (T) Space.getInstance().read(spaceId);
	}
    }

    public void set(T object) {
	if (object == null) {
	    spaceId = spaceIdToBeSet.get();
	    spaceIdToBeSet.set(null);
	} else {
	    spaceId = Space.getInstance().put(object);
	}
    }

    @Override
    public String toString() {
	return get().toString();
    }
}