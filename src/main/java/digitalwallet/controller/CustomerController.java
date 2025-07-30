package digitalwallet.controller;

import digitalwallet.dto.CustomerDto;
import digitalwallet.model.Customer;
import digitalwallet.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<Customer> createCustomer(@RequestBody CustomerDto customer) {
        Customer saved = customerService.createCustomer(Customer.fromDto(customer));
        return ResponseEntity.ok(saved);
    }
}