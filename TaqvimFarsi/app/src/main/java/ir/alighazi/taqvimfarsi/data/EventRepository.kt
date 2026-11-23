package ir.alighazi.taqvimfarsi.data

import android.content.Context
import com.google.gson.Gson
import ir.alighazi.taqvimfarsi.calendar.JalaliCalendar
import java.time.LocalDate
import android.icu.util.IslamicCalendar
import android.icu.util.TimeZone

enum class CalendarType { SHAMSI, QAMARI }
data class CalendarEvent(val month: Int, val day: Int, val title: String, val holiday: Boolean, val calendar: CalendarType)
data class EventFile(val events: List<CalendarEvent>)

class EventRepository(private val context: Context) {
    private val events: List<CalendarEvent> by lazy {
        context.assets.open("holidays.json").bufferedReader().use {
            Gson().fromJson(it, EventFile::class.java).events
        }
    }
    fun forDate(date: LocalDate): List<CalendarEvent> {
        val j = JalaliCalendar.fromGregorian(date)
        val q = IslamicCalendar(TimeZone.getTimeZone("Asia/Tehran")).apply {
            timeInMillis = date.atStartOfDay(java.time.ZoneId.of("Asia/Tehran")).toInstant().toEpochMilli()
        }
        val qMonth = q.get(IslamicCalendar.MONTH) + 1
        val qDay = q.get(IslamicCalendar.DAY_OF_MONTH)
        return events.filter {
            when (it.calendar) {
                CalendarType.SHAMSI -> it.month == j.month && it.day == j.day
                CalendarType.QAMARI -> it.month == qMonth && it.day == qDay
            }
        }
    }
    fun lunarDate(date: LocalDate): Triple<Int, Int, Int> {
        val calendar = IslamicCalendar(TimeZone.getTimeZone("Asia/Tehran")).apply {
            timeInMillis = date.atStartOfDay(java.time.ZoneId.of("Asia/Tehran")).toInstant().toEpochMilli()
        }
        return Triple(calendar.get(IslamicCalendar.YEAR), calendar.get(IslamicCalendar.MONTH) + 1,
            calendar.get(IslamicCalendar.DAY_OF_MONTH))
    }
    fun all() = events
}
