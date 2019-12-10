package a11y

import com.intellij.CommonBundle
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.util.*

object A11yBundle {

    @NonNls
    const val BUNDLE_NAME = "messages.A11yBundle"

    private val BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME)

    fun message(@PropertyKey(resourceBundle = BUNDLE_NAME) key: String, vararg params: Any): String {
        return CommonBundle.message(BUNDLE, key, *params)
    }

}
