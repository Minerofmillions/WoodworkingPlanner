package minerofmillions.woodwork3.state.defaults

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import minerofmillions.woodwork3.state.AppComponent

internal class DefaultAppComponent(componentContext: ComponentContext) : AppComponent,
    ComponentContext by componentContext {
    private val navigation = StackNavigation<AppComponent.Config>()
    override val stack = childStack(
        source = navigation,
        initialConfiguration = AppComponent.Config.StripView,
        handleBackButton = true,
        childFactory = ::child
    )

    private fun child(config: AppComponent.Config, componentContext: ComponentContext): AppComponent.Child =
        when (config) {
            is AppComponent.Config.StripView -> AppComponent.Child.StripViewChild(stripComponent(componentContext))
            is AppComponent.Config.LayoutsView -> AppComponent.Child.LayoutsViewChild(
                layoutsComponent(
                    componentContext,
                    config
                )
            )
        }

    private fun stripComponent(context: ComponentContext) = DefaultStripViewComponent(context)

    private fun layoutsComponent(context: ComponentContext, config: AppComponent.Config.LayoutsView) =
        DefaultLayoutsViewComponent(config.strip, context)
}