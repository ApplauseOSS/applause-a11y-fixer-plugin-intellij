package a11y.init

import a11y.service.A11yService
import com.intellij.openapi.application.PreloadingActivity
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.progress.ProgressIndicator


class PreloadA11yExecutable : PreloadingActivity() {
    private var a11yService = ServiceManager.getService(A11yService::class.java)

    override fun preload(indicator: ProgressIndicator) {
        if (!a11yService.isInitialized()) {
            a11yService.initA11yFixerUtil()
        }
    }
}
