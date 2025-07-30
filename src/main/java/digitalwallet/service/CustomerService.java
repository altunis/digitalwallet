package digitalwallet.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import digitalwallet.repository.CustomerRepository;
import digitalwallet.model.Customer;
import java.util.List;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class CustomerService {
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
    private InMemoryUserDetailsManager inMemoryUserDetailsManager;
	
	@Autowired
    private PasswordEncoder passwordEncoder;
	

	//write getAllCustomer method 
	public List<Customer> getAllCustomers() {
		return customerRepository.findAll();
	}
	
	public Customer createCustomer(Customer customer) {
        customer.setId(null);
        Customer saved = customerRepository.save(customer);
        // Add to in-memory authentication
        UserDetails user = User.builder()
            .username(saved.getName())
            .password(passwordEncoder.encode(saved.getPassword()))
            .roles("USER")
            .build();
        inMemoryUserDetailsManager.createUser(user);
        return saved;
    }

}