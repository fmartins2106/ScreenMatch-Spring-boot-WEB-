package br.com.alura.screammatch.Model;

public class Conversores {

    private Conversores() {

    }

    public static int parseTotalSeasons(String totalSeasons) {
        if (totalSeasons == null || totalSeasons.equalsIgnoreCase("N/A")) {
            return 0; // Ou outro valor padrão que faça sentido para o seu código
        }
        try {
            return Integer.parseInt(totalSeasons);
        } catch (NumberFormatException e) {
            return 0; // Tratamento para qualquer outro valor inválido
        }
    }
}
