package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["phone"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val phone: String,
    val email: String? = null,
    val idType: String = "بطاقة شخصية", // "جواز سفر" or "بطاقة شخصية"
    val idNumber: String = "",
    val country: String = "اليمن",
    val city: String = "صنعاء",
    val district: String = "",
    val area: String = "",
    val passwordHash: String,
    val role: String = "USER", // "USER" or "ADMIN"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "electronic_bookings",
    indices = [Index(value = ["bookingNumber"], unique = true), Index(value = ["userId"])]
)
data class ElectronicBooking(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookingNumber: String, // e.g. SHJ-2026-000001
    val userId: Long,
    val fullName: String,
    val phone: String,
    val country: String,
    val city: String,
    val destination: String,
    val travelDate: String,
    val visaType: String,
    val otherVisaType: String? = null,
    val notes: String? = null,
    val bookingStatus: String = "جديد", // جديد, قيد المراجعة, بانتظار مستندات, بانتظار التحقق من الدفع, تم تأكيد الحجز, قيد التنفيذ, مكتمل, مرفوض, ملغي
    val paymentMethod: String, // Jib, One Cash, Jawali
    val paymentStatus: String = "بانتظار التحقق", // بانتظار التحقق, تم التحقق, غير صالح
    val transactionNumber: String,
    val paymentReceiptPath: String = "",
    val adminNotes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "booking_documents",
    indices = [Index(value = ["bookingId"])]
)
data class BookingDocument(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookingId: Long,
    val documentType: String, // صورة جواز السفر, الصورة الشخصية, صورة الهوية, مستند إضافي
    val fileUrl: String,
    val fileName: String,
    val fileSize: Long = 0L,
    val mimeType: String = "image/jpeg",
    val verificationStatus: String = "بانتظار المراجعة", // بانتظار المراجعة, مقبول, مرفوض
    val uploadedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "visa_types")
data class VisaType(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val active: Boolean = true
)

@Entity(
    tableName = "required_documents",
    indices = [Index(value = ["visaTypeId"])]
)
data class RequiredDocument(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val visaTypeId: Long,
    val documentName: String,
    val required: Boolean = true,
    val active: Boolean = true
)

@Entity(tableName = "payment_methods")
data class PaymentMethod(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val accountNumber: String,
    val instructions: String = "",
    val active: Boolean = true
)

@Entity(
    tableName = "notifications",
    indices = [Index(value = ["userId"])]
)
data class AppNotification(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val bookingId: Long? = null,
    val title: String,
    val message: String,
    val read: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "agency_services")
data class AgencyService(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subtitle: String,
    val description: String,
    val features: String = "",
    val iconName: String = "flight", // flight, visa, umrah, bus, tourism, custom
    val accentColorHex: String = "#0284C7",
    val active: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "agency_settings")
data class AgencySettings(

    @PrimaryKey val id: Long = 1,
    val agencyName: String = "وكالة شجين للسفريات والسياحة",
    val address: String = "صنعاء - شارع خولان - جوار السلامي لمواد البناء",
    val phoneNumbers: String = "+967 777779492, +966 551160835, +967 774191789, +967 770038009",
    val whatsapp: String = "770038009",
    val logo: String = "ic_shajeen_logo",
    val mapUrl: String = "https://maps.google.com/?q=15.3694,44.1910"
)
