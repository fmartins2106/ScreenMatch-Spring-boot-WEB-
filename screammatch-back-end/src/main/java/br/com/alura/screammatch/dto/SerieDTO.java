package br.com.alura.screammatch.dto;

import br.com.alura.screammatch.Model.Categoria;
import jakarta.persistence.*;

public record SerieDTO(Long id,
                       String titulo,
                       Integer totalTemporadas,
                       Double avaliacao,
                       Categoria genero,
                       String atores,
                       String poster,
                       String sinopse){

        }
