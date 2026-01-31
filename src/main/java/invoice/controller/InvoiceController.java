package invoice.controller;

import invoice.entity.Invoice;
import invoice.service.CustomerService;
import invoice.service.InvoiceService;
import invoice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductService productService;

    // ===============================
    // SHOW NEW INVOICE FORM
    // ===============================
    @GetMapping("/new")
    public String newInvoice(Model model) {

        // 🔹 Auto-generate invoice details
        Invoice invoice = new Invoice();
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setInvoiceNumber(invoiceService.generateInvoiceNumber());

        model.addAttribute("invoice", invoice);
        model.addAttribute("customers", customerService.getActiveCustomers());
        model.addAttribute("products", productService.getAllActiveProducts());

        // 🔥 THIS LINE WAS MISSING
        model.addAttribute("content", "invoice/invoice-form");

        // 🔥 MUST RETURN BASE LAYOUT
        return "layout/base";
    }

    // ===============================
    // SAVE MULTI-PRODUCT INVOICE
    // ===============================
    @PostMapping("/save")
    public String saveInvoice(
            @RequestParam Long customerId,
            @RequestParam List<Long> productIds,
            @RequestParam List<Integer> quantities,
            @RequestParam(defaultValue = "0") double discountPercent,
            RedirectAttributes redirectAttributes
    ) {

        try {
            invoiceService.generateInvoice(
                    customerId,
                    productIds,
                    quantities,
                    discountPercent
            );

            redirectAttributes.addFlashAttribute(
                    "success", "Invoice generated successfully");

            return "redirect:/invoice/list";

        } catch (RuntimeException ex) {
            ex.printStackTrace();   // 🔥 keep this
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/invoice/new";
        }
    }

    // ===============================
    // LIST ALL INVOICES
    // ===============================
    @GetMapping("/list")
    public String invoiceList(Model model) {
        model.addAttribute("invoices", invoiceService.getAllInvoices());
        model.addAttribute("content", "invoice/invoice-list");
        return "layout/base";
    }

    // ===============================
    // VIEW INVOICE (PRINT READY)
    // ===============================
    @GetMapping("/view/{id}")
    public String viewInvoice(@PathVariable Long id, Model model) {
        model.addAttribute("invoice", invoiceService.getInvoiceById(id));
        model.addAttribute("content", "invoice/invoice-view");
        return "layout/base";
    }

    // ===============================
    // ADD PAYMENT
    // ===============================
    @PostMapping("/payment/add")
    public String addPayment(
            @RequestParam Long invoiceId,
            @RequestParam double amount,
            @RequestParam String paymentMode,
            RedirectAttributes redirectAttributes
    ) {
        try {
            invoiceService.addPayment(invoiceId, amount, paymentMode);
            redirectAttributes.addFlashAttribute(
                    "success", "Payment added successfully");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/invoice/view/" + invoiceId;
    }

    // ===============================
    // CLEAR FULL PAYMENT
    // ===============================
    @PostMapping("/payment/clear")
    public String clearFullPayment(
            @RequestParam Long invoiceId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            invoiceService.clearFullPayment(invoiceId, "CASH");
            redirectAttributes.addFlashAttribute(
                    "success", "Invoice fully paid");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/invoice/view/" + invoiceId;
    }
}
