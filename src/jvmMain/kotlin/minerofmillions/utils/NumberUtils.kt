package minerofmillions.utils

import kotlin.math.absoluteValue

private const val DOUBLE_EPSILON = 1e-5
private const val FLOAT_EPSILON = 1e-5f

fun Double.approximatelyEquals(other: Double) = minus(other).absoluteValue < DOUBLE_EPSILON
fun Float.approximatelyEquals(other: Float) = minus(other).absoluteValue < FLOAT_EPSILON