package aockt.y2023

import com.github.jonpeterson.kotlin.ranges.LongRangeSet
import io.github.jadarma.aockt.core.Solution

object Y2023D05 : Solution {
    private val inputRegex = """(?s)seeds:\s+(?<seeds>(\d+\s+)+)\n+(?<rest>.*)""".toRegex()
    private val mappingRegex = """(\w+)-to-(\w+) map:\s*\n((\d+\s+\d+\s+\d+\s*\n)+)""".toRegex()


    interface LongValueHolder<T : LongValueHolder<T>> {
        fun getLongValue(): Long
        fun fromLong(value: Long): T
    }

    @JvmInline
    value class Seed(private val value: Long) : Comparable<Seed>, LongValueHolder<Seed> {
        override fun compareTo(other: Seed): Int = value.compareTo(other.value)
        override fun getLongValue(): Long = value
        override fun fromLong(value: Long): Seed = Seed(value)
    }

    @JvmInline
    value class Soil(private val value: Long) : Comparable<Soil>, LongValueHolder<Soil> {
        override fun compareTo(other: Soil): Int = value.compareTo(other.value)
        override fun getLongValue(): Long = value
        override fun fromLong(value: Long): Soil = Soil(value)
    }

    @JvmInline
    value class Fertilizer(private val value: Long) : Comparable<Fertilizer>, LongValueHolder<Fertilizer> {
        override fun compareTo(other: Fertilizer): Int = value.compareTo(other.value)
        override fun getLongValue(): Long = value
        override fun fromLong(value: Long): Fertilizer = Fertilizer(value)
    }

    @JvmInline
    value class Water(private val value: Long) : Comparable<Water>, LongValueHolder<Water> {
        override fun compareTo(other: Water): Int = value.compareTo(other.value)
        override fun getLongValue(): Long = value
        override fun fromLong(value: Long): Water = Water(value)
    }

    @JvmInline
    value class Light(private val value: Long) : Comparable<Light>, LongValueHolder<Light> {
        override fun compareTo(other: Light): Int = value.compareTo(other.value)
        override fun getLongValue(): Long = value
        override fun fromLong(value: Long): Light = Light(value)
    }

    @JvmInline
    value class Temperature(private val value: Long) : Comparable<Temperature>, LongValueHolder<Temperature> {
        override fun compareTo(other: Temperature): Int = value.compareTo(other.value)
        override fun getLongValue(): Long = value
        override fun fromLong(value: Long): Temperature = Temperature(value)
    }

    @JvmInline
    value class Humidity(private val value: Long) : Comparable<Humidity>, LongValueHolder<Humidity> {
        override fun compareTo(other: Humidity): Int = value.compareTo(other.value)
        override fun getLongValue(): Long = value
        override fun fromLong(value: Long): Humidity = Humidity(value)
    }

    @JvmInline
    value class Location(private val value: Long) : Comparable<Location>, LongValueHolder<Location> {
        override fun compareTo(other: Location): Int = value.compareTo(other.value)
        override fun getLongValue(): Long = value
        override fun fromLong(value: Long): Location = Location(value)
    }

    data class Almanac(
        val seeds: List<Seed>,
        val seedToSoil: Mapping<Seed, Soil>,
        val soilToFertilizer: Mapping<Soil, Fertilizer>,
        val fertilizerToWater: Mapping<Fertilizer, Water>,
        val waterToLight: Mapping<Water, Light>,
        val lightToTemperature: Mapping<Light, Temperature>,
        val temperatureToHumidity: Mapping<Temperature, Humidity>,
        val humidityToLocation: Mapping<Humidity, Location>
    ) {
        private val completeMap = seedToSoil.andThen(soilToFertilizer).andThen(fertilizerToWater).andThen(waterToLight)
            .andThen(lightToTemperature).andThen(temperatureToHumidity).andThen(humidityToLocation)
        private val seedRanges: LongRangeSet

        init {
            val ranges = LongRangeSet()
            seeds.chunked(2).map { it.first().getLongValue()..(it.first().getLongValue() + it.last().getLongValue()) }
                .forEach { ranges.add(it) }
            this.seedRanges = ranges
        }

        fun seedToLocation(seed: Seed): Location? = completeMap.map(seed)

        fun locationInSeeds(location: Location): Boolean {
            val seed = completeMap.reverse(location) ?: return false
            return seedRanges.containsValue(seed.getLongValue())
        }
    }

    interface Mapping<S, T> where S : Comparable<S>, S : LongValueHolder<S>, T : LongValueHolder<T>, T : Comparable<T> {
        operator fun contains(value: S): Boolean
        fun map(source: S): T?

        fun reverse(source: T): S?

        fun <U> andThen(map: Mapping<T, U>): Mapping<S, U> where U : Comparable<U>, U : LongValueHolder<U> {
            return CombinedMapping(this, map)
        }
    }

