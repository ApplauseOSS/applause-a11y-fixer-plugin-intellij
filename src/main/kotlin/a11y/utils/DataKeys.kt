package a11y.utils

import com.intellij.openapi.actionSystem.DataKey
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile

object DataKeys {
    val PROJECT = DataKey.create<Project>("project")
    val VIRTUAL_FILE = DataKey.create<VirtualFile>("virtualFile")
    val PSI_FILE = DataKey.create<PsiFile>("psi.File")
}
