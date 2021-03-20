package com.util.comutil.config.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.util.comutil.config.Constant;

import java.io.IOException;
import java.time.LocalDateTime;

public class DateTimeDeserialize extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        String data = p.readValueAs(String.class);
        if (data == null || data.length() == 0) {
            return null;
        }
        return LocalDateTime.parse(data, Constant.DATETIME_FORMATTER);
    }
}
