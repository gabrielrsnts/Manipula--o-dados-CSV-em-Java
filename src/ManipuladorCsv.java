import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
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
        // Verificar se o conjunto é muito grande para evitar OutOfMemoryError
        if (base.size() > 20) {
            System.out.println("AVISO: Conjunto muito grande (" + base.size() + " elementos).");
            System.out.println("O conjunto das partes terá 2^" + base.size() + " = " + (long)Math.pow(2, base.size()) + " subconjuntos.");
            System.out.println("Isso pode causar OutOfMemoryError. Limitando a 20 elementos.");
            
            // Limitar a 20 elementos para evitar problemas de memória
            List<String> elementosLimitados = new ArrayList<>(base).subList(0, Math.min(20, base.size()));
            base = new LinkedHashSet<>(elementosLimitados);
        }
        
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

        if (dados.isEmpty()) {
            System.out.println("Arquivo CSV está vazio ou não foi lido corretamente.");
            return;
        }

        System.out.println("Total de linhas lidas: " + dados.size());
         
        Set<String> temperaturaNorte = new LinkedHashSet<>();
        Set<String> temperaturaNordeste = new LinkedHashSet<>();
        Map<String, List<String>> datasNorte = new HashMap<>();
        Map<String, List<String>> datasNordeste = new HashMap<>();
        Map<String, List<String>> datasIguais = new HashMap<>();

        int comecar = 0;
        // Verificação mais segura para o cabeçalho
        if (dados.size() > 0 && dados.get(0).length > 0 && dados.get(0)[0].matches("\\d{4}-\\d{2}-\\d{2}")) {
            comecar = 1;
        } else if (dados.size() > 0 && dados.get(0).length > 0 && dados.get(0)[0].equals("date")) {
            comecar = 1;
        }

        int linhasProcessadas = 0;
        int linhasComErro = 0;

        for(int i = comecar; i < dados.size(); i++){
            String[] linha = dados.get(i);
            if(linha.length < 3){
                linhasComErro++;
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

                linhasProcessadas++;

            }catch(NumberFormatException e){
                linhasComErro++;
                System.out.println("Erro ao converter temperatura na linha " + (i+1) + ": " + e.getMessage());
            }
        }

        System.out.println("Linhas processadas com sucesso: " + linhasProcessadas);
        System.out.println("Linhas com erro: " + linhasComErro);
        System.out.println("Temperaturas únicas do Norte: " + temperaturaNorte.size());
        System.out.println("Temperaturas únicas do Nordeste: " + temperaturaNordeste.size());

        // Conjunto de temperaturas únicas (sem duplicatas)
        Set<String> uniao = new LinkedHashSet<>(temperaturaNorte);
        uniao.addAll(temperaturaNordeste);
        
        // Lista com todas as temperaturas (incluindo repetições)
        List<String> todasTemperaturas = new ArrayList<>();
        todasTemperaturas.addAll(temperaturaNorte);
        todasTemperaturas.addAll(temperaturaNordeste);
        
        Set<String> intersecao = new LinkedHashSet<>(temperaturaNorte);
        intersecao.retainAll(temperaturaNordeste);
        Set<String> diferenca = new LinkedHashSet<>(temperaturaNorte);
        diferenca.removeAll(temperaturaNordeste);

        //União das temperaturas de A e B
        System.out.println("\n=== ANALISE DOS CONJUNTOS ===");
        System.out.println("União das temperaturas (A U B) - únicas: " + uniao.size());
        System.out.println("Total de temperaturas (incluindo repetições): " + todasTemperaturas.size());
        
        // Exibir todas as temperaturas únicas da união
        System.out.println("\nTodas as temperaturas únicas da união (A U B):");
        for(String temperatura : uniao){
           System.out.println(temperatura + "°C");
        }
        
        // Exibir todas as temperaturas com repetições
        System.out.println("\nTodas as temperaturas (incluindo repetições):");
        for(String temperatura : todasTemperaturas){
           System.out.println(temperatura + "°C");
        }
        
        // Exibir todos os dados da união com suas datas
        System.out.println("\nTodos os dados da união (A U B) com datas:");
        for(String temperatura : uniao){
            System.out.println(temperatura + "°C:");
            List<String> datasNorteTemp = datasNorte.getOrDefault(temperatura, List.of());
            List<String> datasNordesteTemp = datasNordeste.getOrDefault(temperatura, List.of());
            
            if(!datasNorteTemp.isEmpty()) {
                System.out.println("  Norte: " + datasNorteTemp);
            }
            if(!datasNordesteTemp.isEmpty()) {
                System.out.println("  Nordeste: " + datasNordesteTemp);
            }
        }
        
        System.out.println("\nInterseção das temperaturas (A ∩ B): " + intersecao.size());
        
        // Exibir detalhes da interseção
        System.out.println("\nDetalhes da interseção (A ∩ B):");
        for(String t : intersecao){
            System.out.println(t + "°C");
            System.out.println("  Norte:    " + datasNorte.getOrDefault(t, List.of()));
            System.out.println("  Nordeste: " + datasNordeste.getOrDefault(t, List.of()));
            List<String> same = datasIguais.getOrDefault(t, List.of());
            if(!same.isEmpty()){
                System.out.println("  Mesma data em ambas: " + same);
            }
        }
        
        System.out.println("\nDiferença das temperaturas (A - B): " + diferenca.size());
        
        // Exibir detalhes da diferença A - B
        System.out.println("\nDetalhes da diferença (A - B):");
        for (String t : diferenca) {
            System.out.println(t + "°C  |  datas Norte: " + datasNorte.getOrDefault(t, List.of()));
        }
        

        // Corrigir a lógica do universo - apenas temperaturas > 33°C
        Set<String> universoMaiorQue33 = new LinkedHashSet<>();
        for (String t : uniao) {
            try {
                if (Double.parseDouble(t) > 33.00) {
                    universoMaiorQue33.add(t);
                }
            } catch (NumberFormatException e) {
                System.out.println("Erro ao processar temperatura: " + t);
            }
        }
        
        // Criar mapa de datas para o universo > 33°C
        Map<String, List<String>> datasUniverso = new HashMap<>();
        
        for(String t : universoMaiorQue33){
            List<String> combinar = new ArrayList<>();
            combinar.addAll(datasNorte.getOrDefault(t, List.of()));
            combinar.addAll(datasNordeste.getOrDefault(t, List.of()));
            datasUniverso.put(t, combinar);
        }
        
        // Complemento de A em relação ao universo > 33°C
        Set<String> complementoA = new LinkedHashSet<>(universoMaiorQue33);
        complementoA.removeAll(temperaturaNorte);

        System.out.println("\n=== ANALISE DO UNIVERSO > 33C ===");
        System.out.println("Temperaturas maiores que 33°C: " + universoMaiorQue33.size());
        System.out.println("Complemento de A (U \\ A) — distintos: " + complementoA.size());
        
        // Exibir detalhes do complemento de A
        System.out.println("\nDetalhes do complemento de A (U \\ A):");
        for(String t : complementoA){
            System.out.println(t + "°C  |  datas (Norte+Nordeste): " + datasUniverso.getOrDefault(t, List.of()));
        } 


        List<Set<String>> partes = conjuntoDasPartes(intersecao);
        System.out.println("\n=== CONJUNTO DAS PARTES ===");
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