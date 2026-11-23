package ir.alighazi.taqvimfarsi.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ir.alighazi.taqvimfarsi.calendar.JalaliCalendar
import ir.alighazi.taqvimfarsi.calendar.JalaliDate
import ir.alighazi.taqvimfarsi.data.EventRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class CalendarUiState(
    val visibleYear: Int = JalaliCalendar.fromGregorian(LocalDate.now()).year,
    val visibleMonth: Int = JalaliCalendar.fromGregorian(LocalDate.now()).month,
    val selectedDate: LocalDate = LocalDate.now(),
    val today: LocalDate = LocalDate.now()
)

class CalendarViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = EventRepository(app)
    private val _state = MutableStateFlow(CalendarUiState())
    val state: StateFlow<CalendarUiState> = _state
    fun moveMonth(delta: Int) = _state.update { current ->
        var year = current.visibleYear
        var month = current.visibleMonth + delta
        while (month < 1) { month += 12; year-- }
        while (month > 12) { month -= 12; year++ }
        current.copy(visibleYear = year, visibleMonth = month)
    }
    fun select(date: LocalDate) = _state.update { it.copy(selectedDate = date) }
    fun events(date: LocalDate) = repository.forDate(date)
    fun todayEvents() = repository.forDate(LocalDate.now())
    fun lunar(date: LocalDate) = repository.lunarDate(date)
    fun jalali(date: LocalDate) = JalaliCalendar.fromGregorian(date)
    fun gregorianToJalali(date: LocalDate) = JalaliCalendar.fromGregorian(date)
    fun jalaliToGregorian(year: Int, month: Int, day: Int) = JalaliCalendar.toGregorian(JalaliDate(year, month, day))
}
