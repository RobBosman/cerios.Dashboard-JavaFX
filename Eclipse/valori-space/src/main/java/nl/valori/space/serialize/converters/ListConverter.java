package nl.valori.space.serialize.converters;

import java.io.IOException;
import java.util.Collection;

import nl.valori.space.AbstractSpaceCollection;
import nl.valori.space.serialize.ClassInfo;
import nl.valori.space.serialize.Converter;
import nl.valori.space.serialize.Token;
import nl.valori.space.serialize.marshall.MarshallWriter;
import nl.valori.space.serialize.marshall.Processor;

public class ListConverter extends Converter {

    @Override
    public Token getToken() {
	return Token.LIST;
    }

    @Override
    public Class<?>[] getSupportedClasses() {
	return new Class<?>[] { Collection.class };
    }

    @Override
    public boolean serialize(Object object, Class<?> serializableClass, ClassInfo classInfo, Processor processor,
	    MarshallWriter writer) throws IOException {
	Collection<?> collection = (Collection<?>) object;
	boolean isSpaceIdAllowed = AbstractSpaceCollection.class.isAssignableFrom(serializableClass);
	ClassInfo elementClassInfo = null;
	if (classInfo != null) {
	    ClassInfo[] genericTypes = classInfo.getGenericTypes();
	    if (genericTypes.length != 1) {
		throw new RuntimeException("Expected 1 GenericType.");
	    }
	    elementClassInfo = classInfo.getGenericTypes()[0];
	}
	boolean hasSerializedData = false;
	for (Object element : collection) {
	    hasSerializedData |= processor.process(element, elementClassInfo, isSpaceIdAllowed);
	}
	return hasSerializedData;
    }
}