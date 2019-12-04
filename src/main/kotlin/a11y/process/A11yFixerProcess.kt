package a11y.process

import com.intellij.openapi.diagnostic.Logger
import java.io.IOException

class A11yFixerProcess {

    fun run() {
        val processBuilder = ProcessBuilder()

        try {
            val process = processBuilder.start()
            process.waitFor()

            val output = process.outputStream
            LOG.info(output.toString())

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }

    companion object {
        private val LOG = Logger.getInstance(A11yFixerProcess::class.java)
    }
}
