package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.PrimaryButton
import com.example.ui.theme.*
import com.example.viewmodel.ShajeenViewModel

@Composable
fun LoginScreen(
    viewModel: ShajeenViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToAdmin: () -> Unit = {}
) {
    val phone by viewModel.loginPhone.collectAsState()
    val password by viewModel.loginPassword.collectAsState()
    val error by viewModel.loginError.collectAsState()
    val isLoading by viewModel.isAuthLoading.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var showAdminPinDialog by remember { mutableStateOf(false) }
    var adminPinInput by remember { mutableStateOf("") }
    var adminPinError by remember { mutableStateOf<String?>(null) }
    var adminPinVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BrandBackgroundStart, BrandBackgroundEnd)
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Admin Dashboard Top Entry Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = {
                    onNavigateToAdmin()
                },
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 3.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.testTag("admin_control_panel_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "لوحة التحكم",
                        tint = BrandPrimaryDarkBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "لوحة التحكم",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimaryDarkBlue
                        )
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Logo
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 6.dp,
                modifier = Modifier.size(100.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(8.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_shajeen_logo),
                        contentDescription = "شعار وكالة شجين",
                        modifier = Modifier.size(84.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "وكالة شجين للسفريات والسياحة",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = BrandHeaderColor,
                    fontSize = 20.sp
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = "تسجيل الدخول إلى حسابك",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = BrandTextSecondary
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (error != null) {
                        Surface(
                            color = Color(0xFFFEE2E2),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = StatusRejected)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = error ?: "",
                                    color = StatusRejected,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }

                    // Phone input
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { viewModel.loginPhone.value = it },
                        label = { Text("رقم الهاتف أو البريد الإلكتروني") },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = BrandPrimaryBlue)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_phone_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { viewModel.loginPassword.value = it },
                        label = { Text("كلمة المرور") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = BrandPrimaryBlue)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = BrandTextSecondary
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_input")
                    )

                    // Forgot Password
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { showForgotPasswordDialog = true },
                            modifier = Modifier.testTag("forgot_password_button")
                        ) {
                            Text(
                                text = "نسيت كلمة المرور؟",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BrandPrimaryDarkBlue,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Login Button
                    PrimaryButton(
                        text = "تسجيل الدخول",
                        onClick = { viewModel.login(onSuccess = onLoginSuccess) },
                        isLoading = isLoading,
                        icon = Icons.Default.Login
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Register Button
                    OutlinedButton(
                        onClick = onNavigateToRegister,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, BrandPrimaryBlue),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandPrimaryBlue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("go_to_register_button")
                    ) {
                        Text(
                            text = "إنشاء حساب جديد",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = BrandPrimaryDarkBlue
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Demo Accounts Switcher (for testing convenience)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White.copy(alpha = 0.85f),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "تجربة سريعة للحسابات المسجلة مسبقاً:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BrandTextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.loginPhone.value = "770038009"
                                viewModel.loginPassword.value = "admin123"
                                viewModel.login(onSuccess = onLoginSuccess)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("demo_admin_login_btn")
                        ) {
                            Text("دخول كمدير", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                viewModel.loginPhone.value = "771234567"
                                viewModel.loginPassword.value = "user123"
                                viewModel.login(onSuccess = onLoginSuccess)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("demo_user_login_btn")
                        ) {
                            Text("دخول كعميل", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = { Text("استعادة كلمة المرور", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "يرجى التواصل المباشر مع إدارة وكالة شجين عبر واتساب على الرقم 770038009 لتأكيد هويتك وإعادة تعيين كلمة المرور فوراً."
                )
            },
            confirmButton = {
                Button(
                    onClick = { showForgotPasswordDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryBlue)
                ) {
                    Text("حسناً")
                }
            }
        )
    }

    if (showAdminPinDialog) {
        AlertDialog(
            onDismissRequest = { showAdminPinDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    tint = BrandPrimaryBlue,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "الدخول إلى لوحة التحكم",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = BrandHeaderColor
                    ),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "أدخل كلمة سر لوحة التحكم (الافتراضية: 77777) للوصول إلى إدارة الحجوزات وإضافة الخدمات وإرسال الإشعارات وتغيير كلمة السر.",
                        style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = adminPinInput,
                        onValueChange = {
                            adminPinInput = it
                            adminPinError = null
                        },
                        label = { Text("كلمة سر الإدارة (الافتراضية: 77777)") },
                        singleLine = true,
                        visualTransformation = if (adminPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { adminPinVisible = !adminPinVisible }) {
                                Icon(
                                    imageVector = if (adminPinVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = BrandTextSecondary
                                )
                            }
                        },
                        isError = adminPinError != null,
                        supportingText = {
                            if (adminPinError != null) {
                                Text(
                                    text = adminPinError ?: "",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_pin_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.loginWithAdminPin(
                            pin = adminPinInput,
                            onSuccess = {
                                showAdminPinDialog = false
                                onNavigateToAdmin()
                            },
                            onError = { err ->
                                adminPinError = err
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryBlue),
                    modifier = Modifier.testTag("admin_pin_submit_button")
                ) {
                    Text("دخول للوحة التحكم", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdminPinDialog = false }) {
                    Text("إلغاء", color = BrandTextSecondary)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: ShajeenViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val fullName by viewModel.regFullName.collectAsState()
    val phone by viewModel.regPhone.collectAsState()
    val email by viewModel.regEmail.collectAsState()
    val idType by viewModel.regIdType.collectAsState()
    val idNumber by viewModel.regIdNumber.collectAsState()
    val country by viewModel.regCountry.collectAsState()
    val city by viewModel.regCity.collectAsState()
    val district by viewModel.regDistrict.collectAsState()
    val area by viewModel.regArea.collectAsState()
    val password by viewModel.regPassword.collectAsState()
    val error by viewModel.regError.collectAsState()
    val isLoading by viewModel.isAuthLoading.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إنشاء حساب جديد", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "رجوع")
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, shape = RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (error != null) {
                        Surface(
                            color = Color(0xFFFEE2E2),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = error ?: "",
                                color = StatusRejected,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // Full Name
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { viewModel.regFullName.value = it },
                        label = { Text("الاسم الكامل *") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = BrandPrimaryBlue) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("reg_fullname_input")
                    )

                    // Phone
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { viewModel.regPhone.value = it },
                        label = { Text("رقم الهاتف *") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = BrandPrimaryBlue) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("reg_phone_input")
                    )

                    // Email (Optional)
                    OutlinedTextField(
                        value = email,
                        onValueChange = { viewModel.regEmail.value = it },
                        label = { Text("البريد الإلكتروني (اختياري)") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = BrandPrimaryBlue) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // ID Type selector
                    Text(text = "نوع الهوية *", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val types = listOf("بطاقة شخصية", "جواز سفر")
                        types.forEach { type ->
                            val isSelected = idType == type
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.regIdType.value = type },
                                label = { Text(type, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // ID Number
                    OutlinedTextField(
                        value = idNumber,
                        onValueChange = { viewModel.regIdNumber.value = it },
                        label = { Text("رقم الهوية / الجواز *") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = BrandPrimaryBlue) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("reg_idnumber_input")
                    )

                    // Country & City
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = country,
                            onValueChange = { viewModel.regCountry.value = it },
                            label = { Text("الدولة") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = city,
                            onValueChange = { viewModel.regCity.value = it },
                            label = { Text("المدينة") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // District & Area
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = district,
                            onValueChange = { viewModel.regDistrict.value = it },
                            label = { Text("المديرية") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = area,
                            onValueChange = { viewModel.regArea.value = it },
                            label = { Text("المنطقة / الحي") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { viewModel.regPassword.value = it },
                        label = { Text("كلمة المرور *") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = BrandPrimaryBlue) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("reg_password_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    PrimaryButton(
                        text = "إنشاء الحساب",
                        onClick = { viewModel.register(onSuccess = onRegisterSuccess) },
                        isLoading = isLoading,
                        icon = Icons.Default.CheckCircle
                    )
                }
            }
        }
    }
}
