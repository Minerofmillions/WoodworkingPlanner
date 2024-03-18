package minerofmillions.woodwork3.state.defaults

import com.arkivanov.decompose.ComponentContext
import minerofmillions.woodwork3.Strip
import minerofmillions.woodwork3.state.LayoutsViewComponent

class DefaultLayoutsViewComponent(override val strip: Strip, context: ComponentContext) : LayoutsViewComponent,
    ComponentContext by context {
}