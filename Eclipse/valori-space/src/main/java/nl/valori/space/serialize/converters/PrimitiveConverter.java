package nl.valori.space.serialize.converters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import nl.valori.space.serialize.ClassInfo;
import nl.valori.space.serialize.Converter;
import nl.valori.space.serialize.Token;
import nl.valori.space.serialize.marshall.MarshallWriter;
import nl.valori.space.serialize.marshall.Processor;

public class PrimitiveConverter extends Converter {

    private static final Map<Class<?>, Class<?>> TYPE_MAP;
    private static final Class<?>[] SUPPORTED_CLASSES;

    static {
	TYPE_MAP = new HashMap<Class<?>, Class<?>>();
	TYPE_MAP.put(boolean.class, Boolean.class);
	TYPE_MAP.put(byte.class, Byte.class);
	TYPE_MAP.put(char.class, Character.class);
	TYPE_MAP.put(short.class, Short.class);
	TYPE_MAP.put(int.class, Integer.class);
	TYPE_MAP.put(long.class, Long.class);
	TYPE_MAP.put(float.class, Float.class);
	TYPE_MAP.put(double.class, Double.class);

	SUPPORTED_CLASSES = TYPE_MAP.values().toArray(new Class<?>[0]);
    }

    @Override
    public Token getToken() {
	return Token.SIMPLE_VALUE;
    }

    @Override
    public Class<?>[] getSupportedClasses() {
	return SUPPORTED_CLASSES;
    }

    @Override
    public boolean canConvert(Class<?> clazz) {
	return (clazz.isPrimitive() || TYPE_MAP.values().contains(clazz));
    }

    @Override
    public boolean serialize(Object object, Class<?> serializableClass, ClassInfo classInfo, Processor processor,
	    MarshallWriter writer) throws IOException {
	writer.write(object.toString());
	return true;
    }

    @Override
    public Object instantiate(Class<?> clazz, Object... args) {
	Class<?> instanceClazz = TYPE_MAP.get(clazz);
	if (instanceClazz == null) {
	    instanceClazz = clazz;
	}
	return super.instantiate(instanceClazz, args);
    }
}