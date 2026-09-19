package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShajeenTopAppBar(
    title: String,
    subtitle: String? = null,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    unreadCount: Int = 0,
    onNotificationsClick: (() -> Unit)? = null,
    isAdminUser: Boolean = false,
    onAdminClick: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp),
        color = Color.White
    ) {
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_shajeen_logo),
                        contentDescription = "شعار وكالة شجين",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                    )
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BrandHeaderColor
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (!subtitle.isNullOrBlank()) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BrandTextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            },
            navigationIcon = {
                if (showBackButton) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("top_bar_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = BrandHeaderColor
                        )
                    }
                }
            },
            actions = {
                if (onAdminClick != null && isAdminUser) {
                    Surface(
                        onClick = onAdminClick,
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFEFF6FF),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandPrimaryBlue),
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .testTag("admin_panel_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "لوحة الإدارة",
                                tint = BrandPrimaryDarkBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "الإدارة",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = BrandPrimaryDarkBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                if (onNotificationsClick != null) {
                    IconButton(
                        onClick = onNotificationsClick,
                        modifier = Modifier.testTag("notifications_icon_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadCount > 0) {
                                    Badge(
                                        containerColor = StatusRejected,
                                        contentColor = Color.White
                                    ) {
                                        Text(text = if (unreadCount > 9) "+9" else "$unreadCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "الإشعارات",
                                tint = BrandHeaderColor
                            )
                        }
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )
    }
}

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status) {
        "جديد" -> Color(0xFFE0F2FE) to StatusNew
        "قيد المراجعة" -> Color(0xFFFEF9C3) to Color(0xFF854D0E)
        "بانتظار مستندات" -> Color(0xFFFEF3C7) to Color(0xFF92400E)
        "بانتظار التحقق", "بانتظار التحقق من الدفع" -> Color(0xFFFEF3C7) to StatusPendingPayment
        "تم التحقق", "تم تأكيد الحجز" -> Color(0xFFD1FAE5) to StatusConfirmed
        "قيد التنفيذ" -> Color(0xFFEEF2FF) to StatusProcessing
        "مكتمل" -> Color(0xFFDCFCE7) to StatusCompleted
        "مرفوض", "غير صالح" -> Color(0xFFFEE2E2) to StatusRejected
        "ملغي" -> Color(0xFFF1F5F9) to StatusCancelled
        else -> Color(0xFFF1F5F9) to BrandTextSecondary
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Text(
            text = status,
            color = textColor,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            ),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun ShajeenBottomNavBar(
    currentRoute: String,
    unreadNotifications: Int,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier
            .shadow(12.dp)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        val navItems = listOf(
            Triple("home", "الرئيسية", Icons.Default.Home),
            Triple("services", "الخدمات", Icons.Default.Explore),
            Triple("my_bookings", "حجوزاتي", Icons.Default.ConfirmationNumber),
            Triple("notifications", "الإشعارات", Icons.Default.Notifications),
            Triple("profile", "حسابي", Icons.Default.Person)
        )

        navItems.forEach { (route, label, icon) ->
            val isSelected = currentRoute == route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(route) },
                icon = {
                    if (route == "notifications" && unreadNotifications > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = StatusRejected,
                                    contentColor = Color.White
                                ) {
                                    Text(text = if (unreadNotifications > 9) "+9" else "$unreadNotifications")
                                }
                            }
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label
                            )
                        }
                    } else {
                        Icon(
                            imageVector = icon,
                            contentDescription = label
                        )
                    }
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BrandHeaderColor,
                    selectedTextColor = BrandHeaderColor,
                    indicatorColor = Color(0xFFD0EFFC),
                    unselectedIconColor = BrandTextSecondary,
                    unselectedTextColor = BrandTextSecondary
                ),
                modifier = Modifier.testTag("nav_item_$route")
            )
        }
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BrandPrimaryBlue,
            contentColor = Color.White,
            disabledContainerColor = Color(0xFFBBE5F6),
            disabledContentColor = Color.White
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("primary_button_${text.replace(" ", "_")}")
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.5.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}
