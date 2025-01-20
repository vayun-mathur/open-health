package com.health.openhealth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

enum class RecordCategory(
    val nameString: String,
    val icon: Int
) {
    HEART(
        "Heart",
        R.drawable.baseline_heart_broken_24,
    ),
    VITALS(
        "Vitals",
        R.drawable.baseline_monitor_heart_24,
    ),
    ACTIVITY(
        "Activity",
        R.drawable.baseline_local_fire_department_24,
    ),
    NUTRITION(
        "Nutrition",
        R.drawable.baseline_food_bank_24,
    ),
    CYCLE_TRACKING(
        "Cycle Tracking",
        R.drawable.baseline_cyclone_24,
    ),
    COMPOSITION(
        "Composition",
        R.drawable.baseline_pie_chart_24,
    )
}

@Composable
fun BrowseScreen(navController: NavHostController) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController, "browse")
        }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding).padding(16.dp)) {
            Text("Health Categories", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))
            Card {
                RecordCategory.entries.forEachIndexed { idx, it ->
                    if(idx != 0)
                        HorizontalDivider(thickness = 2.dp, color = Color.Black)
                    RecordCategoryRow(it) {
                        navController.navigate("browse/${it.nameString}")
                    }
                }
            }
        }
    }
}

@Composable
fun RecordCategoryRow(recordCategory: RecordCategory, onClick: () -> Unit) {
    Column(Modifier.clickable {onClick()}.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Icon(painterResource(recordCategory.icon), null, Modifier.size(50.dp))
            Spacer(Modifier.width(16.dp))
            Text(recordCategory.nameString)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseCategoryScreen(navController: NavHostController, appDatabase: AppDatabase, category: String) {
    Scaffold(
        topBar = {
            TopAppBar({Text(category)}, navigationIcon = {
                Icon(painterResource(R.drawable.baseline_arrow_back_24), null, Modifier.clickable {
                    navController.popBackStack()
                })
            })
        },
        bottomBar = {
            BottomNavigationBar(navController, "browse")
        }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding).padding(16.dp)) {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                when(RecordCategory.entries.first { it.nameString == category }) {
                    RecordCategory.HEART -> {
                        HeartRateViewData(appDatabase)
                        RestingHeartRateViewData(appDatabase)
                        HeartRateVariabilityViewData(appDatabase)
                        VO2MaxViewData(appDatabase)
                    }
                    RecordCategory.VITALS -> {
                        HeartRateViewData(appDatabase)
                        OxygenSaturationViewData(appDatabase)
                        RespiratoryRateViewData(appDatabase)
                        BloodPressureViewData(appDatabase)
                        BloodGlucoseViewData(appDatabase)
                        BodyTemperatureViewData(appDatabase)
                        VO2MaxViewData(appDatabase)
                    }
                    RecordCategory.COMPOSITION -> {
                        WeightViewData(appDatabase)
                        HeightViewData(appDatabase)
                        BodyFatViewData(appDatabase)
                        BodyWaterMassViewData(appDatabase)
                        BoneMassViewData(appDatabase)
                        LeanBodyMassViewData(appDatabase)
                    }
                    RecordCategory.ACTIVITY -> {
                        ActiveCaloriesBurnedViewData(appDatabase)
                        BasalMetabolicRateViewData(appDatabase)
                        TotalCaloriesBurnedViewData(appDatabase)
                        StepsViewData(appDatabase)
                        FloorsClimbedViewData(appDatabase)
                        DistanceViewData(appDatabase)
                        ElevationGainViewData(appDatabase)
                        SpeedViewData(appDatabase)
                        PowerViewData(appDatabase)
                        WheelchairPushesViewData(appDatabase)
                    }
                    RecordCategory.CYCLE_TRACKING -> {
                        BasalBodyTemperatureViewData(appDatabase)
                        MenstruationPeriodViewData(appDatabase)
                    }
                    RecordCategory.NUTRITION -> {
                        HydrationViewData(appDatabase)
                        NutritionBiotinViewData(appDatabase)
                        NutritionCaffeineViewData(appDatabase)
                        NutritionCalciumViewData(appDatabase)
                        NutritionEnergyViewData(appDatabase)
                        NutritionEnergyFromFatViewData(appDatabase)
                        NutritionChlorideViewData(appDatabase)
                        NutritionCholesterolViewData(appDatabase)
                        NutritionChromiumViewData(appDatabase)
                        NutritionCopperViewData(appDatabase)
                        NutritionDietaryFiberViewData(appDatabase)
                        NutritionFolateViewData(appDatabase)
                        NutritionFolicAcidViewData(appDatabase)
                        NutritionIodineViewData(appDatabase)
                        NutritionIronViewData(appDatabase)
                        NutritionMagnesiumViewData(appDatabase)
                        NutritionManganeseViewData(appDatabase)
                        NutritionMolybdenumViewData(appDatabase)
                        NutritionMonounsaturatedFatViewData(appDatabase)
                        NutritionNiacinViewData(appDatabase)
                        NutritionPantothenicAcidViewData(appDatabase)
                        NutritionPhosphorusViewData(appDatabase)
                        NutritionPolyunsaturatedFatViewData(appDatabase)
                        NutritionPotassiumViewData(appDatabase)
                        NutritionProteinViewData(appDatabase)
                        NutritionRiboflavinViewData(appDatabase)
                        NutritionSaturatedFatViewData(appDatabase)
                        NutritionSeleniumViewData(appDatabase)
                        NutritionSodiumViewData(appDatabase)
                        NutritionSugarViewData(appDatabase)
                        NutritionThiaminViewData(appDatabase)
                        NutritionTotalCarbohydrateViewData(appDatabase)
                        NutritionTotalFatViewData(appDatabase)
                        NutritionTransFatViewData(appDatabase)
                        NutritionUnsaturatedFatViewData(appDatabase)
                        NutritionVitaminAViewData(appDatabase)
                        NutritionVitaminB12ViewData(appDatabase)
                        NutritionVitaminB6ViewData(appDatabase)
                        NutritionVitaminCViewData(appDatabase)
                        NutritionVitaminDViewData(appDatabase)
                        NutritionVitaminEViewData(appDatabase)
                        NutritionVitaminKViewData(appDatabase)
                        NutritionZincViewData(appDatabase)
                    }
                }
            }
        }
    }
}
