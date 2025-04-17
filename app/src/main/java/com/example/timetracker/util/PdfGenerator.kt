package com.example.timetracker.util

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import com.example.timetracker.model.WorkEntry
import java.time.format.DateTimeFormatter

object PdfGenerator {

    fun generatePdf(context: Context, entries: List<WorkEntry>): Uri? {
        val pageWidth = 595 // A4
        val pageHeight = 842

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = document.startPage(pageInfo)

        val canvas = page.canvas
        val paint = Paint()
        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 12f
        }

        var y = 40f

        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 18f
        canvas.drawText("Work Report", (pageWidth / 2).toFloat(), y, paint)
        y += 30f

        // Header row
        canvas.drawText("Date", 40f, y, textPaint)
        canvas.drawText("Start", 100f, y, textPaint)
        canvas.drawText("End", 150f, y, textPaint)
        canvas.drawText("Hours", 200f, y, textPaint)
        canvas.drawText("EN", 260f, y, textPaint)
        canvas.drawText("NL", 420f, y, textPaint)
        y += 20f

        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        entries.forEach {
            if (y > pageHeight - 40) {
                // TODO: Handle multiple pages if needed
                return@forEach
            }

            val duration = it.getDuration().toString()
            canvas.drawText(it.date.format(dateFormatter), 40f, y, textPaint)
            canvas.drawText(it.startTime.toString(), 100f, y, textPaint)
            canvas.drawText(it.endTime.toString(), 150f, y, textPaint)
            canvas.drawText(duration, 200f, y, textPaint)
            canvas.drawText(it.commentEn.take(30), 260f, y, textPaint)
            canvas.drawText(it.commentNl.take(30), 420f, y, textPaint)
            y += 18f
        }

        document.finishPage(page)

        return try {
            val file = File(context.getExternalFilesDir(null), "work_report_${System.currentTimeMillis()}.pdf")
            document.writeTo(FileOutputStream(file))
            document.close()
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
} 