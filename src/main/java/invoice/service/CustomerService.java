package invoice.service;

import invoice.entity.Customer;
import invoice.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> getActiveCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public void saveCustomer(Customer customer) {

        if (customer.getId() != null) {
            // UPDATE CASE
            Customer existing = getCustomerById(customer.getId());

            existing.setName(customer.getName());
            existing.setPhone(customer.getPhone());

            // Email: update ONLY if changed
            if (customer.getEmail() != null &&
                !customer.getEmail().equals(existing.getEmail())) {
                existing.setEmail(customer.getEmail());
            }

            customerRepository.save(existing);

        } else {
            // CREATE CASE
            customerRepository.save(customer);
        }
    }

    public void deleteById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customer.setActive(false);   // SOFT DELETE
    }


}
