package com.github.brunobastosg.validador

import com.github.brunobastosg.util.Modulo11.calcularDigitosCPF
import com.github.brunobastosg.util.Modulo11.calcularDigitosCNPJ

object Validador {
    private val CPF = Regex("[0-9]{11}|[0-9]{3}\\.[0-9]{3}\\.[0-9]{3}-[0-9]{2}")
    private val CNPJ = Regex("[0-9A-Za-z]{12}[0-9]{2}|[0-9A-Za-z]{2}\\.[0-9A-Za-z]{3}\\.[0-9A-Za-z]{3}/[0-9A-Za-z]{4}-[0-9]{2}")

    fun validarCPF(cpf: String): ResultadoValidacao = validar(cpf, "CPF", CPF, ::calcularDigitosCPF)

    fun validarCNPJ(cnpj: String): ResultadoValidacao = validar(cnpj, "CNPJ", CNPJ, ::calcularDigitosCNPJ)

    private fun validar(
        documento: String,
        tipo: String,
        formato: Regex,
        calcularDigitos: (String) -> String
    ): ResultadoValidacao {
        if (!formato.matches(documento)) {
            return ResultadoValidacao(false, "$tipo com tamanho, caracteres ou máscara inválidos.")
        }
        val compacto = documento.replace(".", "").replace("/", "").replace("-", "").uppercase()
        if (compacto.all { it == compacto.first() }) {
            return ResultadoValidacao(false, "$tipo com sequência repetida.")
        }
        if (calcularDigitos(compacto.dropLast(2)) != compacto.takeLast(2)) {
            return ResultadoValidacao(false, "$tipo com dígitos verificadores incorretos.")
        }
        return ResultadoValidacao(true, "$tipo com dígitos verificadores válidos; cadastro não consultado.")
    }
}
