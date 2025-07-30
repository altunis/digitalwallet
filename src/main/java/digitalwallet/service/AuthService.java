package digitalwallet.service;

import digitalwallet.model.Customer;
import digitalwallet.model.Transaction;
import digitalwallet.model.Wallet;
import digitalwallet.repository.CustomerRepository;
import digitalwallet.repository.TransactionRepository;
import digitalwallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private WalletRepository walletRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;

    // This method should be adapted to your user details implementation
    public boolean canAccessCustomer(Long customerId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        // Allow employees
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("EMPLOYEE")) 
        		|| auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }
        // For customers, check if their customerId matches
        // You must adapt this to your user details implementation
        Object principal = auth.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            // Example: get customerId from username or a custom field
            // Replace this with your actual logic
            String username = userDetails.getUsername();
            Long userCustomerId = getCustomerIdByUsername(username); // Implement this lookup
            return userCustomerId != null && userCustomerId.equals(customerId);
        }
        return false;
    }

    // Dummy implementation, replace with your actual lookup
    private Long getCustomerIdByUsername(String username) {
        Customer customer = customerRepository.getCustomerByName(username);
        return customer != null ? customer.getId() : null;
    }
    
    public boolean canAccessWallet(Long walletId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        // Allow employees
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("EMPLOYEE")) 
        		|| auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }
        Wallet wallet = walletRepository.findById(walletId).orElse(null);
        if (wallet == null) return false;
        Object principal = auth.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            String username = userDetails.getUsername();
            Long userCustomerId = getCustomerIdByUsername(username);
            return userCustomerId != null && userCustomerId.equals(wallet.getCustomerId());
        }
        return false;
    }
    
    public boolean canAccessTransaction(Long transactionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        // Allow employees and admins
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("EMPLOYEE"))
            || auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }
        Transaction transaction = transactionRepository.findById(transactionId).orElse(null);
        if (transaction == null) return false;
        Wallet wallet = walletRepository.findById(transaction.getWalletId()).orElse(null);
        if (wallet == null) return false;
        Object principal = auth.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            String username = userDetails.getUsername();
            Long userCustomerId = getCustomerIdByUsername(username);
            return userCustomerId != null && userCustomerId.equals(wallet.getCustomerId());
        }
        return false;
    }
}