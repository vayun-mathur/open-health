package com.health.openhealth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.BasalBodyTemperatureRecord
import androidx.health.connect.client.records.BasalMetabolicRateRecord
import androidx.health.connect.client.records.BloodGlucoseRecord
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyFatRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.BodyWaterMassRecord
import androidx.health.connect.client.records.BoneMassRecord
import androidx.health.connect.client.records.CervicalMucusRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ElevationGainedRecord
import androidx.health.connect.client.records.FloorsClimbedRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HeartRateVariabilityRmssdRecord
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.HydrationRecord
import androidx.health.connect.client.records.IntermenstrualBleedingRecord
import androidx.health.connect.client.records.LeanBodyMassRecord
import androidx.health.connect.client.records.MenstruationFlowRecord
import androidx.health.connect.client.records.MenstruationPeriodRecord
import androidx.health.connect.client.records.NutritionRecord
import androidx.health.connect.client.records.OvulationTestRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.PlannedExerciseSessionRecord
import androidx.health.connect.client.records.PowerRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.RespiratoryRateRecord
import androidx.health.connect.client.records.RestingHeartRateRecord
import androidx.health.connect.client.records.SexualActivityRecord
import androidx.health.connect.client.records.SkinTemperatureRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.records.Vo2MaxRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.records.WheelchairPushesRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass

val TIME_ZONE: ZoneOffset = ZoneOffset.ofHours(-8)

