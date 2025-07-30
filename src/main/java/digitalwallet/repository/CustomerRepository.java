package digitalwallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import digitalwallet.model.Customer;


public interface CustomerRepository extends JpaRepository<Customer, Long> {
	
	public Customer getCustomerByName(String name);

}