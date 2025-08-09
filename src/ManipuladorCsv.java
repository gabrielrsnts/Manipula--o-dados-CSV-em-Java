import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ManipuladorCsv{

    public static List<String[]> lerCSV(String caminhoDoArquivo) {
        List<String[]> dados = new ArrayList<>();
        try {
            List<String> linhas = Files.readAllLines(Paths.get(caminhoDoArquivo));
            for (String linha : linhas) {
                String[] valores = linha.split(",");
                dados.add(valores);
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        }
        return dados;
    }

    private static String casaDecimal(double valor){
        return new BigDecimal(valor).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private static List<Set<String>> conjuntoDasPartes(Set<String> base){
        List<Set<String>> partes = new ArrayList<>();
        partes.add(new LinkedHashSet<>());
        for(String elemento : base){
            int size = partes.size();
            for(int i = 0; i < size; i++){
                Set<String> novo = new LinkedHashSet<>(partes.get(i));
                novo.add(elemento);
                partes.add(novo);
            }
        }
        return partes;
    }


    public static void main(String[] args) {
        String caminho = "src/temperature_series.csv";
        List<String[]> dados = lerCSV(caminho);

         /*Imprimir os dados do CSV
        for(String[] linha : dados){
            for(String valor : linha){
                System.out.print(valor + " ");
            }
            System.out.println( );
        }*/
         

        Set<String> temperaturaNorte = new LinkedHashSet<>();
        Set<String> temperaturaNordeste = new LinkedHashSet<>();
        Map<String, List<String>> datasNorte = new HashMap<>();
        Map<String, List<String>> datasNordeste = new HashMap<>();
        Map<String, List<String>> datasIguais = new HashMap<>();

        int comecar = 0;
        if(!dados.isEmpty() && dados.get(0)[0].matches("\\d{4}-\\d{2}-\\d{2}")){
            comecar = 1;
        }


        for(int i = comecar; i < dados.size(); i++){
            String[] linha = dados.get(i);
            if(linha.length < 3){
                continue;
            }

            String data = linha[0].trim();
            String colunaNorte = linha[1].trim().replace(',', '.');
            String colunaNordeste = linha[2].trim().replace(',', '.');

            try{
                double tempNorte = Double.parseDouble(colunaNorte);
                double tempNordeste = Double.parseDouble(colunaNordeste);
                String tempNorteStr = casaDecimal(tempNorte);
                String tempNordesteStr = casaDecimal(tempNordeste);

                temperaturaNorte.add(tempNorteStr);
                temperaturaNordeste.add(tempNordesteStr);

                datasNorte.computeIfAbsent(tempNorteStr, k -> new ArrayList<>()).add(data);
                datasNordeste.computeIfAbsent(tempNordesteStr, k -> new ArrayList<>()).add(data);

                if (tempNorteStr.equals(tempNordesteStr)) {
                    datasIguais.computeIfAbsent(tempNorteStr, k -> new ArrayList<>()).add(data);
                }

            }catch(NumberFormatException e){
                System.out.println("Erro ao converter temperatura: " + e.getMessage());
            }


        }

        Set<String> uniao = new LinkedHashSet<>(temperaturaNorte);
        uniao.addAll(temperaturaNordeste);
        Set<String> intersecao = new LinkedHashSet<>(temperaturaNorte);
        intersecao.retainAll(temperaturaNordeste);
        Set<String> diferenca = new LinkedHashSet<>(temperaturaNorte);
        diferenca.removeAll(temperaturaNordeste);

        System.out.println("União das temperaturas (A U B): " + uniao.size());
        
        //Para printar os dados da união da temperatuda do Norte e do Nordeste, basta remover o comentario da linha abaixo
        /*
        for(Double temperatura : uniao){
           System.out.println(temperatura);
        }*/

        Set<String> universoMaiorQue33 = new LinkedHashSet<>(temperaturaNorte);
        for(String t : uniao){
            if(Double.parseDouble(t) > 33){
                universoMaiorQue33.add(t);
            }
        }

        
        Set<String> complementoA = new LinkedHashSet<>(universoMaiorQue33);
        complementoA.removeAll(temperaturaNorte);

        System.out.println("Complemento de A: " + complementoA.size());

        Map<String, List<String>> datasUniverso = new HashMap<>();
        for(String t : universoMaiorQue33){
            List<String> combinar = new ArrayList<>();
            combinar.addAll(datasNorte.getOrDefault(t, List.of()));
            combinar.addAll(datasNordeste.getOrDefault(t, List.of()));
            datasUniverso.put(t, combinar);
        } 


        System.out.println("Interseção das temperaturas (A ∩ B): " + intersecao.size());
        for(String t : intersecao){
            System.out.println(t + "°C");
            System.out.println("  Norte:    " + datasNorte.getOrDefault(t, List.of()));
            System.out.println("  Nordeste: " + datasNordeste.getOrDefault(t, List.of()));
            List<String> same = datasIguais.getOrDefault(t, List.of());
            if(!same.isEmpty()){
                System.out.println("  Mesma data em ambas: " + same);
            }
        }

        System.out.println("Diferença das temperaturas (A - B): " + diferenca.size());
        for (String t : diferenca) {
            System.out.println(t + "°C  |  datas Norte: " + datasNorte.getOrDefault(t, List.of()));
        }

        System.out.println("Temperaturas maiores que 33°C: " + universoMaiorQue33.size());
        System.out.println("Complemento de A (U \\ A) — distintos: " + complementoA.size());
        for(String t : complementoA){
            System.out.println(t + "°C  |  datas (Norte+Nordeste): " + datasUniverso.getOrDefault(t, List.of()));
        }

        List<Set<String>> partes = conjuntoDasPartes(intersecao);
        System.out.println("P(A ∩ B) — número de subconjuntos: " + partes.size());

        int limite = Math.min(100, partes.size());
        System.out.println("Primeiros " + limite + " subconjuntos de P(A ∩ B):");
        for (int i = 0; i < limite; i++) {
            System.out.println(partes.get(i));
        }
        if (partes.size() > limite) {
            System.out.println("... (" + (partes.size() - limite) + " subconjuntos não exibidos)");
        }

    }
}