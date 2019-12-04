package a11y

import com.intellij.CommonBundle
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.util.*

object A11yBundle {

    @NonNls
    const val BUNDLE_NAME = "messages.A11yBundle"

    private val BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME)


    /**
     * Available syntax list.
     */
    enum class Syntax {
        GLOB, REGEXP;

        override fun toString(): String {
            return super.toString().toLowerCase()
        }

        companion object {

            @NonNls
            private val KEY = "syntax:"

            fun find(name: String?): Syntax? {
                if (name == null) {
                    return null
                }
                return try {
                    Syntax.valueOf(name.toUpperCase())
                } catch (iae: IllegalArgumentException) {
                    null
                }

            }
        }
    }

    fun message(@PropertyKey(resourceBundle = BUNDLE_NAME) key: String, vararg params: Any): String {
        return CommonBundle.message(BUNDLE, key, *params)
    }

}
