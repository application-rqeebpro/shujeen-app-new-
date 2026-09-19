package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ElectronicBooking
import com.example.ui.components.PrimaryButton
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.viewmodel.ShajeenViewModel

@Composable
fun BookingSuccessScreen(
    booking: ElectronicBooking,
    viewModel: ShajeenViewModel,
    onViewBooking: (Long) -> Unit,
    onNavigateHome: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackgroundStart)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Success check emblem
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(Color(0xFFDCFCE7)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = StatusConfirmed,
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "تم استلام طلب الحجز بنجاح!",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = BrandHeaderColor,
                fontSize = 22.sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "سيتم مراجعة طلبك والتحقق من عملية الدفع من قبل فريق وكالة شجين في أقرب وقت.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = BrandTextSecondary,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Booking Info Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Booking Number Row with Copy
                Surface(
                    color = Color(0xFFEFF8FE),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("رقم الحجز الإلكتروني", style = MaterialTheme.typography.bodySmall, color = BrandTextSecondary)
                            Text(
                                text = booking.bookingNumber,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BrandPrimaryDarkBlue
                                )
                            )
                        }

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Booking Number", booking.bookingNumber)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "تم نسخ رقم الحجز", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", tint = BrandPrimaryBlue)
                        }
                    }
                }

                Divider(color = BrandCardBorder)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("اسم المسافر:", style = MaterialTheme.typography.bodySmall, color = BrandTextSecondary)
                    Text(booking.fullName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("نوع التأشيرة:", style = MaterialTheme.typography.bodySmall, color = BrandTextSecondary)
                    Text(booking.visaType, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("حالة الحجز:", style = MaterialTheme.typography.bodySmall, color = BrandTextSecondary)
                    StatusBadge(status = booking.bookingStatus)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("حالة الدفع:", style = MaterialTheme.typography.bodySmall, color = BrandTextSecondary)
                    StatusBadge(status = booking.paymentStatus)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // WhatsApp Direct Action Button (As strictly specified in prompt)
        Button(
            onClick = { viewModel.openWhatsAppBooking(context, booking) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("whatsapp_confirmation_btn")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "التواصل والمتابعة عبر واتساب",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // View Booking Button
        OutlinedButton(
            onClick = { onViewBooking(booking.id) },
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, BrandPrimaryBlue),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("view_submitted_booking_btn")
        ) {
            Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = BrandPrimaryBlue)
            Spacer(modifier = Modifier.width(8.dp))
            Text("متابعة تفاصيل الحجز", fontWeight = FontWeight.Bold, color = BrandPrimaryBlue)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Return Home Button
        TextButton(onClick = onNavigateHome) {
            Text("العودة إلى الصفحة الرئيسية", color = BrandTextSecondary)
        }
    }
}
