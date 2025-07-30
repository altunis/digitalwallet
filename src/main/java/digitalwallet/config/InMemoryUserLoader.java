package digitalwallet.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import digitalwallet.model.Customer;
import digitalwallet.repository.CustomerRepository;
import jakarta.annotation.PostConstruct;

@Configuration
@DependsOn("inMemoryUserDetailsManager")
public class InMemoryUserLoader {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private InMemoryUserDetailsManager inMemoryUserDetailsManager;


    @PostConstruct
    public void loadCustomers() {
        List<Customer> customers = customerRepository.findAll();
        for (Customer customer : customers) {
            UserDetails user = User.builder()
                .username(customer.getName())
                .password(customer.getPassword())
                .roles("USER")
                .build();
            inMemoryUserDetailsManager.createUser(user);
        }
    }
}