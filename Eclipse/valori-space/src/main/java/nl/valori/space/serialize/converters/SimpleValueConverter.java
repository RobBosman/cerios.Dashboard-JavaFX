package nl.valori.space.serialize.converters;

import java.io.IOException;

import nl.valori.space.serialize.ClassInfo;
import nl.valori.space.serialize.Converter;
import nl.valori.space.serialize.Token;
import nl.valori.space.serialize.marshall.MarshallWriter;
import nl.valori.space.serialize.marshall.Processor;

public class SimpleValueConverter extends Converter {

    private static final Class<?>[] SUPPORTED_CLASSES = { Number.class, String.class };

    @Override
    public Token getToken() {
	return Token.SIMPLE_VALUE;
    }

    @Override
    public Class<?>[] getSupportedClasses() {
	return SUPPORTED_CLASSES;
    }

    @Override
    public boolean serialize(Object object, Class<?> serializableClass, ClassInfo classInfo, Processor processor,
	    MarshallWriter writer) throws IOException {
	writer.write(object.toString());
	return true;
    }
}