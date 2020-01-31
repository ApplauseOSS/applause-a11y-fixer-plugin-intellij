package a11y.service

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
    private val a11yFixerFile = File(a11yFixerTempDir, "applause-accessibility-tool")
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
            resourcePath = "/bin/mac/applause-accessibility-tool"
        }
        if (SystemUtils.IS_OS_LINUX) {
            resourcePath = "/bin/linux/applause-accessibility-tool"
        }
        if (SystemUtils.IS_OS_WINDOWS) {
            resourcePath = "/bin/win/applause-accessibility-tool.exe"
        }

        return resourcePath
    }

    /*
    Initialize the fixer cli tool by copying it out of the jar's resources and into a temporary location on the system
     */
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

            a11yFixerFile.setExecutable(true)

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
            val command = listOf(getCliPath(), "fix", filePath, filePath)
            val processBuilder = ProcessBuilder(command)

            try {
                val process = processBuilder.start()
                process.waitFor()

                fileDocumentManager.reloadFromDisk(document)
                psiDocumentManager.reparseFiles(listOf(psiFile.virtualFile), true)
                psiDocumentManager.commitDocument(document)
                psiFile.virtualFile.refresh(false, false)

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
    fun getViolationCount(@NotNull psiFile: PsiFile): Int {
        return ApplicationManager.getApplication().runReadAction(Computable<Int> {
            val commandJson = listOf(getCliPath(), "report", "--json", psiFile.virtualFile.path)
            val jsonProcessBuilder = ProcessBuilder(commandJson)
            jsonProcessBuilder.redirectErrorStream(true)

            var jsonCounter = 0

            try {
                val jsonProcess = jsonProcessBuilder.start()
                val jsonOutput = IOUtils.toString(jsonProcess.inputStream, StandardCharsets.UTF_8)
                jsonProcess.waitFor()
                val jsonTree = JsonParser().parse(jsonOutput)

                jsonCounter = jsonTree.asJsonArray.size()

            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            return@Computable jsonCounter
        })
    }

    @Throws(Throwable::class)
    fun executeReportHumanReadable(@NotNull psiFile: PsiFile): String {
        return ApplicationManager.getApplication().runReadAction(Computable<String> {
            val commandHuman = listOf(getCliPath(), "report", psiFile.virtualFile.path)
            val humanProcessBuilder = ProcessBuilder(commandHuman)

            var report = ""

            try {
                val humanProcess = humanProcessBuilder.start()
                report = IOUtils.toString(humanProcess.inputStream, StandardCharsets.UTF_8)
                humanProcess.waitFor()

            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            return@Computable report
        })
    }

}
