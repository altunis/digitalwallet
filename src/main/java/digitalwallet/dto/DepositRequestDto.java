package digitalwallet.dto;

public class DepositRequestDto {
    private Long walletId;
    private Long amount;
    private String oppositePartyType;
    private String oppositeParty;

    public Long getWalletId() { return walletId; }
    public void setWalletId(Long walletId) { this.walletId = walletId; }
    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }
    public String getOppositePartyType() { return oppositePartyType; }
    public void setOppositePartyType(String oppositePartyType) { this.oppositePartyType = oppositePartyType; }
    public String getOppositeParty() { return oppositeParty; }
    public void setOppositeParty(String oppositeParty) { this.oppositeParty = oppositeParty; }
}