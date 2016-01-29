package nl.valori.dashboard.model;

import nl.valori.space.annotations.SerializableTransient;

public abstract class AbstractPersistentEntity {

    @SerializableTransient
    private Long id;

    public Long getId() {
	return this.id;
    }

    public void setId(Long id) {
	this.id = id;
    }
}