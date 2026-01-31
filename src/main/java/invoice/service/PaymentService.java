package invoice.service;

import invoice.entity.Invoice;
import invoice.entity.Payment;
import invoice.repository.InvoiceRepository;
import invoice.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Transactional
    public void addPayment(Long invoiceId, double amount) {

        // 1️⃣ Fetch invoice
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        // 2️⃣ Calculate already paid amount
        double totalPaid = invoice.getPayments()
                .stream()
                .mapToDouble(Payment::getPaidAmount)
                .sum();

        // 3️⃣ Remaining balance
        double balance = invoice.getGrandTotal() - totalPaid;

        // 4️⃣ Prevent over-payment
        if (amount <= 0) {
            throw new RuntimeException("Payment amount must be greater than zero");
        }

        if (amount > balance) {
            throw new RuntimeException(
                    "Payment exceeds remaining balance of ₹" + balance
            );
        }

        // 5️⃣ Save payment
        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setPaidAmount(amount);
        payment.setPaymentDate(LocalDate.now());

        paymentRepository.save(payment);

        // 6️⃣ Update invoice status
        double newPaid = totalPaid + amount;

        if (newPaid == invoice.getGrandTotal()) {
            invoice.setStatus("PAID");
        } else {
            invoice.setStatus("PARTIAL");
        }

        invoiceRepository.save(invoice);
    }
    
    @Transactional
    public void clearFullPayment(Long invoiceId) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        double balance = invoice.getBalanceAmount();

        if (balance <= 0) {
            throw new RuntimeException("Invoice already fully paid");
        }

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setPaidAmount(balance);
        payment.setPaymentDate(LocalDate.now());

        paymentRepository.save(payment);

        invoice.setStatus("PAID");
        invoiceRepository.save(invoice);
    }

}
