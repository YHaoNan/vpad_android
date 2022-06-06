package top.yudoge.vpad.toplevel

import java.lang.RuntimeException

class RequirementNotFulfilledException(message: String) : RuntimeException(message)

/**
 * This function check if the value in range [min, max]
 *
 *  Requirement fulfilled when min <= value <= max
 */
fun Int.requireInRange(
    min: Int,
    max: Int,
    message: String = "Requirement is not fulfilled. Int is must >= ${min}, and <= ${max}, but got ${this}"
) {
    if (this < min || this > max)
        throw RequirementNotFulfilledException(message)
}


