package ir.alighazi.taqvimfarsi.calendar

import java.time.LocalDate

/** Jalali/Gregorian conversion based on the jalaali-js break-year algorithm. */
data class JalaliDate(val year: Int, val month: Int, val day: Int) {
    init { require(month in 1..12 && day in 1..31) }
    override fun toString() = "$year/$month/$day"
}

object JalaliCalendar {
    private val breaks = intArrayOf(-61, 9, 38, 199, 426, 686, 756, 818, 1111, 1181, 1210,
        1635, 2060, 2097, 2192, 2262, 2324, 2394, 2456, 3178)
    private data class JalCal(val leap: Int, val gy: Int, val march: Int)
    private fun jalCal(jy: Int): JalCal {
        require(jy >= breaks.first() && jy < breaks.last()) { "سال خارج از محدوده الگوریتم است" }
        val gy = jy + 621
        var leapJ = -14
        var jp = breaks[0]
        var jump = 0
        for (i in 1 until breaks.size) {
            val jm = breaks[i]
            jump = jm - jp
            if (jy < jm) break
            leapJ += (jump / 33) * 8 + (jump % 33) / 4
            jp = jm
        }
        var n = jy - jp
        leapJ += (n / 33) * 8 + ((n % 33) + 3) / 4
        if (jump % 33 == 4 && jump - n == 4) leapJ++
        val leapG = gy / 4 - ((gy / 100 + 1) * 3) / 4 - 150
        val march = 20 + leapJ - leapG
        if (jump - n < 6) n = n - jump + ((jump + 4) / 33) * 33
        var leap = ((n + 1) % 33 - 1) % 4
        if (leap == -1) leap = 4
        return JalCal(leap, gy, march)
    }
    private fun div(a: Int, b: Int) = Math.floorDiv(a, b)
    fun isLeapYear(jy: Int): Boolean = jalCal(jy).leap == 0
    fun toGregorian(j: JalaliDate): LocalDate {
        val r = jalCal(j.year)
        require(j.month in 1..12)
        val maxDay = if (j.month <= 6) 31 else if (j.month <= 11) 30 else if (isLeapYear(j.year)) 30 else 29
        require(j.day in 1..maxDay) { "روز نامعتبر برای ماه شمسی" }
        val marchDate = LocalDate.of(r.gy, 3, r.march)
        val days = if (j.month <= 7) (j.month - 1) * 31 + j.day - 1
                   else 6 * 31 + (j.month - 7) * 30 + j.day - 1
        return marchDate.plusDays(days.toLong())
    }
    fun fromGregorian(date: LocalDate): JalaliDate {
        var jy = date.year - 621
        var yearStart = toGregorian(JalaliDate(jy, 1, 1))
        while (date.isBefore(yearStart)) {
            jy--
            yearStart = toGregorian(JalaliDate(jy, 1, 1))
        }
        var nextYearStart = toGregorian(JalaliDate(jy + 1, 1, 1))
        while (!date.isBefore(nextYearStart)) {
            jy++
            yearStart = nextYearStart
            nextYearStart = toGregorian(JalaliDate(jy + 1, 1, 1))
        }
        val dayOfYear = (date.toEpochDay() - yearStart.toEpochDay()).toInt()
        val month: Int
        val day: Int
        if (dayOfYear < 186) {
            month = dayOfYear / 31 + 1
            day = dayOfYear % 31 + 1
        } else {
            val remaining = dayOfYear - 186
            month = remaining / 30 + 7
            day = remaining % 30 + 1
        }
        return JalaliDate(jy, month, day)
    }
}
