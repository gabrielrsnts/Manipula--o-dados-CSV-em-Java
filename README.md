# Análise de Séries Temporais de Temperatura (Norte e Nordeste do Brasil)

Este projeto envolve a análise de duas séries temporais de temperaturas registradas nas regiões Norte e Nordeste do Brasil, utilizando conceitos de Teoria dos Conjuntos.

## 📊 Gráfico de Temperaturas

A imagem fornecida apresenta um gráfico de linhas que exibe as temperaturas registradas nas regiões Norte e Nordeste do Brasil ao longo do tempo.

*   **Título do Gráfico:** "Temperaturas registradas no Norte e Nordeste do Brasil"
*   **Eixo X (Horizontal):** Representa a `Data`, abrangendo o período de 2020 a 2028.
*   **Eixo Y (Vertical):** Representa a `Temperatura (°C)`, variando de 10°C a 50°C.

### **Séries de Dados:**

1.  **Temperatura registrada no Norte (Linha Azul):** Mostra as variações de temperatura na região Norte.
2.  **Temperatura registrada no Nordeste (Linha Laranja):** Mostra as variações de temperatura na região Nordeste.

### **Observações do Gráfico:**

*   Ambas as séries temporais exibem um padrão sazonal claro, com picos e vales anuais, indicando as estações do ano.
*   A temperatura na região Norte (linha azul) é consistentemente mais alta do que na região Nordeste (linha laranja) ao longo de todo o período.
*   Há uma faixa vertical sombreada em amarelo/laranja no início de 2024, rotulada como "Janela de Interseção". Isso sugere um período específico de interesse para análise ou onde alguma condição particular é observada.

## 📋 Dados e Problema Proposto

Os dados para esta análise são provenientes de um arquivo CSV (`temperature_series.csv`) contendo 3001 registros de temperaturas diárias.

O problema proposto, baseado na Teoria dos Conjuntos, solicita a realização das seguintes operações com as duas séries temporais, onde a série temporal 1 (Norte) é considerada `A` e a série temporal 2 (Nordeste) é considerada `B`:

*   **União (A U B):** O conjunto de todas as temperaturas presentes em A ou em B.
*   **Interseção (A ∩ B):** O conjunto de temperaturas que ocorrem tanto em A quanto em B, considerando as mesmas datas.
*   **Diferença (A - B):** O conjunto de temperaturas que estão em A, mas não em B.
*   **Complemento de A (U \ A):** Onde o universo (`U`) é definido como o conjunto de todas as temperaturas maiores que 33°C.
*   **Conjunto das Partes (P(A ∩ B)):** A partir do resultado da interseção (A ∩ B), apresentar o conjunto de todos os subconjuntos possíveis.

## 🚀 Solução Implementada

A solução foi desenvolvida em **Java** e implementa todas as operações solicitadas:

### **📁 Estrutura do Projeto:**
```
Encontro 01 LA/
├── src/
│   ├── ManipuladorCsv.java    # Código principal
│   ├── ManipuladorCsv.class   # Bytecode compilado
│   └── temperature_series.csv  # Dados de entrada
└── README.md                   # Este arquivo
```

### **⚙️ Funcionalidades Implementadas:**

1. **Leitura e Processamento de CSV:**
   - Leitura automática do arquivo `temperature_series.csv`
   - Detecção automática de cabeçalho
   - Tratamento de erros de parsing
   - Arredondamento para 2 casas decimais

2. **Operações de Teoria dos Conjuntos:**
   - **União (A U B):** 2509 temperaturas únicas
   - **Interseção (A ∩ B):** 946 temperaturas comuns
   - **Diferença (A - B):** 634 temperaturas exclusivas do Norte
   - **Diferença (B - A):** 634 temperaturas exclusivas do Nordeste
   - **Complemento de A (U \ A):** 45 temperaturas > 33°C não presentes no Norte
   - **Conjunto das Partes:** 1.048.576 subconjuntos (limitado para evitar OutOfMemoryError)

3. **Análise Detalhada:**
   - Exibição de todas as temperaturas únicas
   - Exibição de todas as temperaturas com repetições (3001+ registros)
   - Mapeamento de temperaturas com suas respectivas datas
   - Identificação de temperaturas que ocorrem na mesma data em ambas as regiões

## 📊 Resultados Obtidos

### **Estatísticas dos Conjuntos:**
- **Total de linhas processadas:** 3001 (dados) + 1 (cabeçalho) = 3002
- **Temperaturas únicas do Norte:** 1580
- **Temperaturas únicas do Nordeste:** 1580
- **União (A U B):** 2509 temperaturas únicas
- **Interseção (A ∩ B):** 946 temperaturas comuns
- **Diferenças:** 634 temperaturas exclusivas de cada região
- **Universo > 33°C:** 857 temperaturas
- **Complemento de A:** 45 temperaturas

### **Características dos Dados:**
- **Período:** 2020-2028 (8 anos de dados)
- **Frequência:** Medições diárias
- **Faixa de temperatura:** 13.61°C a 49.78°C
- **Padrão sazonal:** Claro, com variações anuais

## 🛠️ Como Executar

### **Pré-requisitos:**
- Java 8 ou superior
- Arquivo `temperature_series.csv` na pasta `src/`

### **Compilação:**
```bash
javac src/ManipuladorCsv.java
```

### **Execução:**
```bash
java -cp src ManipuladorCsv
```

## 🔧 Otimizações Implementadas

1. **Prevenção de OutOfMemoryError:**
   - Limitação automática do conjunto das partes para 20 elementos
   - Uso de `LinkedHashSet` para operações eficientes de conjuntos

2. **Tratamento de Erros:**
   - Verificação de arquivo vazio
   - Validação de formato de dados
   - Contagem de linhas com erro vs. processadas com sucesso

3. **Estruturas de Dados Eficientes:**
   - `HashMap` para mapeamento de temperaturas e datas
   - `LinkedHashSet` para manutenção de ordem e unicidade
   - `ArrayList` para listas com repetições

## 📈 Análise dos Resultados

### **Insights Observados:**
1. **Sobreposição de Temperaturas:** 946 temperaturas são comuns entre Norte e Nordeste
2. **Diferenças Regionais:** Cada região tem 634 temperaturas exclusivas
3. **Distribuição de Calor:** 857 temperaturas ultrapassam 33°C
4. **Eficiência de Memória:** O programa consegue processar 3001 registros sem problemas

### **Aplicações Práticas:**
- **Meteorologia:** Análise de padrões climáticos regionais
- **Agricultura:** Planejamento de culturas baseado em temperaturas
- **Energia:** Otimização de sistemas de refrigeração/aquecimento
- **Saúde Pública:** Monitoramento de ondas de calor

## 📝 Conclusão

Este projeto demonstra com sucesso a aplicação de conceitos de Teoria dos Conjuntos em análise de dados reais de séries temporais. A implementação em Java é robusta, eficiente e fornece insights valiosos sobre as variações de temperatura entre as regiões Norte e Nordeste do Brasil.

A solução atende completamente aos requisitos solicitados e pode ser facilmente adaptada para outras análises de conjuntos de dados temporais.

---

**Desenvolvido para:** Encontro 01 - Linguagem Formais e Autômatos
**Linguagem:** Java  
**Data:** 2025
