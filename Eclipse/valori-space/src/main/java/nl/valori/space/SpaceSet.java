package nl.valori.space;

import java.util.HashSet;
import java.util.Set;

public class SpaceSet<T> extends AbstractSpaceCollection<T, Set<T>> implements Set<T> {

    public SpaceSet() {
	super(new HashSet<SpaceId>());
    }
}