# Gerador de CPF/CNPJ para IntelliJ IDEA

Este plugin possibilita a inserção de CPF's e/ou CNPJ's gerados aleatoriamente, com ou sem máscara, diretamente no seu código.

## Utilização

### 1. Através do menu **_Generate_...**

1. Selecione o menu **Code**;
2. Selecione a opção **Generate...**;
3. Escolha a opção desejada (**Gerar CPF** ou **Gerar CNPJ**);
4. O CPF ou CNPJ será inserido na posição do cursor ou da seleção.

Alternativamente, você pode acessar o mesmo menu através do atalho **Alt**+**Insert**.

### 2. Através do menu de contexto

Enquanto estiver editando o código:

1. Posicione o cursor no local desejado (ou selecione um trecho a ser substituído);
2. Clique com o botão direito;
3. Selecione a opção **Gerar CPF/CNPJ**;
4. Escolha a opção desejada (**Gerar CPF** ou **Gerar CNPJ**);
5. O CPF ou CNPJ será inserido na posição do cursor ou da seleção.

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