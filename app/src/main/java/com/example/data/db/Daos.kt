package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AgencyService
import com.example.data.model.AgencySettings
import com.example.data.model.AppNotification
import com.example.data.model.BookingDocument
import com.example.data.model.ElectronicBooking
import com.example.data.model.PaymentMethod
import com.example.data.model.RequiredDocument
import com.example.data.model.User
import com.example.data.model.VisaType
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): User?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: Long): Flow<User?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserByIdSync(id: Long): User?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE role = 'USER' ORDER BY fullName ASC")
    suspend fun getAllClientUsersSync(): List<User>

    @Query("SELECT COUNT(*) FROM users WHERE role = 'USER'")
    suspend fun getUserCount(): Int

    @Update
    suspend fun updateUser(user: User)
}

@Dao
interface BookingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: ElectronicBooking): Long

    @Update
    suspend fun updateBooking(booking: ElectronicBooking)

    @Query("SELECT * FROM electronic_bookings WHERE id = :id LIMIT 1")
    fun getBookingById(id: Long): Flow<ElectronicBooking?>

    @Query("SELECT * FROM electronic_bookings WHERE id = :id LIMIT 1")
    suspend fun getBookingByIdSync(id: Long): ElectronicBooking?

    @Query("SELECT * FROM electronic_bookings WHERE bookingNumber = :bookingNumber LIMIT 1")
    fun getBookingByNumber(bookingNumber: String): Flow<ElectronicBooking?>

    @Query("SELECT * FROM electronic_bookings WHERE userId = :userId ORDER BY createdAt DESC")
    fun getBookingsByUser(userId: Long): Flow<List<ElectronicBooking>>

    @Query("SELECT * FROM electronic_bookings ORDER BY createdAt DESC")
    fun getAllBookings(): Flow<List<ElectronicBooking>>

    @Query("SELECT bookingNumber FROM electronic_bookings ORDER BY id DESC LIMIT 1")
    suspend fun getLatestBookingNumber(): String?

    @Query("SELECT COUNT(*) FROM electronic_bookings")
    suspend fun getTotalBookingsCount(): Int

    @Query("SELECT COUNT(*) FROM electronic_bookings WHERE bookingStatus = :status")
    suspend fun getCountByBookingStatus(status: String): Int

    @Query("SELECT COUNT(*) FROM electronic_bookings WHERE paymentStatus = 'بانتظار التحقق'")
    suspend fun getPendingPaymentCount(): Int
}

@Dao
interface DocumentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: BookingDocument): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocuments(docs: List<BookingDocument>)

    @Update
    suspend fun updateDocument(doc: BookingDocument)

    @Query("DELETE FROM booking_documents WHERE id = :id")
    suspend fun deleteDocument(id: Long)

    @Query("SELECT * FROM booking_documents WHERE bookingId = :bookingId ORDER BY uploadedAt ASC")
    fun getDocumentsForBooking(bookingId: Long): Flow<List<BookingDocument>>

    @Query("SELECT * FROM booking_documents WHERE bookingId = :bookingId")
    suspend fun getDocumentsForBookingSync(bookingId: Long): List<BookingDocument>
}

@Dao
interface VisaTypeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisaType(visa: VisaType): Long

    @Update
    suspend fun updateVisaType(visa: VisaType)

    @Delete
    suspend fun deleteVisaType(visa: VisaType)

    @Query("DELETE FROM visa_types WHERE id = :id")
    suspend fun deleteVisaTypeById(id: Long)

    @Query("SELECT * FROM visa_types ORDER BY id ASC")
    fun getAllVisaTypes(): Flow<List<VisaType>>

    @Query("SELECT * FROM visa_types WHERE active = 1 ORDER BY id ASC")
    fun getActiveVisaTypes(): Flow<List<VisaType>>

    @Query("SELECT * FROM visa_types WHERE id = :id LIMIT 1")
    suspend fun getVisaTypeById(id: Long): VisaType?
}

@Dao
interface RequiredDocumentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequiredDoc(doc: RequiredDocument): Long

    @Update
    suspend fun updateRequiredDoc(doc: RequiredDocument)

    @Delete
    suspend fun deleteRequiredDoc(doc: RequiredDocument)

    @Query("DELETE FROM required_documents WHERE id = :id")
    suspend fun deleteRequiredDocById(id: Long)

    @Query("SELECT * FROM required_documents WHERE visaTypeId = :visaTypeId AND active = 1 ORDER BY id ASC")
    fun getRequiredDocsForVisa(visaTypeId: Long): Flow<List<RequiredDocument>>

    @Query("SELECT * FROM required_documents WHERE visaTypeId = :visaTypeId AND active = 1 ORDER BY id ASC")
    suspend fun getRequiredDocsForVisaSync(visaTypeId: Long): List<RequiredDocument>

    @Query("SELECT * FROM required_documents ORDER BY visaTypeId ASC, id ASC")
    fun getAllRequiredDocs(): Flow<List<RequiredDocument>>
}

@Dao
interface PaymentMethodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentMethod(pm: PaymentMethod): Long

    @Update
    suspend fun updatePaymentMethod(pm: PaymentMethod)

    @Delete
    suspend fun deletePaymentMethod(pm: PaymentMethod)

    @Query("DELETE FROM payment_methods WHERE id = :id")
    suspend fun deletePaymentMethodById(id: Long)

    @Query("SELECT * FROM payment_methods ORDER BY id ASC")
    fun getAllPaymentMethods(): Flow<List<PaymentMethod>>

    @Query("SELECT * FROM payment_methods WHERE active = 1 ORDER BY id ASC")
    fun getActivePaymentMethods(): Flow<List<PaymentMethod>>
}

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotification): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<AppNotification>)

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    fun getNotificationsForUser(userId: Long): Flow<List<AppNotification>>

    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    fun getAllNotifications(): Flow<List<AppNotification>>

    @Query("UPDATE notifications SET read = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND read = 0")
    fun getUnreadCount(userId: Long): Flow<Int>
}

@Dao
interface AgencyServiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: AgencyService): Long

    @Update
    suspend fun updateService(service: AgencyService)

    @Query("DELETE FROM agency_services WHERE id = :id")
    suspend fun deleteServiceById(id: Long)

    @Query("SELECT * FROM agency_services ORDER BY id ASC")
    fun getAllServices(): Flow<List<AgencyService>>

    @Query("SELECT * FROM agency_services WHERE active = 1 ORDER BY id ASC")
    fun getActiveServices(): Flow<List<AgencyService>>

    @Query("SELECT COUNT(*) FROM agency_services")
    suspend fun getServicesCount(): Int
}

@Dao
interface AgencySettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: AgencySettings)

    @Query("SELECT * FROM agency_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<AgencySettings?>

    @Query("SELECT * FROM agency_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsSync(): AgencySettings?
}
