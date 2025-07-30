package digitalwallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import digitalwallet.model.Transaction;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWalletId(Long walletId);
}