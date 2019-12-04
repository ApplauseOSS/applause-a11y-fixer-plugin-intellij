package a11y.actions

import a11y.A11yBundle
import a11y.commands.FixFileCommandAction
import a11y.utils.DataKeys
import a11y.utils.Icons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.ui.EditorNotifications

class FixAction : DumbAwareAction() {
    init {
        val presentation = templatePresentation
        presentation.text = A11yBundle.message("action.actionText")
        presentation.description = A11yBundle.message("panel.fixA11yIssues")
        presentation.icon = Icons.APPLAUSE
    }


    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val psiFile = e.getData(DataKeys.PSI_FILE)
        if (project != null && psiFile != null) {
            val notifications = EditorNotifications.getInstance(project)

            try {
                FixFileCommandAction(project, psiFile).execute()
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
            }

            notifications.updateAllNotifications()
        }

    }
}
