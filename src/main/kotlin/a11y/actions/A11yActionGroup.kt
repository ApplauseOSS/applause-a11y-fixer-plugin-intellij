package a11y.actions

import a11y.A11yBundle
import a11y.utils.DataKeys
import a11y.utils.Icons
import com.intellij.lang.html.HTMLLanguage
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup

class A11yActionGroup : DefaultActionGroup() {
    init {
        val p = templatePresentation
        p.text = A11yBundle.message("action.group")
        p.description = A11yBundle.message("action.group.description")
        p.icon = Icons.APPLAUSE
    }

    override fun update(e: AnActionEvent) {
        val presentation = e.presentation

        val psiFile = e.getData(DataKeys.PSI_FILE) ?: return

        if (!HTMLLanguage.INSTANCE.`is`(psiFile.language)) {
            presentation.isVisible = false
        }
    }
}
