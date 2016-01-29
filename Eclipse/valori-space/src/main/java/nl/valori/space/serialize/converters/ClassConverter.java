package nl.valori.space.serialize.converters;

import java.io.IOException;

import nl.valori.space.serialize.ClassInfo;
import nl.valori.space.serialize.Converter;
import nl.valori.space.serialize.Token;
import nl.valori.space.serialize.marshall.MarshallWriter;
import nl.valori.space.serialize.marshall.Processor;

public class ClassConverter extends Converter {

    private static final Class<?>[] SUPPORTED_CLASSES = { Class.class };

    @Override
    public Token getToken() {
	return Token.CLASS;
    }

    public Class<?>[] getSupportedClasses() {
	return SUPPORTED_CLASSES;
    }

    @Override
    public boolean serialize(Object object, Class<?> serializableClass, ClassInfo classInfo, Processor processor,
	    MarshallWriter writer) throws IOException {
	Class<?> clazz = (Class<?>) object;
	// TODO - intercept Hibernate classes.
	String result = ClassInfo.asString(clazz, classInfo);
	result = result.replace("org.hibernate.collection.PersistentSet", "HashSet");
	writer.write(result);
	return true;
    }
}