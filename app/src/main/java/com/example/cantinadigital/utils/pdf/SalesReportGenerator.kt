package com.example.cantinadigital.utils.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.cantinadigital.R
import com.example.cantinadigital.ui.features.insights.model.DailySalesReport
import com.example.cantinadigital.ui.features.insights.model.ProductSalesSummary
import com.example.cantinadigital.ui.features.insights.model.SalesReportData
import java.io.File
import java.io.FileOutputStream

object SalesReportGenerator {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842

    private const val MARGIN = 40f
    private const val HEADER_HEIGHT = 120f
    private const val FOOTER_HEIGHT = 35f

    private const val CONTENT_TOP = MARGIN + HEADER_HEIGHT
    private const val CONTENT_BOTTOM = PAGE_HEIGHT - MARGIN - FOOTER_HEIGHT

    private const val LOGO_SIZE = 70

    fun generate(
        context: Context,
        report: SalesReportData
    ): File {

        val document = PdfDocument()

        var pageNumber = 1

        var page = createPage(
            document = document,
            pageNumber = pageNumber
        )

        var canvas = page.canvas

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // ---------------------------------------------------------
        // CABEÇALHO
        // ---------------------------------------------------------

        drawHeader(
            context = context,
            canvas = canvas,
            paint = paint,
            report = report
        )

        var currentY = CONTENT_TOP

        // ---------------------------------------------------------
        // RESUMO
        // ---------------------------------------------------------

        currentY = drawSectionTitle(
            canvas = canvas,
            paint = paint,
            title = "Resumo",
            y = currentY
        )

        currentY = drawSummary(
            canvas = canvas,
            paint = paint,
            report = report,
            y = currentY
        )

        currentY += 28f

        // ---------------------------------------------------------
        // PRODUTOS VENDIDOS
        // ---------------------------------------------------------

        currentY = drawSectionTitle(
            canvas = canvas,
            paint = paint,
            title = "Produtos vendidos",
            y = currentY
        )

        currentY = drawProductTableHeader(
            canvas = canvas,
            paint = paint,
            y = currentY
        )

        for (product in report.products) {

            if (currentY + 42f > CONTENT_BOTTOM) {

                drawFooter(
                    canvas = canvas,
                    paint = paint,
                    pageNumber = pageNumber
                )

                document.finishPage(page)

                pageNumber++

                page = createPage(
                    document = document,
                    pageNumber = pageNumber
                )

                canvas = page.canvas

                drawHeader(
                    context = context,
                    canvas = canvas,
                    paint = paint,
                    report = report
                )

                currentY = CONTENT_TOP

                currentY = drawContinuationTitle(
                    canvas = canvas,
                    paint = paint,
                    title = "Produtos vendidos — continuação",
                    y = currentY
                )

                currentY = drawProductTableHeader(
                    canvas = canvas,
                    paint = paint,
                    y = currentY
                )
            }

            currentY = drawProductRow(
                canvas = canvas,
                paint = paint,
                product = product,
                y = currentY
            )
        }

        // ---------------------------------------------------------
        // VENDAS POR DIA
        // ---------------------------------------------------------

        if (report.dailySales.isNotEmpty()) {

            if (currentY + 100f > CONTENT_BOTTOM) {

                drawFooter(
                    canvas = canvas,
                    paint = paint,
                    pageNumber = pageNumber
                )

                document.finishPage(page)

                pageNumber++

                page = createPage(
                    document = document,
                    pageNumber = pageNumber
                )

                canvas = page.canvas

                drawHeader(
                    context = context,
                    canvas = canvas,
                    paint = paint,
                    report = report
                )

                currentY = CONTENT_TOP
            }

            currentY += 20f

            currentY = drawSectionTitle(
                canvas = canvas,
                paint = paint,
                title = "Vendas por dia",
                y = currentY
            )

            currentY = drawDailySalesTableHeader(
                canvas = canvas,
                paint = paint,
                y = currentY
            )

            for (dailySale in report.dailySales) {

                if (currentY + 38f > CONTENT_BOTTOM) {

                    drawFooter(
                        canvas = canvas,
                        paint = paint,
                        pageNumber = pageNumber
                    )

                    document.finishPage(page)

                    pageNumber++

                    page = createPage(
                        document = document,
                        pageNumber = pageNumber
                    )

                    canvas = page.canvas

                    drawHeader(
                        context = context,
                        canvas = canvas,
                        paint = paint,
                        report = report
                    )

                    currentY = CONTENT_TOP

                    currentY = drawContinuationTitle(
                        canvas = canvas,
                        paint = paint,
                        title = "Vendas por dia — continuação",
                        y = currentY
                    )

                    currentY = drawDailySalesTableHeader(
                        canvas = canvas,
                        paint = paint,
                        y = currentY
                    )
                }

                currentY = drawDailySalesRow(
                    canvas = canvas,
                    paint = paint,
                    dailySale = dailySale,
                    y = currentY
                )
            }
        }

        // ---------------------------------------------------------
        // RESUMO FINANCEIRO
        // ---------------------------------------------------------

        if (currentY + 180f > CONTENT_BOTTOM) {

            drawFooter(
                canvas = canvas,
                paint = paint,
                pageNumber = pageNumber
            )

            document.finishPage(page)

            pageNumber++

            page = createPage(
                document = document,
                pageNumber = pageNumber
            )

            canvas = page.canvas

            drawHeader(
                context = context,
                canvas = canvas,
                paint = paint,
                report = report
            )

            currentY = CONTENT_TOP
        }

        currentY += 20f

        currentY = drawSectionTitle(
            canvas = canvas,
            paint = paint,
            title = "Resumo financeiro",
            y = currentY
        )

        currentY = drawFinancialSummary(
            canvas = canvas,
            paint = paint,
            report = report,
            y = currentY
        )

        // ---------------------------------------------------------
        // RODAPÉ DA ÚLTIMA PÁGINA
        // ---------------------------------------------------------

        drawFooter(
            canvas = canvas,
            paint = paint,
            pageNumber = pageNumber
        )

        document.finishPage(page)

        // ---------------------------------------------------------
        // SALVA O ARQUIVO
        // ---------------------------------------------------------

        val reportsDirectory = File(
            context.cacheDir,
            "reports"
        )

        if (!reportsDirectory.exists()) {
            reportsDirectory.mkdirs()
        }

        val file = File(
            reportsDirectory,
            "CantinaDigital_Relatorio.pdf"
        )

        FileOutputStream(file).use { outputStream ->
            document.writeTo(outputStream)
        }

        document.close()

        return file
    }

