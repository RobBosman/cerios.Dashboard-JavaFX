package nl.valori.space.serialize;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import nl.valori.space.annotations.SerializableTransient;

public class PropertyAccessorMap {

    private static volatile Map<Class<?>, PropertyAccessorMap> cache = new TreeMap<Class<?>, PropertyAccessorMap>(
	    ClassInfo.COMPARATOR);

    public static PropertyAccessorMap getAccessorMap(Class<?> clazz) {
	PropertyAccessorMap accessorMap = null;
	for (Map.Entry<Class<?>, PropertyAccessorMap> entry : cache.entrySet()) {
	    if (entry.getKey().isAssignableFrom(clazz)) {
		accessorMap = entry.getValue();
	    }
	}
	if (accessorMap == null) {
	    accessorMap = new PropertyAccessorMap(clazz);
	    // Don't add empty maps to the cache!
	    if (accessorMap.getMap().isEmpty()) {
		accessorMap = null;
	    }
	    cache.put(clazz, accessorMap);
	}
	return accessorMap;
    }

    private static String getGetterPropertyName(Method method) {
	String propertyName = null;
	if ((Modifier.isPublic(method.getModifiers())) && (method.getParameterTypes().length == 0)) {
	    String methodName = method.getName();
	    if ((methodName.startsWith("get")) && (!void.class.equals(method.getReturnType()))) {
		propertyName = methodName.substring(3);
	    } else if (methodName.startsWith("is")) {
		if ((boolean.class.equals(method.getReturnType())) || (Boolean.class.equals(method.getReturnType()))) {
		    propertyName = methodName.substring(2);
		}
	    }
	}
	return toPropertyCase(propertyName);
    }

    private static String getSetterPropertyName(Method method) {
	String propertyName = null;
	if ((Modifier.isPublic(method.getModifiers())) && (method.getParameterTypes().length == 1)
		&& (method.getName().startsWith("set"))) {
	    propertyName = method.getName().substring(3);
	}
	return toPropertyCase(propertyName);
    }

    private static String toPropertyCase(String propertyName) {
	if (propertyName == null) {
	    return null;
	} else if (propertyName.length() == 0) {
	    return propertyName;
	} else if (Character.isUpperCase(propertyName.charAt(0))) {
	    char[] name = propertyName.toCharArray();
	    name[0] = Character.toLowerCase(name[0]);
	    return new String(name);
	} else {
	    return propertyName;
	}
    }

    private Map<String, PropertyAccessor> map;

    protected PropertyAccessorMap(Class<?> clazz) {
	map = new TreeMap<String, PropertyAccessor>();
	determineProperties(clazz);
    }

    public Set<String> getNames() {
	return map.keySet();
    }

    public PropertyAccessor getAccessor(String propertyName) {
	return getAccessor(propertyName, false);
    }

    protected Map<String, PropertyAccessor> getMap() {
	return map;
    }

    private PropertyAccessor getAccessor(String propertyName, boolean createIfAbsent) {
	PropertyAccessor pa = map.get(propertyName);
	if ((pa == null) && (createIfAbsent)) {
	    pa = new PropertyAccessor();
	    map.put(propertyName, pa);
	}
	return pa;
    }

    private void determineProperties(Class<?> clazz) {
	// Collect the getters and setters of the property owner class.
	Set<String> transientPropertyNames = new HashSet<String>();
	while (!clazz.equals(Object.class)) {
	    // Check each method to see if it is a getter or a setter.
	    for (Method method : clazz.getDeclaredMethods()) {
		String propertyName = getSetterPropertyName(method);
		if (propertyName != null) {
		    getAccessor(propertyName, true).setSetter(method);
		} else {
		    propertyName = getGetterPropertyName(method);
		    if (propertyName != null) {
			getAccessor(propertyName, true).setGetter(method);
		    }
		}
	    }
	    // Keep track of fields that are transient and/or SpaceRefs.
	    for (Field field : clazz.getDeclaredFields()) {
		String propertyName = field.getName();
		getAccessor(propertyName, true).setField(field);
		if ((Modifier.isTransient(field.getModifiers()))
		        || (field.isAnnotationPresent(SerializableTransient.class))) {
		    transientPropertyNames.add(propertyName);
		}
	    }
	    clazz = clazz.getSuperclass();
	}

	// Now remove all properties that are transient, or that have no setter, unless the property type is Collection.
	Set<String> invalidProperties = new HashSet<String>();
	for (Map.Entry<String, PropertyAccessor> entry : map.entrySet()) {
	    String propertyName = entry.getKey();
	    PropertyAccessor pa = entry.getValue();
	    if ((transientPropertyNames.contains(propertyName)) || (!pa.isValid())) {
		invalidProperties.add(propertyName);
	    }
	}

	// Remove the invalid properties.
	for (String invalidProperty : invalidProperties) {
	    map.remove(invalidProperty);
	}
    }
}
