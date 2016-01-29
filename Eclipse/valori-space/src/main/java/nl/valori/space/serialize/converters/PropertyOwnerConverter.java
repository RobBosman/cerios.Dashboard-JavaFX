package nl.valori.space.serialize.converters;

import java.io.IOException;
import java.util.Set;

import nl.valori.space.serialize.ClassInfo;
import nl.valori.space.serialize.Converter;
import nl.valori.space.serialize.PropertyAccessor;
import nl.valori.space.serialize.PropertyAccessorMap;
import nl.valori.space.serialize.Token;
import nl.valori.space.serialize.marshall.MarshallWriter;
import nl.valori.space.serialize.marshall.Processor;

public class PropertyOwnerConverter extends Converter {

    @Override
    public Class<?>[] getSupportedClasses() {
	return new Class<?>[] { Object.class };
    }

    @Override
    public Token getToken() {
	return Token.PROPERTY_OWNER;
    }

    @Override
    public boolean canConvert(Class<?> clazz) {
	if (super.canConvert(clazz)) {
	    PropertyAccessorMap propertyAccessor = PropertyAccessorMap.getAccessorMap(clazz);
	    return ((propertyAccessor != null) && (propertyAccessor.getNames().size() > 0));
	} else {
	    return false;
	}
    }

    @Override
    public boolean serialize(Object object, Class<?> serializableClass, ClassInfo classInfo, Processor processor,
	    MarshallWriter writer) throws IOException {
	// Get the properties.
	PropertyAccessorMap propertyAccessors = PropertyAccessorMap.getAccessorMap(serializableClass);
	Set<String> propertyNames = propertyAccessors.getNames();

	// Serialize each property.
	boolean hasSerializedData = false;
	for (String propertyName : propertyNames) {
	    PropertyAccessor propertyAccessor = propertyAccessors.getAccessor(propertyName);
	    Object value = propertyAccessor.getPropertyValue(object);
	    if (value != null) {
		writer.beginToken(Token.KEY);
		writer.write(propertyName);
		writer.endToken(Token.KEY);
		processor.process(value, propertyAccessor.getClassInfo(), propertyAccessor.isSpaceRef());
		hasSerializedData = true;
	    }
	}
	return hasSerializedData;
    }
}
