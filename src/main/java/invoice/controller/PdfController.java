package invoice.controller;

import invoice.entity.Invoice;
import invoice.service.InvoiceService;
import invoice.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class PdfController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private PdfService pdfService;

    @GetMapping("/invoice/pdf/{id}")
    public void downloadInvoicePdf(
            @PathVariable Long id,
            HttpServletResponse response
    ) throws IOException {

        Invoice invoice = invoiceService.getInvoiceById(id);
        pdfService.generateInvoicePdf(invoice, response);
    }
}
