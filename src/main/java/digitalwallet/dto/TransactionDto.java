package digitalwallet.dto;

public record TransactionDto(Long id, Long walletId, String type, String oppositePartyType, String oppositeParty, int status) {

}