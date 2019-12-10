package a11y.utils

import a11y.A11yBundle
import com.intellij.notification.*
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowId

object Notify {
    val A11Y_GROUP = NotificationGroup(
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
        val notification = Notification(A11yBundle.message("action.group"), title, content, type)
        Notifications.Bus.notify(notification, project)
    }

    fun show(
        project: Project,
        title: String,
        content: String,
        actions: Collection<AnAction>?,
        type: NotificationType
    ) {
        val notification = Notification(A11yBundle.message("action.group"), title, content, type)
        actions?.forEach { notification.addAction(it) }
        Notifications.Bus.notify(notification, project)
    }


    fun show(
        project: Project,
        title: String,
        content: String,
        group: NotificationGroup,
        actions: Collection<AnAction>?,
        type: NotificationType
    ) {
        groupShow(project, title, content, group, type, actions, null)
    }

    private fun groupShow(
        project: Project,
        title: String,
        content: String,
        group: NotificationGroup,
        type: NotificationType,
        actions: Collection<AnAction>?,
        listener: NotificationListener?
    ) {
        val notification = group.createNotification(title, content, type, listener)
        actions?.forEach { notification.addAction(it) }
        Notifications.Bus.notify(notification, project)
    }
}
