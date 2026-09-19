package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AgencyService
import com.example.data.model.AgencySettings
import com.example.data.model.AppNotification
import com.example.data.model.BookingDocument
import com.example.data.model.ElectronicBooking
import com.example.data.model.PaymentMethod
import com.example.data.model.RequiredDocument
import com.example.data.model.User
import com.example.data.model.VisaType
import com.example.util.SecurityUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        ElectronicBooking::class,
        BookingDocument::class,
        VisaType::class,
        RequiredDocument::class,
        PaymentMethod::class,
        AppNotification::class,
        AgencySettings::class,
        AgencyService::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun bookingDao(): BookingDao
    abstract fun documentDao(): DocumentDao
    abstract fun visaTypeDao(): VisaTypeDao
    abstract fun requiredDocumentDao(): RequiredDocumentDao
    abstract fun paymentMethodDao(): PaymentMethodDao
    abstract fun notificationDao(): NotificationDao
    abstract fun agencySettingsDao(): AgencySettingsDao
    abstract fun agencyServiceDao(): AgencyServiceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shajeen_travel.db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(AppDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            val userDao = database.userDao()
            val visaDao = database.visaTypeDao()
            val reqDocDao = database.requiredDocumentDao()
            val paymentDao = database.paymentMethodDao()
            val settingsDao = database.agencySettingsDao()
            val notifDao = database.notificationDao()

            // 1. Seed Agency Settings
            settingsDao.insertOrUpdate(
                AgencySettings(
                    id = 1,
                    agencyName = "وكالة شجين للسفريات والسياحة",
                    address = "صنعاء - شارع خولان - جوار السلامي لمواد البناء",
                    phoneNumbers = "+967 777779492, +966 551160835, +967 774191789, +967 770038009",
                    whatsapp = "770038009",
                    logo = "ic_shajeen_logo",
                    mapUrl = "https://maps.google.com/?q=15.3375,44.2272"
                )
            )

            // 2. Seed Default Admin User
            val adminUser = User(
                fullName = "مدير وكالة شجين",
                phone = "770038009",
                email = "admin@shajeen.com",
                idType = "بطاقة شخصية",
                idNumber = "01010012345",
                country = "اليمن",
                city = "صنعاء",
                district = "السبعين",
                area = "شارع خولان",
                passwordHash = SecurityUtils.hashPassword("admin123"),
                role = "ADMIN"
            )
            userDao.insertUser(adminUser)

            // 3. Seed Demo Client User
            val demoUser = User(
                fullName = "محمد علي أحمد",
                phone = "771234567",
                email = "mohammed@example.com",
                idType = "جواز سفر",
                idNumber = "08765432",
                country = "اليمن",
                city = "صنعاء",
                district = "التحرير",
                area = "حدة",
                passwordHash = SecurityUtils.hashPassword("user123"),
                role = "USER"
            )
            val demoUserId = userDao.insertUser(demoUser)

            // 4. Seed Visa Types
            val vWorkId = visaDao.insertVisaType(
                VisaType(name = "تأشيرة عمل", description = "تأشيرة عمل وإقامة للمملكة والخليج")
            )
            val vTouristId = visaDao.insertVisaType(
                VisaType(name = "تأشيرة سياحية", description = "تأشيرة سياحة وترفيه للدول العربية والعالمية")
            )
            val vVisitId = visaDao.insertVisaType(
                VisaType(name = "تأشيرة زيارة", description = "تأشيرة زيارة عائلية أو تجارية")
            )
            val vUmrahId = visaDao.insertVisaType(
                VisaType(name = "تأشيرة عمرة", description = "تأشيرة أداء مناسك العمرة وزيارة الحرمين")
            )
            val vOtherId = visaDao.insertVisaType(
                VisaType(name = "تأشيرة أخرى", description = "تأشيرات دراسية، علاجية وغيرها")
            )

            // 5. Seed Required Documents
            // Work Visa
            reqDocDao.insertRequiredDoc(RequiredDocument(visaTypeId = vWorkId, documentName = "صورة جواز السفر", required = true))
            reqDocDao.insertRequiredDoc(RequiredDocument(visaTypeId = vWorkId, documentName = "الصورة الشخصية", required = true))
            reqDocDao.insertRequiredDoc(RequiredDocument(visaTypeId = vWorkId, documentName = "صورة الهوية", required = true))
            reqDocDao.insertRequiredDoc(RequiredDocument(visaTypeId = vWorkId, documentName = "عقد العمل أو تفويض التأشيرة", required = true))

            // Tourist Visa
            reqDocDao.insertRequiredDoc(RequiredDocument(visaTypeId = vTouristId, documentName = "صورة جواز السفر", required = true))
            reqDocDao.insertRequiredDoc(RequiredDocument(visaTypeId = vTouristId, documentName = "الصورة الشخصية", required = true))
            reqDocDao.insertRequiredDoc(RequiredDocument(visaTypeId = vTouristId, documentName = "كشف حساب بنكي أو حجز فندقي", required = false))

            // Visit Visa
            reqDocDao.insertRequiredDoc(RequiredDocument(visaTypeId = vVisitId, documentName = "صورة جواز السفر", required = true))
            reqDocDao.insertRequiredDoc(RequiredDocument(visaTypeId = vVisitId, documentName = "الصورة الشخصية", required = true))
            reqDocDao.insertRequiredDoc(RequiredDocument(visaTypeId = vVisitId, documentName = "خطاب الدعوة أو إثبات القرابة", required = true))

            // Umrah Visa
            reqDocDao.insertRequiredDoc(RequiredDocument(visaTypeId = vUmrahId, documentName = "صورة جواز السفر", required = true))
            reqDocDao.insertRequiredDoc(RequiredDocument(visaTypeId = vUmrahId, documentName = "الصورة الشخصية", required = true))
            reqDocDao.insertRequiredDoc(RequiredDocument(visaTypeId = vUmrahId, documentName = "شهادة التطعيم واللقاحات", required = false))

            // Other Visa
            reqDocDao.insertRequiredDoc(RequiredDocument(visaTypeId = vOtherId, documentName = "صورة جواز السفر", required = true))
            reqDocDao.insertRequiredDoc(RequiredDocument(visaTypeId = vOtherId, documentName = "الصورة الشخصية", required = true))
            reqDocDao.insertRequiredDoc(RequiredDocument(visaTypeId = vOtherId, documentName = "المستندات الداعمة للطلب", required = false))

            // 6. Seed Payment Methods
            paymentDao.insertPaymentMethod(
                PaymentMethod(
                    name = "Jib",
                    accountNumber = "770038009",
                    instructions = "قم بالتحويل عبر تطبيق جيب برقم الحساب 770038009 باسم وكالة شجين للسفريات"
                )
            )
            paymentDao.insertPaymentMethod(
                PaymentMethod(
                    name = "One Cash",
                    accountNumber = "770038009",
                    instructions = "قم بالتحويل عبر ون كاش إلى الرقم 770038009 باسم وكالة شجين"
                )
            )
            paymentDao.insertPaymentMethod(
                PaymentMethod(
                    name = "Jawali",
                    accountNumber = "770038009",
                    instructions = "قم بالتحويل عبر محفظة جوالي إلى الرقم 770038009 وكالة شجين للسفريات"
                )
            )

            // 7. Welcome Notification
            notifDao.insertNotification(
                AppNotification(
                    userId = demoUserId,
                    title = "مرحبًا بك في وكالة شجين",
                    message = "يسعدنا انضمامك إلى تطبيق وكالة شجين للسفريات والسياحة. يمكنك الآن تقديم طلبات الحجز والتأشيرات بكل سهولة."
                )
            )

            // 8. Seed Default Agency Services
            val serviceDao = database.agencyServiceDao()
            serviceDao.insertService(
                AgencyService(
                    title = "خدمات التأشيرات والإقامات",
                    subtitle = "تأشيرات عمل، زيارة عائلية، تجارية وسياحية",
                    description = "نقدم خدمات تخليص وإصدار وتفويض كافة أنواع التأشيرات لدول الخليج والشرق الأوسط ومختلف دول العالم، مع متابعة دقيقة لكافة الإجراءات القانونية والمستندية لدى السفارات والجهات المختصة.",
                    features = "إصدار ومتابعة سريعة للتأشيرة\nفحص وتدقيق كافة المستندات والوثائق\nتفويض ومصادقة العقود الرسمية\nخدمة عملاء ومتابعة على مدار الساعة",
                    iconName = "visa",
                    accentColorHex = "#0284C7"
                )
            )
            serviceDao.insertService(
                AgencyService(
                    title = "برامج الحج والعمرة",
                    subtitle = "باقات متميزة لزيارة بيت الله الحرام والمسجد النبوي",
                    description = "تنظيم وتسيير رحلات العمرة طوال العام، بالإضافة إلى برامج الحج المعتمدة بأفضل الأسعار وأعلى مستويات الراحة، مع توفير سكن فندقي قريب من الحرم وخدمات نقل متطورة ومرشدين ذوي خبرة.",
                    features = "فنادق راقية ومصنفة قريبة من الحرمين الشريفين\nحافلات نقل حديثة ومكيفة ومعقمة\nإصدار التأشيرات والتأمين الصحي الشامل\nمرشدون ومطوفون مرافقون للرحلات",
                    iconName = "umrah",
                    accentColorHex = "#10B981"
                )
            )
            serviceDao.insertService(
                AgencyService(
                    title = "حجز وإصدار تذاكر الطيران",
                    subtitle = "على جميع خطوط الطيران العالمية والمحلية بأفضل الأسعار",
                    description = "حجوزات فورية ومؤكدة على كبرى شركات الطيران العالمية والمحلية (اليمنية، الخطوط السعودية، طيران الإمارات، فلاي دبي، والخطوط القطرية) مع أفضل خيارات السفر وأسعار تنافسية وخيارات تعديل مرنة.",
                    features = "مقارنة فورية بين أفضل خطوط الطيران العالمية\nأسعار مخفضة وتنافسية لرحلات الذهاب والعودة\nإمكانية اختيار المقاعد والوجبات الخاصة\nإمكانية التعديل والإلغاء وإعادة الحجز بسهولة",
                    iconName = "flight",
                    accentColorHex = "#3B82F6"
                )
            )
            serviceDao.insertService(
                AgencyService(
                    title = "خدمات النقل البري والسياحي",
                    subtitle = "حافلات VIP حديثة ومكيفة بين اليمن والمملكة والخليج",
                    description = "تسيير رحلات برية منتظمة ومريحة بحافلات حديثة VIP مجهزة بأحدث وسائل الراحة وشاشات ترفيه ومقاعد مريحة للنقل بين مختلف المحافظات اليمنية وإلى مدن المملكة العربية السعودية ودول الخليج.",
                    features = "أسطول حافلات حديثة ومكيفة بأعلى مواصفات الأمان\nمقاعد VIP مريحة قابلة للإمالة وشواحن هواتف\nتسيير رحلات يومية مجدولة ومنتظمة\nخدمات شحن الأمتعة والطرود بأمان وموثوقية",
                    iconName = "bus",
                    accentColorHex = "#F59E0B"
                )
            )
            serviceDao.insertService(
                AgencyService(
                    title = "البرامج والجولات السياحية",
                    subtitle = "رحلات استكشافية وسياحية داخلية ودولية متكاملة",
                    description = "برامج سياحية متكاملة تشمل الإقامة الفندقية والجولات السياحية وخدمات الإرشاد السياحي واستخراج تأشيرات الدخول لوجهات سياحية رائعة مثل تركيا، ماليزيا، مصر، جورجيا، ومختلف الوجهات العالمية واليمنية الساحرة.",
                    features = "برامج عائلية وفردية مصممة خصيصاً للمسافرين\nتوفير مرشدين سياحيين محليين وخدمات الترجمة\nحجوزات فنادق ومنتجعات وشقق فندقية بأسعار خاصة\nجولات ترفيهية واستكشافية لأجمل المعالم التراثية",
                    iconName = "tourism",
                    accentColorHex = "#8B5CF6"
                )
            )
        }
    }
}
