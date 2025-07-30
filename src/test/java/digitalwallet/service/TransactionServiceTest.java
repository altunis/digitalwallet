package digitalwallet.service;

import digitalwallet.Constants;
import digitalwallet.model.Transaction;
import digitalwallet.model.Wallet;
import digitalwallet.repository.TransactionRepository;
import digitalwallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private WalletRepository walletRepository;
    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deposit_approved_updatesBalances() {
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(100L);
        wallet.setUsableBalance(100L);
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArgument(0));

        Transaction tx = transactionService.deposit(1L, 500L, Constants.OPPOSITE_PARTY_TYPE.IBAN,"1234");
        assertEquals(Constants.TRANSACTION_TYPE.DEPOSIT, tx.getType());
        assertEquals(Constants.TRANSACTION_STATUS.APPROVED, tx.getStatus());
        assertEquals(600L, wallet.getBalance());
        assertEquals(600L, wallet.getUsableBalance());
    }

    @Test
    void deposit_pending_onlyBalanceUpdated() {
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(100L);
        wallet.setUsableBalance(100L);
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArgument(0));

        Transaction tx = transactionService.deposit(1L, 2000L, Constants.OPPOSITE_PARTY_TYPE.PAYMENT,"1234");
        assertEquals(Constants.TRANSACTION_TYPE.DEPOSIT, tx.getType());
        assertEquals(Constants.TRANSACTION_STATUS.PENDING, tx.getStatus());
        assertEquals(2100L, wallet.getBalance());
        assertEquals(100L, wallet.getUsableBalance());
    }

    @Test
    void withdraw_approved_updatesBalances() {
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(1000L);
        wallet.setUsableBalance(1000L);
        wallet.setActiveForWithdraw(true);
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArgument(0));

        Transaction tx = transactionService.withdraw(1L, 500L, Constants.OPPOSITE_PARTY_TYPE.IBAN,"1234");
        assertEquals(Constants.TRANSACTION_TYPE.WITHDRAW, tx.getType());
        assertEquals(Constants.TRANSACTION_STATUS.APPROVED, tx.getStatus());
        assertEquals(500L, wallet.getBalance());
        assertEquals(500L, wallet.getUsableBalance());
    }

    @Test
    void withdraw_pending_onlyUsableBalanceUpdated() {
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(1000L);
        wallet.setUsableBalance(2000L); // Fix: set usable balance >= amount
        wallet.setActiveForWithdraw(true);
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArgument(0));

        Transaction tx = transactionService.withdraw(1L, 2000L, Constants.OPPOSITE_PARTY_TYPE.PAYMENT,"1234");
        assertEquals(Constants.TRANSACTION_TYPE.WITHDRAW, tx.getType());
        assertEquals(Constants.TRANSACTION_STATUS.PENDING, tx.getStatus());
        assertEquals(1000L, wallet.getBalance());
        assertEquals(0L, wallet.getUsableBalance());
    }

    @Test
    void withdraw_notAllowed_throwsException() {
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(1000L);
        wallet.setUsableBalance(1000L);
        wallet.setActiveForWithdraw(false);
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        assertThrows(IllegalStateException.class, () ->
            transactionService.withdraw(1L, 100L, Constants.OPPOSITE_PARTY_TYPE.IBAN,"1234")
        );
    }

    @Test
    void approveOrDenyTransaction_approved_deposit() {
        Transaction tx = new Transaction();
        tx.setId(1L);
        tx.setWalletId(1L);
        tx.setType(Constants.TRANSACTION_TYPE.DEPOSIT);
        tx.setStatus(Constants.TRANSACTION_STATUS.PENDING);
        tx.setAmount(500L);
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(1000L);
        wallet.setUsableBalance(1000L);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(tx));
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArgument(0));

        Transaction result = transactionService.approveOrDenyTransaction(1L, Constants.TRANSACTION_STATUS.APPROVED);
        assertEquals(Constants.TRANSACTION_STATUS.APPROVED, result.getStatus());
        assertEquals(1500L, wallet.getUsableBalance());
    }

    @Test
    void listTransactions_returnsTransactions() {
        Transaction tx = new Transaction();
        tx.setId(1L);
        tx.setWalletId(1L);
        when(transactionRepository.findByWalletId(1L)).thenReturn(Collections.singletonList(tx));
        List<Transaction> result = transactionService.listTransactions(1L);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }
}