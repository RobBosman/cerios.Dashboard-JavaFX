package nl.valori.space.serialize.unmarshall;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.valori.space.Space;
import nl.valori.space.SpaceId;
import nl.valori.space.serialize.ClassInfo;
import nl.valori.space.serialize.Converter;
import nl.valori.space.serialize.PropertyAccessor;
import nl.valori.space.serialize.PropertyAccessorMap;
import nl.valori.space.serialize.SerializeContext;
import nl.valori.space.serialize.Token;

public class Unmarshaller {

    private static final int CHAR_BUFFER_SIZE = 1024;

    private SerializeContext serializeContext;
    private StringBuilder sb;
    private Collection<Assignment> delayedAssignments;

    public Unmarshaller() {
	serializeContext = new SerializeContext();
	// Note: delayedAssignments must be a List to which all assignments are added 'in order of appearance'. It is
	// necessary to maintain this order, otherwise assigning values at the end of the process may fail.
	delayedAssignments = new ArrayList<Assignment>();
    }

    public Set<SpaceId> unmarshall(Reader reader) throws IOException {
	sb = new StringBuilder();
	Set<SpaceId> deserializedIds = new HashSet<SpaceId>();

	delayedAssignments.clear();

	// Read the serial data from the input stream.
	CharBuffer charBuffer = CharBuffer.allocate(CHAR_BUFFER_SIZE);
	while (reader.read(charBuffer) != -1) {
	    charBuffer.flip();
	    sb.append(charBuffer.toString());
	}

	// TODO - remove debug logging response
	String received = sb.toString();
	System.err.println("Response:\n" + received);
	try {

	    // Parse the serial data and instantiate objects.
	    while (sb.length() > 0) {
		skipComments();
		SpaceId spaceId = readSpaceId(true);
		Object object = readAnyObject(null);
		spaceId = Space.getInstance().putAt(spaceId, object);
		deserializedIds.add(spaceId);
		skipWhitespaces();
	    }

	    // Now that all Space objects are available, we can assign the values that could not be resolved before.
	    for (Assignment assignment : delayedAssignments) {
		if (!assignment.assign()) {
		    throw new RuntimeException("Could not execute assignment '" + assignment + "'.");
		}
	    }

	} catch (RuntimeException e) {
	    System.err.println("Error at:\n" + sb.toString());
	    throw e;
	}

	return deserializedIds;
    }

    @SuppressWarnings("unchecked")
    private Object readAnyObject(ClassInfo classInfo) {
	skipComments();

	ClassInfo classInfoRead = readClassInfo(false);
	if (classInfoRead != null) {
	    if ((classInfo != null) && (!classInfo.getType().isAssignableFrom(classInfoRead.getType()))) {
		throw new RuntimeException("Specified class '" + classInfoRead.getType().getName()
			+ "' cannot be used; expected class '" + classInfo.getType().getName() + "'.");
	    }
	    classInfo = classInfoRead;
	}
	if (classInfo == null) {
	    throw new RuntimeException("Don't know which class to instantiate.");
	}

	skipComments();

	// If the object is a SpaceId...
	SpaceId spaceId = readSpaceId(classInfo.isSpaceRef());
	if (spaceId != null) {
	    // ...then register and return it.
	    return spaceId;
	}

	Class<?> clazz = classInfo.getType();
	Token nextToken = peekNextToken();
	if (nextToken == null) {
	    throw new RuntimeException("Could not find a token to be parsed.");
	}
	// Determine the kind of token that is expected for the given class.
	Converter converter = serializeContext.getConverter(clazz);
	Token expectedToken = converter.getToken();
	if ((nextToken != expectedToken) && (!nextToken.getBeginTag().equals(expectedToken.getBeginTag()))) {
	    throw new RuntimeException("Expected token " + expectedToken + ", but found " + nextToken + ".");
	}

	Object instance;
	if (expectedToken.equals(Token.SIMPLE_VALUE)) {
	    String simpleValue = Token.SIMPLE_VALUE.readValue(sb, true);
	    instance = converter.instantiate(clazz, simpleValue);
	} else if (expectedToken == Token.ARRAY) {
	    instance = readArray(classInfo, converter);
	} else if (expectedToken == Token.LIST) {
	    instance = converter.instantiate(clazz);
	    readCollection((Collection<Object>) instance, classInfo);
	} else if (expectedToken == Token.MAP) {
	    instance = converter.instantiate(clazz);
	    readMap((Map<Object, Object>) instance, classInfo);
	} else if (expectedToken == Token.PROPERTY_OWNER) {
	    instance = converter.instantiate(clazz);
	    readPropertyOwner(instance, clazz, classInfo);
	} else {
	    throw new RuntimeException("Parsing token " + expectedToken + " is not supported.");
	}
	if (instance == null) {
	    throw new RuntimeException("Parsed null value on token " + expectedToken + ".");
	}
	return instance;
    }

    private ClassInfo readClassInfo(boolean isExpected) {
	String genericClassName = Token.CLASS.readValue(sb, isExpected);
	if (genericClassName != null) {
	    return new ClassInfo(null, genericClassName);
	} else {
	    return null;
	}
    }

    private SpaceId readSpaceId(boolean isExpected) {
	String id = Token.SPACE_ID.readValue(sb, isExpected);
	if (id != null) {
	    return Space.getInstance().getInternalSpaceId(SpaceId.parse(id));
	} else {
	    return null;
	}
    }

