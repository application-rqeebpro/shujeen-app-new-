package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.AgencyService
import com.example.data.model.AgencySettings
import com.example.data.model.AppNotification
import com.example.data.model.BookingDocument
import com.example.data.model.ElectronicBooking
import com.example.data.model.PaymentMethod
import com.example.data.model.RequiredDocument
import com.example.data.model.User
import com.example.data.model.VisaType
import com.example.data.repository.AdminStats
import com.example.data.repository.ShajeenRepository
import com.example.data.storage.DocumentStorageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.URLEncoder

data class UploadedDocItem(
    val documentName: String,
    val isRequired: Boolean,
    val fileInfo: DocumentStorageManager.SavedFileInfo? = null
)

class ShajeenViewModel(application: Application) : AndroidViewModel(application) {

    val storageManager = DocumentStorageManager(application)
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    val repository = ShajeenRepository(database)

    // Current User Session
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> = _currentUser.combine(flowOf(Unit)) { user, _ ->
        user != null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isAdmin: StateFlow<Boolean> = _currentUser.combine(flowOf(Unit)) { user, _ ->
        user?.role == "ADMIN"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Login & Register Form State
    val loginPhone = MutableStateFlow("")
    val loginPassword = MutableStateFlow("")
    val loginError = MutableStateFlow<String?>(null)
    val isAuthLoading = MutableStateFlow(false)

    // Register Form State
    val regFullName = MutableStateFlow("")
    val regPhone = MutableStateFlow("")
    val regEmail = MutableStateFlow("")
    val regIdType = MutableStateFlow("بطاقة شخصية")
    val regIdNumber = MutableStateFlow("")
    val regCountry = MutableStateFlow("اليمن")
    val regCity = MutableStateFlow("صنعاء")
    val regDistrict = MutableStateFlow("")
    val regArea = MutableStateFlow("")
    val regPassword = MutableStateFlow("")
    val regError = MutableStateFlow<String?>(null)

    // Agency Settings
    val agencySettings: StateFlow<AgencySettings?> = repository.getAgencySettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Visa Types
    val activeVisaTypes: StateFlow<List<VisaType>> = repository.getActiveVisaTypes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVisaTypes: StateFlow<List<VisaType>> = repository.getAllVisaTypes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Payment Methods
    val activePaymentMethods: StateFlow<List<PaymentMethod>> = repository.getActivePaymentMethods()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPaymentMethods: StateFlow<List<PaymentMethod>> = repository.getAllPaymentMethods()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Agency Services (Dynamic)
    val allServices: StateFlow<List<AgencyService>> = repository.getAllServices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeServices: StateFlow<List<AgencyService>> = repository.getActiveServices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User Bookings
    val userBookings: StateFlow<List<ElectronicBooking>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getUserBookings(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User Notifications
    val userNotifications: StateFlow<List<AppNotification>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getUserNotifications(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationsCount: StateFlow<Int> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getUnreadNotificationsCount(user.id) else flowOf(0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Admin Bookings & Users
    val adminAllBookings: StateFlow<List<ElectronicBooking>> = repository.getAllBookings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsersList: StateFlow<List<User>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotificationsList: StateFlow<List<AppNotification>> = repository.getAllNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _adminStats = MutableStateFlow(AdminStats())
    val adminStats: StateFlow<AdminStats> = _adminStats.asStateFlow()

    // --- Admin Control Panel Password (Persistent via SharedPreferences) ---
    private val adminPrefs = application.getSharedPreferences("shajeen_admin_prefs", Context.MODE_PRIVATE)
    private val KEY_ADMIN_PIN = "admin_control_password"
    val defaultAdminPin = "77777"

    fun getAdminPin(): String {
        return adminPrefs.getString(KEY_ADMIN_PIN, defaultAdminPin) ?: defaultAdminPin
    }

    fun verifyAdminPin(input: String): Boolean {
        return input.trim() == getAdminPin()
    }

    fun changeAdminPin(currentPin: String, newPin: String): Result<Unit> {
        if (!verifyAdminPin(currentPin)) {
            return Result.failure(Exception("كلمة السر الحالية غير صحيحة"))
        }
        val cleanNewPin = newPin.trim()
        if (cleanNewPin.length < 4) {
            return Result.failure(Exception("كلمة السر الجديدة يجب ألا تقل عن 4 أرقام أو أحرف"))
        }
        adminPrefs.edit().putString(KEY_ADMIN_PIN, cleanNewPin).apply()
        return Result.success(Unit)
    }

    fun loginWithAdminPin(
        pin: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (verifyAdminPin(pin)) {
            viewModelScope.launch {
                // Find admin user in DB or set fallback admin session
                val admin = repository.getAllUsers().firstOrNull()?.find { it.role == "ADMIN" }
                    ?: User(
                        id = 1,
                        fullName = "مدير وكالة شجين",
                        phone = "770038009",
                        role = "ADMIN",
                        passwordHash = ""
                    )
                _currentUser.value = admin
                refreshAdminStats()
                onSuccess()
            }
        } else {
            onError("كلمة سر لوحة التحكم غير صحيحة! كلمة السر الافتراضية هي 77777")
        }
    }

    // --- Booking Wizard State ---
    val wizardCurrentStep = MutableStateFlow(1) // 1: Traveler, 2: Visa, 3: Docs, 4: Payment, 5: Review
    val bookingTravelerName = MutableStateFlow("")
    val bookingPhone = MutableStateFlow("")
    val bookingCountry = MutableStateFlow("اليمن")
    val bookingCity = MutableStateFlow("صنعاء")
    val bookingDestination = MutableStateFlow("")
    val bookingTravelDate = MutableStateFlow("")
    val bookingNotes = MutableStateFlow("")

    val selectedVisaType = MutableStateFlow<VisaType?>(null)
    val customVisaTypeName = MutableStateFlow("")

    val wizardDocItems = MutableStateFlow<List<UploadedDocItem>>(emptyList())

    val selectedPaymentMethod = MutableStateFlow<PaymentMethod?>(null)
    val transactionNumber = MutableStateFlow("")
    val paymentReceiptFile = MutableStateFlow<DocumentStorageManager.SavedFileInfo?>(null)

    val isSubmittingBooking = MutableStateFlow(false)
    val lastSubmittedBooking = MutableStateFlow<ElectronicBooking?>(null)

    init {
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
            refreshAdminStats()
        }
    }

    fun refreshAdminStats() {
        viewModelScope.launch {
            _adminStats.value = repository.getAdminStats()
        }
    }

    // --- Session & Auth Actions ---
    fun login(onSuccess: () -> Unit) {
        if (loginPhone.value.isBlank() || loginPassword.value.isBlank()) {
            loginError.value = "يرجى ملء جميع الحقول المطلوبة"
            return
        }
        viewModelScope.launch {
            isAuthLoading.value = true
            loginError.value = null
            val result = repository.login(loginPhone.value, loginPassword.value)
            isAuthLoading.value = false
            result.onSuccess { user ->
                _currentUser.value = user
                fillWizardWithUser(user)
                refreshAdminStats()
                onSuccess()
            }.onFailure { err ->
                loginError.value = err.message ?: "فشل تسجيل الدخول"
            }
        }
    }

    fun register(onSuccess: () -> Unit) {
        if (regFullName.value.isBlank() || regPhone.value.isBlank() || regPassword.value.isBlank() || regIdNumber.value.isBlank()) {
            regError.value = "يرجى ملء الاسم ورقم الهاتف ورقم الهوية وكلمة المرور"
            return
        }
        viewModelScope.launch {
            isAuthLoading.value = true
            regError.value = null
            val result = repository.register(
                fullName = regFullName.value,
                phone = regPhone.value,
                email = regEmail.value,
                idType = regIdType.value,
                idNumber = regIdNumber.value,
                country = regCountry.value,
                city = regCity.value,
                district = regDistrict.value,
                area = regArea.value,
                plainPass = regPassword.value
            )
            isAuthLoading.value = false
            result.onSuccess { user ->
                _currentUser.value = user
                fillWizardWithUser(user)
                refreshAdminStats()
                onSuccess()
            }.onFailure { err ->
                regError.value = err.message ?: "فشل إنشاء الحساب"
            }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        _currentUser.value = null
        resetBookingWizard()
        onLoggedOut()
    }

    fun switchToAdminDemo() {
        viewModelScope.launch {
            val admin = repository.login("770038009", "admin123").getOrNull()
            if (admin != null) {
                _currentUser.value = admin
                refreshAdminStats()
            }
        }
    }

    fun switchToUserDemo() {
        viewModelScope.launch {
            val user = repository.login("771234567", "user123").getOrNull()
            if (user != null) {
                _currentUser.value = user
                fillWizardWithUser(user)
            }
        }
    }

    private fun fillWizardWithUser(user: User) {
        bookingTravelerName.value = user.fullName
        bookingPhone.value = user.phone
        bookingCountry.value = user.country
        bookingCity.value = user.city
    }

    // --- Booking Wizard Flow ---
    fun startNewBooking() {
        resetBookingWizard()
        _currentUser.value?.let { fillWizardWithUser(it) }
        // Set default payment method if available
        if (activePaymentMethods.value.isNotEmpty()) {
            selectedPaymentMethod.value = activePaymentMethods.value.first()
        }
        wizardCurrentStep.value = 1
    }

    fun resetBookingWizard() {
        wizardCurrentStep.value = 1
        bookingDestination.value = ""
        bookingTravelDate.value = ""
        bookingNotes.value = ""
        selectedVisaType.value = null
        customVisaTypeName.value = ""
        wizardDocItems.value = emptyList()
        transactionNumber.value = ""
        paymentReceiptFile.value = null
        lastSubmittedBooking.value = null
        isSubmittingBooking.value = false
    }

    fun onVisaSelected(visa: VisaType) {
        selectedVisaType.value = visa
        // Load required docs for this visa dynamically from database
        viewModelScope.launch {
            val required = repository.getRequiredDocsForVisaSync(visa.id)
            wizardDocItems.value = required.map { req ->
                UploadedDocItem(
                    documentName = req.documentName,
                    isRequired = req.required,
                    fileInfo = null
                )
            }
        }
    }

    fun updateDocUpload(documentName: String, fileInfo: DocumentStorageManager.SavedFileInfo?) {
        wizardDocItems.value = wizardDocItems.value.map { item ->
            if (item.documentName == documentName) {
                item.copy(fileInfo = fileInfo)
            } else {
                item
            }
        }
    }

    fun addCustomDocToWizard(docName: String, fileInfo: DocumentStorageManager.SavedFileInfo) {
        wizardDocItems.value = wizardDocItems.value + UploadedDocItem(
            documentName = docName,
            isRequired = false,
            fileInfo = fileInfo
        )
    }

    fun submitBooking(onSuccess: (ElectronicBooking) -> Unit) {
        val user = _currentUser.value ?: return
        val visa = selectedVisaType.value
        val visaName = if (visa?.name == "تأشيرة أخرى" && customVisaTypeName.value.isNotBlank()) {
            customVisaTypeName.value.trim()
        } else {
            visa?.name ?: "تأشيرة عامة"
        }

        val payMethod = selectedPaymentMethod.value?.name ?: "Jib"
        val transNum = transactionNumber.value.trim()
        val receiptPath = paymentReceiptFile.value?.filePath ?: ""

        viewModelScope.launch {
            isSubmittingBooking.value = true
            val bookingNum = repository.generateNextBookingNumber()

            val booking = ElectronicBooking(
                bookingNumber = bookingNum,
                userId = user.id,
                fullName = bookingTravelerName.value.ifBlank { user.fullName },
                phone = bookingPhone.value.ifBlank { user.phone },
                country = bookingCountry.value,
                city = bookingCity.value,
                destination = bookingDestination.value,
                travelDate = bookingTravelDate.value,
                visaType = visaName,
                otherVisaType = if (visa?.name == "تأشيرة أخرى") customVisaTypeName.value else null,
                notes = bookingNotes.value.ifBlank { null },
                bookingStatus = "جديد",
                paymentMethod = payMethod,
                paymentStatus = "بانتظار التحقق",
                transactionNumber = transNum,
                paymentReceiptPath = receiptPath
            )

            // Collect uploaded documents
            val docEntities = wizardDocItems.value.mapNotNull { item ->
                item.fileInfo?.let { fileInfo ->
                    BookingDocument(
                        bookingId = 0L,
                        documentType = item.documentName,
                        fileUrl = fileInfo.filePath,
                        fileName = fileInfo.fileName,
                        fileSize = fileInfo.fileSize,
                        mimeType = fileInfo.mimeType,
                        verificationStatus = "بانتظار المراجعة"
                    )
                }
            }

            val newId = repository.createBooking(booking, docEntities)
            val created = booking.copy(id = newId)
            lastSubmittedBooking.value = created
            isSubmittingBooking.value = false
            refreshAdminStats()
            onSuccess(created)
        }
    }

    // --- WhatsApp & External Communication ---
    fun openWhatsAppBooking(context: Context, booking: ElectronicBooking) {
        val agencyWhatsapp = agencySettings.value?.whatsapp ?: "770038009"
        val cleanNumber = agencyWhatsapp.replace("+", "").replace(" ", "").trim()
        val formattedNumber = if (cleanNumber.startsWith("967")) cleanNumber else "967$cleanNumber"

        val message = """
السلام عليكم، أرسلت طلب حجز إلكتروني عبر تطبيق وكالة شجين للسفريات والسياحة.
رقم الحجز: ${booking.bookingNumber}
الاسم: ${booking.fullName}
نوع التأشيرة: ${booking.visaType}
رقم الهاتف: ${booking.phone}
يرجى مراجعة الطلب والتحقق من عملية الدفع.
        """.trimIndent()

        try {
            val encodedMessage = URLEncoder.encode(message, "UTF-8")
            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$formattedNumber&text=$encodedMessage")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر فتح تطبيق واتساب: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    fun openDialer(context: Context, phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phoneNumber.trim()}"))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر فتح لوحة الاتصال", Toast.LENGTH_SHORT).show()
        }
    }

    fun openMapLocation(context: Context) {
        val mapUrl = agencySettings.value?.mapUrl ?: "https://maps.google.com/?q=15.3375,44.2272"
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر فتح تطبيق الخرائط", Toast.LENGTH_SHORT).show()
        }
    }

    // --- Admin Operations ---
    fun adminVerifyPayment(bookingId: Long, isVerified: Boolean, notes: String? = null) {
        viewModelScope.launch {
            val status = if (isVerified) "تم التحقق" else "غير صالح"
            repository.updatePaymentStatus(bookingId, status, notes)
            refreshAdminStats()
        }
    }

    fun adminUpdateBookingStatus(bookingId: Long, newStatus: String, notes: String? = null) {
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, newStatus, notes)
            refreshAdminStats()
        }
    }

    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    // --- Admin Services Management ---
    fun adminAddService(
        title: String,
        subtitle: String,
        description: String,
        features: String,
        iconName: String,
        accentColorHex: String = "#0284C7"
    ) {
        viewModelScope.launch {
            repository.addService(
                AgencyService(
                    title = title.trim(),
                    subtitle = subtitle.trim(),
                    description = description.trim(),
                    features = features.trim(),
                    iconName = iconName,
                    accentColorHex = accentColorHex,
                    active = true
                )
            )
        }
    }

    fun adminUpdateService(service: AgencyService) {
        viewModelScope.launch {
            repository.updateService(service)
        }
    }

    fun adminDeleteService(serviceId: Long) {
        viewModelScope.launch {
            repository.deleteService(serviceId)
        }
    }

    // --- Admin Notifications Management ---
    fun adminSendNotification(
        title: String,
        message: String,
        targetUserId: Long? = null,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            if (targetUserId == null || targetUserId == 0L) {
                repository.sendNotificationToAll(title.trim(), message.trim())
            } else {
                repository.sendNotification(targetUserId, null, title.trim(), message.trim())
            }
            onSuccess()
        }
    }
}
