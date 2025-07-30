package digitalwallet;

public interface Constants {
	
	public static interface TRANSACTION_TYPE{
		public static String DEPOSIT  = "DEPOSIT";
		public static String WITHDRAW = "WITHDRAW";
	}
	
	public static interface OPPOSITE_PARTY_TYPE{
		public static String IBAN  = "IBAN";
		public static String PAYMENT = "PAYMENT";
	}
	public static interface TRANSACTION_STATUS{
		public static int PENDING = 0;
		public static int APPROVED = 1;
		public static int DENIED = -1;
	}

}