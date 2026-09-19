package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ElectronicBooking
import com.example.ui.components.PrimaryButton
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.viewmodel.ShajeenViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MyBookingsScreen(
    viewModel: ShajeenViewModel,
    onBookingClick: (Long) -> Unit,
    onStartNewBooking: () -> Unit
) {
    val bookings by viewModel.userBookings.collectAsState()
    var selectedFilter by remember { mutableStateOf("الكل") }

    val filters = listOf("الكل", "جديد", "قيد المراجعة", "تم تأكيد الحجز", "مكتمل")

    val filteredBookings = when (selectedFilter) {
        "الكل" -> bookings
        else -> bookings.filter { it.bookingStatus == selectedFilter }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackgroundStart)
            .testTag("my_bookings_screen")
    ) {
        // Filters Chip Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { filter ->
                val isSelected = selectedFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = filter },
                    label = {
                        Text(
                            text = filter,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFE0F2FE),
                        selectedLabelColor = BrandPrimaryDarkBlue
                    )
                )
            }
        }

        if (filteredBookings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2E8F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ConfirmationNumber,
                            contentDescription = null,
                            tint = BrandTextSecondary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "لا توجد طلبات حجز حالياً",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BrandHeaderColor
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "يمكنك تقديم طلب حجز تأشيرة جديد وسنتابع طلبك على الفور",
                        style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    PrimaryButton(
                        text = "تقديم طلب حجز جديد",
                        onClick = onStartNewBooking,
                        icon = Icons.Default.Add,
                        modifier = Modifier.width(220.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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

@Composable
fun BookingListItemCard(
    booking: ElectronicBooking,
    onClick: () -> Unit
) {
    val dateStr = remember(booking.createdAt) {
        SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale.getDefault()).format(Date(booking.createdAt))
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("booking_card_${booking.bookingNumber}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = booking.bookingNumber,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = BrandPrimaryDarkBlue
                    )
                )

                StatusBadge(status = booking.bookingStatus)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "المسافر: ${booking.fullName}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            Text(
                text = "نوع التأشيرة: ${booking.visaType} | الوجهة: ${booking.destination.ifBlank { "غير محدد" }}",
                style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = BrandCardBorder)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8), fontSize = 11.sp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("الدفع:", style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary))
                    StatusBadge(status = booking.paymentStatus)
                }
            }
        }
    }
}