@Composable
fun RecordView(name: String, endTime: Instant?, getLastString: AnnotatedString.Builder.(SpanStyle, SpanStyle) -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(24.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(name, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if(endTime != null)
                        Text(LocalDateTime.ofInstant(endTime, TIME_ZONE)!!.toRecentTimeString())
                    Spacer(Modifier.width(8.dp))
                    Icon(painterResource(R.drawable.baseline_arrow_forward_24), null, Modifier.size(16.dp))
                }
            }
            Spacer(Modifier.height(16.dp))
            if(endTime != null) {
                Text(AnnotatedString.Builder().apply{getLastString(MaterialTheme.typography.headlineMedium.toSpanStyle(), MaterialTheme.typography.bodyLarge.toSpanStyle())}.toAnnotatedString())
            } else {
                Text("No data", style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}

private fun LocalDateTime.toRecentTimeString(): String {
    // if more than a year ago
    if(this.isBefore(LocalDateTime.now().minusYears(1)))
        return this.format(DateTimeFormatter.ofPattern("MM dd, yyyy"))
    // if more than 2 days ago
    if(this.isBefore(LocalDateTime.now().minusDays(2)))
        return this.format(DateTimeFormatter.ofPattern("MMM dd"))
    // if more than 1 day ago
    if(this.isBefore(LocalDateTime.now().minusDays(1)))
        return "Yesterday"

    return this.format(DateTimeFormatter.ofPattern("HH:mm"))
}

@Composable
fun HeartRateViewData(appDatabase: AppDatabase) = BasicRecordViewData(appDatabase, "Heart Rate", "bpm", HeartRateRecord::class)

@Composable
fun StepsViewData(appDatabase: AppDatabase) = BasicRecordViewData(appDatabase, "Steps", "steps", StepsRecord::class)

@Composable
fun ActiveCaloriesBurnedViewData(appDatabase: AppDatabase) = BasicRecordViewData(appDatabase, "Active Calories Burned", "kcal", ActiveCaloriesBurnedRecord::class)

@Composable
fun BasalBodyTemperatureViewData(appDatabase: AppDatabase) = BasicRecordViewData(appDatabase, "Basal Body Temperature", "C", BasalBodyTemperatureRecord::class)

@Composable
fun BasalMetabolicRateViewData(appDatabase: AppDatabase) = BasicRecordViewData(appDatabase, "Basal Metabolic Rate", "kcal/h", BasalMetabolicRateRecord::class)

@Composable
fun BloodGlucoseViewData(appDatabase: AppDatabase) = BasicRecordViewData(appDatabase, "Blood Glucose", "mg/dL", BloodGlucoseRecord::class)

@Composable
fun BloodPressureViewData(appDatabase: AppDatabase) = BasicRecordViewData(appDatabase, "Blood Pressure", "mmHg", BloodPressureRecord::class)

@Composable
fun BodyFatViewData(appDatabase: AppDatabase) = BasicRecordViewData(appDatabase, "Body Fat", "%", BodyFatRecord::class)

@Composable
fun BodyTemperatureViewData(appDatabase: AppDatabase) = BasicRecordViewData(appDatabase, "Body Temperature", "C", BodyTemperatureRecord::class)

@Composable
fun BodyWaterMassViewData(appDatabase: AppDatabase) = BasicRecordViewData(appDatabase, "Body Water Mass", "g", BodyWaterMassRecord::class)

@Composable
fun BoneMassViewData(appDatabase: AppDatabase) = BasicRecordViewData(appDatabase, "Bone Mass", "g", BoneMassRecord::class)

@Composable
fun DistanceViewData(appDatabase: AppDatabase) = BasicRecordViewData(appDatabase, "Distance", "m", DistanceRecord::class)

@Composable
fun ElevationGainViewData(appDatabase: AppDatabase) = BasicRecordViewData(appDatabase, "Elevation Gain", "m", ElevationGainedRecord::class)

@Composable
fun FloorsClimbedViewData(appDatabase: AppDatabase) = BasicRecordViewData(appDatabase, "Floors Climbed", "steps", FloorsClimbedRecord::class)

@Composable
fun HeartRateVariabilityViewData(appDatabase: AppDatabase) = BasicRecordViewData(appDatabase, "Heart Rate Variability", "bpm", HeartRateVariabilityRmssdRecord::class)

@Composable
fun HeightViewData(appDatabase: AppDatabase) = BasicRecordViewData(appDatabase, "Height", "cm", HeightRecord::class)

@Composable
fun HydrationViewData(appDatabase: AppDatabase) = BasicRecordViewData(appDatabase, "Hydration", "mL", HydrationRecord::class)

@Composable
fun LeanBodyMassViewData(appDatabase: AppDatabase) = BasicRecordViewData(appDatabase, "Lean Body Mass", "g", LeanBodyMassRecord::class)

@Composable
fun MenstruationPeriodViewData(appDatabase: AppDatabase) = BasicRecordViewData(appDatabase, "Menstruation Period", "days", MenstruationPeriodRecord::class)

@Composable
fun OxygenSaturationViewData(appDatabase: AppDatabase) = BasicRecordViewData(appDatabase, "Oxygen Saturation", "%", OxygenSaturationRecord::class)

@Composable
fun PowerViewData(appDatabase: AppDatabase) = BasicRecordViewData(appDatabase, "Power", "W", PowerRecord::class)

@Composable
fun RespiratoryRateViewData(appDatabase: AppDatabase) = BasicRecordViewData(appDatabase, "Respiratory Rate", "bpm", RespiratoryRateRecord::class)

@Composable
fun RestingHeartRateViewData(appDatabase: AppDatabase) = BasicRecordViewData(appDatabase, "Resting Heart Rate", "bpm", RestingHeartRateRecord::class)

@Composable
fun SpeedViewData(appDatabase: AppDatabase) = BasicRecordViewData(appDatabase, "Speed", "m/s", SpeedRecord::class)

@Composable
fun TotalCaloriesBurnedViewData(appDatabase: AppDatabase) = BasicRecordViewData(appDatabase, "Total Calories Burned", "kcal", TotalCaloriesBurnedRecord::class)

@Composable
fun VO2MaxViewData(appDatabase: AppDatabase) = BasicRecordViewData(appDatabase, "VO2 Max", "L/kg/min", Vo2MaxRecord::class)

@Composable
fun WheelchairPushesViewData(appDatabase: AppDatabase) = BasicRecordViewData(appDatabase, "Wheelchair Pushes", "steps", WheelchairPushesRecord::class)

@Composable
fun WeightViewData(appDatabase: AppDatabase) = BasicRecordViewData(appDatabase, "Weight", "kg", WeightRecord::class)

@Composable
fun BasicRecordView(name: String, unit: String, value: String?, lastTime: Instant?) {
    RecordView(name, lastTime) { large, small ->
        withStyle(large) {
            append(value)
        }
        withStyle(small) {
            append(" $unit")
        }
    }
}

fun getValueAndTime(record: Record): Pair<String?, Instant?> {
    when(record) {
        is HeartRateRecord ->
            return Pair(record.samples.last().beatsPerMinute.toString(), record.samples.last().time)

        is StepsRecord ->
            return Pair(record.count.toString(), record.endTime)

        is ActiveCaloriesBurnedRecord ->
            return Pair(record.energy.inKilocalories.toString(), record.endTime)

        is BasalBodyTemperatureRecord ->
            return Pair(record.temperature.inCelsius.toString(), record.time)

        is BasalMetabolicRateRecord ->
            return Pair(record.basalMetabolicRate.inKilocaloriesPerDay.toString(), record.time)

        is BloodGlucoseRecord ->
            return Pair(record.level.inMilligramsPerDeciliter.toString(), record.time)

        is BloodPressureRecord ->
            return Pair(
                "${record.diastolic.inMillimetersOfMercury}/${record.systolic.inMillimetersOfMercury}",
                record.time
            )

        is BodyFatRecord ->
            return Pair(record.percentage.toString(), record.time)

        is BodyTemperatureRecord ->
            return Pair(record.temperature.inCelsius.toString(), record.time)

        is BodyWaterMassRecord ->
            return Pair(record.mass.inGrams.toString(), record.time)

        is BoneMassRecord ->
            return Pair(record.mass.inGrams.toString(), record.time)

        is CervicalMucusRecord -> {}
        //TODO: this cannot be a default record format (it is categories instead)
        is DistanceRecord ->
            return Pair(record.distance.inMeters.toString(), record.endTime)

        is ElevationGainedRecord ->
            return Pair(record.elevation.inMeters.toString(), record.endTime)

        is FloorsClimbedRecord ->
            return Pair(record.floors.toString(), record.endTime)

        is HeartRateVariabilityRmssdRecord ->
            return Pair(record.heartRateVariabilityMillis.toString(), record.time)

        is HeightRecord ->
            return Pair((record.height.inMeters / 100).toString(), record.time)

        is HydrationRecord ->
            return Pair(record.volume.inMilliliters.toString(), record.endTime)

        is IntermenstrualBleedingRecord -> {}
        //TODO: this cannot be a default record format (it is categories instead)
        is LeanBodyMassRecord ->
            return Pair(record.mass.inGrams.toString(), record.time)

        is MenstruationPeriodRecord ->
            return Pair(
                Duration.between(record.startTime, record.endTime).toDays().toString(),
                record.endTime
            )

        is MenstruationFlowRecord -> {}
        //TODO: this cannot be a default record format (it is categories instead)
        is OvulationTestRecord -> {}
        //TODO: this cannot be a default record format (it is categories instead)
        is OxygenSaturationRecord ->
            return Pair(record.percentage.toString(), record.time)

        is PlannedExerciseSessionRecord -> {}
        //TODO: this cannot be a default record format (this needs to be custom implemented)
        is PowerRecord ->
            return Pair(record.samples.last().power.inWatts.toString(), record.endTime)

        is RespiratoryRateRecord ->
            return Pair(record.rate.toString(), record.time)

        is RestingHeartRateRecord ->
            return Pair(record.beatsPerMinute.toString(), record.time)

        is SexualActivityRecord -> {}
        //TODO: this cannot be a default record format (it is categories instead)
        is SleepSessionRecord -> {}
        //TODO: this cannot be a default record format (this needs to be custom implemented)
        is SpeedRecord ->
            return Pair(record.samples.last().speed.inMetersPerSecond.toString(), record.endTime)

        is TotalCaloriesBurnedRecord ->
            return Pair(record.energy.inKilocalories.toString(), record.endTime)

        is Vo2MaxRecord ->
            return Pair(record.vo2MillilitersPerMinuteKilogram.toString(), record.time)

        is WheelchairPushesRecord ->
            return Pair(record.count.toString(), record.endTime)

        is WeightRecord ->
            return Pair(record.weight.inKilograms.toString(), record.time)

        is SkinTemperatureRecord -> {}
        //TODO: this cannot be a default record format (this needs to be custom implemented probably)
    }

    return Pair(null, null)
}

fun inHealthScope(content: suspend () -> Unit) {
    CoroutineScope(Dispatchers.IO).launch { content() }
}

@Composable
fun <T : Any> BasicRecordViewData(appDatabase: AppDatabase, name: String, unit: String, clazz: KClass<T>) {
    var value by remember { mutableStateOf<String?>(null) }
    var lastTime by remember { mutableStateOf<Instant?>(null) }
    LaunchedEffect(Unit) {
        inHealthScope {
            val records = appDatabase.recordDataDao().getAllOfClass(
                clazz
            )
            if (records.isEmpty()) return@inHealthScope
            val record = gson.fromJson(
                records.last().originalData,
                clazz.java
            ) as Record
            val r = getValueAndTime(record)
            value = r.first
            lastTime = r.second
        }
    }
    BasicRecordView(name, unit, value, lastTime)
}

@Composable
fun NutritionGenericView(name: String, quantity: Double, unit: String, lastTime: Instant) {
    RecordView(name, lastTime) { large, small ->
        withStyle(large) { append("%.3f".format(quantity)) }
        withStyle(small) { append(" $unit") }
    }
}

@Composable
fun NutritionGenericViewData(hc: AppDatabase, name: String, unit: String, getQuantity: (NutritionRecord) -> Double) {
    var total by remember { mutableDoubleStateOf(0.0) }
    LaunchedEffect(Unit) {
        inHealthScope {
            val records = hc.recordDataDao().getAllOfClass(NutritionRecord::class)
            var temp = 0.0
            records.forEach {
                val record = gson.fromJson(it.originalData, NutritionRecord::class.java)
                println(record)
                if(record.endTime.isAfter(Instant.now().minusSeconds(60*60*24*1)))
                    temp += getQuantity(record)
            }
            total = temp
        }
    }
    NutritionGenericView(name, total, unit, Instant.now())
}


// Biotin (commonly measured in micrograms)
@Composable
fun NutritionBiotinViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Biotin", "µg") {
        it.biotin?.inMicrograms ?: 0.0
    }

// Caffeine (commonly in milligrams)
@Composable
fun NutritionCaffeineViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Caffeine", "mg") {
        it.caffeine?.inMilligrams ?: 0.0
    }

