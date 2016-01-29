package nl.valori.space;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class IdentitySet<T> implements Set<T> {

    static class Wrap {

	private Object content;

	public Wrap(Object content) {
	    this.content = content;
	}

	public Object getContent() {
	    return content;
	}

	@Override
	public boolean equals(Object object) {
	    if (object instanceof Wrap) {
		return content == ((Wrap) object).content;
	    } else {
		return content == object;
	    }
	}

	@Override
	public int hashCode() {
	    if (content == null) {
		return 0;
	    } else {
		return content.hashCode();
	    }
	}

	@Override
	public String toString() {
	    return String.valueOf(content);
	}
    }

    public class WrapIterator implements Iterator<T> {

	private Iterator<Wrap> wrapIter = wraps.iterator();

	public boolean hasNext() {
	    return wrapIter.hasNext();
	}

	@SuppressWarnings("unchecked")
	public T next() {
	    return (T) wrapIter.next().getContent();
	}

	public void remove() {
	    wrapIter.remove();
	}
    }

    private Set<Wrap> wraps;

    public IdentitySet() {
	this.wraps = new HashSet<Wrap>();
    }

    public boolean add(T object) {
	return wraps.add(new Wrap(object));
    }

    public boolean addAll(Collection<? extends T> collection) {
	boolean isChanged = false;
	for (T object : collection) {
	    isChanged |= add(object);
	}
	return isChanged;
    }

    public void clear() {
	wraps.clear();
    }

    public boolean contains(Object object) {
	return wraps.contains(new Wrap(object));
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
	return wraps.isEmpty();
    }

    public Iterator<T> iterator() {
	return new WrapIterator();
    }

    public boolean remove(Object object) {
	return wraps.remove(new Wrap(object));
    }

    public boolean removeAll(Collection<?> collection) {
	if (collection == null) {
	    return false;
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
	boolean isChanged = false;
	Iterator<Wrap> iter = wraps.iterator();
	while (iter.hasNext()) {
	    Wrap wrap = iter.next();
	    if (!collection.contains(wrap.getContent())) {
		iter.remove();
		isChanged = true;
	    }
	}
	return isChanged;
    }

    public int size() {
	return wraps.size();
    }

    public Object[] toArray() {
	Object[] array = new Object[wraps.size()];
	int i = 0;
	for (Wrap wrap : wraps) {
	    array[i++] = wrap.getContent();
	}
	return array;
    }

    @SuppressWarnings("unchecked")
    public <U> U[] toArray(U[] a) {
	// TODO - is toArray(U[] a) correct?
	return (U[]) toArray();
    }

    @Override
    public String toString() {
	return wraps.toString();
    }
}
