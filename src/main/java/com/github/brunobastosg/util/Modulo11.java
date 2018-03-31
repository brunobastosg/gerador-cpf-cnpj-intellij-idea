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
package com.github.brunobastosg.util;

public final class Modulo11 {

    private Modulo11() {
    }

    public static String calcularModulo11(String base, int[] multiplicadores) {

        int soma = 0;

        for (int i = base.length() - 1, j = 0; i >= 0; i--, j++) {
            soma += multiplicadores[j] * Integer.parseInt(String.valueOf(base.charAt(i)));
        }

        int resto = soma % 11;

        int dv = resto > 1 ? 11 - resto : 0;

        return String.valueOf(dv);
    }

}
