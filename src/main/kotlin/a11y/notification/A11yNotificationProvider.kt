package a11y.notification

import a11y.A11yBundle
import a11y.service.A11yService
import com.intellij.lang.html.HTMLLanguage
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotifications


class A11yNotificationProvider : EditorNotifications.Provider<EditorNotificationPanel>() {

    private val a11yService = ServiceManager.getService(A11yService::class.java)

    override fun getKey(): Key<EditorNotificationPanel> {
        return KEY
    }

    override fun createNotificationPanel(
        virtualFile: VirtualFile,
        fileEditor: FileEditor,
        project: Project
    ): EditorNotificationPanel? {

        if (a11yService.isDismissed(virtualFile)) return null

        val psiManager = PsiManager.getInstance(project)
        val psiFile = psiManager.findFile(virtualFile) ?: return null

        if (!HTMLLanguage.INSTANCE.`is`(psiFile.language)) return null

        if (!a11yService.isInitialized()) return null

        val violationCount = a11yService.getViolationCount(psiFile)
        if (violationCount < 1) return null

        a11yService.unDismissFile(virtualFile)

        val panel = EditorNotificationPanel()

        panel.setText(A11yBundle.message("panel.fixA11yIssues", violationCount))
        panel.createActionLabel(A11yBundle.message("panel.fixA11yIssues.create"), "com.applause.a11y.FixAction")
        panel.createActionLabel(A11yBundle.message("panel.cancel"))
        {
            a11yService.dismissFile(virtualFile)
            val notifications = EditorNotifications.getInstance(project)
            notifications.updateNotifications(virtualFile)
        }

        return panel
    }

    companion object {
        private val KEY = Key.create<EditorNotificationPanel>(this::class.toString())
    }
}
