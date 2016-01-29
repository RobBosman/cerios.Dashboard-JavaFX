package nl.valori.space.serialize.marshall;

import java.io.IOException;
import java.util.Set;

import nl.valori.space.IdentitySet;
import nl.valori.space.Space;
import nl.valori.space.SpaceId;
import nl.valori.space.serialize.ClassInfo;
import nl.valori.space.serialize.Converter;
import nl.valori.space.serialize.marshall.writers.VoidWriter;

public class ProcessorFirstPhase implements Processor {

    private Marshaller marshaller;
    private Object currentObject;
    private Set<Object> objecsToBeSerialized;

    public ProcessorFirstPhase(Marshaller marshaller) {
	this.marshaller = marshaller;
	this.objecsToBeSerialized = new IdentitySet<Object>();
    }

    public void writeSpaceObject(Object object) throws IOException {
	currentObject = object;
	// Analyze the content of the spaceObject.
	process(currentObject, null, false);
    }

    public boolean process(Object object, ClassInfo classInfo, boolean isSpaceIdAllowed) throws IOException {
	// Skip null objects.
	if (object == null) {
	    return false;
	}
	// Primitives are treated separately.
	if (object.getClass().isPrimitive()) {
	    return true;
	}

	// Find out which (base) class must be used to serialize the object.
	Class<?> serializableClass = marshaller.getSerializableClass(object);

	// Check if the object has been inspected before.
	if (!objecsToBeSerialized.add(object)) {
	    // Yes, this very same object has been processed before. Probably this is due to a circular reference (e.g.
	    // a parent object refers to children and each child object refers to its parent). To solve this, we add the
	    // object to the Space. During serialization all references to it will be replaced by the spaceId.
	    // However, to reduce overhead, primitives or objects of type String will NOT be replaced by their SpaceId.
	    if (Converter.isReferable(serializableClass)) {
		// Mark the object itself to be serialized.
		marshaller.registerNonLazyReference(object);
		return true;
	    }
	}

	// If no SpaceRef should be serialized, but if a SpaceId is presented for serialization...
	if ((!isSpaceIdAllowed) && (object instanceof SpaceId)) {
	    // ...then make sure that the object itself will be serialized too, so the reference can be resolved
	    // during deserialization.
	    marshaller.registerSpaceIdToBeSerialized((SpaceId) object);
	}

	// If the object was already registered in the Space before we started serializing...
	if (Converter.isReferable(serializableClass)) {
	    // If the object is registered in the Space...
	    SpaceId spaceId = Space.getInstance().getId(object);
	    if (spaceId != null) {
		// ...then we will serialize it in combination with its spaceId.
		marshaller.registerSpaceIdToBeSerialized(spaceId);
	    }
	}

	// Get a converter for this Class and check if further analysis is required.
	Converter converter = marshaller.getSerializeContext().getConverter(serializableClass);
	return converter.serialize(object, serializableClass, classInfo, this, VoidWriter.INSTANCE);
    }
}
