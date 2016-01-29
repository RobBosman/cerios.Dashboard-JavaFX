package nl.valori.space.serialize.testdata.race;

public abstract class AbstractPersistentEntity {

    private Long id;

    public Long getId() {
	return this.id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Override
    public int hashCode() {
	return id.intValue();
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	} else if (this.getClass().equals(obj.getClass())) {
	    AbstractPersistentEntity other = (AbstractPersistentEntity) obj;
	    if (this.id == null) {
		return other.id == null;
	    } else {
		return this.id.equals(other.id);
	    }
	} else {
	    return false;
	}
    }
}