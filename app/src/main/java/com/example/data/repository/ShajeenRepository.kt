package com.example.data.repository

import com.example.data.db.AgencyServiceDao
import com.example.data.db.AgencySettingsDao
import com.example.data.db.AppDatabase
import com.example.data.db.BookingDao
import com.example.data.db.DocumentDao
import com.example.data.db.NotificationDao
import com.example.data.db.PaymentMethodDao
import com.example.data.db.RequiredDocumentDao
import com.example.data.db.UserDao
import com.example.data.db.VisaTypeDao
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Calendar

data class AdminStats(
    val totalUsers: Int = 0,
    val totalBookings: Int = 0,
    val newBookings: Int = 0,
    val inReviewBookings: Int = 0,
    val pendingPayments: Int = 0,
    val confirmedBookings: Int = 0,
    val completedBookings: Int = 0
)

class ShajeenRepository(private val database: AppDatabase) {

    private val userDao: UserDao = database.userDao()
    private val bookingDao: BookingDao = database.bookingDao()
    private val documentDao: DocumentDao = database.documentDao()
    private val visaTypeDao: VisaTypeDao = database.visaTypeDao()
    private val requiredDocDao: RequiredDocumentDao = database.requiredDocumentDao()
    private val paymentMethodDao: PaymentMethodDao = database.paymentMethodDao()
    private val notificationDao: NotificationDao = database.notificationDao()
    private val agencySettingsDao: AgencySettingsDao = database.agencySettingsDao()
    private val agencyServiceDao: AgencyServiceDao = database.agencyServiceDao()

