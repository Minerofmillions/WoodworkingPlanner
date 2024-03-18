package minerofmillions.woodwork3.state

import com.arkivanov.decompose.value.MutableValue
import minerofmillions.woodwork3.Strip

interface StripViewComponent {
    val strip: MutableValue<Strip>
}
