/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 Bruno Guimarães
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.brunobastosg.gerador

import com.github.brunobastosg.util.Modulo11.calcularModulo11
import java.security.SecureRandom

object Gerador {
    private val MULTIPLICADORES_CPF = intArrayOf(2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
    private val MULTIPLICADORES_CNPJ = intArrayOf(2, 3, 4, 5, 6, 7, 8, 9, 2, 3, 4, 5, 6)

    private val RND = SecureRandom()

    fun gerarCPF(): String {
        return gerarCPFCNPJ(9, MULTIPLICADORES_CPF)
    }

    fun gerarCNPJ(): String {
        return gerarCPFCNPJ(12, MULTIPLICADORES_CNPJ)
    }

    private fun gerarCPFCNPJ(tamanho: Int, multiplicadores: IntArray): String {
        val base = gerarStringNumerica(tamanho)
        val dv1 = calcularModulo11(base, multiplicadores)
        val dv2 = calcularModulo11(base + dv1, multiplicadores)
        return base + dv1 + dv2
    }

    private fun gerarStringNumerica(tamanho: Int): String {
        val sb = StringBuilder(tamanho)
        for (i in 0 until tamanho) {
            sb.append(RND.nextInt(10))
        }
        return sb.toString()
    }
}