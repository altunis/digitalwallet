package digitalwallet.dto;

public class ApproveRequestDto {
    private Long transactionId;
    private int status;

    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
}