// Calcium (commonly in milligrams)
@Composable
fun NutritionCalciumViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Calcium", "mg") {
        it.calcium?.inMilligrams ?: 0.0
    }

// Energy (commonly in kilocalories)
@Composable
fun NutritionEnergyViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Energy", "kcal") {
        it.energy?.inKilocalories ?: 0.0
    }

// Energy from Fat (commonly in kilocalories)
@Composable
fun NutritionEnergyFromFatViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Energy From Fat", "kcal") {
        it.energyFromFat?.inKilocalories ?: 0.0
    }

// Chloride (commonly in milligrams)
@Composable
fun NutritionChlorideViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Chloride", "mg") {
        it.chloride?.inMilligrams ?: 0.0
    }

// Cholesterol (commonly in milligrams)
@Composable
fun NutritionCholesterolViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Cholesterol", "mg") {
        it.cholesterol?.inMilligrams ?: 0.0
    }

// Chromium (commonly in micrograms)
@Composable
fun NutritionChromiumViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Chromium", "µg") {
        it.chromium?.inMicrograms ?: 0.0
    }

// Copper (commonly in milligrams)
@Composable
fun NutritionCopperViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Copper", "mg") {
        it.copper?.inMilligrams ?: 0.0
    }

// Dietary Fiber (commonly in grams)
@Composable
fun NutritionDietaryFiberViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Dietary Fiber", "g") {
        it.dietaryFiber?.inGrams ?: 0.0
    }

