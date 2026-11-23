package ir.alighazi.taqvimfarsi.calendar

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class JalaliCalendarTest {
    @Test fun knownNowruzBoundaries() {
        assertEquals(JalaliDate(1403, 1, 1), JalaliCalendar.fromGregorian(LocalDate.of(2024, 3, 20)))
        assertEquals(JalaliDate(1403, 12, 30), JalaliCalendar.fromGregorian(LocalDate.of(2025, 3, 20)))
        assertEquals(LocalDate.of(2025, 3, 21), JalaliCalendar.toGregorian(JalaliDate(1404, 1, 1)))
        assertEquals(JalaliDate(1404, 1, 1), JalaliCalendar.fromGregorian(LocalDate.of(2025, 3, 21)))
    }
    @Test fun leapYearHasEsfand30() {
        val leapYear = (1390..1410).first { JalaliCalendar.isLeapYear(it) }
        assertEquals(30, JalaliCalendar.toGregorian(JalaliDate(leapYear, 12, 30))
            .let { JalaliCalendar.fromGregorian(it).day })
    }
    @Test fun leapAndCommonYearRoundTrip() {
        for (year in 1390..1410) for (month in 1..12) {
            val day = if (month <= 6) 31 else if (month <= 11) 30 else if (JalaliCalendar.isLeapYear(year)) 30 else 29
            val j = JalaliDate(year, month, day)
            assertEquals(j, JalaliCalendar.fromGregorian(JalaliCalendar.toGregorian(j)))
        }
    }
    @Test fun rejectsInvalidEsfandDayInCommonYear() {
        val commonYear = (1390..1410).first { !JalaliCalendar.isLeapYear(it) }
        try {
            JalaliCalendar.toGregorian(JalaliDate(commonYear, 12, 30))
            fail("روز ۳۰ اسفند در سال عادی باید نامعتبر باشد")
        } catch (_: IllegalArgumentException) { }
    }
    @Test fun gregorianRoundTripAcrossModernRange() {
        for (year in 1300..1500) {
            val start = JalaliCalendar.toGregorian(JalaliDate(year, 1, 1))
            val end = JalaliCalendar.toGregorian(JalaliDate(year + 1, 1, 1))
            var date = start
            while (date.isBefore(end)) {
                assertEquals(date, JalaliCalendar.toGregorian(JalaliCalendar.fromGregorian(date)))
                date = date.plusDays(1)
            }
        }
    }
    @Test fun gregorianRoundTripAroundNewYear() {
        for (offset in 0L..21L) {
            val date = LocalDate.of(2020, 3, 15).plusDays(offset)
            assertEquals(date, JalaliCalendar.toGregorian(JalaliCalendar.fromGregorian(date)))
        }
    }
}
