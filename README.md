# تقویم فارسی

پروژه اندروید تقویم فارسی با Kotlin، Jetpack Compose، Material 3، MVVM، Glance و WorkManager.

## دریافت سورس
[دانلود سورس کامل مخزن به‌صورت ZIP](https://github.com/alighaziam/taqvim-farsi/archive/refs/heads/main.zip)

پس از استخراج، پوشه `TaqvimFarsi` را در Android Studio باز کنید.

## ساخت و آزمون
- JDK 17+
- Gradle 8.13
- Android SDK Platform 36

در حال حاضر فایل باینری `gradle-wrapper.jar` در مخزن نیست؛ بنابراین برای اجرای محلی باید Gradle 8.13 نصب باشد. از ریشه مخزن اجرا کنید:

```bash
gradle -p TaqvimFarsi testDebugUnitTest
gradle -p TaqvimFarsi assembleDebug
```

گردش‌کار GitHub Actions نیز برای اجرای آزمون‌های واحد و ساخت APK دیباگ اضافه شده است. تا ثبت نتیجه موفق CI، ساخت APK را تأییدشده تلقی نکنید.

## وضعیت نسخه
این نسخه اسکلت اولیه قابل توسعه است. داده‌های مناسبت‌ها نمونه‌اند و باید با تقویم رسمی هر سال تطبیق داده شوند. تاریخ قمری در این نسخه بر اساس تقویم مدنی محاسبه می‌شود و ممکن است با اعلام رسمی رؤیت هلال یک روز اختلاف داشته باشد.
