package nl.valori.space.serialize.marshall;

import java.io.IOException;

import nl.valori.space.Space;
import nl.valori.space.SpaceId;
import nl.valori.space.serialize.ClassInfo;
import nl.valori.space.serialize.Converter;
import nl.valori.space.serialize.SerializeContext;
import nl.valori.space.serialize.Token;

public class ProcessorSecondPhase implements Processor {

    private Marshaller marshaller;
    private MarshallWriter writer;
    private Object currentObject;

    public ProcessorSecondPhase(Marshaller marshaller, MarshallWriter writer) {
	this.marshaller = marshaller;
	this.writer = writer;
    }

    public void writeSpaceId(SpaceId spaceId) throws IOException {
	currentObject = Space.getInstance().read(spaceId);
	// Serialize the spaceId...
	process(spaceId, null, false);
	// ...and the content of the spaceObject.
	process(currentObject, null, false);
    }

    public boolean process(Object object, ClassInfo classInfo, boolean isSpaceIdAllowed) throws IOException {
	if (object == null) {
	    return false;
	}
	Class<?> serializableClass = marshaller.getSerializableClass(object);
	SerializeContext serializeContext = marshaller.getSerializeContext();
	Converter converter = serializeContext.getConverter(serializableClass);

	// Substitute the object with its spaceId whenever possible.
	if ((object != currentObject) && (Converter.isReferable(serializableClass))) {
	    // If the object is registered in the Space...
	    SpaceId spaceId = Space.getInstance().getId(object);
	    if (spaceId != null) {
		// ...and if substituting it with its SpaceId is allowed...
		if ((isSpaceIdAllowed) || (marshaller.isRegisteredToBeSerialized(spaceId))) {
		    // ...then serialize the spaceId instead.
		    object = spaceId;
		    serializableClass = SpaceId.class;
		    converter = serializeContext.getConverter(serializableClass);
		}
	    }
	}

	Token token = converter.getToken();
	// If the token is not a CLASS or SPACE_ID...
	if ((token != Token.CLASS) && (token != Token.SPACE_ID)) {
	    // ...then explicitly write the class name.
	    // However, this is not required if serializableClass equals ClassInfo's class.
	    // Note: serializableClass always represents a class that derives from class Object, so it cannot
	    // represent a primitive type. (Primitives don't have a super class, remember?) So if ClassInfo.class is
	    // a primitive type, it will always differ from serializableClass. In that case writing the class name
	    // must be skipped too.
	    if ((classInfo == null)
		    || ((!serializableClass.equals(classInfo.getType()) && (!classInfo.getType().isPrimitive())))) {
		process(serializableClass, classInfo, false);
	    }
	}

	if (object == currentObject) {
	    currentObject = false;
	}

	writer.beginToken(token);
	boolean hasSerializedData = converter.serialize(object, serializableClass, classInfo, this, writer);
	writer.endToken(token);
	return hasSerializedData;
    }
}