// Folate (commonly in micrograms)
@Composable
fun NutritionFolateViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Folate", "µg") {
        it.folate?.inMicrograms ?: 0.0
    }

// Folic Acid (commonly in micrograms)
@Composable
fun NutritionFolicAcidViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Folic Acid", "µg") {
        it.folicAcid?.inMicrograms ?: 0.0
    }

// Iodine (commonly in micrograms)
@Composable
fun NutritionIodineViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Iodine", "µg") {
        it.iodine?.inMicrograms ?: 0.0
    }

// Iron (commonly in milligrams)
@Composable
fun NutritionIronViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Iron", "mg") {
        it.iron?.inMilligrams ?: 0.0
    }

// Magnesium (commonly in milligrams)
@Composable
fun NutritionMagnesiumViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Magnesium", "mg") {
        it.magnesium?.inMilligrams ?: 0.0
    }

// Manganese (commonly in milligrams)
@Composable
fun NutritionManganeseViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Manganese", "mg") {
        it.manganese?.inMilligrams ?: 0.0
    }

// Molybdenum (commonly in micrograms)
@Composable
fun NutritionMolybdenumViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Molybdenum", "µg") {
        it.molybdenum?.inMicrograms ?: 0.0
    }

// Monounsaturated Fat (commonly in grams)
@Composable
fun NutritionMonounsaturatedFatViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Monounsaturated Fat", "g") {
        it.monounsaturatedFat?.inGrams ?: 0.0
    }

// Niacin (Vitamin B3) (commonly in milligrams)
@Composable
fun NutritionNiacinViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Niacin", "mg") {
        it.niacin?.inMilligrams ?: 0.0
    }

