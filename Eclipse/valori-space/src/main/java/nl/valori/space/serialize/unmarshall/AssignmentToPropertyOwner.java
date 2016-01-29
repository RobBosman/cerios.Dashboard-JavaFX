package nl.valori.space.serialize.unmarshall;

import nl.valori.space.serialize.PropertyAccessor;

public class AssignmentToPropertyOwner extends AbstractAssignment {

    private Object propertyOwner;
    private PropertyAccessor propertyAccessor;

    public AssignmentToPropertyOwner(Object value, Object propertyOwner, PropertyAccessor propertyAccessor) {
	super(value, propertyAccessor.isSpaceRef());
	this.propertyOwner = propertyOwner;
	this.propertyAccessor = propertyAccessor;
    }

    @Override
    public void doAssign() {
	propertyAccessor.setPropertyValue(getResolvedObject(), propertyOwner);
    }
}