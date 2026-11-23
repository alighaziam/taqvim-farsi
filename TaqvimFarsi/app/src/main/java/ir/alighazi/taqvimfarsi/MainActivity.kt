package ir.alighazi.taqvimfarsi

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.alighazi.taqvimfarsi.calendar.JalaliCalendar
import ir.alighazi.taqvimfarsi.ui.CalendarViewModel
import ir.alighazi.taqvimfarsi.worker.ReminderScheduler
import java.time.LocalDate
import java.time.DayOfWeek

class MainActivity : ComponentActivity() {
    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        ReminderScheduler.schedule(this)
        setContent { TaqvimTheme { CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            CalendarScreen()
        } } }
    }
}

@Composable
private fun TaqvimTheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    MaterialTheme(colorScheme = if (dark) darkColorScheme() else lightColorScheme(), content = content)
}

private val persianMonths = listOf("فروردین","اردیبهشت","خرداد","تیر","مرداد","شهریور",
    "مهر","آبان","آذر","دی","بهمن","اسفند")
private val weekdays = listOf("ش","ی","د","س","چ","پ","ج")
private fun fa(value: Int): String = value.toString().map { "۰۱۲۳۴۵۶۷۸۹"[it.digitToInt()] }.joinToString("")
private fun parseLocalizedInt(value: String): Int? = value.mapNotNull {
    it.digitToIntOrNull()?.let { digit -> ('0'.code + digit).toChar() }
}.joinToString("").toIntOrNull()

@Composable
private fun CalendarScreen(vm: CalendarViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    val selectedJ = vm.jalali(state.selectedDate)
    val selectedLunar = vm.lunar(state.selectedDate)
    val selectedEvents = vm.events(state.selectedDate)
    var inputYear by remember { mutableStateOf("") }
    var inputMonth by remember { mutableStateOf("") }
    var inputDay by remember { mutableStateOf("") }
    var conversionResult by remember { mutableStateOf<String?>(null) }
    val jalaliMonthStart = JalaliCalendar.toGregorian(
        ir.alighazi.taqvimfarsi.calendar.JalaliDate(state.visibleYear, state.visibleMonth, 1))
    val monthLength = if (state.visibleMonth <= 6) 31 else if (state.visibleMonth <= 11) 30
        else if (JalaliCalendar.isLeapYear(state.visibleYear)) 30 else 29
    val offset = (jalaliMonthStart.dayOfWeek.value % 7 + 1) % 7
    val days: List<LocalDate?> = List(offset) { null } +
        (1..monthLength).map { day -> JalaliCalendar.toGregorian(
            ir.alighazi.taqvimfarsi.calendar.JalaliDate(state.visibleYear, state.visibleMonth, day)) }
    Scaffold { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("تقویم فارسی", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("${persianMonths[selectedJ.month-1]} ${fa(selectedJ.year)}", style = MaterialTheme.typography.titleLarge)
                    val todayJ = JalaliCalendar.fromGregorian(LocalDate.now())
                    Text("امروز: ${fa(todayJ.day)} ${persianMonths[todayJ.month-1]} ${fa(todayJ.year)}")
                    Text("تاریخ میلادی: ${fa(state.selectedDate.dayOfMonth)} / ${fa(state.selectedDate.monthValue)} / ${fa(state.selectedDate.year)}")
                    Text("تاریخ قمری: ${fa(selectedLunar.third)} / ${fa(selectedLunar.second)} / ${fa(selectedLunar.first)}")
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { vm.moveMonth(-1) }) { Icon(Icons.Default.ChevronRight, "ماه قبل") }
                Text("${persianMonths[state.visibleMonth-1]} ${fa(state.visibleYear)}", style = MaterialTheme.typography.titleLarge)
                IconButton(onClick = { vm.moveMonth(1) }) { Icon(Icons.Default.ChevronLeft, "ماه بعد") }
            }
            Row(Modifier.fillMaxWidth()) {
                weekdays.forEach { Text(it, Modifier.weight(1f), fontWeight = FontWeight.Bold,
                    color = if (it == "ج") Color.Red else MaterialTheme.colorScheme.onSurface,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center) }
            }
            LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.weight(1f), userScrollEnabled = false) {
                items(days) { date ->
                    if (date == null) Box(Modifier.aspectRatio(0.88f))
                    else {
                        val j = vm.jalali(date)
                        val events = vm.events(date)
                        val isHoliday = events.any { it.holiday }
                        val isSelected = date == state.selectedDate
                        val isToday = date == LocalDate.now()
                        Column(
                            Modifier.fillMaxWidth().aspectRatio(0.88f).padding(2.dp)
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent, CircleShape)
                                .clickable { vm.select(date) }.padding(2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(fa(j.day), color = if (isHoliday || date.dayOfWeek == DayOfWeek.FRIDAY) Color.Red else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.Normal)
                            Text(fa(date.dayOfMonth), style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (isToday) Text("●", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
            Text("مناسبت‌های ${fa(selectedJ.day)} ${persianMonths[selectedJ.month-1]}", style = MaterialTheme.typography.titleMedium)
            if (selectedEvents.isEmpty()) Text("مناسبتی ثبت نشده است", color = MaterialTheme.colorScheme.onSurfaceVariant)
            selectedEvents.forEach { event ->
                Text("• ${event.title}${if (event.holiday) " (تعطیل)" else ""}",
                    color = if (event.holiday) Color.Red else MaterialTheme.colorScheme.onSurface)
            }
            Text("تبدیل تاریخ: میلادی ${fa(state.selectedDate.year)}-${fa(state.selectedDate.monthValue)}-${fa(state.selectedDate.dayOfMonth)}  ↔  شمسی ${fa(selectedJ.year)}-${fa(selectedJ.month)}-${fa(selectedJ.day)}",
                style = MaterialTheme.typography.bodySmall)
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("تبدیل تاریخ شمسی به میلادی", style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = inputYear, onValueChange = { inputYear = it.filter(Char::isDigit).take(4) },
                            modifier = Modifier.weight(1.3f), label = { Text("سال") }, singleLine = true)
                        OutlinedTextField(value = inputMonth, onValueChange = { inputMonth = it.filter(Char::isDigit).take(2) },
                            modifier = Modifier.weight(1f), label = { Text("ماه") }, singleLine = true)
                        OutlinedTextField(value = inputDay, onValueChange = { inputDay = it.filter(Char::isDigit).take(2) },
                            modifier = Modifier.weight(1f), label = { Text("روز") }, singleLine = true)
                    }
                    Button(onClick = {
                        conversionResult = try {
                            val converted = JalaliCalendar.toGregorian(
                                ir.alighazi.taqvimfarsi.calendar.JalaliDate(
                                    parseLocalizedInt(inputYear) ?: 0, parseLocalizedInt(inputMonth) ?: 0, parseLocalizedInt(inputDay) ?: 0))
                            "میلادی: ${fa(converted.year)}/${fa(converted.monthValue)}/${fa(converted.dayOfMonth)}"
                        } catch (_: Exception) { "تاریخ واردشده معتبر نیست" }
                    }, modifier = Modifier.fillMaxWidth()) { Text("تبدیل") }
                    conversionResult?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                }
            }
            Text("تاریخ قمری در رویدادها با تقویم مدنی ICU محاسبه می‌شود؛ ممکن است با اعلام رسمی رؤیت هلال یک روز اختلاف داشته باشد.",
                style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
