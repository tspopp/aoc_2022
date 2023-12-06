package day5_23

import org.junit.jupiter.api.Test
import java.io.File


data class Rule(val destinationStart: Long, val sourceRangeStart: Long, val length: Long) {


}

data class Converter(var ruleset: List<Rule>) {


}

fun Converter.mapForward(value: Long): Long {
    for (rule in ruleset) {
        val end = rule.sourceRangeStart + rule.length;
        val range = rule.sourceRangeStart..end
        if (value in range) {
            val diff = rule.sourceRangeStart - rule.destinationStart;
            return value - diff
        }
    }
    // unmapped
    return value;
}

fun Converter.mapBackward(value: Long): Long{
    for (rule in ruleset) {
        val end = rule.destinationStart + rule.length;
        val range = rule.destinationStart..end
        if (value in range) {
            val diff = rule.destinationStart - rule.sourceRangeStart;
            return value - diff
        }
    }
    return value;
}

class Day5 {

    @Test
    fun sampleSilver() {
        readPuzzleInput("sample")

        // 35
    }

    @Test
    fun sampleGold() {
    }

    @Test
    fun silver() {
        readPuzzleInput("input")

        // 282277027
    }

    @Test
    fun gold() {
        //11554136  TOO HIGH
        //11554135
    }
}

fun createConverter(dataset: String): Converter {

    return Converter(
        dataset.substringAfter("\n").split(" ").map { it.split("\n") }.flatten()
            .windowed(3, 3)
            .map { (dest, src, len) -> Rule(dest.toLong(), src.toLong(), len.toLong()) }.toList()
    );
}


fun readPuzzleInput(filename: String) {
    val lines = File("src/test/kotlin/day5_23/$filename").readText()

    val dataset = lines.split("\n\n");

    val seeds = dataset[0].substringAfter(":").split(" ").map { it.toLongOrNull() }.filterNotNull().toSet();

    val seedToSoil = createConverter(dataset[1]);
    val soilToFertilizer = createConverter(dataset[2]);
    val fertilizerToWater = createConverter(dataset[3]);
    val waterToLight = createConverter(dataset[4]);
    val lightToTemperature = createConverter(dataset[5]);
    val temperatureToHumidity = createConverter(dataset[6]);
    val humidityToLocation = createConverter(dataset[7]);

    val solution = seeds.map { seedToSoil.mapForward(it) }.map { soilToFertilizer.mapForward(it) }.map { fertilizerToWater.mapForward(it) }
        .map { waterToLight.mapForward(it) }.map { lightToTemperature.mapForward(it) }.map { temperatureToHumidity.mapForward(it) }
        .map { humidityToLocation.mapForward(it) }.min();

    println(solution);

    val ranges = seeds.windowed(2, 2).map { (start, length) -> start..start + length }.toList();

    for (l in 0..Long.MAX_VALUE) {
        val humidity = humidityToLocation.mapBackward(l);
        val temperate = temperatureToHumidity.mapBackward(humidity);
        val light = lightToTemperature.mapBackward(temperate);
        val water = waterToLight.mapBackward(light);
        val fertilizer = fertilizerToWater.mapBackward(water);
        val soil = soilToFertilizer.mapBackward(fertilizer);
        val seed = seedToSoil.mapBackward(soil);
        val valid = ranges.any { it.contains(seed) };

        if(valid){
            println("local minimum is: " + l);
            break;
        }
    }
}