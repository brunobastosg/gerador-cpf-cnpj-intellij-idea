package com.github.brunobastosg.mcp

import com.github.brunobastosg.constants.Constantes
import com.github.brunobastosg.validador.Validador
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.mcpserver.McpToolCallResultContent
import com.intellij.mcpserver.McpToolset
import com.intellij.mcpserver.impl.util.asTools
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.*

class CpfCnpjMcpToolsetTest : BasePlatformTestCase() {
    private val toolset = CpfCnpjMcpToolset()
    private lateinit var properties: PropertiesComponent
    private var formatoOriginal: String? = null
    private var alfanumericoOriginal: String? = null

    override fun setUp() {
        super.setUp()
        properties = PropertiesComponent.getInstance()
        formatoOriginal = properties.getValue(Constantes.GERAR_SOMENTE_NUMEROS)
        alfanumericoOriginal = properties.getValue(Constantes.CNPJ_ALFANUMERICO)
    }

    override fun tearDown() {
        try {
            properties.setValue(Constantes.GERAR_SOMENTE_NUMEROS, formatoOriginal)
            properties.setValue(Constantes.CNPJ_ALFANUMERICO, alfanumericoOriginal)
        } finally {
            super.tearDown()
        }
    }

    fun testPreferenciasAusentesUsamMascaraECnpjNumerico() {
        properties.unsetValue(Constantes.GERAR_SOMENTE_NUMEROS)
        properties.unsetValue(Constantes.CNPJ_ALFANUMERICO)
        assertTrue(toolset.gerar_cpf().matches(Regex("[0-9]{3}\\.[0-9]{3}\\.[0-9]{3}-[0-9]{2}")))
        assertTrue(toolset.gerar_cnpj().matches(Regex("[0-9]{2}\\.[0-9]{3}\\.[0-9]{3}/[0-9]{4}-[0-9]{2}")))
    }

    fun testMatrizDePreferenciasESobrescritasSemPersistencia() {
        for (somenteNumeros in listOf(false, true)) {
            for (preferenciaAlfanumerica in listOf(false, true)) {
                properties.setValue(Constantes.GERAR_SOMENTE_NUMEROS, somenteNumeros)
                properties.setValue(Constantes.CNPJ_ALFANUMERICO, preferenciaAlfanumerica)
                for (formatado in listOf(null, false, true)) {
                    val comMascara = formatado ?: !somenteNumeros
                    val cpf = toolset.gerar_cpf(formatado)
                    assertEquals(comMascara, cpf.contains('.'))
                    assertEquals(if (comMascara) 14 else 11, cpf.length)
                    for (alfanumerico in listOf(null, false, true)) {
                        val permitirLetras = alfanumerico ?: preferenciaAlfanumerica
                        val documentos = List(10) { toolset.gerar_cnpj(formatado, alfanumerico) }
                        for (cnpj in documentos) {
                            assertEquals(comMascara, cnpj.contains('.'))
                            assertEquals(if (comMascara) 18 else 14, cnpj.length)
                            assertTrue(cnpj.takeLast(2).all { it in '0'..'9' })
                            assertTrue(cnpj, Validador.validarCNPJ(cnpj).valido)
                            if (!permitirLetras) assertFalse(cnpj.any { it in 'A'..'Z' })
                        }
                        if (permitirLetras) assertTrue(documentos.any { cnpj -> cnpj.any { it in 'A'..'Z' } })
                        assertEquals(somenteNumeros, properties.getBoolean(Constantes.GERAR_SOMENTE_NUMEROS))
                        assertEquals(preferenciaAlfanumerica, properties.getBoolean(Constantes.CNPJ_ALFANUMERICO))
                    }
                }
            }
        }
    }

    fun testValidacaoIndependeDasPreferencias() {
        for (configuracao in listOf(false, true)) {
            properties.setValue(Constantes.GERAR_SOMENTE_NUMEROS, configuracao)
            properties.setValue(Constantes.CNPJ_ALFANUMERICO, configuracao)
            assertTrue(toolset.validar_cpf("000.000.002-72").getValue("valido").jsonPrimitive.boolean)
            assertTrue(toolset.validar_cnpj("12.ABC.345/01DE-35").getValue("valido").jsonPrimitive.boolean)
            assertTrue(toolset.validar_cnpj("04252011000110").getValue("valido").jsonPrimitive.boolean)
            assertFalse(toolset.validar_cpf("00000000000").getValue("valido").jsonPrimitive.boolean)
            assertFalse(toolset.validar_cnpj("00000000000000").getValue("valido").jsonPrimitive.boolean)
        }
    }