// Pantothenic Acid (Vitamin B5) (commonly in milligrams)
@Composable
fun NutritionPantothenicAcidViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Pantothenic Acid", "mg") {
        it.pantothenicAcid?.inMilligrams ?: 0.0
    }

// Phosphorus (commonly in milligrams)
@Composable
fun NutritionPhosphorusViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Phosphorus", "mg") {
        it.phosphorus?.inMilligrams ?: 0.0
    }

// Polyunsaturated Fat (commonly in grams)
@Composable
fun NutritionPolyunsaturatedFatViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Polyunsaturated Fat", "g") {
        it.polyunsaturatedFat?.inGrams ?: 0.0
    }

// Potassium (commonly in milligrams)
@Composable
fun NutritionPotassiumViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Potassium", "mg") {
        it.potassium?.inMilligrams ?: 0.0
    }

// Protein (commonly in grams)
@Composable
fun NutritionProteinViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Protein", "g") {
        it.protein?.inGrams ?: 0.0
    }

// Riboflavin (Vitamin B2) (commonly in milligrams)
@Composable
fun NutritionRiboflavinViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Riboflavin (B2)", "mg") {
        it.riboflavin?.inMilligrams ?: 0.0
    }

// Saturated Fat (commonly in grams)
@Composable
fun NutritionSaturatedFatViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Saturated Fat", "g") {
        it.saturatedFat?.inGrams ?: 0.0
    }

// Selenium (commonly in micrograms)
@Composable
fun NutritionSeleniumViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Selenium", "µg") {
        it.selenium?.inMicrograms ?: 0.0
    }

// Sodium (commonly in milligrams)
@Composable
fun NutritionSodiumViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Sodium", "mg") {
        it.sodium?.inMilligrams ?: 0.0
    }

// Sugar (commonly in grams)
@Composable
fun NutritionSugarViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Sugar", "g") {
        it.sugar?.inGrams ?: 0.0
    }

// Thiamin (Vitamin B1) (commonly in milligrams)
@Composable
fun NutritionThiaminViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Thiamin (B1)", "mg") {
        it.thiamin?.inMilligrams ?: 0.0
    }

// Total Carbohydrate (commonly in grams)
@Composable
fun NutritionTotalCarbohydrateViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Total Carbohydrate", "g") {
        it.totalCarbohydrate?.inGrams ?: 0.0
    }

// Total Fat (commonly in grams)
@Composable
fun NutritionTotalFatViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Total Fat", "g") {
        it.totalFat?.inGrams ?: 0.0
    }

// Trans Fat (commonly in grams)
@Composable
fun NutritionTransFatViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Trans Fat", "g") {
        it.transFat?.inGrams ?: 0.0
    }

// Unsaturated Fat (commonly in grams)
@Composable
fun NutritionUnsaturatedFatViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Unsaturated Fat", "g") {
        it.unsaturatedFat?.inGrams ?: 0.0
    }

// Vitamin A (commonly in micrograms of RAE, though sometimes IU)
@Composable
fun NutritionVitaminAViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Vitamin A", "µg") {
        it.vitaminA?.inMicrograms ?: 0.0
    }

// Vitamin B12 (commonly in micrograms)
@Composable
fun NutritionVitaminB12ViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Vitamin B12", "µg") {
        it.vitaminB12?.inMicrograms ?: 0.0
    }

// Vitamin B6 (commonly in milligrams)
@Composable
fun NutritionVitaminB6ViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Vitamin B6", "mg") {
        it.vitaminB6?.inMilligrams ?: 0.0
    }

// Vitamin C (commonly in milligrams)
@Composable
fun NutritionVitaminCViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Vitamin C", "mg") {
        it.vitaminC?.inMilligrams ?: 0.0
    }

// Vitamin D (often micrograms or IU)
@Composable
fun NutritionVitaminDViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Vitamin D", "µg") {
        it.vitaminD?.inMicrograms ?: 0.0
    }

// Vitamin E (commonly in milligrams)
@Composable
fun NutritionVitaminEViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Vitamin E", "mg") {
        it.vitaminE?.inMilligrams ?: 0.0
    }

// Vitamin K (commonly in micrograms)
@Composable
fun NutritionVitaminKViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Vitamin K", "µg") {
        it.vitaminK?.inMicrograms ?: 0.0
    }

// Zinc (commonly in milligrams)
@Composable
fun NutritionZincViewData(appDatabase: AppDatabase) =
    NutritionGenericViewData(appDatabase, "Zinc", "mg") {
        it.zinc?.inMilligrams ?: 0.0
    }
