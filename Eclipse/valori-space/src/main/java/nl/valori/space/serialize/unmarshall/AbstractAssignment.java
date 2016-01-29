package nl.valori.space.serialize.unmarshall;

import nl.valori.space.Space;
import nl.valori.space.SpaceId;

public abstract class AbstractAssignment implements Assignment {

    private Object objectToAssign;
    private boolean isSpaceIdAllowed;
    private boolean isAssignmentDone;

    public AbstractAssignment(Object objectToAssign, boolean isSpaceIdAllowed) {
	this.objectToAssign = objectToAssign;
	this.isSpaceIdAllowed = isSpaceIdAllowed;
    }

    protected abstract void doAssign();

    public boolean assign() {
	if (!isResolved()) {
	    return false;
	}

	if (isAssignmentDone) {
	    throw new RuntimeException("Assignment has already been executed.");
	}
	doAssign();
	isAssignmentDone = true;
	return true;
    }

    protected boolean isResolved() {
	if (objectToAssign instanceof SpaceId) {
	    if (!isSpaceIdAllowed) {
		SpaceId spaceId = (SpaceId) objectToAssign;
		Object resolvedElement = Space.getInstance().read(spaceId);
		if (resolvedElement == null) {
		    return false;
		}
		objectToAssign = resolvedElement;
	    }
	} else if (isSpaceIdAllowed) {
	    throw new IllegalArgumentException("Expected element of class SpaceId, not "
		    + objectToAssign.getClass().getName());
	}
	return true;
    }

    protected Object getResolvedObject() {
	if (!isResolved()) {
	    throw new RuntimeException("SpaceId `" + objectToAssign + "` could not resolved.");
	}
	return objectToAssign;
    }

    @Override
    public String toString() {
	return String.valueOf(objectToAssign);
    }
}