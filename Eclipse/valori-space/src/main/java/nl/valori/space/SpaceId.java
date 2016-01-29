package nl.valori.space;

import java.util.Comparator;

public class SpaceId {

    public static final Comparator<SpaceId> COMPARATOR = new Comparator<SpaceId>() {

	public int compare(SpaceId id1, SpaceId id2) {
	    return id1.id.compareTo(id2.id);
	}
    };

    private static final String ESCAPE_PREFIX = "\\";
    private static final String TEMP_PREFIX = "-";

    public static SpaceId parse(String parseString) {
	return new SpaceId(parseString);
    }

    private String id;

    public SpaceId(String id, boolean isTemporary) {
	if (isTemporary) {
	    this.id = TEMP_PREFIX + id;
	} else if ((id.startsWith(TEMP_PREFIX)) || (id.startsWith(ESCAPE_PREFIX))) {
	    this.id = ESCAPE_PREFIX + id;
	} else {
	    this.id = id;
	}
    }

    public SpaceId(SpaceId other) {
	this.id = other.id;
    }

    private SpaceId(String id) {
	this.id = id;
    }

    public String getId() {
	if (id.startsWith(TEMP_PREFIX)) {
	    return id.substring(TEMP_PREFIX.length());
	} else if (id.startsWith(ESCAPE_PREFIX)) {
	    return id.substring(ESCAPE_PREFIX.length());
	} else {
	    return id;
	}
    }

    public boolean isTemporary() {
	return id.startsWith(TEMP_PREFIX);
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	} else if (obj instanceof SpaceId) {
	    SpaceId other = (SpaceId) obj;
	    return (this.id.equals(other.id));
	} else {
	    return false;
	}
    }

    @Override
    public int hashCode() {
	return id.hashCode();
    }

    @Override
    public String toString() {
	return id;
    }
}