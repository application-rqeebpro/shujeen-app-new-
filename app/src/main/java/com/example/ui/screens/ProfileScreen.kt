package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PrimaryButton
import com.example.ui.theme.*
import com.example.viewmodel.ShajeenViewModel

@Composable
fun ProfileScreen(
    viewModel: ShajeenViewModel,
    onLogout: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()
    val agencySettings by viewModel.agencySettings.collectAsState()

    var showContactNumbersDialog by remember { mutableStateOf(false) }

    val contactNumbers = listOf(
        "+967 777779492",
        "+966 551160835",
        "+967 774191789",
        "+967 770038009"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackgroundStart)
            .testTag("profile_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // User Info Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (isAdmin) Color(0xFF1E293B) else BrandPrimaryBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isAdmin) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentUser?.fullName ?: "المستخدم",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                if (isAdmin) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = Color(0xFFFEF3C7),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "مدير النظام",
                                            color = Color(0xFF92400E),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = currentUser?.phone ?: "",
                                style = MaterialTheme.typography.bodyMedium.copy(color = BrandTextSecondary)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = BrandCardBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    ProfileDetailRow("نوع الهوية", currentUser?.idType ?: "بطاقة شخصية")
                    ProfileDetailRow("رقم الهوية", currentUser?.idNumber ?: "-")
                    ProfileDetailRow("الدولة والمدينة", "${currentUser?.country ?: "اليمن"} - ${currentUser?.city ?: "صنعاء"}")
                    if (!currentUser?.district.isNullOrBlank()) {
                        ProfileDetailRow("المديرية والحي", "${currentUser?.district} - ${currentUser?.area}")
                    }
                }
            }
        }

        // Admin Access Banner (If Admin)
        if (isAdmin) {
            item {
                Card(
                    onClick = onNavigateToAdmin,
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    modifier = Modifier.fillMaxWidth().testTag("profile_admin_access_card")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.Dashboard, contentDescription = null, tint = BrandGold)
                            Column {
                                Text("لوحة إدارة وكالة شجين", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                Text("إدارة الحجوزات، التأشيرات، وسائل الدفع والإحصائيات", color = Color(0xFF94A3B8), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = Color.White)
                    }
                }
            }
        }

        // Official Agency Info Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "بيانات وكالة شجين للسفريات والسياحة",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimaryDarkBlue
                        )
                    )

                    Divider(color = BrandCardBorder)

                    // Address
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = BrandGold, modifier = Modifier.size(20.dp))
                        Column {
                            Text("العنوان:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            Text(
                                text = agencySettings?.address ?: "صنعاء - شارع خولان - جوار السلامي لمواد البناء",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Contact numbers preview
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = BrandPrimaryBlue, modifier = Modifier.size(20.dp))
                        Column {
                            Text("أرقام التواصل الرسمية:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            contactNumbers.forEach { num ->
                                Text(
                                    text = num,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                            }
                        }
                    }

                    // Approved Payment Methods
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.Payments, contentDescription = null, tint = StatusConfirmed, modifier = Modifier.size(20.dp))
                        Column {
                            Text("وسائل الدفع المعتمدة:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            Text("جيب (Jib) - ون كاش (One Cash) - جوالي (Jawali)", style = MaterialTheme.typography.bodyMedium)
                            Text("رقم الحساب الموحد: 770038009", fontWeight = FontWeight.Bold, color = BrandPrimaryDarkBlue, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Agency Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Call Dialog
                        OutlinedButton(
                            onClick = { showContactNumbersDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("اتصال", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        // WhatsApp Direct
                        Button(
                            onClick = {
                                val dummyBooking = com.example.data.model.ElectronicBooking(
                                    bookingNumber = "استفسار",
                                    userId = currentUser?.id ?: 0L,
                                    fullName = currentUser?.fullName ?: "",
                                    phone = currentUser?.phone ?: "",
                                    country = "اليمن",
                                    city = "صنعاء",
                                    destination = "-",
                                    travelDate = "-",
                                    visaType = "عام",
                                    paymentMethod = "-",
                                    transactionNumber = "-"
                                )
                                viewModel.openWhatsAppBooking(context, dummyBooking)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("واتساب", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        // Map location
                        OutlinedButton(
                            onClick = { viewModel.openMapLocation(context) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("الخريطة", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Switch role demo buttons (Useful for testing)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("تبديل الحساب التجريبي:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.switchToAdminDemo() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("الدخول كمدير", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = { viewModel.switchToUserDemo() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("الدخول كعميل", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Logout Button
        item {
            OutlinedButton(
                onClick = { viewModel.logout(onLoggedOut = onLogout) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRejected),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, StatusRejected),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("logout_button")
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, tint = StatusRejected)
                Spacer(modifier = Modifier.width(8.dp))
                Text("تسجيل الخروج", fontWeight = FontWeight.Bold, color = StatusRejected)
            }
        }
    }

    if (showContactNumbersDialog) {
        AlertDialog(
            onDismissRequest = { showContactNumbersDialog = false },
            title = { Text("أرقام التواصل مع الوكالة", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    contactNumbers.forEach { num ->
                        Surface(
                            onClick = {
                                viewModel.openDialer(context, num)
                                showContactNumbersDialog = false
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(num, fontWeight = FontWeight.Bold)
                                Icon(Icons.Default.Call, contentDescription = "اتصال", tint = BrandPrimaryBlue)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showContactNumbersDialog = false }) {
                    Text("إغلاق")
                }
            }
        )
    }
}

@Composable
fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary))
        Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
    }
}
