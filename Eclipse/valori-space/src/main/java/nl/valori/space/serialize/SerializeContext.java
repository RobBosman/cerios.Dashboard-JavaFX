package nl.valori.space.serialize;

import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilderFactory;

import nl.valori.space.annotations.SerializableEntity;
import nl.valori.space.annotations.SerializableEntityPackage;
import nl.valori.space.serialize.converters.ArrayConverter;
import nl.valori.space.serialize.converters.ClassConverter;
import nl.valori.space.serialize.converters.EnumConverter;
import nl.valori.space.serialize.converters.ListConverter;
import nl.valori.space.serialize.converters.MapConverter;
import nl.valori.space.serialize.converters.PrimitiveConverter;
import nl.valori.space.serialize.converters.PropertyOwnerConverter;
import nl.valori.space.serialize.converters.SimpleValueConverter;
import nl.valori.space.serialize.converters.SpaceIdConverter;
import nl.valori.space.serialize.converters.SpaceListConverter;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SerializeContext {

    private static final String SPACE_NAMESPACE = "http://www.valori.nl/space/v20090922";

    private static Set<Converter> converters;
    private static Set<Class<?>> serializableEntities;

    static {
	converters = new TreeSet<Converter>(Converter.COMPARATOR);
	serializableEntities = new HashSet<Class<?>>();

	// Load the standard converters.
	converters.add(new SpaceIdConverter());
	converters.add(new ClassConverter());
	converters.add(new PrimitiveConverter());
	converters.add(new EnumConverter());
	converters.add(new SimpleValueConverter());
	converters.add(new ArrayConverter());
	converters.add(new ListConverter());
	converters.add(new MapConverter());
	converters.add(new SpaceListConverter());
	converters.add(new PropertyOwnerConverter());

	loadConfiguration("space-config.xml");
    }

    public static void addConverter(Converter converter) {
	converters.add(converter);
    }

    private Map<Class<?>, Converter> clazzConverterMap;

    public SerializeContext() {
	clazzConverterMap = new HashMap<Class<?>, Converter>();
    }

    public Class<?> determineSerializableClass(Object object) {
	// Find out if the annotation @SerializableEntity has been specified for this class.
	Class<?> clazz = object.getClass();
	Class<?> serializableClass = object.getClass();
	while (!clazz.equals(Object.class)) {
	    if (isSerializableEntity(clazz)) {
		serializableClass = clazz;
	    }
	    clazz = clazz.getSuperclass();
	}
	return serializableClass;
    }

    public Converter getConverter(Class<?> clazz) {
	Converter cachedConverter = clazzConverterMap.get(clazz);
	if (cachedConverter != null) {
	    return cachedConverter;
	}
	// Choose the most appropriate converter for this object.
	for (Converter converter : converters) {
	    if (converter.canConvert(clazz)) {
		// Register the converter with the objects class, so we can skip searching for it later on during the
		// actual serialization.
		clazzConverterMap.put(clazz, converter);
		return converter;
	    }
	}
	throw new UnsupportedOperationException("No suitable Converter was found for class '" + clazz.getName() + "'.");
    }

    private boolean isSerializableEntity(Class<?> clazz) {
	if (Modifier.isAbstract(clazz.getModifiers())) {
	    return false;
	} else if ((clazz.isAnonymousClass()) || (clazz.isSynthetic())) {
	    return false;
	} else if (serializableEntities.contains(clazz)) {
	    return true;
	} else if (clazz.isAnnotationPresent(SerializableEntity.class)) {
	    return true;
	} else if (clazz.getPackage().isAnnotationPresent(SerializableEntityPackage.class)) {
	    return true;
	} else {
	    return false;
	}
    }

    private static void loadConfiguration(String configFileName) {
	ClassLoader classLoader = SerializeContext.class.getClassLoader();
	InputStream is = classLoader.getResourceAsStream(configFileName);
	if (is == null) {
	    is = classLoader.getResourceAsStream("/" + configFileName);
	}
	if (is != null) {
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    dbf.setNamespaceAware(true);
	    try {
		try {
		    Document doc = dbf.newDocumentBuilder().parse(is);

		    NodeList packageAliasNodes = doc.getElementsByTagNameNS(SPACE_NAMESPACE, "package-alias");
		    for (int i = 0; i < packageAliasNodes.getLength(); i++) {
			Node packageAliasNode = packageAliasNodes.item(i);
			Node packageNameNode = packageAliasNode.getAttributes().getNamedItem("name");
			String packageName = packageNameNode.getTextContent();
			ClassInfo.addPackageAliase(packageName);
		    }

		    NodeList serializableEntityNodes = doc.getElementsByTagNameNS(SPACE_NAMESPACE,
			    "serializable-entity");
		    for (int i = 0; i < serializableEntityNodes.getLength(); i++) {
			Node serializableEntityNode = serializableEntityNodes.item(i);
			Node classNameNode = serializableEntityNode.getAttributes().getNamedItem("class");
			String className = classNameNode.getTextContent();
			serializableEntities.add(ClassInfo.classForName(className));
		    }

		    NodeList converterNodes = doc.getElementsByTagNameNS(SPACE_NAMESPACE, "converter");
		    for (int i = 0; i < converterNodes.getLength(); i++) {
			Node converterNode = converterNodes.item(i);
			Node classNameNode = converterNode.getAttributes().getNamedItem("class");
			String className = classNameNode.getTextContent();
			Converter converter = (Converter) Class.forName(className).newInstance();
			converters.add(converter);
		    }
		} finally {
		    is.close();
		}
	    } catch (Exception e) {
		throw new RuntimeException(e);
	    }
	}
    }
}
