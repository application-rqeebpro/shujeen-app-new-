package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PrimaryButton
import com.example.ui.theme.*
import com.example.viewmodel.AdminPanelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLoginScreen(
    viewModel: AdminPanelViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val passwordInput by viewModel.passwordInput.collectAsState()
    val isPasswordVisible by viewModel.isPasswordVisible.collectAsState()
    val loginError by viewModel.loginError.collectAsState()

    var showTouchKeypad by remember { mutableStateOf(true) }

    val handleLogin = {
        if (passwordInput.isBlank()) {
            Toast.makeText(context, "يرجى إدخال كلمة سر لوحة التحكم (الافتراضية: 77777)", Toast.LENGTH_SHORT).show()
        } else {
            val isSuccess = viewModel.checkPassword(passwordInput)
            if (isSuccess) {
                Toast.makeText(context, "تم التحقق بنجاح، مرحباً بك في لوحة تحكم الإدارة", Toast.LENGTH_SHORT).show()
                onLoginSuccess()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "دخول لوحة تحكم الإدارة",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = BrandHeaderColor
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .testTag("admin_login_back_button")
                            .minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "الرجوع لشاشة تسجيل الدخول",
                            tint = BrandHeaderColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = BrandBackgroundStart
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(BrandBackgroundStart, BrandBackgroundEnd)
                    )
                )
                .testTag("admin_login_screen"),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // =========================================================
                // 1. Visually Appealing Administrative Icon Hero at the Top
                // =========================================================
                AdminTopHeroIcon()

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "وكالة شجين للسفريات والسياحة",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = BrandHeaderColor,
                        fontSize = 20.sp
                    ),
                    textAlign = TextAlign.Center
                )

                // Subtitle Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = BrandPrimaryDarkBlue.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, BrandPrimaryDarkBlue.copy(alpha = 0.15f)),
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                        Text(
                            text = "بوابة الوصول الآمن للوحة التحكم المركزية",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = BrandPrimaryDarkBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // =========================================================
                // 2. Protected Password Card with Masking & Touch Handling
                // =========================================================
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 5.dp, shape = RoundedCornerShape(22.dp)),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Section Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(BrandPrimaryDarkBlue, BrandPrimaryBlue)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "رمز الحماية للمسؤول",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = BrandHeaderColor
                                )
                                Text(
                                    text = "الرمز الافتراضي المعتمد هو: 77777",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = BrandTextSecondary
                                )
                            }
                        }

                        // Visual PIN Dot Indicators (Masked Touch Visualization)
                        PinDotsIndicator(
                            pinLength = passwordInput.length,
                            totalDigits = 5,
                            isError = loginError != null
                        )

                        // Error Banner
                        AnimatedVisibility(
                            visible = loginError != null,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFEE2E2),
                                border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = StatusRejected
                                    )
                                    Text(
                                        text = loginError ?: "",
                                        color = StatusRejected,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }

                        // Masked Password Text Field with Touch Device Keyboard Options
                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { input ->
                                // Restrict input to digits and limit to 10 for clean touch security
                                if (input.all { it.isDigit() } && input.length <= 10) {
                                    viewModel.onPasswordInputChange(input)
                                }
                            },
                            label = { Text("رمز الدخول (PIN)") },
                            placeholder = { Text("•••••") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.VpnKey,
                                    contentDescription = null,
                                    tint = BrandPrimaryDarkBlue
                                )
                            },
                            trailingIcon = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (passwordInput.isNotEmpty()) {
                                        IconButton(
                                            onClick = { viewModel.clearPassword() },
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "مسح الرمز",
                                                tint = Color.Gray,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                    IconButton(
                                        onClick = { viewModel.togglePasswordVisibility() },
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = if (isPasswordVisible) "إخفاء كلمة المرور" else "إظهار كلمة المرور",
                                            tint = if (isPasswordVisible) BrandPrimaryBlue else Color.Gray
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            // Proper masking with bullet character
                            visualTransformation = if (isPasswordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation('\u2022')
                            },
                            // Numeric keypad optimized for touch screens
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.NumberPassword,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = { handleLogin() }),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandPrimaryBlue,
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_password_input")
                        )

                        // Quick Touch Helper Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Touch Auto-fill '77777' Chip
                            AssistChip(
                                onClick = {
                                    viewModel.fillDefaultPassword()
                                },
                                label = {
                                    Text(
                                        text = "تعبئة سريعة: 77777",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFB45309)
                                        )
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Key,
                                        contentDescription = null,
                                        tint = Color(0xFFB45309),
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = Color(0xFFFEF3C7)
                                ),
                                border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("admin_quick_fill_77777_chip")
                            )

                            // Toggle On-Screen Touch Dialpad
                            TextButton(
                                onClick = { showTouchKeypad = !showTouchKeypad },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = if (showTouchKeypad) Icons.Default.KeyboardHide else Icons.Default.Dialpad,
                                    contentDescription = null,
                                    tint = BrandPrimaryBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (showTouchKeypad) "إخفاء لوحة الأرقام" else "لوحة أرقام لمسية",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = BrandPrimaryBlue,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }

                        // =========================================================
                        // 3. Ergonomic On-Screen Touch Dialpad for Touch Devices
                        // =========================================================
                        AnimatedVisibility(
                            visible = showTouchKeypad,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            TouchKeypad(
                                onDigitClick = { digit -> viewModel.appendDigit(digit) },
                                onDeleteClick = { viewModel.deleteLastDigit() },
                                onClearClick = { viewModel.clearPassword() }
                            )
                        }

                        // Submit Button
                        PrimaryButton(
                            text = "الدخول إلى لوحة التحكم",
                            onClick = { handleLogin() },
                            icon = Icons.Default.Login,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_login_submit_btn")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Back to user login button
                TextButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .testTag("admin_return_to_client_login")
                        .minimumInteractiveComponentSize()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = BrandPrimaryBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "العودة لتسجيل دخول العملاء",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = BrandPrimaryBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

/**
 * Visually appealing top hero emblem with layered glowing gradients and security motifs.
 */
@Composable
private fun AdminTopHeroIcon() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(116.dp)
            .padding(4.dp)
    ) {
        // Outer ambient glow ring
        Box(
            modifier = Modifier
                .size(112.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            BrandGold.copy(alpha = 0.25f),
                            BrandPrimaryBlue.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Main Shield Badge Surface with gradient and gold border
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = Color.Transparent,
            shadowElevation = 8.dp,
            modifier = Modifier.size(92.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0F172A), // Dark slate/navy
                                Color(0xFF1E3A8A), // Royal navy
                                Color(0xFF172554)
                            )
                        )
                    )
                    .border(
                        border = BorderStroke(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                listOf(
                                    BrandGold,
                                    Color(0xFFFDE68A),
                                    BrandPrimaryBlue
                                )
                            )
                        ),
                        shape = RoundedCornerShape(32.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Background subtle security shield watermark
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.12f),
                    modifier = Modifier.size(72.dp)
                )

                // Foreground security shield icon
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = "أيقونة بوابة إدارة وكالة شجين",
                    tint = Color.White,
                    modifier = Modifier.size(46.dp)
                )

                // Golden Lock Overlay Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-4).dp, y = (-4).dp)
                        .size(28.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(BrandGold, Color(0xFFD97706))
                            )
                        )
                        .border(1.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

/**
 * Visual PIN dot indicators showing masked bullet feedback for entered digits.
 */
@Composable
private fun PinDotsIndicator(
    pinLength: Int,
    totalDigits: Int = 5,
    isError: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        for (i in 0 until totalDigits) {
            val isFilled = i < pinLength
            val dotColor = when {
                isError -> StatusRejected
                isFilled -> BrandPrimaryBlue
                else -> Color(0xFFCBD5E1)
            }

            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(if (isFilled) dotColor else Color.Transparent)
                    .border(
                        width = 2.dp,
                        color = dotColor,
                        shape = CircleShape
                    )
            )
        }
    }
}

