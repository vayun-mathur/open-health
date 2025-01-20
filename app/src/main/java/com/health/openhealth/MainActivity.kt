package com.health.openhealth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.NutritionRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.health.openhealth.ui.theme.OpenHealthTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import kotlin.reflect.KClass

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val availabilityStatus =
            HealthConnectClient.getSdkStatus(this, "com.google.android.apps.healthdata")
        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE) {
            return // early return as there is no viable integration
        }
        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED) {
            // Optionally redirect to package installer to find a provider, for example:
            val uriString =
                "market://details?id=com.google.android.apps.healthdata&url=healthconnect%3A%2F%2Fonboarding"
            this.startActivity(
                Intent(Intent.ACTION_VIEW).apply {
                    setPackage("com.android.vending")
                    data = Uri.parse(uriString)
                    putExtra("overlay", true)
                    putExtra("callerId", this.`package`)
                }
            )
            return
        }
        val healthConnectClient = HealthConnectClient.getOrCreate(this)

        setContent {
            val context = LocalContext.current
            val database = Room.databaseBuilder(
                context, AppDatabase::class.java, "app_database"
            ).build()

            LaunchedEffect(Unit) {
                CoroutineScope(Dispatchers.IO).launch {
                    if (database.recordDataDao().count() == 0L)
                        readAllForFirstTime(healthConnectClient, database)
                    //TODO: add syncing
//                    else
//                        syncData(healthConnectClient, database, context)
                }
            }

            OpenHealthTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "browse") {
                    composable("summary") {
                        //SummaryScreen()
                    }
                    composable("browse") {
                        BrowseScreen(navController)
                    }
                    composable("browse/{category}") {
                        BrowseCategoryScreen(
                            navController,
                            database,
                            it.arguments?.getString("category")!!
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, current: String) {
    NavigationBar {
        NavigationBarItem(current == "summary", {
            navController.navigate("summary")
        }, icon = {
            Icon(painterResource(R.drawable.baseline_home_24), null)
        }, label = {
            Text("Summary")
        })
        NavigationBarItem(current == "browse", {
            navController.navigate("browse")
        }, icon = {
            Icon(painterResource(R.drawable.baseline_grid_view_24), null)
        }, label = {
            Text("Browse")
        })
    }
}

fun recordToString(record: Record): String {
    if (record is NutritionRecord) {
        return gson.toJson(record, NutritionRecord::class.java)
    }
    return ""
}

fun <T : Record> createRecordData(record: T, clazz: KClass<T>): RecordData {
    return RecordData(
        record.metadata.id,
        recordToString(record),
        clazz
    )
}

suspend fun readAllForFirstTime(hc: HealthConnectClient, appDatabase: AppDatabase) {
    suspend fun <T : Record> readForFirstTime(clazz: KClass<T>) {
        val response = hc.readRecords(
            ReadRecordsRequest(
                clazz,
                timeRangeFilter = TimeRangeFilter.before(Instant.now())
            )
        )
        response.records.forEach { appDatabase.recordDataDao().insert(createRecordData(it, clazz)) }
    }
    readForFirstTime(HeartRateRecord::class)
    readForFirstTime(StepsRecord::class)
    readForFirstTime(ActiveCaloriesBurnedRecord::class)
    readForFirstTime(BasalBodyTemperatureRecord::class)
    readForFirstTime(BasalMetabolicRateRecord::class)
    readForFirstTime(BloodGlucoseRecord::class)
    readForFirstTime(BloodPressureRecord::class)
    readForFirstTime(BodyFatRecord::class)
    readForFirstTime(BodyTemperatureRecord::class)
    readForFirstTime(BodyWaterMassRecord::class)
    readForFirstTime(BoneMassRecord::class)
    readForFirstTime(CervicalMucusRecord::class)
    readForFirstTime(DistanceRecord::class)
    readForFirstTime(ElevationGainedRecord::class)
    readForFirstTime(FloorsClimbedRecord::class)
    readForFirstTime(HeartRateVariabilityRmssdRecord::class)
    readForFirstTime(HeightRecord::class)
    readForFirstTime(HydrationRecord::class)
    readForFirstTime(IntermenstrualBleedingRecord::class)
    readForFirstTime(LeanBodyMassRecord::class)
    readForFirstTime(MenstruationPeriodRecord::class)
    readForFirstTime(MenstruationFlowRecord::class)
    readForFirstTime(OvulationTestRecord::class)
    readForFirstTime(OxygenSaturationRecord::class)
    readForFirstTime(PlannedExerciseSessionRecord::class)
    readForFirstTime(PowerRecord::class)
    readForFirstTime(RespiratoryRateRecord::class)
    readForFirstTime(RestingHeartRateRecord::class)
    readForFirstTime(SexualActivityRecord::class)
    readForFirstTime(SleepSessionRecord::class)
    readForFirstTime(SpeedRecord::class)
    readForFirstTime(TotalCaloriesBurnedRecord::class)
    readForFirstTime(Vo2MaxRecord::class)
    readForFirstTime(WheelchairPushesRecord::class)
    readForFirstTime(WeightRecord::class)
    readForFirstTime(SkinTemperatureRecord::class)
}


//suspend fun syncData(hc: HealthConnectClient, appDatabase: AppDatabase, context: Context) {
//    suspend fun processDeletionChange(change: DeletionChange) {
//        appDatabase.recordDataDao().delete(change.recordId)
//    }
//
//    suspend fun processUpsertionChange(change: UpsertionChange) {
//        println(change)
//        appDatabase.recordDataDao().insert(
//            when(change.record) {
//                is HeartRateRecord ->
//                    createRecordData(change.record as HeartRateRecord)
//                is StepsRecord ->
//                    createRecordData(change.record as StepsRecord)
//                is NutritionRecord ->
//                    createRecordData(change.record as NutritionRecord)
//                else -> {
//                    return
//                }
//            }
//        )
//    }
//
//    suspend fun processChanges(token: String): String {
//        var nextChangesToken = token
//        do {
//            val response = hc.getChanges(nextChangesToken)
//            println("RSP " + nextChangesToken)
//            response.changes.forEach { change ->
//                when (change) {
//                    is UpsertionChange ->
//                        if (change.record.metadata.dataOrigin.packageName != context.packageName) {
//                            processUpsertionChange(change)
//                        }
//                    is DeletionChange -> processDeletionChange(change)
//                }
//            }
//            nextChangesToken = response.nextChangesToken
//        } while (response.hasMore)
//        // Return and store the changes token for use next time.
//        return nextChangesToken
//    }
//
//    val changesToken = hc.getChangesToken(
//        ChangesTokenRequest(recordTypes = setOf(NutritionRecord::class))
//    )
//    processChanges(changesToken)
//}