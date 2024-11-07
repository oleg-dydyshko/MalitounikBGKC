package by.carkva_gazeta.malitounik

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.FileOutputStream

class PdfDocumentAdapter(private val context: Context, private val fileName: String, private val uri: Uri) : PrintDocumentAdapter() {
    override fun onLayout(oldAttributes: PrintAttributes?, newAttributes: PrintAttributes, cancellationSignal: CancellationSignal?, callback: LayoutResultCallback, bundle: Bundle) {
        if (cancellationSignal?.isCanceled == true) {
            callback.onLayoutCancelled()
            return
        } else {
            val builder = PrintDocumentInfo.Builder(fileName)
            builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN).build()
            callback.onLayoutFinished(builder.build(), newAttributes == oldAttributes)
        }
    }

    override fun onWrite(pageRanges: Array<out PageRange>, destination: ParcelFileDescriptor, cancellationSignal: CancellationSignal?, callback: WriteResultCallback) {
        try {
            var bis: BufferedInputStream? = null
            var bos: BufferedOutputStream? = null
            try {
                val outputStream = FileOutputStream(destination.fileDescriptor)
                bis = BufferedInputStream(context.contentResolver.openInputStream(uri))
                val originalSize = bis.available()
                bos = BufferedOutputStream(outputStream)
                val buf = ByteArray(originalSize)
                bis.read(buf)
                do {
                    bos.write(buf)
                } while (bis.read(buf) != -1)
            } catch (_: Throwable) {
            } finally {
                bis?.close()
                bos?.flush()
                bos?.close()
            }
            if (cancellationSignal?.isCanceled == true) {
                callback.onWriteCancelled()
            } else {
                callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
            }
        } catch (_: Exception) {
        }
    }
}