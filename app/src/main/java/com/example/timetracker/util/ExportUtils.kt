package com.example.timetracker.util

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.example.timetracker.domain.model.WorkEntry
import com.example.timetracker.domain.model.WorkSummary
import java.io.File
import java.io.FileOutputStream
import java.time.format.DateTimeFormatter
import java.util.*

object ExportUtils {
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME

    fun generateCsv(entries: List<WorkEntry>, context: Context): File {
        val file = File(context.cacheDir, "work_entries_${System.currentTimeMillis()}.csv")
        FileOutputStream(file).use { output ->
            // Write header
            output.write("Date,Start Time,End Time,Duration (hours),Materials Cost,Comment (EN),Comment (NL)\n".toByteArray())
            
            // Write entries
            entries.forEach { entry ->
                val duration = entry.getDurationHours()
                val line = "${entry.startTime.format(dateFormatter)}," +
                        "${entry.startTime.format(timeFormatter)}," +
                        "${entry.endTime.format(timeFormatter)}," +
                        "%.2f,".format(duration) +
                        "%.2f,".format(entry.materialsCost) +
                        "\"${entry.commentEn?.replace("\"", "\"\"") ?: ""}\"," +
                        "\"${entry.commentNl?.replace("\"", "\"\"") ?: ""}\"\n"
                output.write(line.toByteArray())
            }
        }
        return file
    }

    fun generatePdf(entries: List<WorkEntry>, summary: WorkSummary, context: Context): File {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 12f
        }

        var y = 50f
        val lineHeight = 20f
        val margin = 50f

        // Write header
        canvas.drawText("Work Time Report", margin, y, paint.apply { textSize = 24f })
        y += lineHeight * 2

        // Write entries
        entries.forEach { entry ->
            val duration = entry.getDurationHours()
            canvas.drawText("Date: ${entry.startTime.format(dateFormatter)}", margin, y, paint)
            y += lineHeight
            canvas.drawText("Time: ${entry.startTime.format(timeFormatter)} - ${entry.endTime.format(timeFormatter)}", margin, y, paint)
            y += lineHeight
            canvas.drawText("Duration: %.2f hours".format(duration), margin, y, paint)
            y += lineHeight
            canvas.drawText("Materials Cost: %.2f".format(entry.materialsCost), margin, y, paint)
            y += lineHeight
            canvas.drawText("Comment (EN): ${entry.commentEn ?: ""}", margin, y, paint)
            y += lineHeight
            canvas.drawText("Comment (NL): ${entry.commentNl ?: ""}", margin, y, paint)
            y += lineHeight * 2

            // Add photo if available
            entry.photos.firstOrNull()?.let { photoUri ->
                try {
                    context.contentResolver.openFileDescriptor(photoUri, "r")?.use { pfd ->
                        val bitmap = android.graphics.BitmapFactory.decodeFileDescriptor(pfd.fileDescriptor)
                        val scaledBitmap = android.graphics.Bitmap.createScaledBitmap(bitmap, 300, 300, true)
                        canvas.drawBitmap(scaledBitmap, margin, y, paint)
                        y += 320f
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        // Write summary
        y += lineHeight
        canvas.drawText("Summary", margin, y, paint.apply { textSize = 18f })
        y += lineHeight * 2
        canvas.drawText("Total Hours: %.2f".format(summary.totalHours), margin, y, paint)
        y += lineHeight
        canvas.drawText("Overtime Hours: %.2f".format(summary.overtimeHours), margin, y, paint)
        y += lineHeight
        canvas.drawText("Total Earnings: %.2f".format(summary.totalEarnings), margin, y, paint)

        document.finishPage(page)
        
        val file = File(context.cacheDir, "work_report_${System.currentTimeMillis()}.pdf")
        FileOutputStream(file).use { output ->
            document.writeTo(output)
        }
        document.close()
        
        return file
    }

    fun shareFile(file: File, context: Context, mimeType: String) {
        val uri = androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(android.content.Intent.EXTRA_STREAM, uri)
            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(android.content.Intent.createChooser(intent, "Share Report"))
    }
} 