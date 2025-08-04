package br.com.alura.screammatch.service;

import br.com.alura.screammatch.Model.Categoria;
import br.com.alura.screammatch.Model.Serie;
import br.com.alura.screammatch.dto.EpisodioDTO;
import br.com.alura.screammatch.dto.SerieDTO;
import br.com.alura.screammatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieService {

    @Autowired
    private SerieRepository repositorio;

    public List<SerieDTO> obterTodasAsSeries(){
        return converteDados(repositorio.findAll());
    }

    public List<SerieDTO> obterTop5Series() {
        return converteDados(repositorio.findTop5ByOrderByAvaliacaoDesc());
    }

    public List<SerieDTO> obterLancamentos(){
        return converteDados(repositorio.lancamentosMaisRecentes(PageRequest.of(0,5)));
    }

    private List<SerieDTO> converteDados(List<Serie> series){
        return series
                .stream()
                .map(s -> new SerieDTO(s.getId(),s.getTitulo(),s.getTotalTemporadas(),
                        s.getAvaliacao(),s.getGenero(),s.getAtores(),s.getPoster(),s.getSinopse()))
                .collect(Collectors.toList());
    }

    public SerieDTO obterPorId(Long id) {
        return repositorio.findById(id)
                .map(s -> new SerieDTO(s.getId(), s.getTitulo(),
                        s.getTotalTemporadas(), s.getAvaliacao(),
                        s.getGenero(), s.getAtores(), s.getPoster(), s.getSinopse()))
                .orElse(null); // ou .orElseThrow(() -> new EntityNotFoundException("Série não encontrada"));
    }

    public List<EpisodioDTO> obterTodasTemporadas(Long id) {
        return repositorio.findById(id)
                .map(serie -> serie.getEpisodios().stream()
                        .map(e -> new EpisodioDTO(
                                e.getTitulo(),
                                e.getTemporada(),     // Corrigido: provavelmente é isso
                                e.getNumeroEpisodio() // Corrigido: ou algo equivalente
                        ))
                        .collect(Collectors.toList())
                )
                .orElse(Collections.emptyList());
    }

    public List<EpisodioDTO> obterTemporadasPorNumero(Long id, Long numero) {
        return repositorio.obterEpisodiosPorTemporada(id, numero)
                .stream()
                .map(e -> new EpisodioDTO(e.getTitulo(),e.getTemporada(), e.getNumeroEpisodio()))
                .collect(Collectors.toList());
    }


    public List<SerieDTO> obterSeriesPorCategoria(String nomeGenero) {
        Optional<Categoria> categoria = Categoria.fromPortuges(nomeGenero);
        return converteDados(repositorio.findByGenero(categoria.orElse(null)));
    }

}