    // =============================================================
    // CRIAÇÃO DE PÁGINA
    // =============================================================

    private fun createPage(
        document: PdfDocument,
        pageNumber: Int
    ): PdfDocument.Page {

        val pageInfo = PdfDocument.PageInfo.Builder(
            PAGE_WIDTH,
            PAGE_HEIGHT,
            pageNumber
        ).create()

        return document.startPage(pageInfo)
    }

    // =============================================================
    // CABEÇALHO
    // =============================================================

    private fun drawHeader(
        context: Context,
        canvas: Canvas,
        paint: Paint,
        report: SalesReportData
    ) {

        val logo: Bitmap = PdfUtils.drawableToBitmap(
            context = context,
            drawableRes = R.drawable.inside_app_logo,
            width = LOGO_SIZE,
            height = LOGO_SIZE
        )

        canvas.drawBitmap(
            logo,
            null,
            RectF(
                MARGIN,
                MARGIN,
                MARGIN + LOGO_SIZE,
                MARGIN + LOGO_SIZE
            ),
            paint
        )

        // Nome da aplicação

        paint.typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )

        paint.textSize = 22f

        canvas.drawText(
            "CANTINA DIGITAL",
            MARGIN + 90f,
            MARGIN + 28f,
            paint
        )

        // Nome do relatório

        paint.typeface = Typeface.DEFAULT
        paint.textSize = 15f

        canvas.drawText(
            "Relatório de Vendas",
            MARGIN + 90f,
            MARGIN + 51f,
            paint
        )

        // Período

        paint.textSize = 11f

        canvas.drawText(
            "Período: ${report.periodLabel}",
            MARGIN + 90f,
            MARGIN + 70f,
            paint
        )

        // Data de geração

        canvas.drawText(
            "Gerado em: ${report.generatedAt}",
            MARGIN,
            MARGIN + 100f,
            paint
        )

        // Linha separadora

        paint.strokeWidth = 1f

