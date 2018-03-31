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
package com.github.brunobastosg.action;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

import static com.github.brunobastosg.constants.Constantes.GERAR_SOMENTE_NUMEROS;

public abstract class GerarAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getData(CommonDataKeys.PROJECT);
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);

        if (project == null || editor == null) {
            return;
        }

        Runnable runnable = () -> {
            String dado = gerarDado();
            if (!PropertiesComponent.getInstance().getBoolean(GERAR_SOMENTE_NUMEROS)) {
                dado = formatarDado(dado);
            }
            for (Caret caret : editor.getCaretModel().getAllCarets()) {
                int start;
                Document document = caret.getEditor().getDocument();
                if (caret.hasSelection()) {
                    start = caret.getSelectionStart();
                    int end = caret.getSelectionEnd();

                    document.replaceString(start, end, dado);
                    caret.setSelection(start, start + dado.length());
                } else {
                    start = caret.getOffset();

                    document.insertString(start, dado);
                }
                caret.moveToOffset(start + dado.length());
            }
        };

        WriteCommandAction.runWriteCommandAction(project, runnable);
    }

    protected abstract String gerarDado();
    protected abstract String formatarDado(String dado);
}
