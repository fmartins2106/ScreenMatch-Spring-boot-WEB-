package br.com.alura.screammatch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConverteDados implements IconverteDados {
    private final ObjectMapper mapper = new ObjectMapper();   // Instancia o objeto para converter JSON <-> objetos Java

    @Override
    public <T> T obterDados(String json, Class<T> classe) {  // Método genérico que converte JSON para um objeto da classe T
        try {
            return mapper.readValue(json, classe);  // Usa o ObjectMapper para desserializar o JSON para a classe T
        } catch (JsonProcessingException e) {      // Captura exceção caso o JSON esteja inválido ou não compatível
            throw new RuntimeException(e);         // Rethrow da exceção como RuntimeException para propagar erro
        }
    }
}