    suspend fun checkAndSeedInitialData() = withContext(Dispatchers.IO) {
        val userCount = userDao.getUserCount()
        if (userCount == 0) {
            AppDatabase.populateInitialData(database)
        } else {
            // Ensure default services exist even if users existed from previous version
            if (agencyServiceDao.getServicesCount() == 0) {
                agencyServiceDao.insertService(
                    AgencyService(
                        title = "خدمات التأشيرات والإقامات",
                        subtitle = "تأشيرات عمل، زيارة عائلية، تجارية وسياحية",
                        description = "نقدم خدمات تخليص وإصدار وتفويض كافة أنواع التأشيرات لدول الخليج والشرق الأوسط ومختلف دول العالم، مع متابعة دقيقة لكافة الإجراءات القانونية والمستندية لدى السفارات والجهات المختصة.",
                        features = "إصدار ومتابعة سريعة للتأشيرة\nفحص وتدقيق كافة المستندات والوثائق\nتفويض ومصادقة العقود الرسمية\nخدمة عملاء ومتابعة على مدار الساعة",
                        iconName = "visa",
                        accentColorHex = "#0284C7"
                    )
                )
                agencyServiceDao.insertService(
                    AgencyService(
                        title = "برامج الحج والعمرة",
                        subtitle = "باقات متميزة لزيارة بيت الله الحرام والمسجد النبوي",
                        description = "تنظيم وتسيير رحلات العمرة طوال العام، بالإضافة إلى برامج الحج المعتمدة بأفضل الأسعار وأعلى مستويات الراحة، مع توفير سكن فندقي قريب من الحرم وخدمات نقل متطورة ومرشدين ذوي خبرة.",
                        features = "فنادق راقية ومصنفة قريبة من الحرمين الشريفين\nحافلات نقل حديثة ومكيفة ومعقمة\nإصدار التأشيرات والتأمين الصحي الشامل\nمرشدون ومطوفون مرافقون للرحلات",
                        iconName = "umrah",
                        accentColorHex = "#10B981"
                    )
                )
                agencyServiceDao.insertService(
                    AgencyService(
                        title = "حجز وإصدار تذاكر الطيران",
                        subtitle = "على جميع خطوط الطيران العالمية والمحلية بأفضل الأسعار",
                        description = "حجوزات فورية ومؤكدة على كبرى شركات الطيران العالمية والمحلية (اليمنية، الخطوط السعودية، طيران الإمارات، فلاي دبي، والخطوط القطرية) مع أفضل خيارات السفر وأسعار تنافسية وخيارات تعديل مرنة.",
                        features = "مقارنة فورية بين أفضل خطوط الطيران العالمية\nأسعار مخفضة وتنافسية لرحلات الذهاب والعودة\nإمكانية اختيار المقاعد والوجبات الخاصة\nإمكانية التعديل والإلغاء وإعادة الحجز بسهولة",
                        iconName = "flight",
                        accentColorHex = "#3B82F6"
                    )
                )
                agencyServiceDao.insertService(
                    AgencyService(
                        title = "خدمات النقل البري والسياحي",
                        subtitle = "حافلات VIP حديثة ومكيفة بين اليمن والمملكة والخليج",
                        description = "تسيير رحلات برية منتظمة ومريحة بحافلات حديثة VIP مجهزة بأحدث وسائل الراحة وشاشات ترفيه ومقاعد مريحة للنقل بين مختلف المحافظات اليمنية وإلى مدن المملكة العربية السعودية ودول الخليج.",
                        features = "أسطول حافلات حديثة ومكيفة بأعلى مواصفات الأمان\nمقاعد VIP مريحة قابلة للإمالة وشواحن هواتف\nتسيير رحلات يومية مجدولة ومنتظمة\nخدمات شحن الأمتعة والطرود بأمان وموثوقية",
                        iconName = "bus",
                        accentColorHex = "#F59E0B"
                    )
                )
                agencyServiceDao.insertService(
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

    // --- Authentication ---
    suspend fun login(phone: String, plainPass: String): Result<User> = withContext(Dispatchers.IO) {
        val cleanPhone = phone.trim()
        val user = userDao.getUserByPhone(cleanPhone)
            ?: return@withContext Result.failure(Exception("رقم الهاتف غير مسجل لدينا"))

        if (SecurityUtils.verifyPassword(plainPass.trim(), user.passwordHash)) {
            Result.success(user)
        } else {
            Result.failure(Exception("كلمة المرور غير صحيحة"))
        }
    }

    suspend fun register(
        fullName: String,
        phone: String,
        email: String?,
        idType: String,
        idNumber: String,
        country: String,
        city: String,
        district: String,
        area: String,
        plainPass: String,
        role: String = "USER"
    ): Result<User> = withContext(Dispatchers.IO) {
        val cleanPhone = phone.trim()
        val existing = userDao.getUserByPhone(cleanPhone)
        if (existing != null) {
            return@withContext Result.failure(Exception("رقم الهاتف مسجل مسبقاً، يرجى تسجيل الدخول"))
        }

        val newUser = User(
            fullName = fullName.trim(),
            phone = cleanPhone,
            email = email?.trim()?.ifBlank { null },
            idType = idType,
            idNumber = idNumber.trim(),
            country = country.trim(),
            city = city.trim(),
            district = district.trim(),
            area = area.trim(),
            passwordHash = SecurityUtils.hashPassword(plainPass.trim()),
            role = role
        )

        val id = userDao.insertUser(newUser)
        val created = newUser.copy(id = id)

        // Send welcome notification
        notificationDao.insertNotification(
            AppNotification(
                userId = id,
                title = "مرحبًا بك في وكالة شجين",
                message = "تم إنشاء حسابك بنجاح. يسعدنا تقديم أفضل خدمات السفر والتأشيرات لك."
            )
        )

        Result.success(created)
    }

    fun getUser(userId: Long): Flow<User?> = userDao.getUserById(userId)
    suspend fun getUserSync(userId: Long): User? = withContext(Dispatchers.IO) { userDao.getUserByIdSync(userId) }
    suspend fun updateUser(user: User) = withContext(Dispatchers.IO) { userDao.updateUser(user) }
    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()

    // --- Bookings ---
    suspend fun generateNextBookingNumber(): String = withContext(Dispatchers.IO) {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val lastNum = bookingDao.getLatestBookingNumber() // e.g. SHJ-2026-000005
        var nextSeq = 1
        if (lastNum != null && lastNum.startsWith("SHJ-$year-")) {
            val parts = lastNum.split("-")
            if (parts.size == 3) {
                val numPart = parts[2].toIntOrNull()
                if (numPart != null) {
                    nextSeq = numPart + 1
                }
            }
        }
        String.format(java.util.Locale.US, "SHJ-%d-%06d", year, nextSeq)
    }

    suspend fun createBooking(
        booking: ElectronicBooking,
        documents: List<BookingDocument>
    ): Long = withContext(Dispatchers.IO) {
        val bookingId = bookingDao.insertBooking(booking)
        if (documents.isNotEmpty()) {
            val docsWithId = documents.map { it.copy(bookingId = bookingId) }
            documentDao.insertDocuments(docsWithId)
        }

        // Notification to user
        notificationDao.insertNotification(
            AppNotification(
                userId = booking.userId,
                bookingId = bookingId,
                title = "تم استلام طلب الحجز بنجاح",
                message = "تم إنشاء طلب الحجز رقم ${booking.bookingNumber}. جاري مراجعة الطلب والتحقق من عملية الدفع."
            )
        )

        bookingId
    }

    fun getBooking(id: Long): Flow<ElectronicBooking?> = bookingDao.getBookingById(id)
    suspend fun getBookingSync(id: Long): ElectronicBooking? = withContext(Dispatchers.IO) { bookingDao.getBookingByIdSync(id) }
    fun getBookingByNumber(bookingNumber: String): Flow<ElectronicBooking?> = bookingDao.getBookingByNumber(bookingNumber)
    fun getUserBookings(userId: Long): Flow<List<ElectronicBooking>> = bookingDao.getBookingsByUser(userId)
    fun getAllBookings(): Flow<List<ElectronicBooking>> = bookingDao.getAllBookings()

    suspend fun updateBooking(booking: ElectronicBooking) = withContext(Dispatchers.IO) {
        bookingDao.updateBooking(booking.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun updatePaymentStatus(
        bookingId: Long,
        paymentStatus: String,
        adminNotes: String?
    ) = withContext(Dispatchers.IO) {
        val booking = bookingDao.getBookingByIdSync(bookingId) ?: return@withContext
        val newBookingStatus = when (paymentStatus) {
            "تم التحقق" -> "تم تأكيد الحجز"
            "غير صالح" -> "بانتظار التحقق من الدفع"
            else -> booking.bookingStatus
        }

        val updated = booking.copy(
            paymentStatus = paymentStatus,
            bookingStatus = newBookingStatus,
            adminNotes = adminNotes ?: booking.adminNotes,
            updatedAt = System.currentTimeMillis()
        )
        bookingDao.updateBooking(updated)

        // Notification for payment status update
        val notifTitle = if (paymentStatus == "تم التحقق") "تم تأكيد عملية الدفع" else "تنبيه بخصوص إثبات الدفع"
        val notifMsg = if (paymentStatus == "تم التحقق") {
            "تم التحقق من عملية الدفع الخاصة بالحجز ${booking.bookingNumber} بنجاح وتم تأكيد الحجز."
        } else {
            "إثبات الدفع للحجز ${booking.bookingNumber} غير صالح أو غير مكتمل. يرجى التواصل مع الوكالة."
        }

        notificationDao.insertNotification(
            AppNotification(
                userId = booking.userId,
                bookingId = bookingId,
                title = notifTitle,
                message = notifMsg
            )
        )
    }

    suspend fun updateBookingStatus(
        bookingId: Long,
        bookingStatus: String,
        adminNotes: String?
    ) = withContext(Dispatchers.IO) {
        val booking = bookingDao.getBookingByIdSync(bookingId) ?: return@withContext
        val updated = booking.copy(
            bookingStatus = bookingStatus,
            adminNotes = adminNotes ?: booking.adminNotes,
            updatedAt = System.currentTimeMillis()
        )
        bookingDao.updateBooking(updated)

        notificationDao.insertNotification(
            AppNotification(
                userId = booking.userId,
                bookingId = bookingId,
                title = "تحديث في حالة الحجز ${booking.bookingNumber}",
                message = "تم تغيير حالة الحجز إلى: $bookingStatus${if (!adminNotes.isNullOrBlank()) "\nملاحظة: $adminNotes" else ""}"
            )
        )
    }

    // --- Documents ---
    fun getDocumentsForBooking(bookingId: Long): Flow<List<BookingDocument>> =
        documentDao.getDocumentsForBooking(bookingId)

    suspend fun updateDocumentStatus(docId: Long, status: String) = withContext(Dispatchers.IO) {
        // Find and update doc
    }

    // --- Visa Types & Required Docs ---
    fun getActiveVisaTypes(): Flow<List<VisaType>> = visaTypeDao.getActiveVisaTypes()
    fun getAllVisaTypes(): Flow<List<VisaType>> = visaTypeDao.getAllVisaTypes()

    suspend fun addVisaType(visa: VisaType): Long = withContext(Dispatchers.IO) { visaTypeDao.insertVisaType(visa) }
    suspend fun updateVisaType(visa: VisaType) = withContext(Dispatchers.IO) { visaTypeDao.updateVisaType(visa) }
    suspend fun deleteVisaType(visaId: Long) = withContext(Dispatchers.IO) { visaTypeDao.deleteVisaTypeById(visaId) }

    fun getRequiredDocsForVisa(visaTypeId: Long): Flow<List<RequiredDocument>> =
        requiredDocDao.getRequiredDocsForVisa(visaTypeId)

    suspend fun getRequiredDocsForVisaSync(visaTypeId: Long): List<RequiredDocument> =
        withContext(Dispatchers.IO) { requiredDocDao.getRequiredDocsForVisaSync(visaTypeId) }

    fun getAllRequiredDocs(): Flow<List<RequiredDocument>> = requiredDocDao.getAllRequiredDocs()
    suspend fun addRequiredDoc(doc: RequiredDocument): Long = withContext(Dispatchers.IO) { requiredDocDao.insertRequiredDoc(doc) }
    suspend fun updateRequiredDoc(doc: RequiredDocument) = withContext(Dispatchers.IO) { requiredDocDao.updateRequiredDoc(doc) }
    suspend fun deleteRequiredDoc(id: Long) = withContext(Dispatchers.IO) { requiredDocDao.deleteRequiredDocById(id) }

    // --- Payment Methods ---
    fun getActivePaymentMethods(): Flow<List<PaymentMethod>> = paymentMethodDao.getActivePaymentMethods()
    fun getAllPaymentMethods(): Flow<List<PaymentMethod>> = paymentMethodDao.getAllPaymentMethods()
    suspend fun updatePaymentMethod(pm: PaymentMethod) = withContext(Dispatchers.IO) { paymentMethodDao.updatePaymentMethod(pm) }
    suspend fun addPaymentMethod(pm: PaymentMethod): Long = withContext(Dispatchers.IO) { paymentMethodDao.insertPaymentMethod(pm) }
    suspend fun deletePaymentMethod(id: Long) = withContext(Dispatchers.IO) { paymentMethodDao.deletePaymentMethodById(id) }

    // --- Services Management ---
    fun getAllServices(): Flow<List<AgencyService>> = agencyServiceDao.getAllServices()
    fun getActiveServices(): Flow<List<AgencyService>> = agencyServiceDao.getActiveServices()
    suspend fun addService(service: AgencyService): Long = withContext(Dispatchers.IO) {
        agencyServiceDao.insertService(service)
    }
    suspend fun updateService(service: AgencyService) = withContext(Dispatchers.IO) {
        agencyServiceDao.updateService(service)
    }
    suspend fun deleteService(id: Long) = withContext(Dispatchers.IO) {
        agencyServiceDao.deleteServiceById(id)
    }

    // --- Users Management ---
    suspend fun getAllClientUsersSync(): List<User> = withContext(Dispatchers.IO) {
        userDao.getAllClientUsersSync()
    }

    // --- Notifications ---
    fun getUserNotifications(userId: Long): Flow<List<AppNotification>> =
        notificationDao.getNotificationsForUser(userId)

    fun getAllNotifications(): Flow<List<AppNotification>> =
        notificationDao.getAllNotifications()

    fun getUnreadNotificationsCount(userId: Long): Flow<Int> =
        notificationDao.getUnreadCount(userId)

    suspend fun markNotificationAsRead(id: Long) = withContext(Dispatchers.IO) {
        notificationDao.markAsRead(id)
    }

    suspend fun sendNotification(userId: Long, bookingId: Long?, title: String, message: String) =
        withContext(Dispatchers.IO) {
            notificationDao.insertNotification(
                AppNotification(
                    userId = userId,
                    bookingId = bookingId,
                    title = title,
                    message = message
                )
            )
        }

    suspend fun sendNotificationToAll(title: String, message: String) = withContext(Dispatchers.IO) {
        val clientUsers = userDao.getAllClientUsersSync()
        val notifs = clientUsers.map { user ->
            AppNotification(
                userId = user.id,
                bookingId = null,
                title = title,
                message = message
            )
        }
        if (notifs.isNotEmpty()) {
            notificationDao.insertNotifications(notifs)
        }
    }

    // --- Agency Settings ---
    fun getAgencySettings(): Flow<AgencySettings?> = agencySettingsDao.getSettings()
    suspend fun getAgencySettingsSync(): AgencySettings? = withContext(Dispatchers.IO) { agencySettingsDao.getSettingsSync() }
    suspend fun updateAgencySettings(settings: AgencySettings) = withContext(Dispatchers.IO) {
        agencySettingsDao.insertOrUpdate(settings)
    }

    // --- Admin Stats ---
    suspend fun getAdminStats(): AdminStats = withContext(Dispatchers.IO) {
        val totalUsers = userDao.getUserCount()
        val totalBookings = bookingDao.getTotalBookingsCount()
        val newBookings = bookingDao.getCountByBookingStatus("جديد")
        val inReview = bookingDao.getCountByBookingStatus("قيد المراجعة")
        val pendingPayments = bookingDao.getPendingPaymentCount()
        val confirmed = bookingDao.getCountByBookingStatus("تم تأكيد الحجز")
        val completed = bookingDao.getCountByBookingStatus("مكتمل")

        AdminStats(
            totalUsers = totalUsers,
            totalBookings = totalBookings,
            newBookings = newBookings,
            inReviewBookings = inReview,
            pendingPayments = pendingPayments,
            confirmedBookings = confirmed,
            completedBookings = completed
        )
    }
}
