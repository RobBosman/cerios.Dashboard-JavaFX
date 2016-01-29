package nl.valori.space.serialize.unmarshall;

import nl.valori.space.serialize.ClassInfo;

public class AssignmentToArray extends AbstractAssignment {

    private int index;
    private Object[] array;

    public AssignmentToArray(Object element, int index, Object[] array, ClassInfo arrayClassInfo) {
	super(element, arrayClassInfo.isSpaceRef());
	this.index = index;
	this.array = array;
    }

    @Override
    public void doAssign() {
	array[index] = getResolvedObject();
    }
}