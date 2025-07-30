package digitalwallet.service;

import digitalwallet.exception.InvalidCurrencyException;
import digitalwallet.model.Wallet;
import digitalwallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WalletServiceTest {
    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createWallet_validCurrency_walletCreated() {
        Wallet walletToSave = new Wallet();
        walletToSave.setWalletName("TestWallet");
        walletToSave.setCurrency("USD");
        walletToSave.setActiveForShop(true);
        walletToSave.setActiveForWithdraw(false);
        walletToSave.setBalance(0L);
        walletToSave.setUsableBalance(0L);

        Wallet savedWallet = new Wallet();
        savedWallet.setId(1L);
        savedWallet.setWalletName("TestWallet");
        savedWallet.setCurrency("USD");
        savedWallet.setActiveForShop(true);
        savedWallet.setActiveForWithdraw(false);
        savedWallet.setBalance(0L);
        savedWallet.setUsableBalance(0L);

        when(walletRepository.save(any(Wallet.class))).thenReturn(savedWallet);

        Wallet result = walletService.createWallet(new Long(9L),"TestWallet", "USD", true, false);
        assertNotNull(result);
        assertEquals("TestWallet", result.getWalletName());
        assertEquals("USD", result.getCurrency());
        assertTrue(result.isActiveForShop());
        assertFalse(result.isActiveForWithdraw());
        assertEquals(0L, result.getBalance());
        assertEquals(0L, result.getUsableBalance());
    }

    @Test
    void createWallet_invalidCurrency_throwsException() {
        assertThrows(InvalidCurrencyException.class, () ->
                walletService.createWallet(new Long(10L),"TestWallet", "GBP", true, false)
        );
    }
}
