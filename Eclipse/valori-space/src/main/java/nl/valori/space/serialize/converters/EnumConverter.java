package nl.valori.space.serialize.converters;

public class EnumConverter extends SimpleValueConverter {

    @Override
    public Class<?>[] getSupportedClasses() {
	return new Class<?>[] { Enum.class };
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object instantiate(Class<?> clazz, Object... args) {
	try {
	    if (args.length != 1) {
		throw new RuntimeException("Expected only one argument to instantiate an enum of type "
			+ clazz.getName() + ", not " + args + ".");
	    }
	    return Enum.valueOf((Class<Enum>) clazz, (String) args[0]);
	} catch (SecurityException e) {
	    throw new RuntimeException(e);
	} catch (IllegalArgumentException e) {
	    throw new RuntimeException(e);
	}
    }
}