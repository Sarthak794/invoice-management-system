package invoice.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import invoice.entity.Invoice;
import invoice.entity.InvoiceItem;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.awt.Color;
import java.io.ByteArrayOutputStream;

@Service
public class PdfService {

    // ===============================
    // PDF FOR BROWSER DOWNLOAD
    // ===============================
    public void generateInvoicePdf(
            Invoice invoice,
            HttpServletResponse response
    ) {

        try {
            response.setContentType("application/pdf");
            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=Invoice_" + invoice.getInvoiceNumber() + ".pdf"
            );

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            buildInvoice(document, invoice);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ===============================
    // PDF FOR EMAIL ATTACHMENT
    // ===============================
    public byte[] generateInvoicePdfBytes(Invoice invoice) {

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            buildInvoice(document, invoice);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ===============================
    // COMMON PDF CONTENT (SINGLE SOURCE)
    // ===============================
    private void buildInvoice(Document document, Invoice invoice)
            throws DocumentException {

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("INVOICE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("Invoice No: " + invoice.getInvoiceNumber()));
        document.add(new Paragraph("Date: " + invoice.getInvoiceDate()));
        document.add(new Paragraph("Customer: " + invoice.getCustomer().getName()));
        document.add(new Paragraph("Email: " + invoice.getCustomer().getEmail()));
        document.add(new Paragraph("Status: " + invoice.getStatus()));

        document.add(new Paragraph(" "));

        // ---- ITEMS TABLE ----
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);

        addHeader(table, "Product", "Qty", "Price", "Total");

        for (InvoiceItem item : invoice.getItems()) {
            table.addCell(item.getProduct().getName());
            table.addCell(String.valueOf(item.getQuantity()));
            table.addCell(String.valueOf(item.getPrice()));
            table.addCell(String.valueOf(item.getTotal()));
        }

        document.add(table);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("Subtotal: " + invoice.getSubtotal()));
        document.add(new Paragraph("Tax: " + invoice.getTax()));
        document.add(new Paragraph("Grand Total: " + invoice.getGrandTotal()));
    }

    private void addHeader(PdfPTable table, String... headers) {
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h));
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            table.addCell(cell);
        }
    }
}
