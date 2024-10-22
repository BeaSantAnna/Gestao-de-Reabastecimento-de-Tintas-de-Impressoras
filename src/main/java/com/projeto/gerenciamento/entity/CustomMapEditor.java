package com.projeto.gerenciamento.entity;

import java.beans.PropertyEditorSupport;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomMapEditor extends PropertyEditorSupport {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
    	System.out.println("Valor recebido para conversão: " + text);
        try {
            if (text == null || text.trim().isEmpty()) {
                setValue(null);
            } else {
                Map<String, Integer> map = objectMapper.readValue(text, new TypeReference<Map<String, Integer>>() {});
                setValue(map);
            }
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("JSON inválido: " + text, e);
        }
    }

    @Override
    public String getAsText() {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Integer> map = (Map<String, Integer>) getValue();
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
