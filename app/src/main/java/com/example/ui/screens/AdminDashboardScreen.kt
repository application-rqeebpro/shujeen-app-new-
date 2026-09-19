package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgencyService
import com.example.data.model.AppNotification
import com.example.ui.components.PrimaryButton
import com.example.ui.theme.*
import com.example.viewmodel.AdminPanelViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminPanelViewModel,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    onOpenFullAdmin: () -> Unit = {},
    onBookingClick: (Long) -> Unit = {}
) {
    val context = LocalContext.current

    // State from ViewModel
    val servicesList by viewModel.servicesList.collectAsState()
    val allNotifications by viewModel.allNotifications.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val allBookings by viewModel.allBookings.collectAsState()

    // Dialog controls for the 3 functional cards
    var showAddServiceDialog by remember { mutableStateOf(false) }
    var showSendNotificationDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showResetPasswordConfirmDialog by remember { mutableStateOf(false) }

    // Add Service Local State
    var serviceTitleInput by remember { mutableStateOf("") }
    var serviceSubtitleInput by remember { mutableStateOf("") }
    var serviceDescInput by remember { mutableStateOf("") }
    var serviceFeaturesInput by remember { mutableStateOf("") }
    var serviceIconInput by remember { mutableStateOf("visa") }
    var serviceColorInput by remember { mutableStateOf("#0284C7") }

    // Send Notification Local State
    var notifTitleInput by remember { mutableStateOf("") }
    var notifMessageInput by remember { mutableStateOf("") }
    var notifSendToAll by remember { mutableStateOf(true) }
    var notifSelectedUserId by remember { mutableStateOf<Long?>(null) }
    var isSendingNotification by remember { mutableStateOf(false) }

    // Change Password Local State
    var currentPasswordInput by remember { mutableStateOf("") }
    var newPasswordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    val passwordError by viewModel.changePasswordError.collectAsState()
    val passwordSuccess by viewModel.changePasswordSuccess.collectAsState()

    val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "لوحة تحكم المسؤول",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "وكالة شجين للسفريات والسياحة",
                            style = MaterialTheme.typography.labelSmall,
                            color = BrandTextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("admin_dashboard_back_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.logout()
                            Toast.makeText(context, "تم تسجيل الخروج من لوحة التحكم", Toast.LENGTH_SHORT).show()
                            onLogout()
                        },
                        modifier = Modifier.testTag("admin_dashboard_logout_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "تسجيل الخروج",
                            tint = StatusRejected
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = BrandBackgroundStart
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("admin_dashboard_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Welcome Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(3.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(BrandPrimaryDarkBlue, BrandPrimaryBlue)
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color.White.copy(alpha = 0.2f),
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.AdminPanelSettings,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(26.dp)
                                            )
                                        }
                                    }
                                    Column {
                                        Text(
                                            text = "مرحباً بك في لوحة الإدارة",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                        Text(
                                            text = "إدارة خدمات وإشعارات وحجوزات الوكالة",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color.White.copy(alpha = 0.85f)
                                            )
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White.copy(alpha = 0.25f)
                                ) {
                                    Text(
                                        text = "الرمز: 77777",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Quick metrics row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                QuickStatItem(title = "الخدمات", value = "${servicesList.size}")
                                QuickStatItem(title = "الإشعارات", value = "${allNotifications.size}")
                                QuickStatItem(title = "الحجوزات", value = "${stats.totalBookings}")
                                QuickStatItem(title = "العملاء", value = "${allUsers.filter { it.role != "ADMIN" }.size}")
                            }
                        }
                    }
                }
            }

            // Section Title: Main Functional Cards
            item {
                Text(
                    text = "الإجراءات والعمليات السريعة",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = BrandHeaderColor
                    )
                )
            }

            // -------------------------------------------------------------
            // FUNCTIONAL CARD 1: 'Add Service' (إضافة خدمة)
            // -------------------------------------------------------------
            item {
                FunctionalActionCard(
                    title = "إضافة خدمة",
                    subtitle = "إضافة خدمة جديدة أو باقة سفر إلى دليل خدمات الوكالة مع المميزات والأيقونة واللون.",
                    actionLabel = "إضافة خدمة جديدة الآن",
                    badgeText = "${servicesList.size} خدمة نشطة",
                    icon = Icons.Default.AddBusiness,
                    accentColor = BrandPrimaryBlue,
                    testTag = "card_add_service",
                    onClick = {
                        serviceTitleInput = ""
                        serviceSubtitleInput = ""
                        serviceDescInput = ""
                        serviceFeaturesInput = ""
                        serviceIconInput = "visa"
                        serviceColorInput = "#0284C7"
                        showAddServiceDialog = true
                    }
                )
            }

            // -------------------------------------------------------------
            // FUNCTIONAL CARD 2: 'Send Notification' (إرسال إشعار)
            // -------------------------------------------------------------
            item {
                FunctionalActionCard(
                    title = "إرسال إشعار",
                    subtitle = "بث إشعارات وتنبيهات وعروض ترويجية فورية لجميع العملاء أو لعميل محدد بالاسم ورقم الهاتف.",
                    actionLabel = "إنشاء وإرسال إشعار فوري",
                    badgeText = "${allNotifications.size} إشعار مرسل",
                    icon = Icons.Default.NotificationsActive,
                    accentColor = Color(0xFF0D9488), // Teal
                    testTag = "card_send_notification",
                    onClick = {
                        notifTitleInput = ""
                        notifMessageInput = ""
                        notifSendToAll = true
                        notifSelectedUserId = null
                        showSendNotificationDialog = true
                    }
                )
            }

            // -------------------------------------------------------------
            // FUNCTIONAL CARD 3: 'Change Admin Password' (تغيير كلمة سر المسؤول)
            // -------------------------------------------------------------
            item {
                FunctionalActionCard(
                    title = "تغيير كلمة سر المسؤول",
                    subtitle = "تحديث كلمة مرور الدخول لحماية لوحة التحكم وتعيين كلمة سر جديدة أو استعادة القيمة الافتراضية (77777).",
                    actionLabel = "تحديث كلمة سر اللوحة",
                    badgeText = "محمية برمز أمان",
                    icon = Icons.Default.VpnKey,
                    accentColor = BrandPrimaryDarkBlue,
                    testTag = "card_change_admin_password",
                    onClick = {
                        currentPasswordInput = ""
                        newPasswordInput = ""
                        confirmPasswordInput = ""
                        showChangePasswordDialog = true
                    }
                )
            }

            // Button to open Full Multi-Tab Admin Suite
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = BrandPrimaryBlue
                            )
                            Text(
                                text = "إدارة كافة أقسام الوكالة والحجوزات",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall,
                                color = BrandHeaderColor
                            )
                        }
                        Text(
                            text = "عرض كافة الحجوزات، تحديث حالات الدفع، إدارة أسعار التأشيرات، ووسائل الدفع البنكية.",
                            style = MaterialTheme.typography.bodySmall,
                            color = BrandTextSecondary
                        )
                        OutlinedButton(
                            onClick = onOpenFullAdmin,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Dashboard, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("فتح لوحة التحكم الموسعة (جميع التبويبات)")
                        }
                    }
                }
            }

            // Quick List of Active Services Preview
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "دليل الخدمات المضافة (${servicesList.size})",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall,
                        color = BrandHeaderColor
                    )
                    TextButton(onClick = {
                        serviceTitleInput = ""
                        serviceSubtitleInput = ""
                        serviceDescInput = ""
                        serviceFeaturesInput = ""
                        showAddServiceDialog = true
                    }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إضافة", fontSize = 12.sp)
                    }
                }
            }

            if (servicesList.isEmpty()) {
                item {
                    Text("لا توجد خدمات حالياً. اضغط على 'إضافة خدمة' للبدء.", color = BrandTextSecondary, fontSize = 13.sp)
                }
            } else {
                items(servicesList) { service ->
                    val sColor = try {
                        Color(android.graphics.Color.parseColor(service.accentColorHex))
                    } catch (e: Exception) {
                        BrandPrimaryBlue
                    }
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(sColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (service.iconName) {
                                        "visa" -> Icons.Default.Public
                                        "flight" -> Icons.Default.FlightTakeoff
                                        "umrah" -> Icons.Default.Mosque
                                        "bus" -> Icons.Default.DirectionsBus
                                        "tourism" -> Icons.Default.BeachAccess
                                        else -> Icons.Default.Star
                                    },
                                    contentDescription = null,
                                    tint = sColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(service.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text(service.subtitle, style = MaterialTheme.typography.bodySmall, color = BrandTextSecondary)
                            }
                            IconButton(onClick = { viewModel.toggleServiceActive(service) }) {
                                Icon(
                                    imageVector = if (service.active) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                    contentDescription = null,
                                    tint = if (service.active) StatusConfirmed else BrandTextSecondary
                                )
                            }
                            IconButton(onClick = {
                                viewModel.deleteService(service.id)
                                Toast.makeText(context, "تم حذف الخدمة", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = StatusRejected)
                            }
                        }
                    }
                }
            }
        }
    }

    // =================================================================
    // DIALOG 1: ADD SERVICE (إضافة خدمة)
    // =================================================================
    if (showAddServiceDialog) {
        AlertDialog(
            onDismissRequest = { showAddServiceDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.AddBusiness, contentDescription = null, tint = BrandPrimaryBlue)
                    Text("إضافة خدمة جديدة", fontWeight = FontWeight.Bold)
                }
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
                        placeholder = { Text("مثال: تأشيرات سياحية وتجارية") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_service_title_input")
                    )

                    OutlinedTextField(
                        value = serviceSubtitleInput,
                        onValueChange = { serviceSubtitleInput = it },
                        label = { Text("الوصف المختصر *") },
                        placeholder = { Text("مثال: استخراج سريع خلال 48 ساعة") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_service_subtitle_input")
                    )

                    OutlinedTextField(
                        value = serviceDescInput,
                        onValueChange = { serviceDescInput = it },
                        label = { Text("الشرح والتفاصيل الكاملة *") },
                        placeholder = { Text("اكتب تفاصيل الخدمة وكيفية التقديم عليها...") },
                        minLines = 2,
                        maxLines = 4,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_service_desc_input")
                    )

                    OutlinedTextField(
                        value = serviceFeaturesInput,
                        onValueChange = { serviceFeaturesInput = it },
                        label = { Text("المميزات (كل ميزة في سطر)") },
                        placeholder = { Text("ميزة 1\nميزة 2\nميزة 3") },
                        minLines = 2,
                        maxLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_service_features_input")
                    )

                    // Icon Selector
                    Text("أيقونة الخدمة:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    val iconOptions = listOf(
                        "visa" to "تأشيرات",
                        "flight" to "طيران",
                        "umrah" to "عمرة",
                        "bus" to "نقل بري",
                        "tourism" to "سياحة"
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

                    // Color Selector
                    Text("اللون المميز:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    val colorOptions = listOf(
                        "#0284C7" to "أزرق",
                        "#10B981" to "أخضر",
                        "#F59E0B" to "برتقالي",
                        "#8B5CF6" to "بنفسجي",
                        "#1E3A8A" to "كحلي"
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(colorOptions) { (hex, label) ->
                            val c = try { Color(android.graphics.Color.parseColor(hex)) } catch (e: Exception) { BrandPrimaryBlue }
                            FilterChip(
                                selected = serviceColorInput == hex,
                                onClick = { serviceColorInput = hex },
                                label = {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(c))
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
                            Toast.makeText(context, "يرجى كتابة عنوان الخدمة ووصفها المختصر", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.addService(
                            title = serviceTitleInput,
                            subtitle = serviceSubtitleInput,
                            description = serviceDescInput,
                            features = serviceFeaturesInput,
                            iconName = serviceIconInput,
                            accentColorHex = serviceColorInput,
                            onSuccess = {
                                showAddServiceDialog = false
                                Toast.makeText(context, "تمت إضافة الخدمة بنجاح إلى التطبيق", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryBlue),
                    modifier = Modifier.testTag("dialog_submit_add_service_btn")
                ) {
                    Text("إضافة الخدمة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddServiceDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // =================================================================
    // DIALOG 2: SEND NOTIFICATION (إرسال إشعار)
    // =================================================================
    if (showSendNotificationDialog) {
        AlertDialog(
            onDismissRequest = { showSendNotificationDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFF0D9488))
                    Text("إرسال إشعار للعملاء", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("تحديد المستلمين:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = notifSendToAll,
                            onClick = {
                                notifSendToAll = true
                                notifSelectedUserId = null
                            },
                            label = { Text("جميع العملاء (${allUsers.filter { it.role != "ADMIN" }.size})") }
                        )
                        FilterChip(
                            selected = !notifSendToAll,
                            onClick = {
                                notifSendToAll = false
                                if (notifSelectedUserId == null) {
                                    notifSelectedUserId = allUsers.firstOrNull { it.role != "ADMIN" }?.id
                                }
                            },
                            label = { Text("عميل محدد") }
                        )
                    }

                    if (!notifSendToAll) {
                        val clientUsers = allUsers.filter { it.role != "ADMIN" }
                        if (clientUsers.isEmpty()) {
                            Text("لا يوجد عملاء مسجلون حالياً", color = StatusRejected, fontSize = 12.sp)
                        } else {
                            Text("اختر العميل المستهدف:", style = MaterialTheme.typography.labelSmall)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(clientUsers) { u ->
                                    FilterChip(
                                        selected = notifSelectedUserId == u.id,
                                        onClick = { notifSelectedUserId = u.id },
                                        label = { Text("${u.fullName} (${u.phone})", fontSize = 11.sp) }
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = notifTitleInput,
                        onValueChange = { notifTitleInput = it },
                        label = { Text("عنوان الإشعار *") },
                        placeholder = { Text("مثال: وصول تأشيرات جديدة وتخفيضات للعمرة") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_notif_title_input")
                    )

                    OutlinedTextField(
                        value = notifMessageInput,
                        onValueChange = { notifMessageInput = it },
                        label = { Text("نص الرسالة *") },
                        placeholder = { Text("اكتب نص الإشعار هنا...") },
                        minLines = 3,
                        maxLines = 5,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_notif_message_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (notifTitleInput.isBlank() || notifMessageInput.isBlank()) {
                            Toast.makeText(context, "يرجى كتابة عنوان الإشعار ونصه", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isSendingNotification = true
                        viewModel.sendNotification(
                            title = notifTitleInput,
                            message = notifMessageInput,
                            targetUserId = if (notifSendToAll) null else notifSelectedUserId,
                            onSuccess = {
                                isSendingNotification = false
                                showSendNotificationDialog = false
                                Toast.makeText(context, "تم إرسال الإشعار بنجاح", Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                    enabled = !isSendingNotification,
                    modifier = Modifier.testTag("dialog_submit_send_notif_btn")
                ) {
                    if (isSendingNotification) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                    } else {
                        Text("إرسال الإشعار")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showSendNotificationDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // =================================================================
    // DIALOG 3: CHANGE ADMIN PASSWORD (تغيير كلمة سر المسؤول)
    // =================================================================
    if (showChangePasswordDialog) {
        AlertDialog(
            onDismissRequest = { showChangePasswordDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.VpnKey, contentDescription = null, tint = BrandPrimaryDarkBlue)
                    Text("تغيير كلمة سر المسؤول", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFEF3C7),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "كلمة المرور الافتراضية هي: 77777",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF92400E), fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    if (passwordError != null) {
                        Text(passwordError ?: "", color = StatusRejected, style = MaterialTheme.typography.bodySmall)
                    }
                    if (passwordSuccess != null) {
                        Text(passwordSuccess ?: "", color = StatusConfirmed, style = MaterialTheme.typography.bodySmall)
                    }

                    // Current Password
                    OutlinedTextField(
                        value = currentPasswordInput,
                        onValueChange = { currentPasswordInput = it },
                        label = { Text("كلمة المرور الحالية *") },
                        singleLine = true,
                        visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                                Icon(
                                    imageVector = if (currentPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_current_pass_input")
                    )

                    // New Password
                    OutlinedTextField(
                        value = newPasswordInput,
                        onValueChange = { newPasswordInput = it },
                        label = { Text("كلمة المرور الجديدة (4 خانات على الأقل) *") },
                        singleLine = true,
                        visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                Icon(
                                    imageVector = if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_new_pass_input")
                    )

                    // Confirm New Password
                    OutlinedTextField(
                        value = confirmPasswordInput,
                        onValueChange = { confirmPasswordInput = it },
                        label = { Text("تأكيد كلمة المرور الجديدة *") },
                        singleLine = true,
                        visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_confirm_pass_input")
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedButton(
                        onClick = { showResetPasswordConfirmDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("استعادة كلمة المرور الافتراضية (77777)")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val success = viewModel.changeAdminPassword(
                            currentPass = currentPasswordInput,
                            newPass = newPasswordInput,
                            confirmPass = confirmPasswordInput
                        )
                        if (success) {
                            Toast.makeText(context, "تم حفظ كلمة المرور الجديدة بنجاح", Toast.LENGTH_SHORT).show()
                            showChangePasswordDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryDarkBlue),
                    modifier = Modifier.testTag("dialog_submit_change_pass_btn")
                ) {
                    Text("حفظ كلمة المرور")
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePasswordDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Dialog: Confirm Reset Password to 77777
    if (showResetPasswordConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetPasswordConfirmDialog = false },
            title = { Text("استعادة كلمة المرور الافتراضية", fontWeight = FontWeight.Bold) },
            text = { Text("هل تريد بالتأكيد إعادة ضبط كلمة المرور إلى 77777؟") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetToDefaultPassword()
                        showResetPasswordConfirmDialog = false
                        showChangePasswordDialog = false
                        Toast.makeText(context, "تمت إعادة كلمة المرور إلى 77777", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRejected)
                ) {
                    Text("نعم، إعادة إلى 77777")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetPasswordConfirmDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
private fun QuickStatItem(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White.copy(alpha = 0.8f)
            )
        )
    }
}

@Composable
private fun FunctionalActionCard(
    title: String,
    subtitle: String,
    actionLabel: String,
    badgeText: String,
    icon: ImageVector,
    accentColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(18.dp))
            .testTag(testTag),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BrandHeaderColor
                            )
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = accentColor.copy(alpha = 0.12f),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = badgeText,
                                color = accentColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = actionLabel,
                        tint = BrandTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = BrandTextPrimary,
                    lineHeight = 18.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = actionLabel, fontWeight = FontWeight.Bold)
            }
        }
    }
}
