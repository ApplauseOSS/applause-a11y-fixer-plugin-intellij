package a11y.notification

import a11y.A11yBundle
import a11y.commands.FixFileCommandAction
import com.intellij.lang.html.HTMLLanguage
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotifications
import com.intellij.util.containers.ContainerUtil

class FixHtmlDocumentProvider : EditorNotifications.Provider<EditorNotificationPanel>() {

    private val dismissedFileMap = ContainerUtil.createWeakKeyWeakValueMap<VirtualFile, Boolean>()

    override fun getKey(): Key<EditorNotificationPanel> {
        return KEY
    }

    override fun createNotificationPanel(
        file: VirtualFile, fileEditor: FileEditor,
        project: Project
    ): EditorNotificationPanel? {
        if (dismissedFileMap[file] != null) {
            return null
        }

        val psiFile = PsiManager.getInstance(project).findFile(file) ?: return null

        if (!HTMLLanguage.INSTANCE.`is`(psiFile.language)) {
            return null
        }

        val panel = EditorNotificationPanel()
        val notifications = EditorNotifications.getInstance(project)

        panel.setText(A11yBundle.message("panel.fixA11yIssues"))
        panel.createActionLabel(A11yBundle.message("panel.fixA11yIssues.create")) {
            try {
                FixFileCommandAction(project, psiFile).execute()
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
            }

            notifications.updateAllNotifications()

        }
        panel.createActionLabel(A11yBundle.message("panel.cancel")) {
            dismissedFileMap[file] = true
            notifications.updateAllNotifications()
        }


        return panel
    }

    companion object {
        private val KEY = Key.create<EditorNotificationPanel>(this::class.toString())
    }
}