    private Object[] readArray(ClassInfo arrayClassInfo, Converter converter) {
	ClassInfo elementClassInfo = arrayClassInfo.getArrayType();
	List<Object> rawList = new ArrayList<Object>();
	readElements(rawList, elementClassInfo, Token.ARRAY);
	Object[] array;
	if (converter != null) {
	    array = (Object[]) converter.instantiate(elementClassInfo.getType(), rawList.size());
	} else {
	    array = (Object[]) Array.newInstance(elementClassInfo.getType(), rawList.size());
	}

	for (int i = 0; i < rawList.size(); i++) {
	    Object element = rawList.get(i);
	    AbstractAssignment assignment = new AssignmentToArray(element, i, array, arrayClassInfo);
	    if (!assignment.assign()) {
		delayedAssignments.add(assignment);
	    }
	}
	return array;
    }

    private void readCollection(Collection<Object> collection, ClassInfo collectionClassInfo) {
	ClassInfo[] genericTypes = collectionClassInfo.getGenericTypes();
	if (genericTypes.length != 1) {
	    throw new RuntimeException("Expected 1 element in classInfo, not '" + collectionClassInfo + "'.");
	}
	List<Object> rawList = new ArrayList<Object>();
	readElements(rawList, genericTypes[0], Token.LIST);
	for (Object element : rawList) {
	    AbstractAssignment assignment = new AssignmentToCollection(element, collection);
	    if (!assignment.assign()) {
		delayedAssignments.add(assignment);
	    }
	}
    }

    private void readElements(Collection<Object> rawList, ClassInfo elementClassInfo, Token token) {
	token.readBegin(sb);
	while (!token.readEnd(sb)) {
	    rawList.add(readAnyObject(elementClassInfo));
	}
    }

    private void readMap(Map<Object, Object> map, ClassInfo mapClassInfo) {
	ClassInfo[] entryClassInfo = mapClassInfo.getGenericTypes();
	if (entryClassInfo.length != 2) {
	    throw new RuntimeException("Expected ClassInfo with 2 generic types for Map key and value, not "
		    + entryClassInfo);
	}
	ClassInfo keyClassInfo = entryClassInfo[0];
	ClassInfo valueClassInfo = entryClassInfo[1];

	Token.MAP.readBegin(sb);
	while (!Token.MAP.readEnd(sb)) {
	    Token.KEY.readBegin(sb);
	    Object key = readAnyObject(keyClassInfo);
	    if (!Token.KEY.readEnd(sb)) {
		throw new RuntimeException("Expected end-tag of " + Token.KEY + " here.");
	    }
	    Object value = readAnyObject(valueClassInfo);

	    Assignment assignment = new AssignmentToMap(key, value, map, mapClassInfo);
	    if (!assignment.assign()) {
		delayedAssignments.add(assignment);
	    }
	}
    }

    private void readPropertyOwner(Object propertyOwner, Class<?> serializableClass, ClassInfo classInfo) {
	PropertyAccessorMap propertyAccessorMap = PropertyAccessorMap.getAccessorMap(serializableClass);
	if (propertyAccessorMap == null) {
	    throw new RuntimeException("PropertyOwner '" + classInfo.getClass().getName() + "' has no properties.");
	}
	Token.PROPERTY_OWNER.readBegin(sb);
	while (!Token.PROPERTY_OWNER.readEnd(sb)) {
	    String propertyName = Token.KEY.readValue(sb, true);
	    PropertyAccessor propertyAccessor = propertyAccessorMap.getAccessor(propertyName);
	    if (propertyAccessor == null) {
		throw new RuntimeException("Unknown property '" + propertyName + "' of class "
			+ serializableClass.getName() + ".");
	    }

	    // Take note of the number of delayed assignments.
	    int numDelayedAssignments = delayedAssignments.size();

	    // Read the property value and create an assignment for it.
	    Object value = readAnyObject(propertyAccessor.getClassInfo());
	    AbstractAssignment assignment = new AssignmentToPropertyOwner(value, propertyOwner, propertyAccessor);

	    // If the property has no setter-method, then setting this property implies updating its current value. I.e.
	    // a property of type List will (can!) not be replaced by a new list, so it will be cleared before all
	    // elements of the new list are added to it.
	    // However, if the new list is not yet complete due to delayed assignments, then setting this property must
	    // also be delayed.
	    if (((!propertyAccessor.hasSetter()) && (numDelayedAssignments < delayedAssignments.size()))
		    || (!assignment.assign())) {
		delayedAssignments.add(assignment);
	    }
	}
    }

    private String skipComments() {
	String subsequentComments = "";
	String comment;
	do {
	    comment = Token.COMMENT.readValue(sb, false);
	    if (comment == null) {
		comment = Token.COMMENT_LINE.readValue(sb, false);
	    }
	    if (comment != null) {
		subsequentComments += comment + "\n";
	    }
	} while (comment != null);
	return subsequentComments;
    }

    private void skipWhitespaces() {
	// Skip whitespace.
	int index = 0;
	while ((index < sb.length()) && (Character.isWhitespace(sb.charAt(index)))) {
	    index++;
	}
	sb.delete(0, index);
    }

    private Token peekNextToken() {
	skipWhitespaces();
	return Token.peekNextToken(sb);
    }
}
