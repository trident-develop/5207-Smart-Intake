package com.xd.smartintake.ui.app

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xd.smartintake.data.Supplement
import com.xd.smartintake.data.SupplementStore
import com.xd.smartintake.ui.common.PlusGlyph
import com.xd.smartintake.ui.manage.AddSupplementSheet
import com.xd.smartintake.ui.manage.ManageScreen
import com.xd.smartintake.ui.navigation.BottomNavigationBar
import com.xd.smartintake.ui.navigation.Tab
import com.xd.smartintake.ui.progress.ProgressScreen
import com.xd.smartintake.ui.theme.AppFontFamily
import com.xd.smartintake.ui.theme.CapsuleA
import com.xd.smartintake.ui.theme.CapsuleB
import com.xd.smartintake.ui.theme.CapsuleC
import com.xd.smartintake.ui.theme.CapsuleD
import com.xd.smartintake.ui.theme.CapsuleE
import com.xd.smartintake.ui.theme.SoftGreen
import com.xd.smartintake.ui.theme.SoftGreenDark
import com.xd.smartintake.ui.today.TodayScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SmartIntakeApp() {
    val context = LocalContext.current
    val store = remember { SupplementStore(context) }
    val initial = remember { store.load() }
    val supplements = remember { mutableStateListOf<Supplement>().apply { addAll(initial.list) } }
    var nextId by remember { mutableIntStateOf(initial.nextId) }
    var currentTab by remember { mutableStateOf(Tab.Today) }
    var showAddSheet by remember { mutableStateOf(false) }
    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(store) {
        snapshotFlow { supplements.toList() to nextId }
            .collect { (list, id) -> store.save(list, id) }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavigationBar(
                currentTab = currentTab,
                onSelected = { currentTab = it }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = currentTab == Tab.Manage,
                enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
                exit = fadeOut()
            ) {
                FloatingActionButton(
                    onClick = { showAddSheet = true },
                    containerColor = SoftGreen,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PlusGlyph(modifier = Modifier.size(18.dp), tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Add supplement", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHost) { data ->
                Snackbar(
                    containerColor = SoftGreenDark,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(data.visuals.message, fontFamily = AppFontFamily)
                }
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentTab,
            label = "tab",
            transitionSpec = {
                val forward = targetState.ordinal > initialState.ordinal
                val dir = if (forward) 1 else -1
                (slideInHorizontally(tween(280)) { full -> dir * full / 6 } + fadeIn(tween(280)))
                    .togetherWith(
                        slideOutHorizontally(tween(220)) { full -> -dir * full / 6 } + fadeOut(tween(220))
                    )
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { tab ->
            when (tab) {
                Tab.Today -> TodayScreen(
                    supplements = supplements,
                    onTake = { id, slot ->
                        val idx = supplements.indexOfFirst { it.id == id }
                        if (idx >= 0) {
                            val current = supplements[idx]
                            supplements[idx] = current.copy(takenSlots = current.takenSlots + slot)
                        }
                    }
                )
                Tab.Manage -> ManageScreen(
                    supplements = supplements,
                    onToggleActive = { id ->
                        val idx = supplements.indexOfFirst { it.id == id }
                        if (idx >= 0) supplements[idx] = supplements[idx].copy(active = !supplements[idx].active)
                    },
                    onDelete = { id -> supplements.removeAll { it.id == id } }
                )
                Tab.Progress -> ProgressScreen(supplements = supplements)
            }
        }
    }

    if (showAddSheet) {
        AddSupplementSheet(
            onDismiss = { showAddSheet = false },
            onSave = { name, dosage, slotTimes, courseDays ->
                val accent = listOf(CapsuleA, CapsuleB, CapsuleC, CapsuleD, CapsuleE).random()
                supplements.add(
                    Supplement(
                        id = nextId,
                        name = name,
                        dosage = dosage,
                        slotTimes = slotTimes,
                        courseDays = courseDays,
                        daysCompleted = 0,
                        accent = accent
                    )
                )
                nextId += 1
                showAddSheet = false
                scope.launch { snackbarHost.showSnackbar("Supplement added") }
            }
        )
    }
}
