package nl.valori.space.serialize.unmarshall;

import java.util.Map;

import nl.valori.space.serialize.ClassInfo;

public class AssignmentToMap implements Assignment {

    private Map<Object, Object> map;
    private AbstractAssignment keyAssignment;
    private AbstractAssignment valueAssignment;

    @SuppressWarnings("unchecked")
    public AssignmentToMap(Object key, Object value, Map<?, ?> map, ClassInfo classInfo) {
	ClassInfo[] entryClassInfo = classInfo.getGenericTypes();
	if (entryClassInfo.length != 2) {
	    throw new RuntimeException("Expected ClassInfo with 2 generic types for Map key and value, not "
		    + entryClassInfo);
	}
	this.map = (Map<Object, Object>) map;
	this.keyAssignment = new AbstractAssignment(key, entryClassInfo[0].isSpaceRef()) {

	    @Override
	    protected void doAssign() {
		// Do nothing here
	    }
	};
	this.valueAssignment = new AbstractAssignment(value, entryClassInfo[1].isSpaceRef()) {

	    @Override
	    protected void doAssign() {
		// Do nothing here
	    }
	};
    }

    public boolean assign() {
	if ((!keyAssignment.isResolved()) || (!valueAssignment.isResolved())) {
	    return false;
	}
	map.put(keyAssignment.getResolvedObject(), valueAssignment.getResolvedObject());
	return keyAssignment.assign() && valueAssignment.assign();
    }

    @Override
    public String toString() {
	return keyAssignment.toString() + ", " + valueAssignment;
    }
}