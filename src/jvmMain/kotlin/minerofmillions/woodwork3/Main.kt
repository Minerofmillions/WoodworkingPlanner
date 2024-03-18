package minerofmillions.woodwork3

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.lifecycle.LifecycleController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import minerofmillions.utils.biMapOf
import minerofmillions.woodwork3.state.AppComponent
import minerofmillions.woodwork3.state.defaults.DefaultAppComponent
import minerofmillions.woodwork3.ui.AppContent

val predefinedColors = biMapOf(
    "Cherry" to Color(0xff6a1616),
    "Walnut" to Color(0xff773f1a),
    "Oak" to Color(0xff806517),
    "Maple" to Color(0xffded0c1),
    "Beech" to Color(0xffc5c993),
    "Teak" to Color(0xffc29467),
    "Ash" to Color(0xffab9f8d)
)

fun main() {
    val lifecycle = LifecycleRegistry()
    val root: AppComponent = DefaultAppComponent(DefaultComponentContext(lifecycle))

    application {
        val windowState = rememberWindowState(placement = WindowPlacement.Maximized)

        LifecycleController(lifecycle, windowState)

        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = "Cutting Board Planner",
            icon = painterResource("icon.png"),
            content = {
                MaterialTheme {
                    AppContent(root)
                }
            }
        )
    }
}