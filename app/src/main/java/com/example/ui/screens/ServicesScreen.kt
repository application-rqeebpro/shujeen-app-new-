package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PrimaryButton
import com.example.ui.theme.*
import com.example.viewmodel.ShajeenViewModel

data class DetailedService(
    val title: String,
    val subtitle: String,
    val description: String,
    val features: List<String>,
    val icon: ImageVector,
    val accentColor: Color
)

@Composable
fun ServicesScreen(
    viewModel: ShajeenViewModel? = null,
    onBookServiceClick: () -> Unit
) {
    val dynamicServices by viewModel?.activeServices?.collectAsState() ?: androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf(emptyList())
    }

    val defaultFallbackServices = listOf(
        DetailedService(
            title = "خدمات التأشيرات والإقامات",
            subtitle = "إصدار ومتابعة وتصديق كافة أنواع التأشيرات",
            description = "نوفر في وكالة شجين حلولاً متكاملة لتسهيل إجراءات التأشيرات الرسمية بأعلى سرعة ودقة ومتابعة مستمرة مع الجهات المختصة.",
            features = listOf(
                "تأشيرات العمل والإقامة للمملكة العربية السعودية والخليج",
                "تأشيرات الزيارة العائلية والتجارية",
                "التأشيرات السياحية العالمية لدول العالم",
                "تصديق المستندات والشهادات وتفويض التأشيرات"
            ),
            icon = Icons.Default.Public,
            accentColor = BrandPrimaryBlue
        ),
        DetailedService(
            title = "برامج الحج والعمرة",
            subtitle = "خدمة ضيوف الرحمن بأفضل البرامج",
            description = "برامج متميزة ومريحة لأداء مناسك الحج والعمرة بأعلى درجات الرعاية والاهتمام.",
            features = listOf(
                "إصدار تأشيرات العمرة بسرعة وسهولة",
                "حجوزات فنادق قريبة من الحرم المكي والمسجد النبوي",
                "نقل بري وجوي حديث ومريح من مختلف المحافظات",
                "إشراف ومتابعة على مدار الساعة لخدمة المعتمرين"
            ),
            icon = Icons.Default.Mosque,
            accentColor = BrandGold
        ),
        DetailedService(
            title = "حجوزات تذاكر الطيران",
            subtitle = "أفضل الأسعار على جميع خطوط الطيران",
            description = "إمكانية حجز وتعديل تذاكر الطيران الداخلية والدولية لجميع الوجهات العالمية مع تسهيلات في الدفع.",
            features = listOf(
                "مقارنة الأسعار لاختيار أنسب رحلة",
                "إصدار فوري للتذاكر وتعديل المواعيد",
                "خدمة الوزن الإضافي واختيار المقاعد",
                "دعم فني وتنبيهات فورية بمواعيد الإقلاع"
            ),
            icon = Icons.Default.FlightTakeoff,
            accentColor = BrandPrimaryDarkBlue
        ),
        DetailedService(
            title = "النقل البري والسياحي",
            subtitle = "رحلات برية آمنة ومريحة",
            description = "أسطول نقل حديث من باصات النقل الجماعي وسيارات VIP المكيفة للتنقل بين المدن وإلى المنافذ البرية.",
            features = listOf(
                "باصات حديثة مجهزة ومكيفة",
                "رحلات يومية منتظمة إلى مختلف المنافذ",
                "سائقون ذوو خبرة وكفاءة عالية",
                "تسهيل إجراءات العبور بالتعاون مع الجهات الرسمية"
            ),
            icon = Icons.Default.DirectionsBus,
            accentColor = Color(0xFF10B981)
        ),
        DetailedService(
            title = "البرامج والجولات السياحية",
            subtitle = "اكتشف أجمل الوجهات السياحية",
            description = "تنظيم رحلات وجولات سياحية متكاملة للأفراد والعائلات والمجموعات إلى مختلف المعالم التراثية والطبيعية.",
            features = listOf(
                "جولات سياحية تاريخية وتراثية",
                "توفير مرشدين سياحيين مؤهلين",
                "حزم شاملة للإقامة والتنقل والوجبات",
                "برامج مخصصة حسب رغبة العميل"
            ),
            icon = Icons.Default.BeachAccess,
            accentColor = Color(0xFF8B5CF6)
        )
    )

    val services: List<DetailedService> = if (dynamicServices.isNotEmpty()) {
        dynamicServices.map { ds ->
            val iconVec = when (ds.iconName) {
                "visa" -> Icons.Default.Public
                "umrah" -> Icons.Default.Mosque
                "flight" -> Icons.Default.FlightTakeoff
                "bus" -> Icons.Default.DirectionsBus
                "tourism" -> Icons.Default.BeachAccess
                else -> Icons.Default.Star
            }
            val clr = try {
                Color(android.graphics.Color.parseColor(ds.accentColorHex))
            } catch (e: Exception) {
                BrandPrimaryBlue
            }
            DetailedService(
                title = ds.title,
                subtitle = ds.subtitle,
                description = ds.description,
                features = ds.features.split("\n").map { it.trim() }.filter { it.isNotBlank() },
                icon = iconVec,
                accentColor = clr
            )
        }
    } else {
        defaultFallbackServices
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackgroundStart)
            .testTag("services_screen_list"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "دليل خدمات وكالة شجين",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = BrandHeaderColor
                )
            )
            Text(
                text = "اختر الخدمة المناسبة وقدّم طلبك مباشرة عبر التطبيق",
                style = MaterialTheme.typography.bodyMedium.copy(color = BrandTextSecondary)
            )
        }

        items(services) { service ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(service.accentColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = service.icon,
                                contentDescription = null,
                                tint = service.accentColor,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = service.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = service.subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = service.description,
                        style = MaterialTheme.typography.bodyMedium.copy(color = BrandTextPrimary, lineHeight = 20.sp)
                    )

                    if (service.features.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            service.features.forEach { feature ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = StatusConfirmed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = feature,
                                        style = MaterialTheme.typography.bodySmall.copy(color = BrandTextPrimary)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    PrimaryButton(
                        text = "طلب الخدمة الآن",
                        onClick = onBookServiceClick,
                        icon = Icons.Default.Send,
                        modifier = Modifier.height(44.dp)
                    )
                }
            }
        }
    }
}
