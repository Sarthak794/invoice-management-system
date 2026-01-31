package invoice.service;

import invoice.entity.Invoice;
import invoice.repository.InvoiceRepository;
import invoice.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public long totalInvoices() {
        return invoiceRepository.count();
    }

    public double totalRevenue() {
        return invoiceRepository.findAll()
                .stream()
                .mapToDouble(Invoice::getGrandTotal)
                .sum();
    }

    public double totalPaid() {
        return paymentRepository.findAll()
                .stream()
                .mapToDouble(p -> p.getPaidAmount())
                .sum();
    }

    public double totalOutstanding() {
        return totalRevenue() - totalPaid();
    }

    public List<Invoice> recentInvoices() {
    	return invoiceRepository.findAll()
    	        .stream()
    	        .limit(5)
    	        .collect(Collectors.toList());
    }
}
