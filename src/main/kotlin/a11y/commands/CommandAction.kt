package a11y.commands

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project

abstract class CommandAction<T>(
    private val project: Project
) {

    @Throws(Throwable::class)
    protected abstract fun compute(): T

    @Throws(Throwable::class)
    fun execute(): T {
        return WriteCommandAction.writeCommandAction(project).compute<T, Throwable> { this@CommandAction.compute() }
    }
}
