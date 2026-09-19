package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ElectronicBooking
import com.example.ui.components.PrimaryButton
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.viewmodel.ShajeenViewModel

data class ServiceItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun HomeScreen(
    viewModel: ShajeenViewModel,
    onNavigateToBookingWizard: () -> Unit,
    onNavigateToMyBookings: () -> Unit,
    onNavigateToServices: () -> Unit,
    onBookingClick: (Long) -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val userBookings by viewModel.userBookings.collectAsState()
    val agencySettings by viewModel.agencySettings.collectAsState()

    val services = listOf(
        ServiceItem("تأشيرات العمل والسياحة", "إصدار ومتابعة التأشيرات بكفاءة", Icons.Default.Public, BrandPrimaryBlue),
        ServiceItem("رحلات الحج والعمرة", "برامج متكاملة لزيارة بيت الله الحرام", Icons.Default.Mosque, BrandGold),
        ServiceItem("تذاكر الطيران", "حجز تذاكر لكافة الوجهات والخطوط", Icons.Default.FlightTakeoff, BrandPrimaryDarkBlue),
        ServiceItem("النقل البري والسياحي", "خدمات نقل آمنة وسيارات مجهزة", Icons.Default.DirectionsBus, Color(0xFF10B981)),
        ServiceItem("البرامج السياحية", "جولات متكاملة داخل وخارج اليمن", Icons.Default.BeachAccess, Color(0xFF8B5CF6)),
        ServiceItem("عروض خاصة وحصرية", "أفضل الأسعار والتخفيضات الموسمية", Icons.Default.LocalOffer, Color(0xFFEC4899))
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackgroundStart)
            .testTag("home_screen_list"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Hero Greeting Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(BrandHeaderColor, BrandPrimaryDarkBlue)
                        ),
                        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "مرحبًا بك،",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFCBD5E1))
                            )
                            Text(
                                text = currentUser?.fullName ?: "ضيفنا العزيز",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        // Logo emblem
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.95f),
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_shajeen_logo),
                                    contentDescription = "شعار شجين",
                                    modifier = Modifier.size(44.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Agency Address Banner
                    Surface(
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = BrandGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = agencySettings?.address ?: "صنعاء - شارع خولان - جوار السلامي لمواد البناء",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Electronic Booking Highlight Banner (The Central Feature)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0F2FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlightTakeoff,
                                contentDescription = null,
                                tint = BrandPrimaryBlue,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "الحجز الإلكتروني الفوري",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BrandHeaderColor
                                )
                            )
                            Text(
                                text = "قدّم طلب التأشيرة وارفع المستندات وأرسل إثبات الدفع بكل سهولة",
                                style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary),
                                maxLines = 2
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    PrimaryButton(
                        text = "تقديم طلب حجز إلكتروني الآن",
                        onClick = {
                            viewModel.startNewBooking()
                            onNavigateToBookingWizard()
                        },
                        icon = Icons.Default.AddCircleOutline,
                        modifier = Modifier.testTag("home_start_booking_btn")
                    )
                }
            }
        }

        // Quick Action Bar (WhatsApp, Call, Map)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // WhatsApp
                Card(
                    onClick = {
                        val bookingPlaceholder = ElectronicBooking(
                            bookingNumber = "استفسار_عام",
                            userId = currentUser?.id ?: 0L,
                            fullName = currentUser?.fullName ?: "عميل",
                            phone = currentUser?.phone ?: "",
                            country = "اليمن",
                            city = "صنعاء",
                            destination = "استفسار",
                            travelDate = "-",
                            visaType = "استفسار عام",
                            paymentMethod = "Jib",
                            transactionNumber = "-"
                        )
                        viewModel.openWhatsAppBooking(context, bookingPlaceholder)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7))
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("واتساب الوكالة", fontWeight = FontWeight.Bold, color = Color(0xFF15803D), fontSize = 13.sp)
                    }
                }

                // Call
                Card(
                    onClick = { viewModel.openDialer(context, "770038009") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2FE))
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.PhoneInTalk, contentDescription = null, tint = BrandPrimaryBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("اتصال مباشر", fontWeight = FontWeight.Bold, color = BrandPrimaryBlue, fontSize = 13.sp)
                    }
                }

                // Location Map
                Card(
                    onClick = { viewModel.openMapLocation(context) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Map, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("موقع الوكالة", fontWeight = FontWeight.Bold, color = Color(0xFFB45309), fontSize = 13.sp)
                    }
                }
            }
        }

        // Recent Bookings Section
        if (userBookings.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "آخر الحجوزات",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        TextButton(onClick = onNavigateToMyBookings) {
                            Text("عرض الكل (${userBookings.size})", color = BrandPrimaryBlue)
                        }
                    }

                    // Display up to 2 recent bookings
                    userBookings.take(2).forEach { booking ->
                        BookingPreviewCard(
                            booking = booking,
                            onClick = { onBookingClick(booking.id) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }

        // Services Grid
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "خدمات وكالة شجين",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    TextButton(onClick = onNavigateToServices) {
                        Text("جميع الخدمات", color = BrandPrimaryBlue)
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    services.chunked(2).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowItems.forEach { service ->
                                Card(
                                    onClick = {
                                        viewModel.startNewBooking()
                                        onNavigateToBookingWizard()
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(service.color.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = service.icon,
                                                contentDescription = null,
                                                tint = service.color,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text(
                                            text = service.title,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = service.description,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = BrandTextSecondary,
                                                fontSize = 11.sp
                                            ),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
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

@Composable
fun BookingPreviewCard(
    booking: ElectronicBooking,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("booking_item_${booking.bookingNumber}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = booking.bookingNumber,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = BrandPrimaryDarkBlue
                    )
                )

                StatusBadge(status = booking.bookingStatus)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "نوع التأشيرة: ${booking.visaType}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                    )
                    Text(
                        text = "الوجهة: ${booking.destination.ifBlank { "غير محدد" }}",
                        style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "حالة الدفع:",
                        style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary)
                    )
                    StatusBadge(status = booking.paymentStatus)
                }
            }
        }
    }
}
