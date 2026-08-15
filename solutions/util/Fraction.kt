package util

import java.math.BigInteger

class Fraction private constructor(val numerator: BigInteger, val denominator: BigInteger) : Comparable<Fraction> {

    constructor(value: Long) : this(BigInteger.valueOf(value), BigInteger.ONE)

    val sign: Int get() = numerator.signum()
    val isInteger: Boolean get() = denominator == BigInteger.ONE

    operator fun plus(other: Fraction): Fraction =
        of(numerator * other.denominator + other.numerator * denominator, denominator * other.denominator)

    operator fun minus(other: Fraction): Fraction =
        of(numerator * other.denominator - other.numerator * denominator, denominator * other.denominator)

    operator fun times(other: Fraction): Fraction =
        of(numerator * other.numerator, denominator * other.denominator)

    operator fun div(other: Fraction): Fraction =
        of(numerator * other.denominator, denominator * other.numerator)

    operator fun unaryMinus(): Fraction = Fraction(numerator.negate(), denominator)

    override fun compareTo(other: Fraction): Int = (numerator * other.denominator).compareTo(other.numerator * denominator)

    override fun equals(other: Any?): Boolean =
        other is Fraction && numerator == other.numerator && denominator == other.denominator

    override fun hashCode(): Int = 31 * numerator.hashCode() + denominator.hashCode()

    override fun toString(): String = if (isInteger) "$numerator" else "$numerator/$denominator"

    companion object {
        fun of(numerator: Long, denominator: Long): Fraction =
            of(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator))

        fun of(numerator: BigInteger, denominator: BigInteger): Fraction {
            require(denominator.signum() != 0) { "Denominator must not be zero." }
            val divisor = numerator.gcd(denominator).let { if (denominator.signum() < 0) it.negate() else it }
            return Fraction(numerator / divisor, denominator / divisor)
        }
    }
}
