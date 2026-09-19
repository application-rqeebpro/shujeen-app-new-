package com.example.ui.components

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.storage.DocumentStorageManager
import com.example.ui.theme.*
import java.io.File

@Composable
fun DocumentUploadCard(
    docName: String,
    isRequired: Boolean,
    fileInfo: DocumentStorageManager.SavedFileInfo?,
    storageManager: DocumentStorageManager,
    onFileSelected: (DocumentStorageManager.SavedFileInfo) -> Unit,
    onFileRemoved: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showOptionsSheet by remember { mutableStateOf(false) }
    var showPreviewDialog by remember { mutableStateOf(false) }

    // System Activity Launchers for Android
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val saved = storageManager.saveDocumentFromUri(it, subfolder = "documents")
            onFileSelected(saved)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            val saved = storageManager.saveBitmap(it, prefix = docName.replace(" ", "_"))
            onFileSelected(saved)
        }
    }

    val pdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val saved = storageManager.saveDocumentFromUri(it, subfolder = "pdf_docs")
            onFileSelected(saved)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("doc_card_${docName.replace(" ", "_")}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (fileInfo != null) Color(0xFFD1FAE5) else Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (fileInfo != null) Icons.Default.CheckCircle else Icons.Default.Description,
                            contentDescription = null,
                            tint = if (fileInfo != null) StatusConfirmed else BrandPrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = docName,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            if (isRequired) {
                                Text(
                                    text = " *",
                                    color = StatusRejected,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        }

                        Text(
                            text = if (isRequired) "مطلوب" else "اختياري",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isRequired) BrandTextSecondary else Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                if (fileInfo != null) {
                    Surface(
                        color = Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "تم الرفع ✓",
                            color = StatusConfirmed,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (fileInfo != null) {
                // File info and action buttons (Preview, Replace, Delete)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (fileInfo.mimeType.contains("pdf")) Icons.Default.PictureAsPdf else Icons.Default.Image,
                                contentDescription = null,
                                tint = BrandPrimaryDarkBlue,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = fileInfo.fileName,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    maxLines = 1
                                )
                                Text(
                                    text = "${fileInfo.fileSize / 1024} KB",
                                    style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary)
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            // Preview
                            IconButton(
                                onClick = { showPreviewDialog = true },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = "معاينة",
                                    tint = BrandPrimaryBlue
                                )
                            }

                            // Replace
                            IconButton(
                                onClick = { showOptionsSheet = true },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Autorenew,
                                    contentDescription = "استبدال",
                                    tint = StatusReview
                                )
                            }

                            // Delete
                            IconButton(
                                onClick = onFileRemoved,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "حذف",
                                    tint = StatusRejected
                                )
                            }
                        }
                    }
                }
            } else {
                // Upload button
                OutlinedButton(
                    onClick = { showOptionsSheet = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = BrandPrimaryBlue
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, BrandPrimaryBlue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "رفع الملف (كاميرا / معرض / PDF)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }

    // Modal Sheet for Source Selection
    if (showOptionsSheet) {
        AlertDialog(
            onDismissRequest = { showOptionsSheet = false },
            title = {
                Text(
                    text = "اختيار مصدر المستند",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "اختر الطريقة المناسبة لرفع: $docName",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Camera
                    Card(
                        onClick = {
                            showOptionsSheet = false
                            cameraLauncher.launch(null)
                        },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F9FF)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = BrandPrimaryBlue)
                            Column {
                                Text("الكاميرا (تصوير فوري)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text("التقاط صورة واضحة ومباشرة للمستند", style = MaterialTheme.typography.bodySmall, color = BrandTextSecondary)
                            }
                        }
                    }

                    // Gallery (Images)
                    Card(
                        onClick = {
                            showOptionsSheet = false
                            galleryLauncher.launch("image/*")
                        },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = StatusConfirmed)
                            Column {
                                Text("معرض الصور (JPG / PNG)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text("اختيار صورة من استوديو الهاتف", style = MaterialTheme.typography.bodySmall, color = BrandTextSecondary)
                            }
                        }
                    }

                    // Files / PDF
                    Card(
                        onClick = {
                            showOptionsSheet = false
                            pdfLauncher.launch("application/pdf")
                        },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = StatusPendingPayment)
                            Column {
                                Text("ملف مستند (PDF)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text("اختيار ملف بصيغة PDF من الهاتف", style = MaterialTheme.typography.bodySmall, color = BrandTextSecondary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showOptionsSheet = false }) {
                    Text("إلغاء", color = BrandTextSecondary)
                }
            }
        )
    }

    // Preview Dialog
    if (showPreviewDialog && fileInfo != null) {
        Dialog(onDismissRequest = { showPreviewDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "معاينة: $docName",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val file = File(fileInfo.filePath)
                    if (fileInfo.mimeType.contains("pdf")) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = StatusRejected,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = fileInfo.fileName, fontWeight = FontWeight.Bold)
                            Text(text = "ملف PDF صالح ومحفوظ بأمان في النظام", style = MaterialTheme.typography.bodySmall, color = BrandTextSecondary)
                        }
                    } else if (file.exists()) {
                        AsyncImage(
                            model = file,
                            contentDescription = docName,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.05f))
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showPreviewDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimaryBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("إغلاق المعاينة", color = Color.White)
                    }
                }
            }
        }
    }
}
