package a11y.service

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.util.containers.ContainerUtil
import org.apache.commons.io.IOUtils
import org.apache.commons.lang.SystemUtils
import org.jetbrains.annotations.NotNull
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets

class A11yService {
    private val pluginId = "a11y-fixer-plugin"
    private val a11yFixerTempDir = FileUtilRt.createTempDirectory(pluginId, "", false)
    private val a11yFixerFile = File(a11yFixerTempDir, "a11y-fixer")
    private val dismissedFileMap = ContainerUtil.createWeakKeyWeakValueMap<VirtualFile, Boolean>()

    private fun getCliPath(): String {
        return a11yFixerFile.absolutePath
    }

    fun dismissFile(file: VirtualFile) {
        dismissedFileMap[file] = true
    }

    fun unDismissFile(file: VirtualFile) {
        dismissedFileMap.remove(file)
    }

    fun isDismissed(file: VirtualFile): Boolean {
        return dismissedFileMap[file] ?: false
    }

    fun isInitialized(): Boolean {
        return a11yFixerFile.exists()
    }

    private fun getPathToExecutable(): String {
        var resourcePath = ""

        if (SystemUtils.IS_OS_MAC_OSX) {
            resourcePath = "/bin/mac/a11y-fixer"
        }
        if (SystemUtils.IS_OS_LINUX) {
            resourcePath = "/bin/linux/a11y-fixer"
        }
        if (SystemUtils.IS_OS_WINDOWS) {
            resourcePath = "/bin/win/a11y-fixer.exe"
        }

        return resourcePath
    }

    fun initA11yFixerUtil() {
        try {
            val inputStream = javaClass.getResourceAsStream(this.getPathToExecutable())

            val fileOutputStream = FileOutputStream(a11yFixerFile)
            val buffer = ByteArray(32768)
            var length: Int

            while (inputStream.read(buffer).also { length = it } > 0) {
                fileOutputStream.write(buffer, 0, length)
            }

            fileOutputStream.close()
            inputStream.close()

            a11yFixerFile.setExecutable(true);

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    @Throws(Throwable::class)
    fun executeFix(@NotNull project: Project, @NotNull psiFile: PsiFile): Boolean {
        return ApplicationManager.getApplication().runWriteAction(Computable<Boolean> {
            val psiDocumentManager: PsiDocumentManager = PsiDocumentManager.getInstance(project)
            val fileDocumentManager: FileDocumentManager = FileDocumentManager.getInstance()

            val document = psiDocumentManager.getDocument(psiFile) ?: return@Computable false

            project.save()
            fileDocumentManager.saveDocument(document)

            val filePath = psiFile.virtualFile.path
            val command = listOf<String>(getCliPath(), "fix", filePath, filePath)
            val processBuilder = ProcessBuilder(command)

            try {
                val process = processBuilder.start()
                process.waitFor()

                fileDocumentManager.reloadFromDisk(document)
                psiDocumentManager.reparseFiles(listOf(psiFile.virtualFile), true)
                psiDocumentManager.commitDocument(document)

                return@Computable true

            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            return@Computable false
        })
    }

    @Throws(Throwable::class)
    fun executeReport(@NotNull psiFile: PsiFile): JsonArray {
        return ApplicationManager.getApplication().runReadAction(Computable<JsonArray> {
            val command = listOf<String>(getCliPath(), "report", "--json", psiFile.virtualFile.path)
            val processBuilder = ProcessBuilder(command)
            var jsonArray = JsonArray()

            try {
                val process = processBuilder.start()
                process.waitFor()

                val output = IOUtils.toString(process.inputStream, StandardCharsets.UTF_8)
                val jsonTree = JsonParser().parse(output)

                if (jsonTree.isJsonArray) {
                    jsonArray = jsonTree.asJsonArray
                }

            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            return@Computable jsonArray
        })
    }

}