    class DirectMapping<S, T>(
        private val range: ClosedRange<S>, private val start: T
    ) : Mapping<S, T> where S : Comparable<S>, S : LongValueHolder<S>, T : LongValueHolder<T>, T : Comparable<T> {
        private val reverseRange =
            start.rangeTo(genNew(start.getLongValue() + (range.endInclusive.getLongValue() - range.start.getLongValue())))

        override operator fun contains(value: S): Boolean = range.contains(value)
        override fun map(source: S): T? {
            if (range.contains(source)) {
                return start.fromLong(source.getLongValue() - range.start.getLongValue() + start.getLongValue())
            }
            return null
        }

        fun genNew(value: Long): T = start.fromLong(value)

        fun genSource(value: Long): S = range.start.fromLong(value)
        override fun reverse(source: T): S? {
            if (reverseRange.contains(source)) {
                return range.start.fromLong(source.getLongValue() - reverseRange.start.getLongValue() + range.start.getLongValue())
            }
            return null
        }
    }

    class ListMapping<S, T>(private val mappings: List<DirectMapping<S, T>>) :
        Mapping<S, T> where S : Comparable<S>, S : LongValueHolder<S>, T : LongValueHolder<T>, T : Comparable<T> {
        override fun contains(value: S): Boolean {
            return mappings.map { directMapping -> value in directMapping }.any { true }
        }

        override fun map(source: S): T {
            return mappings.firstNotNullOfOrNull { it.map(source) } ?: mappings[0].genNew(source.getLongValue())
        }

        override fun reverse(source: T): S {
            return mappings.firstNotNullOfOrNull { it.reverse(source) } ?: mappings[0].genSource(source.getLongValue())
        }

    }

    class CombinedMapping<S, T, U>(
        private val first: Mapping<S, T>, private val second: Mapping<T, U>
    ) : Mapping<S, U> where S : Comparable<S>, S : LongValueHolder<S>, T : LongValueHolder<T>, T : Comparable<T>, U : Comparable<U>, U : LongValueHolder<U> {

        override operator fun contains(value: S): Boolean =
            first.contains(value) and (first.map(value)?.let { second.contains(it) } ?: false)

        override fun map(source: S): U? = first.map(source)?.let { second.map(it) }
        override fun reverse(source: U): S? = second.reverse(source)?.let { first.reverse(it) }
    }


    override fun partOne(input: String): Long {
        val almanac = parseInput(input)
        return almanac.seeds.mapNotNull { almanac.seedToLocation(it) }.map { it.getLongValue() }.minOf { it }
    }

    override fun partTwo(input: String): Long {
        val almanac = parseInput(input)

        return generateSequence(1L) { it + 1 }.mapNotNull { if (almanac.locationInSeeds(Location(it))) (it) else null }
            .first()

    }

    private fun parseInput(input: String): Almanac {
        val data = inputRegex.matchEntire(input)

        val seeds = data!!.groups["seeds"]!!.value.trim().split(" ").map(String::toLong).map { Seed(it) }

        val mappingsString = data.groups["rest"]!!.value

        val mappings = mappingRegex.findAll(mappingsString)

        var seed: ListMapping<Seed, Soil>? = null
        var soil: ListMapping<Soil, Fertilizer>? = null
        var fertilizer: ListMapping<Fertilizer, Water>? = null
        var water: ListMapping<Water, Light>? = null
        var light: ListMapping<Light, Temperature>? = null
        var temperature: ListMapping<Temperature, Humidity>? = null
        var humidity: ListMapping<Humidity, Location>? = null
        mappings.forEach { matchResult ->
            val (sourceCategory, destCategory, mapString) = matchResult.destructured

            val mapLines = mapString.trim().lines()
            val values = mapLines.map { line ->
                line.trim().split(" ").map(String::toLong)
            }
            when (sourceCategory) {
                "seed" -> seed =
                    ListMapping(values.map { DirectMapping(Seed(it[1]).rangeTo(Seed(it[1] + it[2])), Soil(it[0])) })

                "soil" -> soil = ListMapping(values.map {
                    DirectMapping(
                        Soil(it[1]).rangeTo(Soil(it[1] + it[2])), Fertilizer(it[0])
                    )
                })

                "fertilizer" -> fertilizer = ListMapping(values.map {
                    DirectMapping(
                        Fertilizer(it[1]).rangeTo(Fertilizer(it[1] + it[2])), Water(it[0])
                    )
                })

                "water" -> water =
                    ListMapping(values.map { DirectMapping(Water(it[1]).rangeTo(Water(it[1] + it[2])), Light(it[0])) })

                "light" -> light = ListMapping(values.map {
                    DirectMapping(
                        Light(it[1]).rangeTo(Light(it[1] + it[2])), Temperature(it[0])
                    )
                })

                "temperature" -> temperature = ListMapping(values.map {
                    DirectMapping(
                        Temperature(it[1]).rangeTo(Temperature(it[1] + it[2])), Humidity(it[0])
                    )
                })

                "humidity" -> humidity = ListMapping(values.map {
                    DirectMapping(
                        Humidity(it[1]).rangeTo(Humidity(it[1] + it[2])), Location(it[0])
                    )
                })

                else -> {
                    throw NotImplementedError("Missing Mapping Definition")
                }
            }
        }

        return Almanac(seeds, seed!!, soil!!, fertilizer!!, water!!, light!!, temperature!!, humidity!!)
    }


}


