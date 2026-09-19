package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.BookingDocument
import com.example.data.model.ElectronicBooking
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.viewmodel.ShajeenViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailScreen(
    bookingId: Long,
    viewModel: ShajeenViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val isAdmin by viewModel.isAdmin.collectAsState()

    var booking by remember { mutableStateOf<ElectronicBooking?>(null) }
    var documents by remember { mutableStateOf<List<BookingDocument>>(emptyList()) }
    var previewFilePath by remember { mutableStateOf<String?>(null) }

    // Admin change status dialog
    var showStatusDialog by remember { mutableStateOf(false) }
    var showPaymentVerifyDialog by remember { mutableStateOf(false) }
    var adminNotesInput by remember { mutableStateOf("") }

    LaunchedEffect(bookingId) {
        viewModel.repository.getBooking(bookingId).collect {
            booking = it
            adminNotesInput = it?.adminNotes ?: ""
        }
    }

    LaunchedEffect(bookingId) {
        viewModel.repository.getDocumentsForBooking(bookingId).collect {
            documents = it
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = booking?.bookingNumber ?: "تفاصيل الحجز",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    booking?.let { b ->
                        IconButton(onClick = { viewModel.openWhatsAppBooking(context, b) }) {
                            Icon(Icons.Default.Chat, contentDescription = "واتساب", tint = Color(0xFF16A34A))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = BrandBackgroundStart
    ) { padding ->
        val currentBooking = booking
        if (currentBooking == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BrandPrimaryBlue)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .testTag("booking_detail_screen"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Status Header Card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "حالة طلب الحجز:",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                StatusBadge(status = currentBooking.bookingStatus)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "حالة التحقق من الدفع:",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                StatusBadge(status = currentBooking.paymentStatus)
                            }

                            val formattedDate = SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale.getDefault())
                                .format(Date(currentBooking.createdAt))
                            Text(
                                text = "تاريخ التقديم: $formattedDate",
                                style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary)
                            )

                            if (!currentBooking.adminNotes.isNullOrBlank()) {
                                Surface(
                                    color = Color(0xFFFEF9C3),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "ملاحظات إدارة وكالة شجين:",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF854D0E)
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = currentBooking.adminNotes,
                                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF713F12))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Traveler & Trip Details Card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "بيانات المسافر والتأشيرة",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BrandPrimaryDarkBlue
                                )
                            )
                            Divider(color = BrandCardBorder)

                            DetailRow("اسم المسافر", currentBooking.fullName)
                            DetailRow("رقم الهاتف", currentBooking.phone)
                            DetailRow("بلد الإقامة", "${currentBooking.country} - ${currentBooking.city}")
                            DetailRow("نوع التأشيرة", currentBooking.visaType)
                            DetailRow("وجهة السفر", currentBooking.destination.ifBlank { "غير محدد" })
                            if (currentBooking.travelDate.isNotBlank()) {
                                DetailRow("تاريخ السفر التقريبي", currentBooking.travelDate)
                            }
                            if (!currentBooking.notes.isNullOrBlank()) {
                                DetailRow("ملاحظات المسافر", currentBooking.notes)
                            }
                        }
                    }
                }

                // Uploaded Documents Card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "المستندات المرفقة (${documents.size})",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BrandPrimaryDarkBlue
                                )
                            )
                            Divider(color = BrandCardBorder)

                            if (documents.isEmpty()) {
                                Text("لم يتم إرفاق مستندات بعد", color = BrandTextSecondary, style = MaterialTheme.typography.bodySmall)
                            } else {
                                documents.forEach { doc ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFFF8FAFC),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandCardBorder),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(
                                                    imageVector = if (doc.mimeType.contains("pdf")) Icons.Default.PictureAsPdf else Icons.Default.Image,
                                                    contentDescription = null,
                                                    tint = BrandPrimaryBlue
                                                )
                                                Column {
                                                    Text(doc.documentType, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                                    Text(doc.fileName, style = MaterialTheme.typography.bodySmall, color = BrandTextSecondary, maxLines = 1)
                                                }
                                            }

                                            Button(
                                                onClick = { previewFilePath = doc.fileUrl },
                                                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryBlue),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                                modifier = Modifier.height(34.dp)
                                            ) {
                                                Text("معاينة", fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Payment & Receipt Details Card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "بيانات الدفع والتحويل",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BrandPrimaryDarkBlue
                                )
                            )
                            Divider(color = BrandCardBorder)

                            DetailRow("وسيلة التحويل", currentBooking.paymentMethod)
                            DetailRow("رقم الحوالة / العملية", currentBooking.transactionNumber)

                            if (currentBooking.paymentReceiptPath.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("صورة سند التحويل:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)

                                val receiptFile = File(currentBooking.paymentReceiptPath)
                                if (receiptFile.exists()) {
                                    AsyncImage(
                                        model = receiptFile,
                                        contentDescription = "سند التحويل",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color.Black.copy(alpha = 0.05f))
                                    )

                                    OutlinedButton(
                                        onClick = { previewFilePath = currentBooking.paymentReceiptPath },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.ZoomIn, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("معاينة السند بالحجم الكامل")
                                    }
                                }
                            }
                        }
                    }
                }

                // Admin Management Actions (Visible if Admin)
                if (isAdmin) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, BrandPrimaryBlue),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = BrandPrimaryDarkBlue)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("إجراءات الإدارة لهذا الحجز", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = BrandPrimaryDarkBlue)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { showPaymentVerifyDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryDarkBlue),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("التحقق من الدفع", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { showStatusDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryBlue),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("تعديل حالة الحجز", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // WhatsApp Follow-up Button
                item {
                    Button(
                        onClick = { viewModel.openWhatsAppBooking(context, currentBooking) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("متابعة واستفسار عبر واتساب", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }

    // Dialog: Full image preview
    if (previewFilePath != null) {
        Dialog(onDismissRequest = { previewFilePath = null }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("معاينة المستند", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    val file = File(previewFilePath ?: "")
                    if (file.exists()) {
                        AsyncImage(
                            model = file,
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    } else {
                        Text("الملف غير متوفر", color = StatusRejected)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { previewFilePath = null },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryBlue),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("إغلاق", color = Color.White)
                    }
                }
            }
        }
    }

    // Dialog: Admin Verify Payment
    if (showPaymentVerifyDialog) {
        AlertDialog(
            onDismissRequest = { showPaymentVerifyDialog = false },
            title = { Text("التحقق من إثبات الدفع", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("حدد حالة عملية الدفع لسند الحوالة:")
                    OutlinedTextField(
                        value = adminNotesInput,
                        onValueChange = { adminNotesInput = it },
                        label = { Text("ملاحظة للإشعار (اختياري)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.adminVerifyPayment(bookingId, true, adminNotesInput)
                        showPaymentVerifyDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusConfirmed)
                ) {
                    Text("تم التحقق (صالح) ✓")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.adminVerifyPayment(bookingId, false, adminNotesInput)
                        showPaymentVerifyDialog = false
                    }
                ) {
                    Text("غير صالح ✗", color = StatusRejected)
                }
            }
        )
    }

    // Dialog: Admin Change Booking Status
    if (showStatusDialog) {
        val statuses = listOf("جديد", "قيد المراجعة", "بانتظار مستندات", "تم تأكيد الحجز", "قيد التنفيذ", "مكتمل", "مرفوض", "ملغي")
        var selectedStatus by remember { mutableStateOf(booking?.bookingStatus ?: "جديد") }

        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text("تحديث حالة الحجز", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    statuses.forEach { st ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = selectedStatus == st,
                                onClick = { selectedStatus = st }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(st)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = adminNotesInput,
                        onValueChange = { adminNotesInput = it },
                        label = { Text("ملاحظة لإرسالها للعميل") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.adminUpdateBookingStatus(bookingId, selectedStatus, adminNotesInput)
                        showStatusDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryBlue)
                ) {
                    Text("حفظ التحديث")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStatusDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary))
        Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
    }
}
