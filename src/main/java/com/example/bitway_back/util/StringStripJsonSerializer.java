package com.example.bitway_back.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class StringStripJsonSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            // Strip 실행 후 빈 문자열 여부 확인
            String valueStripped = value.strip();
            gen.writeString(!valueStripped.isEmpty() ? valueStripped : null);
        }
    }
}
