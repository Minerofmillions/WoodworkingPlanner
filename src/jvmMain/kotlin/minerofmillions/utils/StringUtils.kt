package minerofmillions.utils

fun String.rotation(rotationIndex: Int) = substring(rotationIndex) + substring(0 until rotationIndex)