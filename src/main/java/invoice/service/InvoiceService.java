package invoice.service;

import invoice.entity.Customer;
import invoice.entity.Invoice;
import invoice.entity.InvoiceItem;
import invoice.entity.Payment;
import invoice.entity.Product;
import invoice.repository.InvoiceRepository;
import invoice.repository.PaymentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceService {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductService productService;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    // ==================================================
    // GENERATE MULTI-PRODUCT INVOICE (NO TAX, % DISCOUNT)
    // ==================================================
    @Transactional
    public Invoice generateInvoice(
            Long customerId,
            List<Long> productIds,
            List<Integer> quantities,
            double discountPercent
    ) {

        if (productIds == null || quantities == null || productIds.isEmpty()) {
            throw new RuntimeException("Invoice must contain at least one product");
        }

        // 1️⃣ Fetch customer
        Customer customer = customerService.getCustomerById(customerId);

        // 2️⃣ Create invoice
        Invoice invoice = new Invoice();
        invoice.setCustomer(customer);
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setStatus("DUE");
        invoice.setItems(new ArrayList<>());

        double subTotal = 0;

        // 3️⃣ Process products
        for (int i = 0; i < productIds.size(); i++) {

            Long productId = productIds.get(i);
            Integer qty = quantities.get(i);

            if (productId == null || qty == null || qty <= 0) continue;

            Product product = productService.getById(productId);

            // Stock validation
            if (product.getStock() < qty) {
                throw new RuntimeException(
                        "Insufficient stock for product: " + product.getName()
                );
            }

            // Reduce stock
            productService.reduceStock(product, qty);

            // Create invoice item
            InvoiceItem item = new InvoiceItem();
            item.setInvoice(invoice);
            item.setProduct(product);
            item.setQuantity(qty);
            item.setPrice(product.getPrice());

            double total = product.getPrice() * qty;
            item.setTotal(total);

            subTotal += total;
            invoice.getItems().add(item);
        }

        if (invoice.getItems().isEmpty()) {
            throw new RuntimeException("Invoice must contain valid products");
        }

        // 4️⃣ Calculate totals (NO TAX)
        invoice.setSubtotal(subTotal);

        double discountAmount = subTotal * discountPercent / 100;
        invoice.setDiscount(discountAmount);

        invoice.setGrandTotal(subTotal - discountAmount);

        // 5️⃣ Save invoice (items saved via cascade)
        return invoiceRepository.save(invoice);
    }

    // ==================================================
    // GENERATE INVOICE NUMBER
    // ==================================================
    public String generateInvoiceNumber() {
        long count = invoiceRepository.count() + 1;
        return "INV-" + String.format("%05d", count);
    }

    // ==================================================
    // FETCH SINGLE INVOICE
    // ==================================================
    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    // ==================================================
    // FETCH ALL INVOICES
    // ==================================================
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    // ==================================================
    // ADD PAYMENT
    // ==================================================
    @Transactional
    public void addPayment(Long invoiceId, double amount, String paymentMode) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (amount <= 0) {
            throw new RuntimeException("Payment amount must be greater than zero");
        }

        double totalPaid = paymentRepository
                .findByInvoiceId(invoiceId)
                .stream()
                .mapToDouble(Payment::getPaidAmount)
                .sum();

        double balance = invoice.getGrandTotal() - totalPaid;

        if (amount > balance) {
            throw new RuntimeException("Payment exceeds remaining balance");
        }

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setPaidAmount(amount);
        payment.setPaymentMode(paymentMode);
        payment.setPaymentDate(LocalDate.now());

        paymentRepository.save(payment);

        invoice.setStatus(
                (totalPaid + amount) == invoice.getGrandTotal()
                        ? "PAID"
                        : "PARTIAL"
        );

        invoiceRepository.save(invoice);
    }

    // ==================================================
    // CLEAR FULL PAYMENT
    // ==================================================
    @Transactional
    public void clearFullPayment(Long invoiceId, String paymentMode) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        double totalPaid = paymentRepository
                .findByInvoiceId(invoiceId)
                .stream()
                .mapToDouble(Payment::getPaidAmount)
                .sum();

        double balance = invoice.getGrandTotal() - totalPaid;

        if (balance <= 0) {
            throw new RuntimeException("Invoice already fully paid");
        }

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setPaidAmount(balance);
        payment.setPaymentMode(paymentMode);
        payment.setPaymentDate(LocalDate.now());

        paymentRepository.save(payment);

        invoice.setStatus("PAID");
        invoiceRepository.save(invoice);
    }
}
