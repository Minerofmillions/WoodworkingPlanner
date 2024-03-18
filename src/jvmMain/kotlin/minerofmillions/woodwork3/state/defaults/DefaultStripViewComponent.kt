package minerofmillions.woodwork3.state.defaults

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import minerofmillions.woodwork3.Strip
import minerofmillions.woodwork3.state.StripViewComponent

internal class DefaultStripViewComponent(context: ComponentContext) : StripViewComponent, ComponentContext by context {
    override val strip: MutableValue<Strip> = MutableValue(emptyList())
}