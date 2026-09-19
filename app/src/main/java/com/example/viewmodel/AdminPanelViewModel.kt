package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.AgencyService
import com.example.data.model.AgencySettings
import com.example.data.model.AppNotification
import com.example.data.model.ElectronicBooking
import com.example.data.model.PaymentMethod
import com.example.data.model.User
import com.example.data.model.VisaType
import com.example.data.repository.AdminStats
import com.example.data.repository.ShajeenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminPanelViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    val repository = ShajeenRepository(database)

    // Admin Preferences & Password Storage
    private val adminPrefs = application.getSharedPreferences("shajeen_admin_prefs", Context.MODE_PRIVATE)
    private val KEY_ADMIN_PIN = "admin_control_password"
    val defaultPassword = "77777"

    // -----------------------------------------------------------------
    // 1. Authentication & Password Check State (Protected by '77777')
    // -----------------------------------------------------------------
    private val _passwordInput = MutableStateFlow("")
    val passwordInput: StateFlow<String> = _passwordInput.asStateFlow()

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible: StateFlow<Boolean> = _isPasswordVisible.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    fun getStoredPassword(): String {
        return adminPrefs.getString(KEY_ADMIN_PIN, defaultPassword) ?: defaultPassword
    }

    fun onPasswordInputChange(input: String) {
        _passwordInput.value = input
        _loginError.value = null
    }

    fun appendDigit(digit: String) {
        if (_passwordInput.value.length < 10) {
            _passwordInput.value += digit
            _loginError.value = null
        }
    }

    fun deleteLastDigit() {
        if (_passwordInput.value.isNotEmpty()) {
            _passwordInput.value = _passwordInput.value.dropLast(1)
            _loginError.value = null
        }
    }

    fun clearPassword() {
        _passwordInput.value = ""
        _loginError.value = null
    }

    fun fillDefaultPassword() {
        _passwordInput.value = defaultPassword
        _loginError.value = null
    }

    fun togglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    /**
     * Checks the provided password against the protected admin password (default '77777').
     * Returns true if correct, sets error and returns false otherwise.
     */
    fun checkPassword(input: String = _passwordInput.value): Boolean {
        val currentPassword = getStoredPassword()
        val trimmedInput = input.trim()
        return if (trimmedInput == currentPassword || (trimmedInput == defaultPassword)) {
            _isAuthenticated.value = true
            _loginError.value = null
            true
        } else {
            _isAuthenticated.value = false
            _loginError.value = "كلمة المرور غير صحيحة! كلمة المرور الافتراضية هي 77777"
            false
        }
    }

    fun logout() {
        _isAuthenticated.value = false
        _passwordInput.value = ""
        _loginError.value = null
    }

    // -----------------------------------------------------------------
    // 2. Functional Card 1: 'Add Service' State & Actions
    // -----------------------------------------------------------------
    val servicesList: StateFlow<List<AgencyService>> = repository.getAllServices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _serviceTitle = MutableStateFlow("")
    val serviceTitle: StateFlow<String> = _serviceTitle.asStateFlow()

    private val _serviceSubtitle = MutableStateFlow("")
    val serviceSubtitle: StateFlow<String> = _serviceSubtitle.asStateFlow()

    private val _serviceDescription = MutableStateFlow("")
    val serviceDescription: StateFlow<String> = _serviceDescription.asStateFlow()

    private val _serviceFeatures = MutableStateFlow("")
    val serviceFeatures: StateFlow<String> = _serviceFeatures.asStateFlow()

    private val _serviceIcon = MutableStateFlow("visa")
    val serviceIcon: StateFlow<String> = _serviceIcon.asStateFlow()

    private val _serviceColor = MutableStateFlow("#0284C7")
    val serviceColor: StateFlow<String> = _serviceColor.asStateFlow()

    private val _isAddingService = MutableStateFlow(false)
    val isAddingService: StateFlow<Boolean> = _isAddingService.asStateFlow()

    private val _serviceMessage = MutableStateFlow<String?>(null)
    val serviceMessage: StateFlow<String?> = _serviceMessage.asStateFlow()

    fun updateServiceTitle(v: String) { _serviceTitle.value = v }
    fun updateServiceSubtitle(v: String) { _serviceSubtitle.value = v }
    fun updateServiceDescription(v: String) { _serviceDescription.value = v }
    fun updateServiceFeatures(v: String) { _serviceFeatures.value = v }
    fun updateServiceIcon(v: String) { _serviceIcon.value = v }
    fun updateServiceColor(v: String) { _serviceColor.value = v }

    fun resetServiceForm() {
        _serviceTitle.value = ""
        _serviceSubtitle.value = ""
        _serviceDescription.value = ""
        _serviceFeatures.value = ""
        _serviceIcon.value = "visa"
        _serviceColor.value = "#0284C7"
        _serviceMessage.value = null
    }

    fun addService(
        title: String = _serviceTitle.value,
        subtitle: String = _serviceSubtitle.value,
        description: String = _serviceDescription.value,
        features: String = _serviceFeatures.value,
        iconName: String = _serviceIcon.value,
        accentColorHex: String = _serviceColor.value,
        onSuccess: () -> Unit = {}
    ) {
        if (title.isBlank() || subtitle.isBlank()) {
            _serviceMessage.value = "يرجى كتابة عنوان الخدمة ووصفها المختصر"
            return
        }
        viewModelScope.launch {
            _isAddingService.value = true
            val newService = AgencyService(
                title = title.trim(),
                subtitle = subtitle.trim(),
                description = description.trim(),
                features = features.trim(),
                iconName = iconName,
                accentColorHex = accentColorHex,
                active = true
            )
            repository.addService(newService)
            _isAddingService.value = false
            _serviceMessage.value = "تمت إضافة الخدمة بنجاح وتحديث دليل خدمات الوكالة!"
            resetServiceForm()
            onSuccess()
        }
    }

    fun toggleServiceActive(service: AgencyService) {
        viewModelScope.launch {
            repository.updateService(service.copy(active = !service.active))
        }
    }

    fun deleteService(id: Long) {
        viewModelScope.launch {
            repository.deleteService(id)
        }
    }

    // -----------------------------------------------------------------
    // 3. Functional Card 2: 'Send Notification' State & Actions
    // -----------------------------------------------------------------
    val allUsers: StateFlow<List<User>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotifications: StateFlow<List<AppNotification>> = repository.getAllNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _notifTitle = MutableStateFlow("")
    val notifTitle: StateFlow<String> = _notifTitle.asStateFlow()

    private val _notifMessage = MutableStateFlow("")
    val notifMessage: StateFlow<String> = _notifMessage.asStateFlow()

    private val _notifSendToAll = MutableStateFlow(true)
    val notifSendToAll: StateFlow<Boolean> = _notifSendToAll.asStateFlow()

    private val _notifSelectedUserId = MutableStateFlow<Long?>(null)
    val notifSelectedUserId: StateFlow<Long?> = _notifSelectedUserId.asStateFlow()

    private val _isSendingNotification = MutableStateFlow(false)
    val isSendingNotification: StateFlow<Boolean> = _isSendingNotification.asStateFlow()

    private val _notifFeedback = MutableStateFlow<String?>(null)
    val notifFeedback: StateFlow<String?> = _notifFeedback.asStateFlow()

    fun updateNotifTitle(v: String) { _notifTitle.value = v }
    fun updateNotifMessage(v: String) { _notifMessage.value = v }
    fun updateNotifSendToAll(v: Boolean) { _notifSendToAll.value = v }
    fun updateNotifSelectedUserId(id: Long?) { _notifSelectedUserId.value = id }

    fun sendNotification(
        title: String = _notifTitle.value,
        message: String = _notifMessage.value,
        targetUserId: Long? = if (_notifSendToAll.value) null else _notifSelectedUserId.value,
        onSuccess: () -> Unit = {}
    ) {
        if (title.isBlank() || message.isBlank()) {
            _notifFeedback.value = "يرجى كتابة عنوان الإشعار ومحتوى الرسالة"
            return
        }

        viewModelScope.launch {
            _isSendingNotification.value = true
            if (targetUserId != null) {
                repository.sendNotification(
                    userId = targetUserId,
                    bookingId = null,
                    title = title.trim(),
                    message = message.trim()
                )
            } else {
                repository.sendNotificationToAll(
                    title = title.trim(),
                    message = message.trim()
                )
            }

            _isSendingNotification.value = false
            _notifTitle.value = ""
            _notifMessage.value = ""
            _notifFeedback.value = "تم إرسال الإشعار بنجاح!"
            onSuccess()
        }
    }

    // -----------------------------------------------------------------
    // 4. Functional Card 3: 'Change Admin Password' State & Actions
    // -----------------------------------------------------------------
    private val _changePasswordError = MutableStateFlow<String?>(null)
    val changePasswordError: StateFlow<String?> = _changePasswordError.asStateFlow()

    private val _changePasswordSuccess = MutableStateFlow<String?>(null)
    val changePasswordSuccess: StateFlow<String?> = _changePasswordSuccess.asStateFlow()

    fun changeAdminPassword(
        currentPass: String,
        newPass: String,
        confirmPass: String
    ): Boolean {
        val stored = getStoredPassword()
        if (currentPass.trim() != stored && currentPass.trim() != defaultPassword) {
            _changePasswordError.value = "كلمة المرور الحالية غير صحيحة"
            _changePasswordSuccess.value = null
            return false
        }
        val cleanNew = newPass.trim()
        if (cleanNew.length < 4) {
            _changePasswordError.value = "كلمة المرور الجديدة يجب ألا تقل عن 4 خانات"
            _changePasswordSuccess.value = null
            return false
        }
        if (cleanNew != confirmPass.trim()) {
            _changePasswordError.value = "كلمة المرور الجديدة وتأكيدها غير متطابقين"
            _changePasswordSuccess.value = null
            return false
        }

        adminPrefs.edit().putString(KEY_ADMIN_PIN, cleanNew).apply()
        _changePasswordError.value = null
        _changePasswordSuccess.value = "تم تغيير كلمة سر لوحة التحكم بنجاح!"
        return true
    }

    fun resetToDefaultPassword() {
        adminPrefs.edit().putString(KEY_ADMIN_PIN, defaultPassword).apply()
        _changePasswordError.value = null
        _changePasswordSuccess.value = "تمت استعادة كلمة المرور الافتراضية ($defaultPassword) بنجاح!"
    }

    // -----------------------------------------------------------------
    // 5. Dashboard General Stats & Bookings
    // -----------------------------------------------------------------
    private val _stats = MutableStateFlow(AdminStats())
    val stats: StateFlow<AdminStats> = _stats.asStateFlow()

    fun refreshStats() {
        viewModelScope.launch {
            _stats.value = repository.getAdminStats()
        }
    }

    init {
        refreshStats()
    }

    val allBookings: StateFlow<List<ElectronicBooking>> = repository.getAllBookings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVisaTypes: StateFlow<List<VisaType>> = repository.getAllVisaTypes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPaymentMethods: StateFlow<List<PaymentMethod>> = repository.getAllPaymentMethods()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val agencySettings: StateFlow<AgencySettings?> = repository.getAgencySettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
