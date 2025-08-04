package br.com.alura.screammatch.principal;

import br.com.alura.screammatch.Model.*;
import br.com.alura.screammatch.repository.SerieRepository;
import br.com.alura.screammatch.service.ConsumoAPI;
import br.com.alura.screammatch.service.ConverteDados;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private static final Scanner scanner = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConverteDados converteDados = new ConverteDados();
    private final String ENDERECO = "http://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=257b8762";
    List<DadosSerie> dadosSeries = new ArrayList<>();
    List<Serie> seriesPesquisadas = new ArrayList<>();

    private SerieRepository repository;
    private List<Serie> series = new ArrayList<>();

    private Optional<Serie> serieBusca;


    public Principal(SerieRepository repository) {
        this.repository = repository;
    }

    public void exibeMenu() {
        int opcao = -1;
        while (opcao !=0){
            var menu = """
                1 - Buscar séries
                2 - Buscar episódios
                3 - Listar séries pesquisadas
                4 - Buscar série por titulo
                5 - Buscar séries por ator.
                6 - Top 5 séries mais bem avaliadas.
                7 - Buscar séries por categoria.
                8 - Pesquisa por temporadas e avaliação.
                9 - Pesquisa por nome do eposodio.
                10- Top 5 episodios.
                11- Busca episodios a partir de uma data.
                0 - Sair """;

            System.out.println(menu);
            try {
                opcao = Integer.parseInt(scanner.nextLine().trim());
            }catch (NumberFormatException e){
                System.out.println("Erro. Digite uma opção válida.");
            }
            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    buscarSeriesListadas();
                    break;
                case 4:
                    buscarSerieTitulo();
                    break;
                case 5:
                    buscarSeriesPorAtor();
                    break;
                case 6:
                    buscarTopSeries();
                    break;
                case 7:
                    buscarSeriesPorCategoria();
                    break;
                case 8:
                    filtrarSeriesTemporadas();
                    break;
                case 9:
                    pesquisaNomeEpisodio();
                    break;
                case 10:
                    topEpisodiosPorSerie();
                    break;
                case 11:
                    buscarEpisodioPorData();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }
    
    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        repository.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = scanner.nextLine();
        var json = consumoAPI.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = converteDados.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        buscarSeriesListadas();
        System.out.println("Escolha uma série pelo nome:");
        String nomeSerie = scanner.nextLine().trim();
        Optional<Serie> serieBuscada = repository.findByTituloContainingIgnoreCase(nomeSerie);
        if ((serieBuscada.isPresent())){
            var serieEncontrada = serieBuscada.get();
            List<DadosTemporada> temporadas = new ArrayList<>();
            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoAPI.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = converteDados.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodios> episodios = temporadas.stream()
                    .flatMap(d -> d.episodeos().stream()
                            .map(e -> new Episodios(d.numero(),e)))
                    .collect(Collectors.toList());
            serieEncontrada.setEpisodios(episodios);
            repository.save(serieEncontrada);
        }else {
            System.out.println("Série não encontrada.");
        }
    }

    public void buscarSeriesListadas(){
        series = repository.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                        .forEach(System.out::println);
    }

    public void buscarSerieTitulo(){
        System.out.println("Ecolha uma série pelo titulo:");
        String nomeSerie = scanner.nextLine().trim();
        serieBusca = repository.findByTituloContainingIgnoreCase(nomeSerie);
        if (serieBusca.isPresent()){
            System.out.println("Dados série:"+serieBusca.get());
            return;
        }
        System.out.println("Série não encontrada.");
    }

    private void buscarSeriesPorAtor(){
        System.out.println("Digite o nome do ator:");
        String nomeAtor = scanner.nextLine().trim();
        System.out.println("Avaliação a partir de qual valor?:");
        var avalicao = Double.parseDouble(scanner.nextLine().trim());
        List<Serie> seriesEncontradas = repository.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor,avalicao);
        System.out.println("Séries em que  "+nomeAtor+" atuou:");
        seriesEncontradas.forEach(s ->
                System.out.println(s.getTitulo() +" avaliação: "+s.getAvaliacao()));
    }

    private void buscarTopSeries(){
        List<Serie> serieTop = repository.findTop5ByOrderByAvaliacaoDesc();
        serieTop.forEach(s -> System.out.println(s.getTitulo()+" |Avaliação:"+s.getAvaliacao()));
    }

    private void buscarSeriesPorCategoria(){
        System.out.println("Deseja buscar uma série de que categoria/Gênero?:");
        String genero = scanner.nextLine().trim();
        Optional<Categoria> optionalCategoria = Categoria.fromPortuges(genero);
        if (optionalCategoria.isEmpty()) {
            System.out.println("Categoria inválida: \"" + genero + "\". Tente novamente.");
            return; // Ou faça um loop para repetir a pergunta
        }

        Categoria categoria = optionalCategoria.get();
        List<Serie> seriesPorCategoria = repository.findByGenero(categoria);

        if (seriesPorCategoria.isEmpty()) {
            System.out.println("Nenhuma série encontrada para a categoria: " + genero);
        } else {
            System.out.println("Séries da categoria: " + genero);
            seriesPorCategoria.forEach(System.out::println);
        }
    }

    private void filtrarSeriesTemporadas(){
        try {
            System.out.println("Filtrar séries até quantas temparadas?:");
            int serieTemp = Integer.parseInt(scanner.nextLine().trim());
            if (serieTemp < 0 || serieTemp > 100){
                System.out.println("Digite um número válido.");
                return;
            }
            System.out.println("Com avaliação a partir de quanto ?:");
            double avaliacao = Double.parseDouble(scanner.nextLine().trim());
            if (avaliacao < 0 || avaliacao > 10){
                System.out.println("Erro, digite um valor válido para avaliação.");
                return;
            }
            List<Serie> seriesEncontradas = repository.seriesPorTemporadaEAvaliacao(serieTemp,avaliacao);
            System.out.println("Séries encontradas:");
            seriesEncontradas.forEach(s -> System.out.println("Nome:"+s.getTitulo()+" |Temporadas:"+s.getTotalTemporadas()+" |Avaliação:"+s.getAvaliacao()));
        }catch (NumberFormatException e){
            System.out.println("Erro, digite um número válido.");
        }
    }

    public void pesquisaNomeEpisodio(){
        System.out.println("Qual o nome do episódio para buscar: ?");
        String trechoEpisodio = scanner.nextLine().trim();
        List<Episodios> episodioEncontrado = repository.episodioPorTrecho(trechoEpisodio);
        episodioEncontrado.forEach(e -> System.out.printf("Série: %s |Temporada: %s |Episodio %s - %s\n",
                e.getSerie().getTitulo(),e.getTemporada(),e.getNumeroEpisodio(),e.getTitulo()));
    }

