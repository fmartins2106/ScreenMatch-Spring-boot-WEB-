package br.com.alura.screammatch.Model;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "series")
public class Serie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String titulo;
    private Integer totalTemporadas;
    private Double avaliacao;
    @Enumerated(EnumType.STRING)
    private Categoria genero;
    private String atores;
    private String poster;
    private String sinopse;

    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Episodios> episodios = new ArrayList<>();

    public Serie() {
    }

    public Serie(DadosSerie dadosSerie) {
        this.titulo = dadosSerie.titulo();
        String totalTemporadasStr = dadosSerie.totalTemporadas();
        this.totalTemporadas = (totalTemporadasStr != null && !totalTemporadasStr.isBlank())
                ? Integer.valueOf(totalTemporadasStr)
                : 0; // ou lance uma exceção, se preferir
        String avaliacaoStr = dadosSerie.avaliacao();
        this.avaliacao = Optional.ofNullable(avaliacaoStr)
                .filter(s -> !s.isBlank())
                .map(Double::valueOf)
                .orElse(0.0);
        String generoRaw = dadosSerie.genero();
        String generoPrimeiro = (generoRaw != null && !generoRaw.isBlank())
                ? generoRaw.split(",")[0].trim()
                : null;

        this.genero = Categoria.fromString(generoPrimeiro)
                .orElse(Categoria.DEFAUT); // ou null, ou lançar exceção, como preferir
        this.atores = dadosSerie.atores();
        this.poster = dadosSerie.poster();
        this.sinopse = dadosSerie.sinopse();
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getTotalTemporadas() {
        return totalTemporadas;
    }

    public void setTotalTemporadas(Integer totalTemporadas) {
        this.totalTemporadas = totalTemporadas;
    }

    public Double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public Categoria getGenero() {
        return genero;
    }

    public void setGenero(Categoria genero) {
        this.genero = genero;
    }

    public String getAtores() {
        return atores;
    }

    public void setAtores(String atores) {
        this.atores = atores;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Episodios> getEpisodios() {
        return episodios;
    }

    public void setEpisodios(List<Episodios> episodios) {
        if (episodios != null){
            episodios.forEach(e-> e.setSerie(this));
        }
        this.episodios = episodios;
    }

    @Override
    public String toString() {
        return  "titulo='" + titulo + '\'' +
                ", totalTemporadas='" + totalTemporadas + '\'' +
                ", avaliacao=" + avaliacao +
                ", genero=" + genero +
                ", atores='" + atores + '\'' +
                ", poster='" + poster + '\'' +
                ", sinopse='" + sinopse + '\'';
    }
}
