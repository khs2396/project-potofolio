public class ManagerDTO {
	private int MANAGER_SEQ; // 매니저 순번 (PRIMARY KEY,
	private String MANAGER_NAME; // 매니저이름
	private String MANAGER_TEL; // 매니저 전화번호
	private int MANAGER_CODE; // 매니저 코드 번호

	public ManagerDTO() {
	} // 기본 생성자

	public ManagerDTO(int mANAGER_SEQ, String mANAGER_NAME, String mANAGER_TEL, int mANAGER_CODE) {
		MANAGER_SEQ = mANAGER_SEQ;
		MANAGER_NAME = mANAGER_NAME;
		MANAGER_TEL = mANAGER_TEL;
		MANAGER_CODE = mANAGER_CODE;
	}

	@Override
	public String toString() {

		String str = String.format("%d\t%s\t %s\t %d\n", MANAGER_SEQ, MANAGER_NAME, MANAGER_TEL, MANAGER_CODE);
		return str;

	}

	public int getMANAGER_SEQ() {
		return MANAGER_SEQ;
	}

	public void setMANAGER_SEQ(int mANAGER_SEQ) {
		MANAGER_SEQ = mANAGER_SEQ;
	}

	public String getMANAGER_NAME() {
		return MANAGER_NAME;
	}

	public void setMANAGER_NAME(String mANAGER_NAME) {
		MANAGER_NAME = mANAGER_NAME;
	}

	public String getMANAGER_TEL() {
		return MANAGER_TEL;
	}

	public void setMANAGER_TEL(String mANAGER_TEL) {
		MANAGER_TEL = mANAGER_TEL;
	}

	public int getMANAGER_CODE() {
		return MANAGER_CODE;
	}

	public void setMANAGER_CODE(int mANAGER_CODE) {
		MANAGER_CODE = mANAGER_CODE;
	}

}
