package com.example.bitway_back.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class StringStripJsonDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        String value = p.getValueAsString();

        if (value == null) {
            return null;
        }

        String valueStripped = value.strip();

        return !valueStripped.isEmpty() ? valueStripped : null;
    }
}

