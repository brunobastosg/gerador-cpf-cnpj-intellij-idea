/**
 * The MIT License (MIT)
 *
 *  Copyright (c) 2018 Bruno Guimarães
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package com.github.brunobastosg.gerador;

import com.github.brunobastosg.util.Modulo11;

import java.security.SecureRandom;

public final class Gerador {

    private static final int[] MULTIPLICADORES_CPF = {2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
    private static final int[] MULTIPLICADORES_CNPJ = {2, 3, 4, 5, 6, 7, 8, 9, 2, 3, 4, 5, 6};

    private static final SecureRandom RND = new SecureRandom();

    private Gerador() {

    }

    public static String gerarCPF() {
        return gerarCPFCNPJ(9, MULTIPLICADORES_CPF);
    }

    public static String gerarCNPJ() {
        return gerarCPFCNPJ(12, MULTIPLICADORES_CNPJ);
    }

    private static String gerarCPFCNPJ(int tamanho, int[] multiplicadores) {
        String base = gerarStringNumerica(tamanho);
        String dv1 = Modulo11.calcularModulo11(base, multiplicadores);
        String dv2 = Modulo11.calcularModulo11(base.concat(dv1), multiplicadores);
        return base.concat(dv1).concat(dv2);
    }

    private static String gerarStringNumerica(int tamanho) {
        StringBuilder sb = new StringBuilder(tamanho);
        for (int i = 0; i < tamanho; i++) {
            sb.append(RND.nextInt(10));
        }
        return sb.toString();
    }

}
