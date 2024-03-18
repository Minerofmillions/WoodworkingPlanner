package minerofmillions.woodwork3.state

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import minerofmillions.woodwork3.Strip

interface AppComponent {
    val stack: Value<ChildStack<*, Child>>

    sealed class Child {
        data class StripViewChild(val component: StripViewComponent) : Child()
        data class LayoutsViewChild(val component: LayoutsViewComponent) : Child()
    }

    @Parcelize
    sealed class Config : Parcelable {
        object StripView : Config()
        class LayoutsView(val strip: Strip) : Config()
    }
}