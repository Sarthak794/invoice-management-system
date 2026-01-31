package invoice.controller;

import invoice.entity.Invoice;
import invoice.service.EmailService;
import invoice.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EmailController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/invoice/email/{id}")
    public String sendInvoiceEmail(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {

        try {
            Invoice invoice = invoiceService.getInvoiceById(id);
            emailService.sendInvoiceEmail(invoice);
            redirectAttributes.addFlashAttribute(
                    "success", "Invoice emailed successfully"
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error", "Failed to send invoice email"
            );
        }

        return "redirect:/invoice/view/" + id;
    }
}
