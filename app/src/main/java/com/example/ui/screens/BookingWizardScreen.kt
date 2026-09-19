package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ElectronicBooking
import com.example.data.model.PaymentMethod
import com.example.data.model.VisaType
import com.example.ui.components.DocumentUploadCard
import com.example.ui.components.PrimaryButton
import com.example.ui.theme.*
import com.example.viewmodel.ShajeenViewModel

@Composable
fun BookingWizardScreen(
    viewModel: ShajeenViewModel,
    onNavigateBack: () -> Unit,
    onBookingCreated: (ElectronicBooking) -> Unit
) {
    val context = LocalContext.current
    val currentStep by viewModel.wizardCurrentStep.collectAsState()
    val isSubmitting by viewModel.isSubmittingBooking.collectAsState()

    val activeVisas by viewModel.activeVisaTypes.collectAsState()
    val activePayments by viewModel.activePaymentMethods.collectAsState()

    // Step names
    val stepTitles = listOf(
        "بيانات المسافر",
        "نوع التأشيرة",
        "المستندات",
        "الدفع",
        "المراجعة"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackgroundStart)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Wizard Top Header
        Surface(
            color = Color.White,
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, contentDescription = "إلغاء", tint = BrandHeaderColor)
                    }

                    Text(
                        text = "طلب حجز إلكتروني",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BrandHeaderColor
                        )
                    )

                    Text(
                        text = "خطوة $currentStep من 5",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = BrandPrimaryBlue,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Indicator
                LinearProgressIndicator(
                    progress = { currentStep / 5f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = BrandPrimaryBlue,
                    trackColor = Color(0xFFE2E8F0)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Current Step Title
                Text(
                    text = stepTitles.getOrElse(currentStep - 1) { "" },
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = BrandPrimaryDarkBlue
                    )
                )
            }
        }

        // Wizard Content Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (currentStep) {
                1 -> WizardStep1TravelerInfo(viewModel)
                2 -> WizardStep2VisaSelection(viewModel, activeVisas)
                3 -> WizardStep3Documents(viewModel)
                4 -> WizardStep4Payment(viewModel, activePayments)
                5 -> WizardStep5Review(viewModel)
            }
        }

        // Bottom Navigation Buttons
        Surface(
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button if past step 1
                if (currentStep > 1) {
                    OutlinedButton(
                        onClick = { viewModel.wizardCurrentStep.value = currentStep - 1 },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("السابق", fontWeight = FontWeight.Bold)
                    }
                }

                // Next / Submit Button
                val isLastStep = currentStep == 5
                val btnModifier = if (currentStep > 1) Modifier.weight(2f) else Modifier.fillMaxWidth()

                PrimaryButton(
                    text = if (isLastStep) "تأكيد وإرسال طلب الحجز" else "التالي",
                    onClick = {
                        if (isLastStep) {
                            viewModel.submitBooking { createdBooking ->
                                onBookingCreated(createdBooking)
                            }
                        } else {
                            // Validation before advancing
                            val canAdvance = when (currentStep) {
                                1 -> {
                                    val valid = viewModel.bookingTravelerName.value.isNotBlank() &&
                                            viewModel.bookingPhone.value.isNotBlank() &&
                                            viewModel.bookingDestination.value.isNotBlank()
                                    if (!valid) {
                                        Toast.makeText(context, "يرجى تعبئة الاسم ورقم الهاتف والوجهة", Toast.LENGTH_SHORT).show()
                                    }
                                    valid
                                }
                                2 -> {
                                    val valid = viewModel.selectedVisaType.value != null
                                    if (!valid) {
                                        Toast.makeText(context, "يرجى اختيار نوع التأشيرة", Toast.LENGTH_SHORT).show()
                                    }
                                    valid
                                }
                                3 -> {
                                    // Check required docs
                                    val missing = viewModel.wizardDocItems.value.filter { it.isRequired && it.fileInfo == null }
                                    if (missing.isNotEmpty()) {
                                        Toast.makeText(context, "يرجى رفع المستندات المطلوبة: ${missing.joinToString { it.documentName }}", Toast.LENGTH_LONG).show()
                                        false
                                    } else {
                                        true
                                    }
                                }
                                4 -> {
                                    val valid = viewModel.selectedPaymentMethod.value != null &&
                                            viewModel.transactionNumber.value.isNotBlank() &&
                                            viewModel.paymentReceiptFile.value != null
                                    if (!valid) {
                                        Toast.makeText(context, "يرجى إدخال رقم الحوالة ورفع إثبات الدفع", Toast.LENGTH_SHORT).show()
                                    }
                                    valid
                                }
                                else -> true
                            }
                            if (canAdvance) {
                                viewModel.wizardCurrentStep.value = currentStep + 1
                            }
                        }
                    },
                    isLoading = isSubmitting,
                    icon = if (isLastStep) Icons.Default.Send else Icons.AutoMirrored.Filled.ArrowBack,
                    modifier = btnModifier.height(50.dp)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 1: Traveler Info
// -------------------------------------------------------------
@Composable
fun WizardStep1TravelerInfo(viewModel: ShajeenViewModel) {
    val name by viewModel.bookingTravelerName.collectAsState()
    val phone by viewModel.bookingPhone.collectAsState()
    val country by viewModel.bookingCountry.collectAsState()
    val city by viewModel.bookingCity.collectAsState()
    val destination by viewModel.bookingDestination.collectAsState()
    val travelDate by viewModel.bookingTravelDate.collectAsState()
    val notes by viewModel.bookingNotes.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "البيانات الأساسية للمسافر",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { viewModel.bookingTravelerName.value = it },
                        label = { Text("اسم المسافر الكامل *") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = BrandPrimaryBlue) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("wizard_name_input")
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { viewModel.bookingPhone.value = it },
                        label = { Text("رقم الهاتف للتواصل *") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = BrandPrimaryBlue) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("wizard_phone_input")
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = country,
                            onValueChange = { viewModel.bookingCountry.value = it },
                            label = { Text("بلد الإقامة") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = city,
                            onValueChange = { viewModel.bookingCity.value = it },
                            label = { Text("المدينة") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = destination,
                        onValueChange = { viewModel.bookingDestination.value = it },
                        label = { Text("وجهة السفر (الدولة / المدينة) *") },
                        placeholder = { Text("مثال: السعودية - الرياض") },
                        leadingIcon = { Icon(Icons.Default.FlightTakeoff, contentDescription = null, tint = BrandPrimaryBlue) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("wizard_destination_input")
                    )

                    OutlinedTextField(
                        value = travelDate,
                        onValueChange = { viewModel.bookingTravelDate.value = it },
                        label = { Text("تاريخ السفر التقريبي") },
                        placeholder = { Text("مثال: 2026/10/15") },
                        leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = BrandPrimaryBlue) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { viewModel.bookingNotes.value = it },
                        label = { Text("ملاحظات أو طلبات خاصة (اختياري)") },
                        leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null, tint = BrandPrimaryBlue) },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 2: Visa Selection
// -------------------------------------------------------------
@Composable
fun WizardStep2VisaSelection(
    viewModel: ShajeenViewModel,
    visas: List<VisaType>
) {
    val selectedVisa by viewModel.selectedVisaType.collectAsState()
    val customVisaName by viewModel.customVisaTypeName.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "اختر نوع التأشيرة المطلوبة:",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "سيتم تحديد المستندات المطلوبة تلقائياً بحسب نوع التأشيرة المختارة",
                style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary)
            )
        }

        items(visas) { visa ->
            val isSelected = selectedVisa?.id == visa.id

            Card(
                onClick = { viewModel.onVisaSelected(visa) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFFEFF8FE) else Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) BrandPrimaryBlue else BrandCardBorder
                ),
                elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("visa_card_${visa.name.replace(" ", "_")}")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { viewModel.onVisaSelected(visa) },
                        colors = RadioButtonDefaults.colors(selectedColor = BrandPrimaryBlue)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = visa.name,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) BrandPrimaryDarkBlue else BrandTextPrimary
                            )
                        )
                        Text(
                            text = visa.description,
                            style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary)
                        )
                    }

                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = BrandPrimaryBlue
                        )
                    }
                }
            }
        }

        // If "تأشيرة أخرى" is selected, show input field
        if (selectedVisa?.name == "تأشيرة أخرى") {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "حدد اسم أو نوع التأشيرة الأخرى:",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = customVisaName,
                            onValueChange = { viewModel.customVisaTypeName.value = it },
                            placeholder = { Text("مثال: تأشيرة دراسية، تأشيرة علاجية...") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 3: Required Documents Upload
// -------------------------------------------------------------
@Composable
fun WizardStep3Documents(viewModel: ShajeenViewModel) {
    val docItems by viewModel.wizardDocItems.collectAsState()
    val storageManager = viewModel.storageManager

    var showAddCustomDialog by remember { mutableStateOf(false) }
    var customDocName by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "المستندات المطلوبة للتأشيرة:",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "يمكنك رفع الصور عبر الكاميرا أو المعرض أو اختيار ملف PDF. تأكد من وضوح المستندات.",
                style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary)
            )
        }

        if (docItems.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = BrandPrimaryBlue, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("جاري تحميل قائمة المستندات الخاصة بنوع التأشيرة...")
                    }
                }
            }
        } else {
            items(docItems) { item ->
                DocumentUploadCard(
                    docName = item.documentName,
                    isRequired = item.isRequired,
                    fileInfo = item.fileInfo,
                    storageManager = storageManager,
                    onFileSelected = { saved ->
                        viewModel.updateDocUpload(item.documentName, saved)
                    },
                    onFileRemoved = {
                        viewModel.updateDocUpload(item.documentName, null)
                    }
                )
            }
        }

        // Add additional document button
        item {
            OutlinedButton(
                onClick = { showAddCustomDialog = true },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("إضافة مستند إضافي آخر")
            }
        }
    }

    if (showAddCustomDialog) {
        AlertDialog(
            onDismissRequest = { showAddCustomDialog = false },
            title = { Text("إضافة مستند إضافي", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = customDocName,
                    onValueChange = { customDocName = it },
                    label = { Text("اسم المستند") },
                    placeholder = { Text("مثال: كشف حساب، بطاقة لقاح...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customDocName.isNotBlank()) {
                            viewModel.wizardDocItems.value = viewModel.wizardDocItems.value + com.example.viewmodel.UploadedDocItem(
                                documentName = customDocName.trim(),
                                isRequired = false,
                                fileInfo = null
                            )
                            customDocName = ""
                            showAddCustomDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryBlue)
                ) {
                    Text("إضافة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCustomDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

// -------------------------------------------------------------
// STEP 4: Payment
// -------------------------------------------------------------
@Composable
fun WizardStep4Payment(
    viewModel: ShajeenViewModel,
    paymentMethods: List<PaymentMethod>
) {
    val context = LocalContext.current
    val selectedMethod by viewModel.selectedPaymentMethod.collectAsState()
    val transactionNum by viewModel.transactionNumber.collectAsState()
    val receiptFile by viewModel.paymentReceiptFile.collectAsState()
    val storageManager = viewModel.storageManager

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "طريقة الدفع وإثبات التحويل:",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "اختر وسيلة الدفع المعتمدة وحوّل الرسوم ثم أرفق رقم وسند الحوالة",
                style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary)
            )
        }

        // Payment Method Selector
        items(paymentMethods) { method ->
            val isSelected = selectedMethod?.id == method.id

            Card(
                onClick = { viewModel.selectedPaymentMethod.value = method },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFFEFF8FE) else Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) BrandPrimaryBlue else BrandCardBorder
                ),
                elevation = CardDefaults.cardElevation(if (isSelected) 3.dp else 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("payment_method_${method.name}")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.selectedPaymentMethod.value = method },
                                colors = RadioButtonDefaults.colors(selectedColor = BrandPrimaryBlue)
                            )

                            Text(
                                text = method.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) BrandPrimaryDarkBlue else BrandTextPrimary
                                )
                            )
                        }

                        // Copy Account Number Button
                        OutlinedButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Account Number", method.accountNumber)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "تم نسخ الرقم: ${method.accountNumber}", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("نسخ الرقم", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("رقم الحساب / المحفظة:", style = MaterialTheme.typography.bodySmall)
                            Text(
                                text = method.accountNumber,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BrandHeaderColor
                                )
                            )
                        }
                    }

                    if (method.instructions.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = method.instructions,
                            style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary)
                        )
                    }
                }
            }
        }

        // Transaction Number Input
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "بيانات سند التحويل",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )

                    OutlinedTextField(
                        value = transactionNum,
                        onValueChange = { viewModel.transactionNumber.value = it },
                        label = { Text("رقم الحوالة / رقم العملية *") },
                        placeholder = { Text("أدخل رقم السند أو العملية") },
                        leadingIcon = { Icon(Icons.Default.Receipt, contentDescription = null, tint = BrandPrimaryBlue) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("transaction_number_input")
                    )
                }
            }
        }

        // Payment Receipt Upload
        item {
            DocumentUploadCard(
                docName = "إثبات الدفع (صورة السند أو الإشعار)",
                isRequired = true,
                fileInfo = receiptFile,
                storageManager = storageManager,
                onFileSelected = { saved ->
                    viewModel.paymentReceiptFile.value = saved
                },
                onFileRemoved = {
                    viewModel.paymentReceiptFile.value = null
                }
            )
        }
    }
}

