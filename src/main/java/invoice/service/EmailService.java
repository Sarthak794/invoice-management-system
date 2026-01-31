package invoice.service;

import invoice.entity.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PdfService pdfService;

    public void sendInvoiceEmail(Invoice invoice) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            helper.setTo(invoice.getCustomer().getEmail());
            helper.setSubject("Invoice " + invoice.getInvoiceNumber());
            helper.setText(
                    "Dear " + invoice.getCustomer().getName()
                            + ",\n\nPlease find your invoice attached.\n\nRegards."
            );

            byte[] pdfBytes = pdfService.generateInvoicePdfBytes(invoice);

            helper.addAttachment(
                    "Invoice_" + invoice.getInvoiceNumber() + ".pdf",
                    new ByteArrayResource(pdfBytes)
            );

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Email sending failed", e);
        }
    }
}
