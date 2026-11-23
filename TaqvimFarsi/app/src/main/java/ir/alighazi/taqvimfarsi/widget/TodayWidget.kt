package ir.alighazi.taqvimfarsi.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Column
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.unit.dp
import ir.alighazi.taqvimfarsi.calendar.JalaliCalendar
import ir.alighazi.taqvimfarsi.data.EventRepository
import java.time.LocalDate

class TodayWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val today = LocalDate.now()
            val j = JalaliCalendar.fromGregorian(today)
            val event = EventRepository(context).forDate(today).firstOrNull()?.title ?: "مناسبتی ثبت نشده"
            Column(GlanceModifier.padding(12.dp)) {
                Text("امروز: ${j.year}/${j.month}/${j.day}")
                Text(event)
            }
        }
    }
}
class TodayWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TodayWidget()
}
