package nl.valori.space.serialize.converters;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import nl.valori.space.serialize.ClassInfo;
import nl.valori.space.serialize.Converter;
import nl.valori.space.serialize.Token;
import nl.valori.space.serialize.marshall.MarshallWriter;
import nl.valori.space.serialize.marshall.Processor;

public class DateTimeConverter extends Converter {

    private static final Class<?>[] TARGET_CLASSES = { Calendar.class, Date.class };

    @Override
    public Token getToken() {
	return Token.SIMPLE_VALUE;
    }

    @Override
    public Class<?>[] getSupportedClasses() {
	return TARGET_CLASSES;
    }

    @Override
    public boolean canConvert(Class<?> clazz) {
	for (Class<?> supportedClass : TARGET_CLASSES) {
	    if (supportedClass.isAssignableFrom(clazz)) {
		return true;
	    }
	}
	return false;
    }

    @Override
    public boolean serialize(Object object, Class<?> serializableClass, ClassInfo classInfo, Processor processor,
	    MarshallWriter writer) throws IOException {
	Date date;
	if (object instanceof Date) {
	    date = (Date) object;
	} else if (object instanceof Calendar) {
	    date = ((Calendar) object).getTime();
	} else {
	    throw new RuntimeException("Unsupported type: " + object.getClass().getName());
	}

	writer.write(String.valueOf(date.getTime()));
	return true;
    }

    @Override
    public Object instantiate(Class<?> clazz, Object... args) {
	String formattedDate = (String) args[0];
	Date date = new Date(Long.valueOf(formattedDate));
	if (Date.class.isAssignableFrom(clazz)) {
	    return date;
	} else if (Calendar.class.isAssignableFrom(clazz)) {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    return cal;
	} else {
	    throw new RuntimeException("Unsupported type: " + clazz.getName());
	}
    }
}