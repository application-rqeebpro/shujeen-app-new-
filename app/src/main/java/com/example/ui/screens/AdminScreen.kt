package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgencyService
import com.example.data.model.AgencySettings
import com.example.data.model.AppNotification
import com.example.data.model.ElectronicBooking
import com.example.data.model.PaymentMethod
import com.example.data.model.User
import com.example.data.model.VisaType
import com.example.ui.components.PrimaryButton
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.viewmodel.ShajeenViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: ShajeenViewModel,
    onNavigateBack: () -> Unit,
    onBookingClick: (Long) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(
        "الإحصائيات",
        "الحجوزات",
        "إدارة الخدمات",
        "إرسال الإشعارات",
        "كلمة سر اللوحة",
        "التأشيرات",
        "وسائل الدفع",
        "بيانات الوكالة"
    )

    val stats by viewModel.adminStats.collectAsState()
    val allBookings by viewModel.adminAllBookings.collectAsState()
    val allServices by viewModel.allServices.collectAsState()
    val allUsers by viewModel.allUsersList.collectAsState()
    val allNotifications by viewModel.allNotificationsList.collectAsState()
    val allVisas by viewModel.allVisaTypes.collectAsState()
    val allPayments by viewModel.allPaymentMethods.collectAsState()
    val agencySettings by viewModel.agencySettings.collectAsState()

    // Search and filter for bookings
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("الكل") }

    // Dialogs for Admin Management
    var showAddVisaDialog by remember { mutableStateOf(false) }
    var newVisaName by remember { mutableStateOf("") }
    var newVisaDesc by remember { mutableStateOf("") }

    var editingPaymentMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    var editPayAccNum by remember { mutableStateOf("") }
    var editPayInstructions by remember { mutableStateOf("") }

    var showEditSettingsDialog by remember { mutableStateOf(false) }
    var editAgencyAddress by remember { mutableStateOf(agencySettings?.address ?: "") }
    var editAgencyPhones by remember { mutableStateOf(agencySettings?.phoneNumbers ?: "") }
    var editAgencyWhatsapp by remember { mutableStateOf(agencySettings?.whatsapp ?: "") }

    // Services Management State
    var showServiceDialog by remember { mutableStateOf(false) }
    var editingService by remember { mutableStateOf<AgencyService?>(null) }
    var serviceTitleInput by remember { mutableStateOf("") }
    var serviceSubtitleInput by remember { mutableStateOf("") }
    var serviceDescInput by remember { mutableStateOf("") }
    var serviceFeaturesInput by remember { mutableStateOf("") }
    var serviceIconInput by remember { mutableStateOf("visa") }
    var serviceColorInput by remember { mutableStateOf("#0284C7") }

    // Notifications Management State
    var notifTitleInput by remember { mutableStateOf("") }
    var notifMessageInput by remember { mutableStateOf("") }
    var notifSendToAll by remember { mutableStateOf(true) }
    var notifSelectedUserId by remember { mutableStateOf<Long?>(null) }
    var isSendingNotification by remember { mutableStateOf(false) }

    // Control Panel Password State
    var currentPinInput by remember { mutableStateOf("") }
    var newPinInput by remember { mutableStateOf("") }
    var confirmNewPinInput by remember { mutableStateOf("") }
    var pinErrorMessage by remember { mutableStateOf<String?>(null) }
    var pinSuccessMessage by remember { mutableStateOf<String?>(null) }
    var currentPinVisible by remember { mutableStateOf(false) }
    var newPinVisible by remember { mutableStateOf(false) }
    var showResetPinDialog by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale.getDefault()) }

    LaunchedEffect(Unit) {
        viewModel.refreshAdminStats()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "لوحة تحكم إدارة وكالة شجين",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = BrandBackgroundStart
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("admin_screen")
        ) {
            // Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = BrandPrimaryBlue,
                edgePadding = 16.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                when (index) {
                                    0 -> Icon(Icons.Default.Dashboard, contentDescription = null, modifier = Modifier.size(16.dp))
                                    1 -> Icon(Icons.Default.ConfirmationNumber, contentDescription = null, modifier = Modifier.size(16.dp))
                                    2 -> Icon(Icons.Default.MiscellaneousServices, contentDescription = null, modifier = Modifier.size(16.dp))
                                    3 -> Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                                    4 -> Icon(Icons.Default.VpnKey, contentDescription = null, modifier = Modifier.size(16.dp))
                                    5 -> Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(16.dp))
                                    6 -> Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                                    7 -> Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                // -------------------------------------------------------------
                // 0. STATS TAB
                // -------------------------------------------------------------
                0 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text("إحصائيات الوكالة العامة", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                StatCard("إجمالي الحجوزات", "${stats.totalBookings}", Icons.Default.ConfirmationNumber, BrandPrimaryBlue, Modifier.weight(1f))
                                StatCard("إجمالي العملاء", "${stats.totalUsers}", Icons.Default.People, BrandPrimaryDarkBlue, Modifier.weight(1f))
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                StatCard("حجوزات جديدة", "${stats.newBookings}", Icons.Default.FiberNew, StatusNew, Modifier.weight(1f))
                                StatCard("قيد المراجعة", "${stats.inReviewBookings}", Icons.Default.HourglassTop, StatusReview, Modifier.weight(1f))
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                StatCard("مدفوعات معلقة", "${stats.pendingPayments}", Icons.Default.PendingActions, StatusPendingPayment, Modifier.weight(1f))
                                StatCard("حجوزات مؤكدة", "${stats.confirmedBookings}", Icons.Default.CheckCircle, StatusConfirmed, Modifier.weight(1f))
                            }
                        }

                        item {
                            StatCard("حجوزات مكتملة", "${stats.completedBookings}", Icons.Default.DoneAll, StatusCompleted, Modifier.fillMaxWidth())
                        }
                    }
                }

                // -------------------------------------------------------------
                // 1. BOOKINGS MANAGEMENT TAB
                // -------------------------------------------------------------
                1 -> {
                    val filteredBookings = allBookings.filter { b ->
                        val matchesQuery = b.bookingNumber.contains(searchQuery, ignoreCase = true) ||
                                b.fullName.contains(searchQuery, ignoreCase = true) ||
                                b.phone.contains(searchQuery)
                        val matchesStatus = selectedStatusFilter == "الكل" || b.bookingStatus == selectedStatusFilter
                        matchesQuery && matchesStatus
                    }

                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        // Search bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("بحث برقم الحجز أو اسم العميل أو الهاتف...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Status filter chips
                        val filterList = listOf("الكل", "جديد", "قيد المراجعة", "بانتظار التحقق من الدفع", "تم تأكيد الحجز", "مكتمل", "مرفوض")
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(filterList) { status ->
                                FilterChip(
                                    selected = selectedStatusFilter == status,
                                    onClick = { selectedStatusFilter = status },
                                    label = { Text(status) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (filteredBookings.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("لا توجد نتائج مطابقة", color = BrandTextSecondary)
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(filteredBookings) { booking ->
                                    BookingListItemCard(
                                        booking = booking,
                                        onClick = { onBookingClick(booking.id) }
                                    )
                                }
                            }
                        }
                    }
                }

                // -------------------------------------------------------------
                // 2. SERVICES MANAGEMENT TAB (إضافة وإدارة الخدمات)
                // -------------------------------------------------------------
                2 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "دليل خدمات وكالة شجين",
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = BrandHeaderColor
                                            )
                                            Text(
                                                text = "يمكنك إضافة خدمات جديدة وتعديل تفاصيل الخدمات والمميزات المعروضة للعملاء بالتطبيق.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = BrandTextSecondary
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Button(
                                            onClick = {
                                                editingService = null
                                                serviceTitleInput = ""
                                                serviceSubtitleInput = ""
                                                serviceDescInput = ""
                                                serviceFeaturesInput = ""
                                                serviceIconInput = "visa"
                                                serviceColorInput = "#0284C7"
                                                showServiceDialog = true
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryBlue),
                                            modifier = Modifier.testTag("admin_add_service_btn")
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("إضافة خدمة")
                                        }
                                    }
                                }
                            }
                        }

                        if (allServices.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("لا توجد خدمات مضافة حالياً. اضغط على 'إضافة خدمة' لإضافة خدمة جديدة.", color = BrandTextSecondary)
                                }
                            }
                        } else {
                            items(allServices) { service ->
                                val serviceColor = try {
                                    Color(android.graphics.Color.parseColor(service.accentColorHex))
                                } catch (e: Exception) {
                                    BrandPrimaryBlue
                                }

                                val serviceIconVector = when (service.iconName) {
                                    "visa" -> Icons.Default.Public
                                    "umrah" -> Icons.Default.Mosque
                                    "flight" -> Icons.Default.FlightTakeoff
                                    "bus" -> Icons.Default.DirectionsBus
                                    "tourism" -> Icons.Default.BeachAccess
                                    else -> Icons.Default.Star
                                }

                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(2.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(44.dp)
                                                    .clip(CircleShape)
                                                    .background(serviceColor.copy(alpha = 0.15f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = serviceIconVector,
                                                    contentDescription = null,
                                                    tint = serviceColor,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }

                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = service.title,
                                                        fontWeight = FontWeight.Bold,
                                                        style = MaterialTheme.typography.titleMedium,
                                                        color = BrandHeaderColor
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = if (service.active) StatusConfirmed.copy(alpha = 0.15f) else StatusRejected.copy(alpha = 0.15f)
                                                    ) {
                                                        Text(
                                                            text = if (service.active) "نشط" else "معطل",
                                                            color = if (service.active) StatusConfirmed else StatusRejected,
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }
                                                Text(
                                                    text = service.subtitle,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = BrandTextSecondary
                                                )
                                            }

                                            // Action Buttons
                                            Row {
                                                IconButton(
                                                    onClick = {
                                                        viewModel.adminUpdateService(service.copy(active = !service.active))
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = if (service.active) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                                        contentDescription = "تبديل الحالة",
                                                        tint = if (service.active) StatusConfirmed else BrandTextSecondary
                                                    )
                                                }

                                                IconButton(
                                                    onClick = {
                                                        editingService = service
                                                        serviceTitleInput = service.title
                                                        serviceSubtitleInput = service.subtitle
                                                        serviceDescInput = service.description
                                                        serviceFeaturesInput = service.features
                                                        serviceIconInput = service.iconName
                                                        serviceColorInput = service.accentColorHex
                                                        showServiceDialog = true
                                                    }
                                                ) {
                                                    Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = BrandPrimaryBlue)
                                                }

                                                IconButton(
                                                    onClick = {
                                                        viewModel.adminDeleteService(service.id)
                                                        Toast.makeText(context, "تم حذف الخدمة", Toast.LENGTH_SHORT).show()
                                                    }
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = StatusRejected)
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = service.description,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = BrandTextPrimary
                                        )

                                        if (service.features.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Surface(
                                                shape = RoundedCornerShape(10.dp),
                                                color = Color(0xFFF8FAFC),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    service.features.split("\n").filter { it.isNotBlank() }.forEach { feat ->
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.CheckCircle,
                                                                contentDescription = null,
                                                                tint = serviceColor,
                                                                modifier = Modifier.size(14.dp)
                                                            )
                                                            Text(
                                                                text = feat.trim(),
                                                                style = MaterialTheme.typography.bodySmall,
                                                                color = BrandTextPrimary
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // -------------------------------------------------------------
                // 3. SEND NOTIFICATIONS TAB (إرسال الإشعارات)
                // -------------------------------------------------------------
                3 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Send Notification Card
                        item {
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(3.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(BrandPrimaryBlue.copy(alpha = 0.12f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.NotificationsActive,
                                                contentDescription = null,
                                                tint = BrandPrimaryBlue,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = "إرسال إشعار فوري للعملاء",
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = BrandHeaderColor
                                            )
                                            Text(
                                                text = "يمكنك إرسال عروض وتنبيهات مباشرة لجميع المستخدمين أو لعميل محدد.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = BrandTextSecondary
                                            )
                                        }
                                    }

                                    Divider(color = BrandCardBorder)

                                    // Target Selection
                                    Text("المستلمون:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        FilterChip(
                                            selected = notifSendToAll,
                                            onClick = {
                                                notifSendToAll = true
                                                notifSelectedUserId = null
                                            },
                                            label = { Text("جميع العملاء (${allUsers.filter { it.role != "ADMIN" }.size})") },
                                            leadingIcon = {
                                                if (notifSendToAll) Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                            }
                                        )

                                        FilterChip(
                                            selected = !notifSendToAll,
                                            onClick = {
                                                notifSendToAll = false
                                                if (notifSelectedUserId == null && allUsers.isNotEmpty()) {
                                                    notifSelectedUserId = allUsers.firstOrNull { it.role != "ADMIN" }?.id
                                                }
                                            },
                                            label = { Text("عميل محدد") },
                                            leadingIcon = {
                                                if (!notifSendToAll) Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                                            }
                                        )
                                    }

                                    // Specific user selector if chosen
                                    if (!notifSendToAll) {
                                        val clientUsers = allUsers.filter { it.role != "ADMIN" }
                                        if (clientUsers.isEmpty()) {
                                            Text("لا يوجد عملاء مسجلون حالياً", color = StatusRejected, fontSize = 12.sp)
                                        } else {
                                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text("اختر العميل:", style = MaterialTheme.typography.bodySmall, color = BrandTextSecondary)
                                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    items(clientUsers) { u ->
                                                        FilterChip(
                                                            selected = notifSelectedUserId == u.id,
                                                            onClick = { notifSelectedUserId = u.id },
                                                            label = { Text("${u.fullName} (${u.phone})") }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Notification Title
                                    OutlinedTextField(
                                        value = notifTitleInput,
                                        onValueChange = { notifTitleInput = it },
                                        label = { Text("عنوان الإشعار *") },
                                        placeholder = { Text("مثال: عروض عمرة رجب وشعبان بتخفيضات خاصة") },
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("admin_notif_title_input")
                                    )

                                    // Notification Body
                                    OutlinedTextField(
                                        value = notifMessageInput,
                                        onValueChange = { notifMessageInput = it },
                                        label = { Text("نص الإشعار *") },
                                        placeholder = { Text("اكتب تفاصيل الإشعار أو العرض الترويجي للعملاء...") },
                                        minLines = 3,
                                        maxLines = 5,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("admin_notif_body_input")
                                    )

                                    // Send Button
                                    PrimaryButton(
                                        text = if (notifSendToAll) "إرسال الإشعار لجميع العملاء" else "إرسال الإشعار للعميل",
                                        onClick = {
                                            if (notifTitleInput.isBlank() || notifMessageInput.isBlank()) {
                                                Toast.makeText(context, "يرجى كتابة عنوان الإشعار ونصه", Toast.LENGTH_SHORT).show()
                                                return@PrimaryButton
                                            }
                                            isSendingNotification = true
                                            viewModel.adminSendNotification(
                                                title = notifTitleInput.trim(),
                                                message = notifMessageInput.trim(),
                                                targetUserId = if (notifSendToAll) null else notifSelectedUserId,
                                                onSuccess = {
                                                    isSendingNotification = false
                                                    notifTitleInput = ""
                                                    notifMessageInput = ""
                                                    Toast.makeText(context, "تم إرسال الإشعار بنجاح", Toast.LENGTH_LONG).show()
                                                }
                                            )
                                        },
                                        isLoading = isSendingNotification,
                                        icon = Icons.Default.Send,
                                        modifier = Modifier.testTag("admin_send_notification_btn")
                                    )
                                }
                            }
                        }

                        // Sent Notifications Log
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "سجل الإشعارات المرسلة (${allNotifications.size})",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = BrandHeaderColor
                                )
                            }
                        }

                        if (allNotifications.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("لا توجد إشعارات مرسلة بعد", color = BrandTextSecondary)
                                }
                            }
                        } else {
                            items(allNotifications) { notif ->
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(1.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.Top,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(BrandPrimaryBlue.copy(alpha = 0.12f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Notifications,
                                                contentDescription = null,
                                                tint = BrandPrimaryBlue,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = notif.title,
                                                    fontWeight = FontWeight.Bold,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    color = BrandHeaderColor
                                                )
                                                Text(
                                                    text = dateFormat.format(Date(notif.createdAt)),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = BrandTextSecondary
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = notif.message,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = BrandTextPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // -------------------------------------------------------------
                // 4. CONTROL PANEL PASSWORD TAB (تغيير كلمة السر للوحة التحكم)
                // -------------------------------------------------------------
                4 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(3.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(BrandPrimaryDarkBlue.copy(alpha = 0.12f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = BrandPrimaryDarkBlue,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = "تغيير كلمة سر لوحة التحكم",
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = BrandHeaderColor
                                            )
                                            Text(
                                                text = "تستخدم كلمة السر هذه للدخول إلى لوحة التحكم من أعلى شاشة تسجيل الدخول.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = BrandTextSecondary
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFFEF3C7),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(20.dp))
                                            Text(
                                                text = "كلمة السر الافتراضية للوحة التحكم هي: 77777\nيمكنك تغييرها بأي وقت وحفظها بشكل آمن.",
                                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF92400E)),
                                                lineHeight = 18.sp
                                            )
                                        }
                                    }

                                    if (pinErrorMessage != null) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color(0xFFFEE2E2),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = StatusRejected)
                                                Text(text = pinErrorMessage ?: "", color = StatusRejected, style = MaterialTheme.typography.bodySmall)
                                            }
                                        }
                                    }

                                    if (pinSuccessMessage != null) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color(0xFFDCFCE7),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusConfirmed)
                                                Text(text = pinSuccessMessage ?: "", color = StatusConfirmed, style = MaterialTheme.typography.bodySmall)
                                            }
                                        }
                                    }

                                    // Current PIN
                                    OutlinedTextField(
                                        value = currentPinInput,
                                        onValueChange = {
                                            currentPinInput = it
                                            pinErrorMessage = null
                                            pinSuccessMessage = null
                                        },
                                        label = { Text("كلمة السر الحالية للوحة التحكم *") },
                                        singleLine = true,
                                        visualTransformation = if (currentPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                        trailingIcon = {
                                            IconButton(onClick = { currentPinVisible = !currentPinVisible }) {
                                                Icon(
                                                    imageVector = if (currentPinVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                    contentDescription = null
                                                )
                                            }
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("admin_current_pin_input")
                                    )

                                    // New PIN
                                    OutlinedTextField(
                                        value = newPinInput,
                                        onValueChange = {
                                            newPinInput = it
                                            pinErrorMessage = null
                                            pinSuccessMessage = null
                                        },
                                        label = { Text("كلمة السر الجديدة (4 خانات على الأقل) *") },
                                        singleLine = true,
                                        visualTransformation = if (newPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                        trailingIcon = {
                                            IconButton(onClick = { newPinVisible = !newPinVisible }) {
                                                Icon(
                                                    imageVector = if (newPinVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                    contentDescription = null
                                                )
                                            }
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("admin_new_pin_input")
                                    )

                                    // Confirm New PIN
                                    OutlinedTextField(
                                        value = confirmNewPinInput,
                                        onValueChange = {
                                            confirmNewPinInput = it
                                            pinErrorMessage = null
                                            pinSuccessMessage = null
                                        },
                                        label = { Text("تأكيد كلمة السر الجديدة *") },
                                        singleLine = true,
                                        visualTransformation = if (newPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("admin_confirm_new_pin_input")
                                    )

                                    // Save Button
                                    PrimaryButton(
                                        text = "حفظ كلمة السر الجديدة",
                                        onClick = {
                                            if (newPinInput.trim() != confirmNewPinInput.trim()) {
                                                pinErrorMessage = "كلمة السر الجديدة وتأكيدها غير متطابقين"
                                                return@PrimaryButton
                                            }
                                            val result = viewModel.changeAdminPin(
                                                currentPin = currentPinInput.trim(),
                                                newPin = newPinInput.trim()
                                            )
                                            result.fold(
                                                onSuccess = {
                                                    pinSuccessMessage = "تم تحديث وحفظ كلمة سر لوحة التحكم بنجاح!"
                                                    pinErrorMessage = null
                                                    currentPinInput = ""
                                                    newPinInput = ""
                                                    confirmNewPinInput = ""
                                                    Toast.makeText(context, "تم حفظ كلمة السر الجديدة", Toast.LENGTH_SHORT).show()
                                                },
                                                onFailure = { err ->
                                                    pinErrorMessage = err.message ?: "حدث خطأ أثناء تغيير كلمة السر"
                                                    pinSuccessMessage = null
                                                }
                                            )
                                        },
                                        icon = Icons.Default.VpnKey,
                                        modifier = Modifier.testTag("admin_save_new_pin_button")
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Reset to Default PIN
                                    OutlinedButton(
                                        onClick = { showResetPinDialog = true },
                                        shape = RoundedCornerShape(12.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("استعادة كلمة السر الافتراضية (77777)")
                                    }
                                }
                            }
                        }
                    }
                }

                // -------------------------------------------------------------
                // 5. VISAS MANAGEMENT TAB
                // -------------------------------------------------------------
                5 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("أنواع التأشيرات المتاحة", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Button(
                                    onClick = { showAddVisaDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryBlue)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("إضافة تأشيرة")
                                }
                            }
                        }

                        items(allVisas) { visa ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(visa.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                        Text(visa.description, style = MaterialTheme.typography.bodySmall, color = BrandTextSecondary)
                                    }

                                    Row {
                                        IconButton(
                                            onClick = {
                                                coroutineScope.launch {
                                                    viewModel.repository.updateVisaType(visa.copy(active = !visa.active))
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (visa.active) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                                contentDescription = "تفعيل/تعطيل",
                                                tint = if (visa.active) StatusConfirmed else StatusRejected
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                coroutineScope.launch {
                                                    viewModel.repository.deleteVisaType(visa.id)
                                                    Toast.makeText(context, "تم حذف التأشيرة", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = StatusRejected)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // -------------------------------------------------------------
                // 6. PAYMENT METHODS MANAGEMENT TAB
                // -------------------------------------------------------------
                6 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text("إدارة وسائل الدفع والحسابات", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text("يمكنك تعديل أرقام الحسابات والتعليمات المعروضة للعميل", style = MaterialTheme.typography.bodySmall, color = BrandTextSecondary)
                        }

                        items(allPayments) { pm ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(pm.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                        IconButton(
                                            onClick = {
                                                editingPaymentMethod = pm
                                                editPayAccNum = pm.accountNumber
                                                editPayInstructions = pm.instructions
                                            }
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = BrandPrimaryBlue)
                                        }
                                    }

                                    Surface(
                                        color = Color(0xFFF1F5F9),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("رقم الحساب / المحفظة:")
                                            Text(pm.accountNumber, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    if (pm.instructions.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(pm.instructions, style = MaterialTheme.typography.bodySmall, color = BrandTextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }

                // -------------------------------------------------------------
                // 7. AGENCY SETTINGS TAB
                // -------------------------------------------------------------
                7 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("بيانات الوكالة المعروضة للعملاء", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                        IconButton(
                                            onClick = {
                                                editAgencyAddress = agencySettings?.address ?: ""
                                                editAgencyPhones = agencySettings?.phoneNumbers ?: ""
                                                editAgencyWhatsapp = agencySettings?.whatsapp ?: ""
                                                showEditSettingsDialog = true
                                            }
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = BrandPrimaryBlue)
                                        }
                                    }

                                    Divider(color = BrandCardBorder)

                                    DetailRow("اسم الوكالة", agencySettings?.agencyName ?: "وكالة شجين للسفريات والسياحة")
                                    DetailRow("العنوان", agencySettings?.address ?: "صنعاء - شارع خولان - جوار السلامي لمواد البناء")
                                    DetailRow("أرقام الهواتف", agencySettings?.phoneNumbers ?: "")
                                    DetailRow("رقم الواتساب", agencySettings?.whatsapp ?: "770038009")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // -----------------------------------------------------------------
    // DIALOGS
    // -----------------------------------------------------------------

    // Dialog: Add / Edit Service (إضافة وتعديل الخدمات)
    if (showServiceDialog) {
        AlertDialog(
            onDismissRequest = { showServiceDialog = false },
            title = {
                Text(
                    text = if (editingService == null) "إضافة خدمة جديدة" else "تعديل الخدمة",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = serviceTitleInput,
                        onValueChange = { serviceTitleInput = it },
                        label = { Text("عنوان الخدمة *") },
                        placeholder = { Text("مثال: حجز وإصدار تذاكر الطيران") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = serviceSubtitleInput,
                        onValueChange = { serviceSubtitleInput = it },
                        label = { Text("وصف مختصر *") },
                        placeholder = { Text("مثال: على أفضل خطوط الطيران العالمية والمحلية") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = serviceDescInput,
                        onValueChange = { serviceDescInput = it },
                        label = { Text("التفاصيل الكاملة للخدمة *") },
                        minLines = 2,
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = serviceFeaturesInput,
                        onValueChange = { serviceFeaturesInput = it },
                        label = { Text("المميزات (اكتب كل ميزة في سطر منفصل)") },
                        placeholder = { Text("ميزة 1\nميزة 2\nميزة 3") },
                        minLines = 3,
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Icon Selection Chips
                    Text("أيقونة الخدمة:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    val iconOptions = listOf(
                        "visa" to "تأشيرات",
                        "umrah" to "عمرة وحج",
                        "flight" to "طيران",
                        "bus" to "نقل بري",
                        "tourism" to "سياحة",
                        "other" to "عامة"
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(iconOptions) { (key, label) ->
                            FilterChip(
                                selected = serviceIconInput == key,
                                onClick = { serviceIconInput = key },
                                label = { Text(label, fontSize = 11.sp) }
                            )
                        }
                    }

                    // Color Selection Chips
                    Text("لون التمييز:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    val colorOptions = listOf(
                        "#0284C7" to "أزرق شجين",
                        "#10B981" to "أخضر",
                        "#F59E0B" to "برتقالي",
                        "#8B5CF6" to "بنفسجي",
                        "#1E3A8A" to "كحلي داكن"
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(colorOptions) { (hex, label) ->
                            val chipColor = try { Color(android.graphics.Color.parseColor(hex)) } catch (e: Exception) { BrandPrimaryBlue }
                            FilterChip(
                                selected = serviceColorInput == hex,
                                onClick = { serviceColorInput = hex },
                                label = {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(chipColor))
                                        Text(label, fontSize = 11.sp)
                                    }
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (serviceTitleInput.isBlank() || serviceSubtitleInput.isBlank()) {
                            Toast.makeText(context, "يرجى تعبئة عنوان الخدمة ووصفها", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (editingService == null) {
                            viewModel.adminAddService(
                                title = serviceTitleInput,
                                subtitle = serviceSubtitleInput,
                                description = serviceDescInput,
                                features = serviceFeaturesInput,
                                iconName = serviceIconInput,
                                accentColorHex = serviceColorInput
                            )
                            Toast.makeText(context, "تمت إضافة الخدمة بنجاح", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.adminUpdateService(
                                editingService!!.copy(
                                    title = serviceTitleInput.trim(),
                                    subtitle = serviceSubtitleInput.trim(),
                                    description = serviceDescInput.trim(),
                                    features = serviceFeaturesInput.trim(),
                                    iconName = serviceIconInput,
                                    accentColorHex = serviceColorInput
                                )
                            )
                            Toast.makeText(context, "تم تحديث بيانات الخدمة", Toast.LENGTH_SHORT).show()
                        }
                        showServiceDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryBlue)
                ) {
                    Text(if (editingService == null) "إضافة الخدمة" else "حفظ التعديلات")
                }
            },
            dismissButton = {
                TextButton(onClick = { showServiceDialog = false }) { Text("إلغاء") }
            }
        )
    }

    // Dialog: Reset Admin PIN
    if (showResetPinDialog) {
        AlertDialog(
            onDismissRequest = { showResetPinDialog = false },
            title = { Text("استعادة كلمة السر الافتراضية", fontWeight = FontWeight.Bold) },
            text = {
                Text("هل أنت متأكد من رغبتك في إعادة ضبط كلمة سر لوحة التحكم إلى القيمة الافتراضية: 77777؟")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.changeAdminPin(
                            currentPin = viewModel.getAdminPin(),
                            newPin = viewModel.defaultAdminPin
                        )
                        showResetPinDialog = false
                        pinSuccessMessage = "تمت استعادة كلمة السر الافتراضية (77777) بنجاح!"
                        pinErrorMessage = null
                        Toast.makeText(context, "تم ضبط كلمة السر إلى 77777", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRejected)
                ) {
                    Text("نعم، استعادة إلى 77777")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetPinDialog = false }) { Text("إلغاء") }
            }
        )
    }

    // Dialog: Add Visa
    if (showAddVisaDialog) {
        AlertDialog(
            onDismissRequest = { showAddVisaDialog = false },
            title = { Text("إضافة نوع تأشيرة جديد", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newVisaName,
                        onValueChange = { newVisaName = it },
                        label = { Text("اسم التأشيرة") },
                        placeholder = { Text("مثال: تأشيرة دراسية") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newVisaDesc,
                        onValueChange = { newVisaDesc = it },
                        label = { Text("الوصف") },
                        placeholder = { Text("وصف الشروط والمتطلبات") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newVisaName.isNotBlank()) {
                            coroutineScope.launch {
                                viewModel.repository.addVisaType(
                                    VisaType(name = newVisaName.trim(), description = newVisaDesc.trim())
                                )
                                newVisaName = ""
                                newVisaDesc = ""
                                showAddVisaDialog = false
                                Toast.makeText(context, "تمت إضافة التأشيرة", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryBlue)
                ) {
                    Text("إضافة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddVisaDialog = false }) { Text("إلغاء") }
            }
        )
    }

    // Dialog: Edit Payment Method
    editingPaymentMethod?.let { pm ->
        AlertDialog(
            onDismissRequest = { editingPaymentMethod = null },
            title = { Text("تعديل وسيلة الدفع: ${pm.name}", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editPayAccNum,
                        onValueChange = { editPayAccNum = it },
                        label = { Text("رقم الحساب / المحفظة") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPayInstructions,
                        onValueChange = { editPayInstructions = it },
                        label = { Text("التعليمات") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.repository.updatePaymentMethod(
                                pm.copy(accountNumber = editPayAccNum.trim(), instructions = editPayInstructions.trim())
                            )
                            editingPaymentMethod = null
                            Toast.makeText(context, "تم تحديث وسيلة الدفع", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryBlue)
                ) {
                    Text("حفظ")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingPaymentMethod = null }) { Text("إلغاء") }
            }
        )
    }

    // Dialog: Edit Agency Settings
    if (showEditSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showEditSettingsDialog = false },
            title = { Text("تعديل بيانات الوكالة", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editAgencyAddress,
                        onValueChange = { editAgencyAddress = it },
                        label = { Text("العنوان") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editAgencyPhones,
                        onValueChange = { editAgencyPhones = it },
                        label = { Text("أرقام الهواتف") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editAgencyWhatsapp,
                        onValueChange = { editAgencyWhatsapp = it },
                        label = { Text("رقم الواتساب") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.repository.updateAgencySettings(
                                (agencySettings ?: AgencySettings()).copy(
                                    address = editAgencyAddress.trim(),
                                    phoneNumbers = editAgencyPhones.trim(),
                                    whatsapp = editAgencyWhatsapp.trim()
                                )
                            )
                            showEditSettingsDialog = false
                            Toast.makeText(context, "تم حفظ بيانات الوكالة", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryBlue)
                ) {
                    Text("حفظ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditSettingsDialog = false }) { Text("إلغاء") }
            }
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = BrandHeaderColor))
            Text(text = title, style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary))
        }
    }
}
