package digitalwallet.service;

import digitalwallet.Constants;
import digitalwallet.exception.InvalidOppositePartyTypeException;
import digitalwallet.model.Transaction;
import digitalwallet.model.Wallet;
import digitalwallet.repository.TransactionRepository;
import digitalwallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private WalletRepository walletRepository;

    @Transactional
    public Transaction deposit(Long walletId, Long amount, String oppositePartyType, String oppositeParty) {
        if (!Constants.OPPOSITE_PARTY_TYPE.IBAN.equals(oppositePartyType) && !Constants.OPPOSITE_PARTY_TYPE.PAYMENT.equals(oppositePartyType)) {
            throw new InvalidOppositePartyTypeException("Opposite Party Type must be IBAN or PAYMENT");
        }
        Optional<Wallet> walletOpt = walletRepository.findById(walletId);
        if (walletOpt.isEmpty()) throw new IllegalArgumentException("Wallet not found");
        Wallet wallet = walletOpt.get();
        Transaction tx = new Transaction();
        tx.setWalletId(walletId);
        tx.setType(Constants.TRANSACTION_TYPE.DEPOSIT);
        tx.setOppositePartyType(oppositePartyType);
        tx.setAmount(amount);
        tx.setOppositeParty(oppositeParty);
        tx.setTimestamp(LocalDateTime.now());
        if (amount > 1000) {
            tx.setStatus(Constants.TRANSACTION_STATUS.PENDING);
        } else {
            tx.setStatus(Constants.TRANSACTION_STATUS.APPROVED);
        }
        transactionRepository.save(tx);
        // Update wallet balances
        wallet.setBalance(wallet.getBalance() + amount);
        if (tx.getStatus() == Constants.TRANSACTION_STATUS.APPROVED) {
            wallet.setUsableBalance(wallet.getUsableBalance() + amount);
        }
        walletRepository.save(wallet);
        return tx;
    }

    @Transactional
    public Transaction withdraw(Long walletId, Long amount, String oppositePartyType, String oppositeParty) {
        if (!Constants.OPPOSITE_PARTY_TYPE.IBAN.equals(oppositePartyType) && !Constants.OPPOSITE_PARTY_TYPE.PAYMENT.equals(oppositePartyType)) {
            throw new InvalidOppositePartyTypeException("Opposite Party Type must be IBAN or PAYMENT");
        }
        Optional<Wallet> walletOpt = walletRepository.findById(walletId);
        if (walletOpt.isEmpty()) throw new IllegalArgumentException("Wallet not found");
        Wallet wallet = walletOpt.get();
        if (!wallet.isActiveForWithdraw()) throw new IllegalStateException("Withdraw not allowed for this wallet");
        if (wallet.getUsableBalance() < amount) throw new IllegalStateException("Insufficient usable balance");
        Transaction tx = new Transaction();
        tx.setWalletId(walletId);
        tx.setType(Constants.TRANSACTION_TYPE.WITHDRAW);
        tx.setOppositePartyType(oppositePartyType);
        tx.setOppositeParty(oppositeParty);
        tx.setAmount(amount);
        tx.setTimestamp(LocalDateTime.now());
        if (amount > 1000) {
            tx.setStatus(Constants.TRANSACTION_STATUS.PENDING);
        } else {
            tx.setStatus(Constants.TRANSACTION_STATUS.APPROVED);
        }
        transactionRepository.save(tx);
        // Update wallet balances
        if (tx.getStatus() == Constants.TRANSACTION_STATUS.APPROVED) {
            wallet.setBalance(wallet.getBalance() - amount);
            wallet.setUsableBalance(wallet.getUsableBalance() - amount);
        } else {
            wallet.setUsableBalance(wallet.getUsableBalance() - amount);
        }
        walletRepository.save(wallet);
        return tx;
    }

    public List<Transaction> listTransactions(Long walletId) {
        return transactionRepository.findByWalletId(walletId);
    }

    @Transactional
    public Transaction approveOrDenyTransaction(Long transactionId, int status) {
        Optional<Transaction> txOpt = transactionRepository.findById(transactionId);
        if (txOpt.isEmpty()) throw new IllegalArgumentException("Transaction not found");
        Transaction tx = txOpt.get();
        if (tx.getStatus() != Constants.TRANSACTION_STATUS.PENDING) throw new IllegalStateException("Transaction already processed");
        tx.setStatus(status);
        transactionRepository.save(tx);
        Optional<Wallet> walletOpt = walletRepository.findById(tx.getWalletId());
        if (walletOpt.isEmpty()) throw new IllegalArgumentException("Wallet not found");
        Wallet wallet = walletOpt.get();
        if (tx.getType().equals(Constants.TRANSACTION_TYPE.DEPOSIT)) {
            if (status == Constants.TRANSACTION_STATUS.APPROVED) {
                wallet.setUsableBalance(wallet.getUsableBalance() + tx.getAmount());
            } else if (status == Constants.TRANSACTION_STATUS.DENIED) {
                // Only revert balance, usableBalance was not changed for pending deposit
                wallet.setBalance(wallet.getBalance() - tx.getAmount());
            }
        } else if (tx.getType().equals(Constants.TRANSACTION_TYPE.WITHDRAW)) {
            if (status == Constants.TRANSACTION_STATUS.APPROVED) {
                wallet.setBalance(wallet.getBalance() - tx.getAmount());
            } else if (status == Constants.TRANSACTION_STATUS.DENIED) {
                // Revert usable balance for denied withdraw
                wallet.setUsableBalance(wallet.getUsableBalance() + tx.getAmount());
            }
        }
        walletRepository.save(wallet);
        return tx;
    }
}