// -------------------------------------------------------------
// STEP 5: Review & Submit
// -------------------------------------------------------------
@Composable
fun WizardStep5Review(viewModel: ShajeenViewModel) {
    val name by viewModel.bookingTravelerName.collectAsState()
    val phone by viewModel.bookingPhone.collectAsState()
    val destination by viewModel.bookingDestination.collectAsState()
    val travelDate by viewModel.bookingTravelDate.collectAsState()
    val notes by viewModel.bookingNotes.collectAsState()

    val selectedVisa by viewModel.selectedVisaType.collectAsState()
    val customVisaName by viewModel.customVisaTypeName.collectAsState()

    val docItems by viewModel.wizardDocItems.collectAsState()
    val selectedMethod by viewModel.selectedPaymentMethod.collectAsState()
    val transactionNum by viewModel.transactionNumber.collectAsState()
    val receiptFile by viewModel.paymentReceiptFile.collectAsState()

    val displayVisaName = if (selectedVisa?.name == "تأشيرة أخرى" && customVisaName.isNotBlank()) {
        customVisaName
    } else {
        selectedVisa?.name ?: "-"
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "مراجعة تفاصيل طلب الحجز قبل الإرسال:",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "يرجى التأكد من صحة جميع البيانات والمستندات قبل الضغط على تأكيد وإرسال الطلب.",
                style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary)
            )
        }

        // Traveler Summary Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = BrandPrimaryBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("بيانات المسافر والرحلة", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    }

                    Divider(color = BrandCardBorder)

                    ReviewRow("اسم المسافر", name)
                    ReviewRow("رقم الهاتف", phone)
                    ReviewRow("وجهة السفر", destination)
                    if (travelDate.isNotBlank()) ReviewRow("تاريخ السفر", travelDate)
                    if (notes.isNotBlank()) ReviewRow("ملاحظات", notes)
                }
            }
        }

        // Visa Summary Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Public, contentDescription = null, tint = BrandPrimaryBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("نوع التأشيرة المختارة", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    }

                    Divider(color = BrandCardBorder)

                    ReviewRow("نوع التأشيرة", displayVisaName)
                }
            }
        }

        // Documents Summary Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = BrandPrimaryBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("المستندات المرفوعة", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    }

                    Divider(color = BrandCardBorder)

                    docItems.forEach { doc ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(doc.documentName, style = MaterialTheme.typography.bodyMedium)
                            if (doc.fileInfo != null) {
                                Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(6.dp)) {
                                    Text("تم الرفع ✓", color = StatusConfirmed, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            } else {
                                Text("لم يُرفع", color = BrandTextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Payment Summary Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Payments, contentDescription = null, tint = BrandPrimaryBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("بيانات الدفع والتحويل", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    }

                    Divider(color = BrandCardBorder)

                    ReviewRow("طريقة التحويل", selectedMethod?.name ?: "-")
                    ReviewRow("رقم الحوالة", transactionNum)
                    ReviewRow("إثبات الدفع", if (receiptFile != null) "مرفق (${receiptFile?.fileName})" else "غير مرفق")
                }
            }
        }
    }
}

@Composable
fun ReviewRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = BrandHeaderColor
            )
        )
    }
}