        canvas.drawLine(
            MARGIN,
            MARGIN + 112f,
            PAGE_WIDTH - MARGIN,
            MARGIN + 112f,
            paint
        )
    }

    // =============================================================
    // TÍTULOS
    // =============================================================

    private fun drawSectionTitle(
        canvas: Canvas,
        paint: Paint,
        title: String,
        y: Float
    ): Float {

        paint.typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )

        paint.textSize = 16f

        canvas.drawText(
            title,
            MARGIN,
            y + 18f,
            paint
        )

        return y + 40f
    }

    private fun drawContinuationTitle(
        canvas: Canvas,
        paint: Paint,
        title: String,
        y: Float
    ): Float {

        paint.typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )

        paint.textSize = 15f

        canvas.drawText(
            title,
            MARGIN,
            y + 18f,
            paint
        )

        return y + 40f
    }

    // =============================================================
    // RESUMO
    // =============================================================

    private fun drawSummary(
        canvas: Canvas,
        paint: Paint,
        report: SalesReportData,
        y: Float
    ): Float {

        val boxBottom = y + 75f

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f

        canvas.drawRoundRect(
            RectF(
                MARGIN,
                y,
                PAGE_WIDTH - MARGIN,
                boxBottom
            ),
            8f,
            8f,
            paint
        )

        paint.style = Paint.Style.FILL

        val columnWidth =
            (PAGE_WIDTH - (MARGIN * 2)) / 3f

        drawSummaryItem(
            canvas = canvas,
            paint = paint,
            title = "Pedidos",
            value = report.totalOrders.toString(),
            x = MARGIN + 15f,
            y = y + 27f
        )

        drawSummaryItem(
            canvas = canvas,
            paint = paint,
            title = "Produtos vendidos",
            value = report.totalItemsSold.toString(),
            x = MARGIN + columnWidth + 15f,
            y = y + 27f
        )

        drawSummaryItem(
            canvas = canvas,
            paint = paint,
            title = "Faturamento",
            value = PdfUtils.formatCurrency(
                report.totalRevenue
            ),
            x = MARGIN + (columnWidth * 2) + 15f,
            y = y + 27f
        )

        return boxBottom + 20f
    }

    private fun drawFinancialSummary(
        canvas: Canvas,
        paint: Paint,
        report: SalesReportData,
        y: Float
    ): Float {

        val boxBottom = y + 150f

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f

        canvas.drawRoundRect(
            RectF(
                MARGIN,
                y,
                PAGE_WIDTH - MARGIN,
                boxBottom
            ),
            8f,
            8f,
            paint
        )

        paint.style = Paint.Style.FILL

        var currentY = y + 28f

        drawFinancialRow(
            canvas = canvas,
            paint = paint,
            label = "Faturamento bruto",
            value = PdfUtils.formatCurrency(
                report.totalRevenue
            ),
            y = currentY
        )

        currentY += 28f

        drawFinancialRow(
            canvas = canvas,
            paint = paint,
            label = "Taxas da cantina",
            value = PdfUtils.formatCurrency(
                report.cantinaRoyalties
            ),
            y = currentY
        )

        currentY += 28f

//        drawFinancialRow(
//            canvas = canvas,
//            paint = paint,
//            label = "Saídas do caixa",
//            value = PdfUtils.formatCurrency(
//                report.totalExits
//            ),
//            y = currentY
//        )

        currentY += 28f

        // Linha de destaque antes do saldo
        paint.strokeWidth = 1f

        canvas.drawLine(
            MARGIN + 12f,
            currentY - 10f,
            PAGE_WIDTH - MARGIN - 12f,
            currentY - 10f,
            paint
        )

        drawFinancialRow(
            canvas = canvas,
            paint = paint,
            label = "Saldo atual",
            value = PdfUtils.formatCurrency(
                report.totalBalance
            ),
            y = currentY + 10f,
            bold = true
        )

        return boxBottom + 20f
    }

    private fun drawFinancialRow(
        canvas: Canvas,
        paint: Paint,
        label: String,
        value: String,
        y: Float,
        bold: Boolean = false
    ) {

        paint.typeface = Typeface.create(
            Typeface.DEFAULT,
            if (bold) Typeface.BOLD else Typeface.NORMAL
        )

        paint.textSize = if (bold) 12f else 11f

        canvas.drawText(
            label,
            MARGIN + 15f,
            y,
            paint
        )

        val valueWidth = paint.measureText(value)

        canvas.drawText(
            value,
            PAGE_WIDTH - MARGIN - 15f - valueWidth,
            y,
            paint
        )
    }

    private fun drawSummaryItem(
        canvas: Canvas,
        paint: Paint,
        title: String,
        value: String,
        x: Float,
        y: Float
    ) {

        paint.typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )

        paint.textSize = 17f

        canvas.drawText(
            value,
            x,
            y,
            paint
        )

        paint.typeface = Typeface.DEFAULT
        paint.textSize = 10f

        canvas.drawText(
            title,
            x,
            y + 20f,
            paint
        )
    }

    // =============================================================
    // TABELA DE PRODUTOS
    // =============================================================

    private fun drawProductTableHeader(
        canvas: Canvas,
        paint: Paint,
        y: Float
    ): Float {

        paint.typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )

        paint.textSize = 10f

        canvas.drawText(
            "Produto",
            MARGIN,
            y + 15f,
            paint
        )

        canvas.drawText(
            "Quantidade",
            390f,
            y + 15f,
            paint
        )

        canvas.drawText(
            "Receita",
            490f,
            y + 15f,
            paint
        )

        paint.strokeWidth = 1f

        canvas.drawLine(
            MARGIN,
            y + 25f,
            PAGE_WIDTH - MARGIN,
            y + 25f,
            paint
        )

        return y + 40f
    }

    private fun drawProductRow(
        canvas: Canvas,
        paint: Paint,
        product: ProductSalesSummary,
        y: Float
    ): Float {

        paint.typeface = Typeface.DEFAULT
        paint.textSize = 11f

        canvas.drawText(
            product.productName,
            MARGIN,
            y + 15f,
            paint
        )

        canvas.drawText(
            "${product.quantitySold} un.",
            400f,
            y + 15f,
            paint
        )

        canvas.drawText(
            PdfUtils.formatCurrency(
                product.revenue
            ),
            490f,
            y + 15f,
            paint
        )

        paint.strokeWidth = 0.5f

        canvas.drawLine(
            MARGIN,
            y + 28f,
            PAGE_WIDTH - MARGIN,
            y + 28f,
            paint
        )

        return y + 42f
    }

    // =============================================================
    // TABELA DE VENDAS POR DIA
    // =============================================================

    private fun drawDailySalesTableHeader(
        canvas: Canvas,
        paint: Paint,
        y: Float
    ): Float {

        paint.typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )

        paint.textSize = 10f

        canvas.drawText(
            "Data",
            MARGIN,
            y + 15f,
            paint
        )

        canvas.drawText(
            "Pedidos",
            250f,
            y + 15f,
            paint
        )

        canvas.drawText(
            "Produtos",
            350f,
            y + 15f,
            paint
        )

        canvas.drawText(
            "Faturamento",
            450f,
            y + 15f,
            paint
        )

        paint.strokeWidth = 1f

        canvas.drawLine(
            MARGIN,
            y + 25f,
            PAGE_WIDTH - MARGIN,
            y + 25f,
            paint
        )

        return y + 40f
    }

    private fun drawDailySalesRow(
        canvas: Canvas,
        paint: Paint,
        dailySale: DailySalesReport,
        y: Float
    ): Float {

        paint.typeface = Typeface.DEFAULT
        paint.textSize = 11f

        canvas.drawText(
            dailySale.date,
            MARGIN,
            y + 15f,
            paint
        )

        canvas.drawText(
            dailySale.totalOrders.toString(),
            260f,
            y + 15f,
            paint
        )

        canvas.drawText(
            dailySale.totalItems.toString(),
            365f,
            y + 15f,
            paint
        )

        canvas.drawText(
            PdfUtils.formatCurrency(
                dailySale.revenue
            ),
            450f,
            y + 15f,
            paint
        )

        paint.strokeWidth = 0.5f

        canvas.drawLine(
            MARGIN,
            y + 28f,
            PAGE_WIDTH - MARGIN,
            y + 28f,
            paint
        )

        return y + 38f
    }

    // =============================================================
    // RODAPÉ
    // =============================================================

    private fun drawFooter(
        canvas: Canvas,
        paint: Paint,
        pageNumber: Int
    ) {

        paint.typeface = Typeface.DEFAULT
        paint.textSize = 9f

        canvas.drawText(
            "Cantina Digital",
            MARGIN,
            PAGE_HEIGHT - MARGIN,
            paint
        )

        val pageText = "Página $pageNumber"

        val textWidth = paint.measureText(pageText)

        canvas.drawText(
            pageText,
            PAGE_WIDTH - MARGIN - textWidth,
            PAGE_HEIGHT - MARGIN,
            paint
        )
    }
}