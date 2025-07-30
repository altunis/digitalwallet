package digitalwallet.model;

import digitalwallet.dto.TransactionDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Transaction {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id; 
	Long walletId; 
	String oppositePartyType; // IBAN or PAYMENT
	String oppositeParty;
	int status; // PENDING, APPROVED, DENIED
	Long amount;
	LocalDateTime timestamp;
	String type; // DEPOSIT or WITHDRAW
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getWalletId() {
		return walletId;
	}
	public void setWalletId(Long walletId) {
		this.walletId = walletId;
	}
	public String getOppositePartyType() {
		return oppositePartyType;
	}
	public void setOppositePartyType(String oppositePartyType) {
		this.oppositePartyType = oppositePartyType;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	
	public Long getAmount() {
		return amount;
	}
	public void setAmount(Long amount) {
		this.amount = amount;
	}
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getOppositeParty() {
		return oppositeParty;
	}
	public void setOppositeParty(String oppositeParty) {
		this.oppositeParty = oppositeParty;
	}
	
	public TransactionDto toDto() {
		return new TransactionDto(getId(), getWalletId(), getType(), getOppositePartyType(), getOppositeParty(), getStatus());
	}
	
}