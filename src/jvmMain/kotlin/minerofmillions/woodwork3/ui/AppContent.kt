package minerofmillions.woodwork3.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import minerofmillions.woodwork3.state.AppComponent

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun AppContent(component: AppComponent, modifier: Modifier = Modifier) {
    Children(stack = component.stack, modifier = modifier, animation = stackAnimation(fade() + scale())) {
        when (val child = it.instance) {
            is AppComponent.Child.StripViewChild -> StripViewContent(child.component)
            is AppComponent.Child.LayoutsViewChild -> TODO()
        }
    }
}