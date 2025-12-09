
public class MemberDTO {
	private int MEMBER_SEQ; // 회원 고유번호
	private String MEMBER_ID; // 회원 ID
	private String MEMBER_PW; // 비밀번호
	private String MEMBER_NAME; // 이름
	private String MEMBER_TEL; // 전화번호
	private int MEMBER_GENDER; // 성별 (1:남, 2:여)
	private String MEMBER_BIRTH; // 생년월일 (예: 900101)

	public MemberDTO() {
	} // 기본 생성자

	public MemberDTO(int mEMBER_SEQ, String mEMBER_ID, String mEMBER_PW, String mEMBER_NAME, String mEMBER_TEL,
			int mEMBER_GENDER, String mEMBER_BIRTH) {
		MEMBER_SEQ = mEMBER_SEQ;
		MEMBER_ID = mEMBER_ID;
		MEMBER_PW = mEMBER_PW;
		MEMBER_NAME = mEMBER_NAME;
		MEMBER_TEL = mEMBER_TEL;
		MEMBER_GENDER = mEMBER_GENDER;
		MEMBER_BIRTH = mEMBER_BIRTH;
	}

	@Override
	public String toString() {

		String str = String.format("%d\t%s\t%s\t%s\t%s\t%d\t%s\n", MEMBER_SEQ, MEMBER_ID, MEMBER_PW, MEMBER_NAME,MEMBER_TEL ,MEMBER_GENDER ,MEMBER_BIRTH);
		return str;
	}

	public int getMEMBER_SEQ() {
		return MEMBER_SEQ;
	}

	public void setMEMBER_SEQ(int mEMBER_SEQ) {
		MEMBER_SEQ = mEMBER_SEQ;
	}

	public String getMEMBER_ID() {
		return MEMBER_ID;
	}

	public void setMEMBER_ID(String mEMBER_ID) {
		MEMBER_ID = mEMBER_ID;
	}

	public String getMEMBER_PW() {
		return MEMBER_PW;
	}

	public void setMEMBER_PW(String mEMBER_PW) {
		MEMBER_PW = mEMBER_PW;
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

	public int getMEMBER_GENDER() {
		return MEMBER_GENDER;
	}

	public void setMEMBER_GENDER(int mEMBER_GENDER) {
		MEMBER_GENDER = mEMBER_GENDER;
	}

	public String getMEMBER_BIRTH() {
		return MEMBER_BIRTH;
	}

	public void setMEMBER_BIRTH(String mEMBER_BIRTH) {
		MEMBER_BIRTH = mEMBER_BIRTH;
	}

}
