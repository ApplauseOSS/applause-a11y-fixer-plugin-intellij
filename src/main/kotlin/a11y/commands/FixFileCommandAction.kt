package a11y.commands

import a11y.A11yBundle
import a11y.process.A11yFixerProcess
import a11y.utils.Notify
import com.intellij.notification.NotificationType
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile

class FixFileCommandAction(
    private val project: Project,
    private val file: PsiFile
) : CommandAction<PsiFile>(project) {

    private val fixerProcess = A11yFixerProcess()
    private val manager: PsiDocumentManager = PsiDocumentManager.getInstance(project)

    @Throws(Throwable::class)
    override fun compute(): PsiFile {
        val document: Document? = manager.getDocument(file)
        if (document != null) {
//            fixerProcess.run()

            val checksRan = 0
            val checkResult = A11yBundle.message("notify.contentFoundIssues", 0)

            Notify.show(
                project,
                A11yBundle.message("notify.title"),
                A11yBundle.message("notify.content", checksRan, checkResult),
                NotificationType.INFORMATION
            )

            manager.commitDocument(document)
        }
        return file
    }
}
