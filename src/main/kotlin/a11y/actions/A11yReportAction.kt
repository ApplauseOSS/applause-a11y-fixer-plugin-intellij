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
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.EditorNotifications
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.Action
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextArea

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
            val violationCount = a11yService.getViolationCount(psiFile)
            val humanReadableReport = a11yService.executeReportHumanReadable(psiFile)

            var description = A11yBundle.message("notify.contentFoundIssues", 0)
            if (violationCount >= 1) {
                description = A11yBundle.message("notify.contentFoundIssues", violationCount)
            }

            var actions = listOf<AnAction>()
            if (violationCount >= 1) {
                val action = object : AnAction(A11yBundle.message("action.detailsLink"), null, null) {
                    override fun actionPerformed(e: AnActionEvent) {
                        val dialog = object : DialogWrapper(project, true) {
                            init {
                                init()
                                title = A11yBundle.message("report.details.title")
                            }

                            override fun createActions(): Array<Action> {
                                return arrayOf(okAction)
                            }

                            override fun createCenterPanel(): JComponent? {
                                val dialogPanel = JPanel(BorderLayout())

                                val textArea = JTextArea(humanReadableReport)
                                textArea.isEditable = false

                                val scroll = JBScrollPane(textArea)
                                scroll.preferredSize = Dimension(800, 500)
                                scroll.horizontalScrollBarPolicy = JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
                                scroll.verticalScrollBarPolicy = JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
                                dialogPanel.add(scroll)

                                return dialogPanel
                            }
                        }
                        dialog.show()

                    }
                }
                actions = listOf(action)
            }

            Notify.show(
                project,
                A11yBundle.message("notify.title"),
                description,
                Notify.A11Y_GROUP,
                actions,
                NotificationType.INFORMATION
            )

            notifications.updateNotifications(psiFile.virtualFile)

        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }

    }
}
