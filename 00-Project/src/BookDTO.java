
public class BookDTO {
    private int BOOK_SEQ;          // 책 고유번호
    private String BOOK_NAME;      // 책 이름
    private String BOOK_AUTHOR;    // 저자
    private String BOOK_PUBLISHER; // 출판사
    private String BOOK_GENRE;     // 장르
    private int BOOK_CHECK;        // 책 상태 (1: 대여가능, 2: 대여중 등)
	
    public BookDTO() {}		//기본 생성자

	public BookDTO(int bOOK_SEQ, String bOOK_NAME, String bOOK_AUTHOR, String bOOK_PUBLISHER, String bOOK_GENRE,
			int bOOK_CHECK) {
		BOOK_SEQ = bOOK_SEQ;
		BOOK_NAME = bOOK_NAME;
		BOOK_AUTHOR = bOOK_AUTHOR;
		BOOK_PUBLISHER = bOOK_PUBLISHER;
		BOOK_GENRE = bOOK_GENRE;
		BOOK_CHECK = bOOK_CHECK;
	}

	@Override
	public String toString() {
		String str = String.format("%d\t%s\t%s\t%s\t%s\t%d\n", 
				BOOK_SEQ, BOOK_NAME, BOOK_AUTHOR, BOOK_PUBLISHER, BOOK_GENRE, BOOK_CHECK);
return str;
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

	public String getBOOK_AUTHOR() {
		return BOOK_AUTHOR;
	}

	public void setBOOK_AUTHOR(String bOOK_AUTHOR) {
		BOOK_AUTHOR = bOOK_AUTHOR;
	}

	public String getBOOK_PUBLISHER() {
		return BOOK_PUBLISHER;
	}

	public void setBOOK_PUBLISHER(String bOOK_PUBLISHER) {
		BOOK_PUBLISHER = bOOK_PUBLISHER;
	}

	public String getBOOK_GENRE() {
		return BOOK_GENRE;
	}

	public void setBOOK_GENRE(String bOOK_GENRE) {
		BOOK_GENRE = bOOK_GENRE;
	}

	public int getBOOK_CHECK() {
		return BOOK_CHECK;
	}

	public void setBOOK_CHECK(int bOOK_CHECK) {
		BOOK_CHECK = bOOK_CHECK;
	}


    
}
