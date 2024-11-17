package by.carkva_gazeta.malitounik

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Size
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

internal class PdfRendererCore(fileDescriptor: ParcelFileDescriptor) {

    private var isRendererOpen = false
    private val openPages = ConcurrentHashMap<Int, PdfRenderer.Page>()
    private var pdfRenderer: PdfRenderer? = null

    init {
        pdfRenderer = PdfRenderer(fileDescriptor).also { isRendererOpen = true }
    }

    fun getPageCount(): Int {
        synchronized(this) {
            if (!isRendererOpen) return 0
            return pdfRenderer?.pageCount ?: 0
        }
    }

    fun renderPage(pageNo: Int, bitmap: Bitmap, onBitmapReady: ((success: Boolean, pageNo: Int, bitmap: Bitmap?) -> Unit)? = null) {
        if (pageNo >= getPageCount()) {
            onBitmapReady?.invoke(false, pageNo, null)
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            synchronized(this@PdfRendererCore) {
                if (!isRendererOpen) return@launch
                openPageSafely(pageNo)?.use { pdfPage ->
                    try {
                        bitmap.eraseColor(Color.WHITE)
                        pdfPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        CoroutineScope(Dispatchers.Main).launch { onBitmapReady?.invoke(true, pageNo, bitmap) }
                    } catch (e: Exception) {
                        CoroutineScope(Dispatchers.Main).launch { onBitmapReady?.invoke(false, pageNo, null) }
                    }
                }
            }
        }
    }

    private suspend fun <T> withPdfPage(pageNo: Int, block: (PdfRenderer.Page) -> T): T? = withContext(Dispatchers.IO) {
        synchronized(this@PdfRendererCore) {
            pdfRenderer?.openPage(pageNo)?.use { page ->
                return@withContext block(page)
            }
        }
        null
    }

    private val pageDimensionCache = mutableMapOf<Int, Size>()

    fun getPageDimensionsAsync(pageNo: Int, callback: (Size) -> Unit) {
        pageDimensionCache[pageNo]?.let {
            callback(it)
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val size = withPdfPage(pageNo) { page ->
                Size(page.width, page.height).also { pageSize ->
                    pageDimensionCache[pageNo] = pageSize
                }
            } ?: Size(1, 1)

            withContext(Dispatchers.Main) {
                callback(size)
            }
        }
    }

    private fun openPageSafely(pageNo: Int): PdfRenderer.Page? {
        synchronized(this) {
            if (!isRendererOpen) return null
            closeAllOpenPages()
            return pdfRenderer?.openPage(pageNo)?.also { page ->
                openPages[pageNo] = page
            }
        }
    }

    private fun closeAllOpenPages() {
        synchronized(this) {
            openPages.values.forEach { page ->
                try {
                    page.close()
                } catch (_: IllegalStateException) {
                }
            }
            openPages.clear()
        }
    }
}