    fun testAcoesDoEditorPreservamPreferenciasEMascaras() {
        for (somenteNumeros in listOf(false, true)) {
            properties.setValue(Constantes.GERAR_SOMENTE_NUMEROS, somenteNumeros)
            properties.setValue(Constantes.CNPJ_ALFANUMERICO, false)
            for ((acao, tamanhoSemMascara) in listOf("GerarCPF" to 11, "GerarCNPJ" to 14)) {
                myFixture.configureByText("teste.txt", "<caret>")
                val actionManager = ActionManager.getInstance()
                val action = if (acao == "GerarCPF") {
                    actionManager.getAction("GeradorCPFCNPJ.GerarCPF")
                } else {
                    actionManager.getAction("GeradorCPFCNPJ.GerarCNPJ")
                }
                myFixture.testAction(action)
                val documento = myFixture.editor.document.text
                assertEquals(!somenteNumeros, documento.contains('.'))
                assertEquals(if (somenteNumeros) tamanhoSemMascara else tamanhoSemMascara + if (acao == "GerarCPF") 3 else 4,
                    documento.length)
            }
        }
    }

    fun testRegistroESchemasDoHostMcp() {
        assertTrue(McpToolset.EP.extensionList.any { it is CpfCnpjMcpToolset })
        val tools = toolset.asTools().associateBy { it.descriptor.name }
        assertEquals(setOf("gerar_cpf", "gerar_cnpj", "validar_cpf", "validar_cnpj"), tools.keys)
        val parametros = mapOf(
            "gerar_cpf" to setOf("formatado"),
            "gerar_cnpj" to setOf("formatado", "alfanumerico"),
            "validar_cpf" to setOf("cpf"),
            "validar_cnpj" to setOf("cnpj")
        )
        for ((nome, tool) in tools) {
            val schema = tool.descriptor.inputSchema
            assertEquals(parametros.getValue(nome), schema.parameters.keys)
            assertEquals(if (nome.startsWith("validar")) parametros.getValue(nome) else emptySet<String>(),
                schema.requiredParameters)
            assertTrue(tool.descriptor.description.isNotBlank())
        }
    }

    fun testArgumentosAusentesOuDeTipoIncorretoNaoRetornamSucesso() {
        val tools = toolset.asTools().associateBy { it.descriptor.name }
        for (nome in listOf("validar_cpf", "validar_cnpj")) {
            assertThrows(IllegalStateException::class.java) {
                runBlocking { tools.getValue(nome).call(buildJsonObject {}) }
            }
        }
        assertThrows(SerializationException::class.java) {
            runBlocking {
                tools.getValue("gerar_cpf").call(buildJsonObject { put("formatado", "nao-e-booleano") })
            }
        }
    }

    fun testChamadasPeloHostRetornamTextoEObjetoJsonSemDuplaCodificacao() = runBlocking {
        val tools = toolset.asTools().associateBy { it.descriptor.name }
        properties.setValue(Constantes.GERAR_SOMENTE_NUMEROS, true)
        properties.setValue(Constantes.CNPJ_ALFANUMERICO, false)
        for ((nome, parametro) in listOf("gerar_cpf" to "cpf", "gerar_cnpj" to "cnpj")) {
            for (formatado in listOf(null, false, true)) {
                val args = buildJsonObject {
                    put("formatado", formatado?.let { JsonPrimitive(it) } ?: JsonNull)
                    if (nome == "gerar_cnpj") put("alfanumerico", true)
                }
                val geracao = tools.getValue(nome).call(args)
                assertFalse(geracao.isError)
                val documento = (geracao.content.single() as McpToolCallResultContent.Text).text
                assertEquals(formatado == true, documento.contains('.'))
                val validacao = tools.getValue("validar_$parametro").call(buildJsonObject { put(parametro, documento) })
                assertFalse(validacao.isError)
                val texto = (validacao.content.single() as McpToolCallResultContent.Text).text
                val objeto = Json.parseToJsonElement(texto).jsonObject
                assertEquals(setOf("valido", "motivo"), objeto.keys)
                assertTrue(objeto.getValue("valido").jsonPrimitive.boolean)
                assertTrue(objeto.getValue("motivo").jsonPrimitive.content.isNotBlank())
            }
            val semArgumentos = tools.getValue(nome).call(buildJsonObject {})
            val documento = (semArgumentos.content.single() as McpToolCallResultContent.Text).text
            assertFalse(documento.contains('.'))
        }
        for ((nome, parametro) in listOf("validar_cpf" to "cpf", "validar_cnpj" to "cnpj")) {
            val resultado = tools.getValue(nome).call(buildJsonObject { put(parametro, "invalido") })
            assertFalse(resultado.isError)
            val texto = (resultado.content.single() as McpToolCallResultContent.Text).text
            assertFalse(Json.parseToJsonElement(texto).jsonObject.getValue("valido").jsonPrimitive.boolean)
        }
    }
}
