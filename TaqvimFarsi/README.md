# تقویم فارسی

اپلیکیشن آفلاین تقویم شمسی با Kotlin، Jetpack Compose، Material 3، MVVM، Glance و WorkManager.

## قابلیت‌ها
- تقویم ماهانه شمسی با ارقام فارسی، تاریخ میلادی هم‌زمان و علامت‌گذاری روز جاری و تعطیلات.
- خواندن مناسبت‌ها از `app/src/main/assets/holidays.json`.
- تبدیل جلالی/میلادی با الگوریتم break-year و آزمون مرز نوروز، کبیسه و رفت‌وبرگشت روزانه در بازه ۱۳۰۰ تا ۱۵۰۰.
- نمایش تاریخ قمری با ICU `IslamicCalendar` در حالت civil/tabular.
- ویجت صفحه اصلی با Glance.
- یادآور روزانه با WorkManager و مجوز اعلان در Android 13+.
- تم روشن/تاریک بر اساس تنظیم سیستم و رابط RTL.

## نکات صحت داده
مناسبت‌های JSON صرفاً چند نمونه برای نمایش ساختار هستند. پیش از انتشار، عنوان‌ها، تاریخ‌ها و تعطیل بودن باید با تقویم رسمی کشور و منابع رسمی همان سال تطبیق داده شوند. تاریخ قمری مبتنی بر تقویم مدنی است و ممکن است به دلیل رؤیت هلال با تقویم رسمی یک روز اختلاف داشته باشد.

## نیازمندی‌ها
- Android Studio سازگار با AGP 8.13
- JDK 17+
- Android SDK Platform 36
- اتصال اینترنت فقط برای دریافت وابستگی‌های Gradle در اولین build؛ خود اپ پس از نصب آفلاین کار می‌کند.

## بیلد
در ریشه پروژه:
```bash
./gradlew test
./gradlew assembleDebug
```

خروجی APK: `app/build/outputs/apk/debug/app-debug.apk`

برای ویندوز:
```bat
gradlew.bat test
gradlew.bat assembleDebug
```

## ساخت keystore برای Release
```bash
keytool -genkeypair -v -keystore taqvim-release.jks -keyalg RSA -keysize 4096 -validity 10000 -alias taqvim
```
فایل `keystore.properties.example` را به `keystore.properties` کپی و مقادیر واقعی را وارد کنید. فایل keystore و `keystore.properties` را در مخزن عمومی قرار ندهید.

## محدودیت‌های نسخه اولیه
- WorkManager زمان اجرای دقیق در ساعت مشخص را تضمین نمی‌کند.
- تاریخ قمری civil است، نه مبتنی بر رؤیت هلال.
- فونت وزیرمتن در این نسخه اضافه نشده و فونت سیستم استفاده می‌شود.
- `gradle-wrapper.jar` در بسته موجود نیست؛ در صورت نیاز Gradle 8.13 را نصب کنید. آزمون‌ها و APK در محیط تهیه این بسته اجرا/تأیید نشده‌اند.
