package br.com.alura.screammatch.service;

public interface IconverteDados {
    <T> T obterDados(String json, Class<T> classe);

}
