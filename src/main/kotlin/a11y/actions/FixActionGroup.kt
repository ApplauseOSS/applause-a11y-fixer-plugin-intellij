package a11y.actions

import a11y.A11yBundle
import a11y.utils.DataKeys
import a11y.utils.Icons
import com.intellij.lang.html.HTMLLanguage
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class FixActionGroup : ActionGroup() {
    init {
        val p = templatePresentation
        p.text = A11yBundle.message("action.group")
        p.description = A11yBundle.message("action.group.description")
        p.icon = Icons.APPLAUSE
    }

    override fun update(e: AnActionEvent) {
        val presentation = e.presentation

        val psiFile = e.getData(DataKeys.PSI_FILE)
        if (psiFile != null) {
            if (!HTMLLanguage.INSTANCE.`is`(psiFile.language)) {
                presentation.isVisible = false
            }
        }
    }

    override fun getChildren(anActionEvent: AnActionEvent?): Array<AnAction?> {
        val actions = arrayOfNulls<AnAction>(1)
        actions[0] = FixAction()
        return actions
    }
}
