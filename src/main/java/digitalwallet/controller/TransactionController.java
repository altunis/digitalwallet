package digitalwallet.controller;

import digitalwallet.model.Transaction;
import digitalwallet.service.TransactionService;
import digitalwallet.dto.WithdrawRequestDto;
import digitalwallet.dto.DepositRequestDto;
import digitalwallet.dto.ApproveRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @PreAuthorize("@authService.canAccessWallet(#walletId)")
    @GetMapping("/list")
    public ResponseEntity<List<Transaction>> listTransactions(@RequestParam Long walletId) {
        List<Transaction> transactions = transactionService.listTransactions(walletId);
        return ResponseEntity.ok(transactions);
    }

    @PreAuthorize("@authService.canAccessWallet(#request.walletId)")
    @PostMapping("/withdraw")
    public ResponseEntity<Transaction> withdraw(@RequestBody WithdrawRequestDto request) {
        Transaction tx = transactionService.withdraw(request.getWalletId(), request.getAmount(), request.getOppositePartyType(), request.getOppositeParty());
        return ResponseEntity.ok(tx);
    }

    @PreAuthorize("@authService.canAccessWallet(#request.walletId)")
    @PostMapping("/deposit")
    public ResponseEntity<Transaction> deposit(@RequestBody DepositRequestDto request) {
        Transaction tx = transactionService.deposit(request.getWalletId(), request.getAmount(), request.getOppositePartyType(), request.getOppositeParty());
        return ResponseEntity.ok(tx);
    }

    @PreAuthorize("@authService.canAccessTransaction(#request.transactionId)")
    @PostMapping("/approve")
    public ResponseEntity<Transaction> approveOrDenyTransaction(@RequestBody ApproveRequestDto request) {
        Transaction tx = transactionService.approveOrDenyTransaction(request.getTransactionId(), request.getStatus());
        return ResponseEntity.ok(tx);
    }
}