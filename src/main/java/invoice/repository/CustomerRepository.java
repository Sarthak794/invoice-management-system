package invoice.repository;

import invoice.entity.Customer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	List<Customer> findByActiveTrue();

//    boolean existsByEmail(String email);
//
//    boolean existsByEmailAndIdNot(String email, Long id);
}


