# Gerador de CPF/CNPJ para IntelliJ IDEA

Este plugin possibilita a inserção de CPF's e/ou CNPJ's gerados aleatoriamente, com ou sem máscara, diretamente no seu código.

Também disponibiliza geração e validação de CPF/CNPJ para clientes de IA pelo servidor MCP nativo da JetBrains.

## Requisitos

IntelliJ Platform **2025.2 ou superior**, com o plugin **MCP Server** (`com.intellij.mcpServer`) habilitado. Para utilizar as ferramentas MCP, habilite também o servidor nas configurações da IDE.

## Utilização pelo MCP

1. Abra **Settings > Tools > MCP Server** e habilite **Enable MCP Server**.
2. Configure seu cliente de IA pela configuração automática da IDE ou copie a configuração de conexão disponibilizada nessa tela para o cliente.
3. Reconecte o cliente para descobrir as ferramentas abaixo. Não é necessário iniciar outro servidor ou configurar uma porta própria deste plugin.

| Ferramenta | ID MCP | Parâmetros | Retorno |
|---|---|---|---|
| Gerar CPF | `gerar_cpf` | `formatado` opcional | CPF como texto |
| Gerar CNPJ | `gerar_cnpj` | `formatado` e `alfanumerico` opcionais | CNPJ como texto |
| Validar CPF | `validar_cpf` | `cpf`: texto obrigatório | JSON com `valido` e `motivo` |
| Validar CNPJ | `validar_cnpj` | `cnpj`: texto obrigatório | JSON com `valido` e `motivo` |

As chamadas MCP apenas retornam os resultados: não inserem no editor, não alteram arquivos e não modificam as configurações do plugin.

### Preferências e sobrescritas

Omitir um parâmetro de geração (ou enviar `null`) utiliza a preferência atual do plugin:

- `formatado`: segue a opção **Somente números**. Por padrão, os documentos têm máscara.
- `alfanumerico`: segue a opção **CNPJ alfanumérico**. Por padrão, o CNPJ é numérico.

Enviar `true` ou `false` sobrescreve a preferência **somente naquela chamada**. Os parâmetros são independentes: é possível pedir um CNPJ alfanumérico sem máscara ou numérico com máscara, qualquer que seja a configuração atual.

Por compatibilidade, a chave persistida `formatting_enabled` mantém sua semântica histórica invertida: `true` significa **Somente números**, isto é, **sem máscara**. Já o parâmetro MCP `formatado=true` significa **com máscara**, inclusive quando essa chave estiver `true`. A chave `alphanumeric_cnpj=true` permite letras na geração de CNPJ. A opção sem máscara remove a pontuação, não as letras de um CNPJ alfanumérico.

O cliente de IA interpreta o pedido em linguagem natural e escolhe os parâmetros estruturados. Exemplos:

| Pedido | Ferramenta e argumentos |
|---|---|
| “Gere um CPF” | `gerar_cpf({})` |
| “Gere um CPF formatado” ou “com formatação” | `gerar_cpf({"formatado": true})` |
| “Gere um CPF sem máscara” | `gerar_cpf({"formatado": false})` |
| “Gere um CNPJ alfanumérico com formatação” | `gerar_cnpj({"alfanumerico": true, "formatado": true})` |
| “Gere um CNPJ numérico sem formatação” | `gerar_cnpj({"alfanumerico": false, "formatado": false})` |
| “Valide o CPF 000.000.002-72” | `validar_cpf({"cpf": "000.000.002-72"})` |
| “Valide o CNPJ 12.ABC.345/01DE-35” | `validar_cnpj({"cnpj": "12.ABC.345/01DE-35"})` |

A geração alfanumérica permite números e letras maiúsculas na base; os dois dígitos verificadores continuam numéricos. Assim como na geração pelos menus, uma amostra aleatória pode conter apenas números. A geração descarta documentos inteiramente repetidos, para não retornar valores rejeitados pela validação.

### Regras de validação

A validação aceita o documento compacto ou com máscara canônica (`000.000.002-72`, `04.252.011/0001-10` ou `12.ABC.345/01DE-35`). CNPJ numérico e alfanumérico são aceitos independentemente das preferências; letras minúsculas são normalizadas para maiúsculas. Máscaras incorretas, espaços extras e caracteres não permitidos são rejeitados, não removidos silenciosamente.

São conferidos os dois dígitos verificadores pelo módulo 11. Documentos inteiramente compostos pelo mesmo caractere, como `00000000000` ou `00000000000000`, também são rejeitados. **Não há consulta cadastral**, verificação de existência, titularidade ou situação na Receita Federal.

Exemplo de resultado:

```json
{"valido": true, "motivo": "CPF com dígitos verificadores válidos; cadastro não consultado."}
```

Uma entrada documental inválida retorna `valido=false` com o motivo. Erros de protocolo, como ausência do argumento obrigatório ou tipo incorreto, seguem o mecanismo de erro do servidor MCP. No IntelliJ 2025.2, o host entrega o objeto JSON no conteúdo textual MCP; basta interpretá-lo uma vez, sem dupla codificação.

## Utilização

### 1. Através do menu **_Generate_...**

1. Selecione o menu **Code**;
2. Selecione a opção **Generate...**;
3. Escolha a opção desejada (**Gerar CPF** ou **Gerar CNPJ**);
4. O CPF ou CNPJ será inserido na posição do cursor ou da seleção.
   1. Se houver múltiplos cursores, será gerado um CPF ou CNPJ diferente para cada cursor.

Alternativamente, você pode acessar o mesmo menu através do atalho **Alt**+**Insert**.

### 2. Através do menu de contexto

Enquanto estiver editando o código:

1. Posicione o cursor no local desejado (ou selecione um trecho a ser substituído);
2. Clique com o botão direito;
3. Selecione a opção **Gerar CPF/CNPJ**;
4. Escolha a opção desejada (**Gerar CPF** ou **Gerar CNPJ**);
5. O CPF ou CNPJ será inserido na posição do cursor ou da seleção.
   1. Se houver múltiplos cursores, será gerado um CPF ou CNPJ diferente para cada cursor.

## Habilitar/desabilitar máscara

Por padrão, este plugin gera os valores de CPF e CNPJ com máscara. Caso queira gerar apenas os números, sem máscara, há duas formas:

### 1. Através do menu de contexto

1. Clique com o botão direito;
2. Selecione a opção **Gerar CPF/CNPJ**;
3. Habilite a opção **Somente números**.

### 2. Através da opção **_Find Action_**

1. Selecione o menu **Help** e depois a opção **Find Action...** (ou utilize o atalho **Ctrl**+**Shift**+**A**);
2. Navegue com as setas do teclado ou digite o texto **Somente números** para buscar a ação;
3. Aperte **ENTER** ou clique com o mouse para habilitá-la.

## Habilitar/desabilitar CNPJ alfanumérico

Por padrão, este plugin gera o CNPJ numérico. Caso queira testar o novo formato de CNPJ alfanumérico, com previsão de implantação em janeiro de 2026, há duas formas:

### 1. Através do menu de contexto

1. Clique com o botão direito;
2. Selecione a opção **Gerar CPF/CNPJ**;
3. Habilite a opção **CNPJ alfanumérico**.

### 2. Através da opção **_Find Action_**

1. Selecione o menu **Help** e depois a opção **Find Action...** (ou utilize o atalho **Ctrl**+**Shift**+**A**);
2. Navegue com as setas do teclado ou digite o texto **CNPJ alfanumérico** para buscar a ação;
3. Aperte **ENTER** ou clique com o mouse para habilitá-la.