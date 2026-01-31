package invoice.controller;

import invoice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/payment/add")
    public String addPayment(
            @RequestParam Long invoiceId,
            @RequestParam double amount,
            RedirectAttributes redirectAttributes
    ) {
        try {
            paymentService.addPayment(invoiceId, amount);
            redirectAttributes.addFlashAttribute("success", "Payment added successfully");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/invoice/view/" + invoiceId;
    }
    
    @PostMapping("/payment/clear")
    public String clearFullPayment(
            @RequestParam Long invoiceId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            paymentService.clearFullPayment(invoiceId);
            redirectAttributes.addFlashAttribute(
                    "success", "Invoice fully paid and closed");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/invoice/view/" + invoiceId;
    }

}
