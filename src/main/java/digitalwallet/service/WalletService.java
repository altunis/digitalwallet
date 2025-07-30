package digitalwallet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import digitalwallet.Constants;
import digitalwallet.exception.CustomerNotFoundException;
import digitalwallet.exception.InvalidCurrencyException;
import digitalwallet.model.Currency;
import digitalwallet.model.Transaction;
import digitalwallet.model.Wallet;
import digitalwallet.repository.CustomerRepository;
import digitalwallet.repository.TransactionRepository;
import digitalwallet.repository.WalletRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private CustomerRepository customerRepository;	

    /**
     * Create a new wallet with given details below
     * WalletName: Name of wallet to be created, Currency: Currency of wallet, ActiveForShopping: Wallet can be used for shopping, ActiveForWithdraw: Wallet can be used for withdraw
     * Acceptable currencies are TRY, USD, EUR, the method should throw an exception if the currency is not one of these
     */
    public Wallet createWallet(Long customerId, String walletName, String currency, boolean activeForShopping, boolean activeForWithdraw) {
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException("Customer not found: " + customerId);
        }
        Currency currencyEnum;
        try {
            currencyEnum = Currency.fromString(currency);
        } catch (IllegalArgumentException e) {
            throw new InvalidCurrencyException("Invalid currency: " + currency + ". Acceptable currencies are TRY, USD, EUR.");
        }
        Wallet wallet = new Wallet();
        wallet.setCustomerId(customerId);
        wallet.setWalletName(walletName);
        wallet.setCurrency(currencyEnum.name());
        wallet.setActiveForShop(activeForShopping);
        wallet.setActiveForWithdraw(activeForWithdraw);
        wallet.setBalance(0L);
        wallet.setUsableBalance(0L);
        Wallet savedWallet = walletRepository.save(wallet);
        return savedWallet;
    }

    public List<Wallet> listWallets(Long customerId, String currency, Long minAmount) {
        if (currency != null && minAmount != null) {
        	walletRepository.findByCustomerIdAndCurrencyAndBalanceGreaterThanEqual(customerId, currency, minAmount);
            return walletRepository.findByCustomerIdAndCurrencyAndBalanceGreaterThanEqual(customerId, currency, minAmount);
        } else if (currency != null) {
            return walletRepository.findByCustomerIdAndCurrency(customerId, currency);
        } else if (minAmount != null) {
            return walletRepository.findByCustomerIdAndBalanceGreaterThanEqual(customerId, minAmount);
        } else {
            return walletRepository.findByCustomerId(customerId);
        }
    }

    public Transaction deposit(Long walletId, Long amount, String oppositePartyType) {
        if (!Constants.OPPOSITE_PARTY_TYPE.IBAN.equals(oppositePartyType) && !Constants.OPPOSITE_PARTY_TYPE.PAYMENT.equals(oppositePartyType)) {
            throw new InvalidCurrencyException("Opposite Party Type must be IBAN or PAYMENT");
        }
        Optional<Wallet> walletOpt = walletRepository.findById(walletId);
        if (walletOpt.isEmpty()) throw new IllegalArgumentException("Wallet not found");
        Wallet wallet = walletOpt.get();
        Transaction tx = new Transaction();
        tx.setWalletId(walletId);
        tx.setType(Constants.TRANSACTION_TYPE.DEPOSIT);
        tx.setOppositePartyType(oppositePartyType);
        tx.setAmount(amount);
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

    public Transaction withdraw(Long walletId, Long amount, String oppositePartyType) {
        if (!Constants.OPPOSITE_PARTY_TYPE.IBAN.equals(oppositePartyType) && !Constants.OPPOSITE_PARTY_TYPE.PAYMENT.equals(oppositePartyType)) {
            throw new InvalidCurrencyException("Opposite Party Type must be IBAN or PAYMENT");
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
        if (tx.getType() == Constants.TRANSACTION_TYPE.DEPOSIT && status == Constants.TRANSACTION_STATUS.APPROVED) {
            wallet.setUsableBalance(wallet.getUsableBalance() + tx.getAmount());
        } else if (tx.getType() == Constants.TRANSACTION_TYPE.WITHDRAW && status == Constants.TRANSACTION_STATUS.APPROVED) {
            wallet.setBalance(wallet.getBalance() - tx.getAmount());
        } else if (tx.getType() == Constants.TRANSACTION_TYPE.WITHDRAW && status != Constants.TRANSACTION_STATUS.APPROVED) {
            wallet.setUsableBalance(wallet.getUsableBalance() + tx.getAmount()); // revert usable balance
        }
        walletRepository.save(wallet);
        return tx;
    }

}