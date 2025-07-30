package digitalwallet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import digitalwallet.dto.WalletDto;
import digitalwallet.model.Wallet;
import digitalwallet.service.AuthService;
import digitalwallet.service.WalletService;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {
    @Autowired
    private WalletService walletService;
    


    @PreAuthorize("@authService.canAccessCustomer(#walletDto.customerId)")
    @PostMapping("/create")
    public ResponseEntity<Wallet> createWallet(@RequestBody WalletDto walletDto) {
        Wallet wallet = walletService.createWallet(
            walletDto.customerId(),
            walletDto.walletName(),
            walletDto.currency(),
            walletDto.isActiveForShop(),
            walletDto.isActiveForWithdraw()
        );
        return ResponseEntity.ok(wallet);
    }

    @PreAuthorize("@authService.canAccessCustomer(#customerId)")
    @GetMapping("/list")
    public ResponseEntity<?> listWallets(@RequestParam Long customerId,
                                         @RequestParam(required = false) String currency,
                                         @RequestParam(required = false) Long minAmount) {
        return ResponseEntity.ok(walletService.listWallets(customerId, currency, minAmount));
    }
}