import java.sql.Date;

public class RentalDTO {
    private int RENTAL_SEQ;        // 대여 고유번호
    private String MEMBER_ID;      // 회원 ID
    private String MEMBER_NAME;    // 회원 이름
    private String MEMBER_TEL;     // 회원 전화번호
    private int BOOK_SEQ;          // 책 고유번호
    private String BOOK_NAME;      // 책 이름
    private java.sql.Date RENTAL_RENTAL; // 대여일
    private java.sql.Date RENTAL_RETURN; // 반납 예정일
    private int RENTAL_CHECK;      // 반납 확인 (1: 대여중, 2: 반납완료)
    private int RENTAL_OVERDUE;    // 연체 일수
	
    public RentalDTO() {} //기본 생성자

	public RentalDTO(int rENTAL_SEQ, String mEMBER_ID, String mEMBER_NAME, String mEMBER_TEL, int bOOK_SEQ,
			String bOOK_NAME, Date rENTAL_RENTAL, Date rENTAL_RETURN, int rENTAL_CHECK, int rENTAL_OVERDUE) {
		RENTAL_SEQ = rENTAL_SEQ;
		MEMBER_ID = mEMBER_ID;
		MEMBER_NAME = mEMBER_NAME;
		MEMBER_TEL = mEMBER_TEL;
		BOOK_SEQ = bOOK_SEQ;
		BOOK_NAME = bOOK_NAME;
		RENTAL_RENTAL = rENTAL_RENTAL;
		RENTAL_RETURN = rENTAL_RETURN;
		RENTAL_CHECK = rENTAL_CHECK;
		RENTAL_OVERDUE = rENTAL_OVERDUE;
	}

	@Override
	public String toString() {
		String str = String.format("%d\t %s\t %s\t %s\t %d\t %s\t %s\t %s\t %d\t %d\n",
				RENTAL_SEQ, MEMBER_ID, MEMBER_NAME, MEMBER_TEL, BOOK_SEQ, BOOK_NAME, RENTAL_RENTAL,RENTAL_RETURN, RENTAL_CHECK,RENTAL_OVERDUE);
return str;

	}

	public int getRENTAL_SEQ() {
		return RENTAL_SEQ;
	}

	public void setRENTAL_SEQ(int rENTAL_SEQ) {
		RENTAL_SEQ = rENTAL_SEQ;
	}

	public String getMEMBER_ID() {
		return MEMBER_ID;
	}

	public void setMEMBER_ID(String mEMBER_ID) {
		MEMBER_ID = mEMBER_ID;
	}

	public String getMEMBER_NAME() {
		return MEMBER_NAME;
	}

	public void setMEMBER_NAME(String mEMBER_NAME) {
		MEMBER_NAME = mEMBER_NAME;
	}

	public String getMEMBER_TEL() {
		return MEMBER_TEL;
	}

	public void setMEMBER_TEL(String mEMBER_TEL) {
		MEMBER_TEL = mEMBER_TEL;
	}

	public int getBOOK_SEQ() {
		return BOOK_SEQ;
	}

	public void setBOOK_SEQ(int bOOK_SEQ) {
		BOOK_SEQ = bOOK_SEQ;
	}

	public String getBOOK_NAME() {
		return BOOK_NAME;
	}

	public void setBOOK_NAME(String bOOK_NAME) {
		BOOK_NAME = bOOK_NAME;
	}

	public java.sql.Date getRENTAL_RENTAL() {
		return RENTAL_RENTAL;
	}

	public void setRENTAL_RENTAL(java.sql.Date rENTAL_RENTAL) {
		RENTAL_RENTAL = rENTAL_RENTAL;
	}

	public java.sql.Date getRENTAL_RETURN() {
		return RENTAL_RETURN;
	}

	public void setRENTAL_RETURN(java.sql.Date rENTAL_RETURN) {
		RENTAL_RETURN = rENTAL_RETURN;
	}

	public int getRENTAL_CHECK() {
		return RENTAL_CHECK;
	}

	public void setRENTAL_CHECK(int rENTAL_CHECK) {
		RENTAL_CHECK = rENTAL_CHECK;
	}

	public int getRENTAL_OVERDUE() {
		return RENTAL_OVERDUE;
	}

	public void setRENTAL_OVERDUE(int rENTAL_OVERDUE) {
		RENTAL_OVERDUE = rENTAL_OVERDUE;
	}
	


}