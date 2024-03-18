package minerofmillions.woodwork3.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import minerofmillions.woodwork3.state.StripViewComponent

@Composable
fun StripViewContent(component: StripViewComponent) {
    val strip by component.strip.subscribeAsState()
}