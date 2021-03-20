package com.util.comutil.config.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.bson.types.ObjectId;

import java.io.IOException;

public class ObjectIdDeserialize extends JsonDeserializer<ObjectId> {

    @Override
    public ObjectId deserialize(JsonParser p, DeserializationContext ctx)
            throws IOException, JsonProcessingException {
        String data = p.readValueAs(String.class);
        if (data == null || data.length() == 0) {
            return null;
        }
        // return new ObjectId(Base64.decodeBase64(data));
        return new ObjectId(data);
    }
}
