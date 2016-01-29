package nl.valori.space.serialize.converters;

import java.io.IOException;

import nl.valori.space.AbstractSpaceCollection;
import nl.valori.space.serialize.ClassInfo;
import nl.valori.space.serialize.marshall.MarshallWriter;
import nl.valori.space.serialize.marshall.Processor;

public class SpaceListConverter extends ListConverter {

    @Override
    public Class<?>[] getSupportedClasses() {
	return new Class<?>[] { AbstractSpaceCollection.class };
    }

    @Override
    public boolean serialize(Object object, Class<?> serializableClass, ClassInfo classInfo, Processor processor,
	    MarshallWriter writer) throws IOException {
	AbstractSpaceCollection<?, ?> abstractSpaceCollection = (AbstractSpaceCollection<?, ?>) object;
	abstractSpaceCollection.fetchLazyData();
	return super.serialize(abstractSpaceCollection.getIds(), serializableClass, classInfo, processor, writer);
    }
}
