package nl.valori.space;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public class SpaceList<T> extends AbstractSpaceCollection<T, List<T>> implements List<T> {

    protected class SpaceListIterator extends SpaceIterator implements ListIterator<T> {

	private ListIterator<SpaceId> spaceIdIter;

	public SpaceListIterator() {
	    spaceIdIter = ids().listIterator();
	}

	public SpaceListIterator(int index) {
	    spaceIdIter = ids().listIterator(index);
	}

	public void add(T e) {
	    spaceIdIter.add(Space.getInstance().put(e));
	}

	public boolean hasPrevious() {
	    return spaceIdIter.hasPrevious();
	}

	public int nextIndex() {
	    return spaceIdIter.nextIndex();
	}

	@SuppressWarnings("unchecked")
	public T previous() {
	    return (T) Space.getInstance().read(spaceIdIter.previous());
	}

	public int previousIndex() {
	    return spaceIdIter.previousIndex();
	}

	public void set(T e) {
	    spaceIdIter.set(Space.getInstance().put(e));
	}
    }

    public SpaceList() {
	super(new ArrayList<SpaceId>());
    }

    private List<SpaceId> ids() {
	return (List<SpaceId>) getIds();
    }

    public void add(int index, T element) {
	ids().add(index, Space.getInstance().put(element));
    }

    public boolean addAll(int index, Collection<? extends T> collection) {
	int oldSize = ids().size();
	for (T t : collection) {
	    add(index++, t);
	}
	return oldSize != ids().size();
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
	return (T) Space.getInstance().read(ids().get(index));
    }

    public int indexOf(Object obj) {
	List<SpaceId> ids = ids();
	for (int index = 0; index < ids.size(); index++) {
	    if (Space.getInstance().read(ids.get(index)).equals(obj)) {
		return index;
	    }
	}
	return -1;
    }

    public int lastIndexOf(Object obj) {
	List<SpaceId> ids = ids();
	for (int index = ids.size(); index >= 0; index--) {
	    if (Space.getInstance().read(ids.get(index)).equals(obj)) {
		return index;
	    }
	}
	return -1;
    }

    public ListIterator<T> listIterator() {
	fetchLazyData();
	return new SpaceListIterator();
    }

    public ListIterator<T> listIterator(int index) {
	fetchLazyData();
	return new SpaceListIterator(index);
    }

    @SuppressWarnings("unchecked")
    public T remove(int index) {
	return (T) Space.getInstance().read(ids().remove(index));
    }

    @SuppressWarnings("unchecked")
    public T set(int index, T element) {
	return (T) ids().set(index, Space.getInstance().put(element));
    }

    @SuppressWarnings("unchecked")
    public List<T> subList(int fromIndex, int toIndex) {
	List<T> subList = new ArrayList<T>();
	for (SpaceId spaceId : ids().subList(fromIndex, toIndex)) {
	    subList.add((T) Space.getInstance().read(spaceId));
	}
	return subList;
    }
}