package com.eq.charactertracker.model.imports;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// The exporter emits "spells": {} instead of "spells": [] for a character with no
// scribed spells (empty table serializes as an object, not an array). Treat any
// object here as an empty list instead of failing deserialization.
public class SpellImportListDeserializer extends JsonDeserializer<List<SpellImport>> {

    @Override
    public List<SpellImport> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        List<SpellImport> result = new ArrayList<>();
        if (node.isArray()) {
            for (JsonNode item : node) {
                result.add(p.getCodec().treeToValue(item, SpellImport.class));
            }
        }
        return result;
    }
}
