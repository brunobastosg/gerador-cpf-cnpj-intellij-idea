package com.github.brunobastosg.mcp

import com.github.brunobastosg.constants.Constantes
import com.github.brunobastosg.formatador.Formatador
import com.github.brunobastosg.gerador.Gerador
import com.github.brunobastosg.validador.ResultadoValidacao
import com.github.brunobastosg.validador.Validador
import com.intellij.ide.util.PropertiesComponent
import com.intellij.mcpserver.McpToolset
import com.intellij.mcpserver.annotations.McpDescription
import com.intellij.mcpserver.annotations.McpTool
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@Suppress("FunctionName", "unused")
class CpfCnpjMcpToolset : McpToolset {
    @McpTool
    @McpDescription(
        "Gerar CPF: retorna um CPF aleatório como texto, sem inserir no editor. " +
            "Sem formatado, segue a preferência atual do menu Somente números. " +
            "Para pedidos com formatação/com máscara envie formatado=true; sem formatação/somente números, false. " +
            "A sobrescrita vale apenas para esta chamada e não altera as configurações."
    )
    fun gerar_cpf(
        @McpDescription("true: com máscara; false: sem máscara; omitido ou null: seguir a preferência do plugin.")
        formatado: Boolean? = null
    ): String {
        val cpf = Gerador.gerarCPF()
        return if (usarFormatacao(formatado)) Formatador.formatarCPF(cpf) else cpf
    }

    @McpTool
    @McpDescription(
        "Gerar CNPJ: retorna um CNPJ aleatório numérico ou alfanumérico como texto, sem inserir no editor. " +
            "Sem parâmetros, segue as preferências atuais de máscara e CNPJ alfanumérico. " +
            "Com formatação/com máscara: formatado=true; sem formatação: formatado=false. " +
            "CNPJ alfanumérico: alfanumerico=true, mesmo se desabilitado no plugin; " +
            "CNPJ numérico: alfanumerico=false, mesmo se habilitado. " +
            "As sobrescritas são independentes, valem apenas para esta chamada e não alteram as configurações."
    )
    fun gerar_cnpj(
        @McpDescription("true: com máscara; false: sem máscara; omitido ou null: seguir a preferência do plugin.")
        formatado: Boolean? = null,
        @McpDescription("true: permitir letras A-Z na base; false: apenas números; omitido ou null: seguir a preferência do plugin.")
        alfanumerico: Boolean? = null
    ): String {
        val incluirLetras = alfanumerico
            ?: PropertiesComponent.getInstance().getBoolean(Constantes.CNPJ_ALFANUMERICO, false)
        val cnpj = Gerador.gerarCNPJ(incluirLetras)
        return if (usarFormatacao(formatado)) Formatador.formatarCNPJ(cnpj) else cnpj
    }

    @McpTool
    @McpDescription(
        "Validar CPF: confere os dígitos verificadores e rejeita sequências repetidas. " +
            "Aceita CPF com máscara canônica ou apenas números. Não consulta existência ou situação cadastral. " +
            "Retorna JSON com valido (boolean) e motivo (texto), inclusive para entradas inválidas."
    )
    fun validar_cpf(
        @McpDescription("CPF como texto, com 11 números ou no formato 000.000.002-72.")
        cpf: String
    ): JsonObject = Validador.validarCPF(cpf).comoJson()

    @McpTool
    @McpDescription(
        "Validar CNPJ: confere os dígitos verificadores e rejeita sequências repetidas. " +
            "Aceita CNPJ numérico ou alfanumérico, com máscara canônica ou compacto, independentemente das configurações. " +
            "Letras minúsculas são normalizadas para maiúsculas; os dois dígitos finais devem ser numéricos. " +
            "Não consulta existência ou situação cadastral. Retorna JSON com valido (boolean) e motivo (texto)."
    )
    fun validar_cnpj(
        @McpDescription("CNPJ como texto: base de 12 números/letras A-Z e 2 dígitos verificadores, com ou sem máscara.")
        cnpj: String
    ): JsonObject = Validador.validarCNPJ(cnpj).comoJson()

    private fun usarFormatacao(formatado: Boolean?): Boolean =
        formatado ?: !PropertiesComponent.getInstance().getBoolean(Constantes.GERAR_SOMENTE_NUMEROS, false)

    private fun ResultadoValidacao.comoJson(): JsonObject = buildJsonObject {
        put("valido", valido)
        put("motivo", motivo)
    }
}
