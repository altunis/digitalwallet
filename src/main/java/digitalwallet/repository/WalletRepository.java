package digitalwallet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import digitalwallet.model.Wallet;

public interface WalletRepository  extends JpaRepository<Wallet, Long> {
   List<Wallet> findByCustomerId(Long customerId);
   List<Wallet> findByCustomerIdAndCurrency(Long customerId, String currency);
   List<Wallet> findByCustomerIdAndBalanceGreaterThanEqual(Long customerId, Long amount);
   List<Wallet> findByCustomerIdAndCurrencyAndBalanceGreaterThanEqual(Long customerId, String currency, Long minAmount);


}
