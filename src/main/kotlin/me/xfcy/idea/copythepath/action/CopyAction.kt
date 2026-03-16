/**
 * Copyright (c) 2021 ChenY <xfcypc@foxmail.com>
 * CopyThePath is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package me.xfcy.idea.copythepath.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import me.xfcy.idea.copythepath.setting.PathType
import me.xfcy.idea.copythepath.setting.SettingState
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class CopyAction: AnAction() {

    override fun actionPerformed(e: AnActionEvent) {

        val project = e.project
        val projectPath = project?.basePath ?: ""

        val state = project?.getService(SettingState::class.java)
        val prefix = state?.pathPrefix ?: ""
        val pathType = state?.pathType ?: PathType.RELATIVE

        val file = CommonDataKeys.VIRTUAL_FILE.getData(e.dataContext)
        var filePath = file?.canonicalPath ?: ""

        if (pathType == PathType.RELATIVE) {
            if (filePath == projectPath) {
                filePath = prefix
            } else if (
                projectPath.isNotBlank() &&
                filePath.startsWith(projectPath)
            ) {
                val subStart = projectPath.length + 1
                filePath = prefix + filePath.substring(subStart)
            }
        } else {
            // ABSOLUTE: use absolute path with prefix
            filePath = prefix + filePath
        }

        // 获取编辑器中的选中范围并附加行号
        val editor = CommonDataKeys.EDITOR.getData(e.dataContext)
        val selectionModel = editor?.selectionModel

        if (selectionModel?.hasSelection() == true) {
            val startOffset = selectionModel.selectionStart
            val endOffset = selectionModel.selectionEnd

            val startLine = editor.document.getLineNumber(startOffset) + 1
            val endLine = editor.document.getLineNumber(endOffset) + 1

            filePath += if (startLine == endLine) {
                ":$startLine"
            } else {
                ":$startLine-$endLine"
            }
        }

        val stringSelection = StringSelection(filePath)
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(stringSelection, stringSelection)

    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = (e.project != null)
    }
}