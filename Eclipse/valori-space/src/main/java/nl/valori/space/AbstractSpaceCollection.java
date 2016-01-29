package nl.valori.space;

import java.util.Collection;
import java.util.Iterator;

public abstract class AbstractSpaceCollection<T, C extends Collection<T>> implements Collection<T> {

    protected class SpaceIterator implements Iterator<T> {

	private Iterator<SpaceId> spaceIdIter = ids.iterator();

	public boolean hasNext() {
	    return spaceIdIter.hasNext();
	}

	@SuppressWarnings("unchecked")
	public T next() {
	    return (T) Space.getInstance().read(spaceIdIter.next());
	}

	public void remove() {
	    spaceIdIter.remove();
	}
    }

    private Collection<SpaceId> ids;
    private transient C lazyObjects;

    public AbstractSpaceCollection(Collection<SpaceId> ids) {
	this.ids = ids;
    }

    public Collection<SpaceId> getIds() {
	return ids;
    }

    public void set(C collection) {
	// If the 'other' collection is an AbstractSpaceCollection too...
	if (collection instanceof AbstractSpaceCollection<?, ?>) {
	    // ...then just take the SpaceId's.
	    ids = ((AbstractSpaceCollection<?, ?>) collection).ids;
	} else {
	    lazyObjects = collection;
	}
    }

    public void fetchLazyData() {
	if (lazyObjects != null) {
	    C objects = lazyObjects;
	    lazyObjects = null;
	    if (objects.size() != ids.size()) {
		ids.clear();
		addAll(objects);
	    }
	}
    }

    public boolean add(T object) {
	return ids.add(Space.getInstance().put(object));
    }

    public boolean addAll(Collection<? extends T> collection) {
	boolean isChanged = false;
	for (T object : collection) {
	    isChanged |= add(object);
	}
	return isChanged;
    }

    public void clear() {
	if (lazyObjects != null) {
	    lazyObjects.clear();
	}
	ids.clear();
    }

    public boolean contains(Object value) {
	fetchLazyData();
	for (SpaceId id : ids) {
	    if (Space.getInstance().read(id).equals(value)) {
		return true;
	    }
	}
	return false;
    }

    public boolean containsAll(Collection<?> collection) {
	for (Object value : collection) {
	    if (!contains(value)) {
		return false;
	    }
	}
	return true;
    }

    public boolean isEmpty() {
	if (lazyObjects != null) {
	    return lazyObjects.isEmpty();
	} else {
	    return ids.isEmpty();
	}
    }

    public Iterator<T> iterator() {
	fetchLazyData();
	return new SpaceIterator();
    }

    public boolean remove(Object object) {
	if (lazyObjects != null) {
	    return lazyObjects.remove(object);
	} else {
	    return ids.remove(Space.getInstance().getId(object));
	}
    }

    public boolean removeAll(Collection<?> collection) {
	if (collection == null) {
	    return false;
	}
	if (lazyObjects != null) {
	    lazyObjects.removeAll(collection);
	}
	boolean isChanged = false;
	for (Object object : collection) {
	    isChanged |= remove(object);
	}
	return isChanged;
    }

    public boolean retainAll(Collection<?> collection) {
	if (collection == null) {
	    return false;
	}
	fetchLazyData();
	boolean isChanged = false;
	Iterator<SpaceId> iter = ids.iterator();
	while (iter.hasNext()) {
	    SpaceId id = iter.next();
	    if (!collection.contains(Space.getInstance().read(id))) {
		iter.remove();
		isChanged = true;
	    }
	}
	return isChanged;
    }

    public int size() {
	fetchLazyData();
	return ids.size();
    }

    public Object[] toArray() {
	fetchLazyData();
	Object[] array = new Object[ids.size()];
	int i = 0;
	for (SpaceId id : ids) {
	    array[i++] = Space.getInstance().read(id);
	}
	return array;
    }

    @SuppressWarnings("unchecked")
    public <U> U[] toArray(U[] a) {
	// TODO - is toArray(U[] a) correct?
	return (U[]) toArray();
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == this) {
	    return true;
	}
	if (obj instanceof AbstractSpaceCollection<?, ?>) {
	    AbstractSpaceCollection<?, ?> other = (AbstractSpaceCollection<?, ?>) obj;
	    if ((this.lazyObjects == null) && (other.lazyObjects == null)) {
		return this.ids.equals(other.ids);
	    } else if ((this.lazyObjects != null) && (other.lazyObjects != null)) {
		return this.lazyObjects.equals(other.lazyObjects);
	    }
	}
	if (obj instanceof Collection<?>) {
	    Collection<?> other = (Collection<?>) obj;
	    if (this.size() != other.size()) {
		return false;
	    }
	    Iterator<?> thisIter = this.iterator();
	    Iterator<?> otherIter = other.iterator();
	    while ((thisIter.hasNext()) && (otherIter.hasNext())) {
		if (!thisIter.next().equals(otherIter.next())) {
		    return false;
		}
	    }
	    return true;
	} else {
	    return false;
	}
    }

    @Override
    public String toString() {
	if (lazyObjects != null) {
	    return lazyObjects.toString();
	} else {
	    return ids.toString();
	}
    }
}
