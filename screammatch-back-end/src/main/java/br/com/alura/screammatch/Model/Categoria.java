package br.com.alura.screammatch.Model;

import java.util.Arrays;
import java.util.Optional;

public enum Categoria {
    ACAO("Action","Ação"),
    COMEDIA("Comedy","Comédia"),
    DRAMA("Drama","Drama"),
    POLICIAL("Crime","Crime"),
    TERROR("Horror","Terror"),
    SUSPENSE("Thriller","Suspense"),
    DEFAUT("","");

    private final String categoriaOmdb;

    private final String categoriaPortugues;

    Categoria(String categoriaOmdb, String categoriaPortugues) {
        this.categoriaOmdb = categoriaOmdb;
        this.categoriaPortugues = categoriaPortugues;
    }

    public String getCategoriaOmdb() {
        return categoriaOmdb;
    }

    public String getCategoriaPortugues() {
        return categoriaPortugues;
    }

    //    public static Categoria fromString(String text) {
//        for (Categoria categoria : Categoria.values()) {
//            if (categoria.getCategoriaOmdb().equalsIgnoreCase(text)) {
//                return categoria;
//            }
//        }
//        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
//    }
    public static Optional<Categoria> fromString(String text) {
        if (text == null || text.isBlank()) {
            return Optional.empty();
        }
        return Arrays.stream(Categoria.values())
                .filter(c -> c.getCategoriaOmdb().equalsIgnoreCase(text.trim()))
                .findFirst();
    }

    public static Optional<Categoria> fromPortuges(String text) {
        if (text == null || text.isBlank()) {
            return Optional.empty();
        }
        return Arrays.stream(Categoria.values())
                .filter(c -> c.getCategoriaPortugues().equalsIgnoreCase(text.trim()))
                .findFirst();
    }

}
