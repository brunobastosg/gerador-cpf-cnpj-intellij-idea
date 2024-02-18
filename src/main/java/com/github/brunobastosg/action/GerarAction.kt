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
package com.github.brunobastosg.action

import com.github.brunobastosg.constants.Constantes
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction

abstract class GerarAction : AnAction() {
    override fun actionPerformed(anActionEvent: AnActionEvent) {
        val project = anActionEvent.getData(CommonDataKeys.PROJECT)
        val editor = anActionEvent.getData(CommonDataKeys.EDITOR)

        if (project == null || editor == null) {
            return
        }

        val runnable = Runnable {
            var dado = gerarDado()
            if (!PropertiesComponent.getInstance().getBoolean(Constantes.GERAR_SOMENTE_NUMEROS)) {
                dado = formatarDado(dado)
            }
            for (caret in editor.caretModel.allCarets) {
                var start: Int
                val document = caret.editor.document
                if (caret.hasSelection()) {
                    start = caret.selectionStart
                    val end = caret.selectionEnd

                    document.replaceString(start, end, dado)
                    caret.setSelection(start, start + dado.length)
                } else {
                    start = caret.offset

                    document.insertString(start, dado)
                }
                caret.moveToOffset(start + dado.length)
            }
        }

        WriteCommandAction.runWriteCommandAction(project, runnable)
    }

    protected abstract fun gerarDado(): String
    protected abstract fun formatarDado(dado: String): String
}