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
package me.xfcy.idea.copythepath.setting

import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComboBox
import javax.swing.JPanel
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class SettingComponent(project: Project) {

    private var prefix = ""
    private var pathType = PathType.RELATIVE
    private val examplePath = "path/to/your/file.ext"

    val mainPanel: JPanel
    val pathPrefixText: JBTextField
    val pathTypeCombo: JComboBox<PathType>
    val preview: JBLabel

    init {
        val state = SettingState.getInstance(project)
        prefix = state?.pathPrefix ?: ""
        pathType = state?.pathType ?: PathType.RELATIVE
        pathPrefixText = JBTextField(prefix)
        pathTypeCombo = JComboBox(PathType.values())
        pathTypeCombo.selectedItem = pathType
        preview = JBLabel(prefix + examplePath)

        mainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(JBLabel("Path type: "), pathTypeCombo, 1, false)
                .addLabeledComponent(JBLabel("Path prefix: "), pathPrefixText, 1, false)
                .addLabeledComponent(JBLabel("Preview: "), preview, 1, false)
                .addComponentFillVertically(JPanel(), 0)
                .panel

        pathPrefixText.document.addDocumentListener(object: DocumentListener {
            override fun insertUpdate(p0: DocumentEvent?) { updatePreview() }
            override fun removeUpdate(p0: DocumentEvent?) { updatePreview() }
            override fun changedUpdate(p0: DocumentEvent?) { updatePreview() }
        })

        pathTypeCombo.addActionListener { updatePreview() }
    }

    private fun updatePreview() {
        val selectedType = pathTypeCombo.selectedItem as PathType
        preview.text = when (selectedType) {
            PathType.RELATIVE -> pathPrefixText.text + examplePath
            PathType.ABSOLUTE -> "/$examplePath"
        }
    }
}