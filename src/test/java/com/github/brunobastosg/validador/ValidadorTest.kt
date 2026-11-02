package com.github.brunobastosg.validador

import com.github.brunobastosg.formatador.Formatador
import com.github.brunobastosg.gerador.Gerador
import com.github.brunobastosg.util.Modulo11
import org.junit.Assert.*
import org.junit.Test

class ValidadorTest {
    @Test
    fun `valida vetores conhecidos com e sem mascara`() {
        listOf("52998224725", "529.982.247-25", "00000000272", "000.000.002-72").forEach {
            assertTrue(it, Validador.validarCPF(it).valido)
        }
        listOf("04252011000110", "04.252.011/0001-10", "12ABC34501DE35", "12.ABC.345/01DE-35",
            "12abc34501de35", "12.abc.345/01de-35").forEach {
            assertTrue(it, Validador.validarCNPJ(it).valido)
        }
        assertEquals("25", Modulo11.calcularDigitosCPF("529982247"))
        assertEquals("10", Modulo11.calcularDigitosCNPJ("042520110001"))
        assertEquals("35", Modulo11.calcularDigitosCNPJ("12ABC34501DE"))
    }

    @Test
    fun `rejeita digitos verificadores incorretos`() {
        listOf("52998224735", "529.982.247-24").forEach {
            assertFalse(it, Validador.validarCPF(it).valido)
            assertTrue(Validador.validarCPF(it).motivo.contains("dígitos verificadores incorretos"))
        }
        listOf("04252011000120", "04.252.011/0001-11", "12ABC34501DE45", "12.ABC.345/01DE-34").forEach {
            assertFalse(it, Validador.validarCNPJ(it).valido)
        }
    }

    @Test
    fun `rejeita entradas malformadas sem sanitizacao permissiva`() {
        listOf("", "5299822472", "529982247255", " 52998224725", "52998224725\n",
            "529-982-247.25", "529..982.247-25", "5299822472A", "５2998224725").forEach {
            val resultado = Validador.validarCPF(it)
            assertFalse(it, resultado.valido)
            assertTrue(resultado.motivo.isNotBlank())
        }
        listOf("", "0425201100011", "042520110001100", " 04252011000110",
            "04-252-011/0001.10", "12.ABC.345/01DE-3A", "12ABC34501DEAA",
            "12@BC34501DE35", "12ÁBC34501DE35", "12ABC34501DE35\n").forEach {
            val resultado = Validador.validarCNPJ(it)
            assertFalse(it, resultado.valido)
            assertTrue(resultado.motivo.isNotBlank())
        }
    }

    @Test
    fun `rejeita todas as sequencias numericas repetidas`() {
        for (digito in '0'..'9') {
            val cpf = digito.toString().repeat(11)
            val cnpj = digito.toString().repeat(14)
            listOf(cpf, Formatador.formatarCPF(cpf)).forEach {
                assertFalse(it, Validador.validarCPF(it).valido)
                assertTrue(Validador.validarCPF(it).motivo.contains("sequência repetida"))
            }
            listOf(cnpj, Formatador.formatarCNPJ(cnpj)).forEach {
                assertFalse(it, Validador.validarCNPJ(it).valido)
                assertTrue(Validador.validarCNPJ(it).motivo.contains("sequência repetida"))
            }
        }
    }

    @Test
    fun `preserva geracao e formatacao com digitos conferidos independentemente`() {
        repeat(500) {
            val cpf = Gerador.gerarCPF()
            assertTrue(cpf.matches(Regex("[0-9]{11}")))
            assertTrue(Validador.validarCPF(cpf).valido)
            assertEquals(digitosIndependentes(cpf.take(9), false), cpf.takeLast(2))
            assertEquals("${cpf.take(3)}.${cpf.substring(3, 6)}.${cpf.substring(6, 9)}-${cpf.takeLast(2)}",
                Formatador.formatarCPF(cpf))
            for (alfanumerico in listOf(false, true)) {
                val cnpj = Gerador.gerarCNPJ(alfanumerico)
                assertTrue(cnpj.matches(Regex(if (alfanumerico) "[0-9A-Z]{12}[0-9]{2}" else "[0-9]{14}")))
                assertTrue(Validador.validarCNPJ(cnpj).valido)
                assertEquals(digitosIndependentes(cnpj.take(12), true), cnpj.takeLast(2))
                assertEquals("${cnpj.take(2)}.${cnpj.substring(2, 5)}.${cnpj.substring(5, 8)}/${cnpj.substring(8, 12)}-${cnpj.takeLast(2)}",
                    Formatador.formatarCNPJ(cnpj))
            }
        }
    }

    private fun digitosIndependentes(base: String, cnpj: Boolean): String {
        fun digito(valor: String): Int {
            val pesos = if (cnpj) {
                if (valor.length == 12) listOf(5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
                else listOf(6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
            } else (valor.length + 1 downTo 2).toList()
            val resto = valor.toList().zip(pesos).sumOf { (caractere, peso) -> (caractere.code - '0'.code) * peso } % 11
            return if (resto < 2) 0 else 11 - resto
        }
        val primeiro = digito(base)
        return "$primeiro${digito(base + primeiro)}"
    }
}
