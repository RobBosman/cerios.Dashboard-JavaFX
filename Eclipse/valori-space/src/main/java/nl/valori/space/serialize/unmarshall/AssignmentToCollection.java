package nl.valori.space.serialize.unmarshall;

import java.util.Collection;

import nl.valori.space.AbstractSpaceCollection;

public class AssignmentToCollection extends AbstractAssignment {

    private Collection<?> collection;

    public AssignmentToCollection(Object element, Collection<?> collection) {
	super(element, (collection instanceof AbstractSpaceCollection<?, ?>));
	if (collection instanceof AbstractSpaceCollection<?, ?>) {
	    this.collection = ((AbstractSpaceCollection<?, ?>) collection).getIds();
	} else {
	    this.collection = collection;
	}
    }

    @SuppressWarnings("unchecked")
    @Override
    public void doAssign() {
	((Collection<Object>) collection).add(getResolvedObject());
    }
}