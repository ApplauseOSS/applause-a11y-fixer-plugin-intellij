package a11y.utils

import a11y.A11yBundle
import com.intellij.notification.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowId

object Notify {
    private val NOTIFICATION_GROUP = NotificationGroup(
        A11yBundle.message("action.group"),
        NotificationDisplayType.BALLOON,
        true,
        ToolWindowId.PROJECT_VIEW
    )

    fun show(
        project: Project,
        title: String,
        content: String,
        type: NotificationType
    ) {
        show(project, title, content, NOTIFICATION_GROUP, type, null)
    }

    fun show(
        project: Project,
        title: String,
        content: String,
        group: NotificationGroup,
        type: NotificationType,
        listener: NotificationListener?
    ) {
        val notification = group.createNotification(title, content, type, listener)
        Notifications.Bus.notify(notification, project)
    }
}
