package nl.valori.space.serialize;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import nl.valori.space.AbstractSpaceCollection;
import nl.valori.space.Space;
import nl.valori.space.SpaceId;
import nl.valori.space.SpaceRef;

public class PropertyAccessor {

    private Method getter;
    private Method setter;
    private Field field;
    private ClassInfo classInfo;

    public void setGetter(Method getter) {
	this.getter = getter;
    }

    public void setSetter(Method setter) {
	this.setter = setter;
    }

    public void setField(Field field) {
	this.field = field;
    }

    public ClassInfo getClassInfo() {
	if (classInfo == null) {
	    boolean useFieldType = false;
	    if (field != null) {
		// Get the most specific type: the fieldType or the getterReturnType.
		Class<?> fieldType = field.getType();
		Class<?> getterType = getter.getReturnType();
		if ((fieldType.isAssignableFrom(SpaceRef.class)) || (getterType.isAssignableFrom(fieldType))) {
		    useFieldType = true;
		} else if (!getterType.isAssignableFrom(fieldType)) {
		    throw new RuntimeException("The field type '" + fieldType.getName()
			    + "' is not compatible with the return type of the getter method " + getterType.getName());
		}
	    }
	    if (useFieldType) {
		classInfo = new ClassInfo(field.getType(), field.toGenericString());
	    } else {
		classInfo = new ClassInfo(getter.getReturnType(), getter.toGenericString());
	    }
	}
	return classInfo;
    }

    public boolean hasSetter() {
	return (setter != null);
    }

    public boolean isSpaceRef() {
	return SpaceRef.class.isAssignableFrom(getClassInfo().getType());
    }

    public boolean isValid() {
	if (getter == null) {
	    return false;
	}
	// Properties of type Collection or Map don't require a setter.
	Class<?> propertyType = getClassInfo().getType();
	return (setter != null) || (Collection.class.isAssignableFrom(propertyType))
		|| (Map.class.isAssignableFrom(propertyType));
    }

    public Object getPropertyValue(Object propertyOwner) {
	try {
	    Object value = getter.invoke(propertyOwner, (Object[]) null);
	    if (isSpaceRef()) {
		SpaceId spaceId = Space.getInstance().getId(value);
		return spaceId;
	    } else {
		return value;
	    }
	} catch (IllegalArgumentException e) {
	    throw new RuntimeException(e);
	} catch (IllegalAccessException e) {
	    throw new RuntimeException(e);
	} catch (InvocationTargetException e) {
	    throw new RuntimeException(e);
	}
    }

    public void setPropertyValue(Object value, Object propertyOwner) {
	if (value instanceof SpaceId) {
	    if (!isSpaceRef()) {
		throw new IllegalArgumentException("Expected value of class SpaceId, not " + value.getClass().getName());
	    }
	    SpaceRef.setValue((SpaceId) value);
	    setSingleValue(null, propertyOwner);
	    return;
	}

	Class<?> propertyType = getClassInfo().getType();
	if ((value != null) && (!propertyType.isPrimitive()) && (!propertyType.isAssignableFrom(value.getClass()))) {
	    throw new RuntimeException("Cannot set a value of class '" + value.getClass().getName()
		    + "' to a property of class '" + propertyType.getName() + "'.");
	}

	if (setter != null) {
	    setSingleValue(value, propertyOwner);
	} else if (value instanceof AbstractSpaceCollection<?, ?>) {
	    setSpaceCollectionValue((AbstractSpaceCollection<?, ?>) value, propertyOwner);
	} else if (value instanceof Collection<?>) {
	    setCollectionValue((Collection<?>) value, propertyOwner);
	} else if (value instanceof Map<?, ?>) {
	    setMapValue((Map<?, ?>) value, propertyOwner);
	} else {
	    throw new UnsupportedOperationException("Don't know how to set a value of class "
		    + value.getClass().getName() + " to a property of class " + propertyType.getName());
	}
    }

    private void setSingleValue(Object value, Object propertyOwner) {
	try {
	    setter.invoke(propertyOwner, new Object[] { value });
	} catch (IllegalArgumentException e) {
	    throw new RuntimeException(e);
	} catch (IllegalAccessException e) {
	    throw new RuntimeException(e);
	} catch (InvocationTargetException e) {
	    throw new RuntimeException(e);
	}
    }

    private void setSpaceCollectionValue(AbstractSpaceCollection<?, ?> spaceCollectionValue, Object propertyOwner) {
	Object propertyValue = getPropertyValue(propertyOwner);
	if (propertyValue instanceof AbstractSpaceCollection<?, ?>) {
	    AbstractSpaceCollection<?, ?> spaceCollectionPropertyValue = (AbstractSpaceCollection<?, ?>) propertyValue;
	    spaceCollectionPropertyValue.clear();
	    spaceCollectionPropertyValue.getIds().addAll(spaceCollectionValue.getIds());
	} else {
	    throw new RuntimeException("Cannot assign a SpaceCollection to a '" + getClassInfo() + "'.");
	}
    }

    @SuppressWarnings("unchecked")
    private void setCollectionValue(Collection<?> collectionValue, Object propertyOwner) {
	Object propertyValue = getPropertyValue(propertyOwner);
	if (propertyValue instanceof Collection<?>) {
	    Collection<Object> collection = (Collection<Object>) propertyValue;
	    collection.clear();
	    collection.addAll(collectionValue);
	} else {
	    throw new RuntimeException("Cannot assign a Collection to a '" + getClassInfo() + "'.");
	}
    }

    @SuppressWarnings("unchecked")
    private void setMapValue(Map<?, ?> mapValue, Object propertyOwner) {
	Object propertyValue = getPropertyValue(propertyOwner);
	if (propertyValue instanceof Map<?, ?>) {
	    Map<Object, Object> map = (Map<Object, Object>) propertyValue;
	    map.clear();
	    map.putAll(mapValue);
	} else {
	    throw new RuntimeException("Cannot assign a Map to a '" + getClassInfo() + "'.");
	}
    }
}
