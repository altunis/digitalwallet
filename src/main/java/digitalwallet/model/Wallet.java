package digitalwallet.model;

import digitalwallet.dto.WalletDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Wallet {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	Long customerId;
	String walletName;
	String currency;
	boolean isActiveForShop;
	boolean isActiveForWithdraw;
	Long balance;
	Long usableBalance;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}
	public String getWalletName() {
		return walletName;
	}
	public void setWalletName(String walletName) {
		this.walletName = walletName;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public boolean isActiveForShop() {
		return isActiveForShop;
	}
	public void setActiveForShop(boolean isActiveForShop) {
		this.isActiveForShop = isActiveForShop;
	}
	public boolean isActiveForWithdraw() {
		return isActiveForWithdraw;
	}
	public void setActiveForWithdraw(boolean isActiveForWithdraw) {
		this.isActiveForWithdraw = isActiveForWithdraw;
	}
	public Long getBalance() {
		return balance;
	}
	public void setBalance(Long balance) {
		this.balance = balance;
	}
	public Long getUsableBalance() {
		return usableBalance;
	}
	public void setUsableBalance(Long usableBalance) {
		this.usableBalance = usableBalance;
	} 
	
	public WalletDto toDto() {
		return new WalletDto(getCustomerId(), getWalletName(), getCurrency(), isActiveForShop(),isActiveForWithdraw(), getBalance(), getUsableBalance());
	}
}
