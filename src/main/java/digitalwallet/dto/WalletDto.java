package digitalwallet.dto;

public record WalletDto(Long customerId, String walletName, String currency, boolean isActiveForShop, boolean isActiveForWithdraw, Long balance, Long usableBalance )
{

}
