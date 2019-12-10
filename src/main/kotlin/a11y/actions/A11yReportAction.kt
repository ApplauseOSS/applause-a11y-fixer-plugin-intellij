package a11y.actions

import a11y.A11yBundle
import a11y.service.A11yService
import a11y.utils.DataKeys
import a11y.utils.Icons
import a11y.utils.Notify
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.ServiceManager
import com.intellij.ui.EditorNotifications

class A11yReportAction : AnAction(
    A11yBundle.message("action.reportText"),
    A11yBundle.message("action.reportDescription"),
    Icons.APPLAUSE
) {
    private val a11yService = ServiceManager.getService(A11yService::class.java)


    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val notifications = EditorNotifications.getInstance(project)

        val psiFile = e.getData(DataKeys.PSI_FILE) ?: return


        try {
            val jsonArray = a11yService.executeReport(psiFile)

            var description = A11yBundle.message("notify.contentFoundIssues", 0)
            if (jsonArray.size() >= 1) {
                description = A11yBundle.message("notify.contentFoundIssues", jsonArray.size())
            }

            Notify.show(
                project,
                A11yBundle.message("notify.title"),
                description,
                NotificationType.INFORMATION
            )

            notifications.updateNotifications(psiFile.virtualFile)

        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }

    }
}