/**
 * Ergonomic on-screen touch dialpad designed for comfortable touch interaction.
 */
@Composable
private fun TouchKeypad(
    onDigitClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onClearClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFF8FAFC))
            .border(BorderStroke(1.dp, Color(0xFFE2E8F0)), RoundedCornerShape(18.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val rows = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9")
        )

        for (row in rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (digit in row) {
                    KeypadButton(
                        text = digit,
                        onClick = { onDigitClick(digit) }
                    )
                }
            }
        }

        // Bottom row: Clear, '0', Backspace
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Clear Key
            KeypadActionButton(
                icon = Icons.Default.ClearAll,
                label = "مسح",
                onClick = onClearClick,
                containerColor = Color(0xFFF1F5F9),
                contentColor = Color(0xFF64748B)
            )

            // '0' Key
            KeypadButton(
                text = "0",
                onClick = { onDigitClick("0") }
            )

            // Backspace Key
            KeypadActionButton(
                icon = Icons.Default.Backspace,
                label = "حذف",
                onClick = onDeleteClick,
                containerColor = Color(0xFFFEE2E2),
                contentColor = StatusRejected
            )
        }
    }
}

@Composable
private fun KeypadButton(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .size(54.dp)
            .minimumInteractiveComponentSize()
            .testTag("touch_keypad_btn_$text")
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = BrandHeaderColor,
                    fontSize = 20.sp
                )
            )
        }
    }
}

@Composable
private fun KeypadActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = containerColor,
        modifier = Modifier
            .size(54.dp)
            .minimumInteractiveComponentSize()
            .testTag("touch_keypad_action_$label")
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