//    public void topEpisodiosPorSerie(){
//        buscarSerieTitulo();
//        if (serieBusca.isPresent()){
//            Serie serie = serieBusca.get();
//            List<Episodios> topEpisodios = repository.topEpisodiosPorSerie(serie, PageRequest.of(0,5));
//            topEpisodios.forEach(e -> System.out.printf("Série: %s |Temporada: %s |Episodio %s - %s |Avaliação:%.2f\n",
//                    e.getSerie().getTitulo(),e.getTemporada(),e.getNumeroEpisodio(),e.getTitulo(),e.getAvaliacao()));
//        }
//    }

    public void topEpisodiosPorSerie() {
        buscarSerieTitulo();
        if (serieBusca.isPresent()) {
            Serie serie = serieBusca.get();
            List<Episodios> topEpisodios = repository.topEpisodiosPorSerie(serie, PageRequest.of(0,5));
            Set<Long> idsImpressos = new HashSet<>();
            topEpisodios.forEach(e -> {
                if (idsImpressos.add(e.getId())) {
                    System.out.printf("Série: %s | Temporada: %s | Episodio %s - %s | Avaliação: %.2f\n",
                            e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo(), e.getAvaliacao());
                }
            });
        } else {
            System.out.println("Série não encontrada");
        }
    }

    private void buscarEpisodioPorData(){
        buscarSerieTitulo();
        if (serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            System.out.println("Digite o ano limite de lançamento:");
            int anoLancamento = Integer.parseInt(scanner.nextLine().trim());
            List<Episodios> episodiosAno = repository.episodiosPorSerieEAno(serie,anoLancamento);
            episodiosAno.forEach(System.out::println);
        }
    }


//    public void getDadosSerie(){
//        System.out.println("Digite o nome da série:");
//        var nomeSerie = scanner.nextLine();
//        var json = consumoAPI.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
//
//        if (json.contains("\"Response\":\"False\"")) {
//            System.out.println("Série não encontrada.");
//            return;
//        }
//
//        DadosSerie dados = converteDados.obterDados(json, DadosSerie.class);
//        int total = Conversores.parseTotalSeasons(dados.totalTemporadas());
//
//        List<DadosTemporada> temporadas = new ArrayList<>();
//
//        System.out.println("Título: " + dados.titulo());
//        System.out.println("Ano: " + dados.ano());
//        System.out.println("Total de Temporadas: " + total);
//
//        for (int i = 1; i <= total; i++) {
//            json = consumoAPI.obterDados(ENDERECO + nomeSerie.replace(" ","+")+"&Season="+ i +API_KEY);
//            DadosTemporada dadosTemporada = converteDados.obterDados(json, DadosTemporada.class);
//            temporadas.add(dadosTemporada);
//        }
/// /        temporadas.forEach(System.out::println);
/// /
/// /        temporadas.forEach(t -> t.episodeos().forEach(e -> System.out.println(e.Titulo())));
//
//        List<DadosEpisodeos> dadosEpisodeos = temporadas.stream()
//                .flatMap(temporada -> temporada.episodeos().stream())
//                        .collect(Collectors.toList());
//
//        dadosEpisodeos.stream().sorted(Comparator.comparing(DadosEpisodeos::avaliacao).reversed())
//                .filter(dadosEpisodeos1 -> !"N/A".equals(dadosEpisodeos1.avaliacao()))
//                .peek(e -> System.out.println("Primeiro filtro (N/A) " + e))
//                .sorted(Comparator.comparing(DadosEpisodeos::avaliacao).reversed())
//                .peek( e-> System.out.println("Ordenação "+e))
//                .limit(10)
//                .map(e -> e.Titulo().toUpperCase())
//                .peek(e -> System.out.println("Mapeamento "+e))
//                .forEach(System.out::println);
//
//        List<Episodios> episodios = temporadas.stream()
//                .flatMap(t -> t.episodeos().stream()
//                        .map(d -> new Episodios(t.numero(),d)))
//                .collect(Collectors.toList());
//
//        episodios.forEach(System.out::println);
//
//        System.out.println("A partir de qual ano você deseja ver o episódio?");
//        var ano = scanner.nextInt();
//        scanner.nextLine();
//
//        LocalDate localDate = LocalDate.of(ano,1,1);
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//        episodios.stream()
//                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(localDate))
//                .forEach(episodios1 -> System.out.println(
//                        "Temporada: "+ episodios1.getTemporada()+
//                                "| Episodio "+episodios1.getTitulo()+
//                                "| Data Lançamento: "+episodios1.getDataLancamento().format(dateTimeFormatter)
//                ));
//    }
}
