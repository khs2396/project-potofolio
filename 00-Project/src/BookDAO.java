import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// 도서 관련 DB 처리 클래스
// 도서 등록, 조회, 수정, 삭제 등 DB 작업을 담당
public class BookDAO {
	// DB 연결을 위한 Connection 객체
	Connection conn;
	// SQL 실행을 위한 PreparedStatement 객체
	PreparedStatement pstmt;
	// SQL 결과를 저장하는 ResultSet 객체
	ResultSet rs;

	// 생성자: 오라클 드라이버를 로드함
	public BookDAO() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// DB에 접속하여 Connection 객체를 반환하는 메서드
	public Connection getConnection() {
		String url = "";
		String user = "";
		String password = "";

		try {
			conn = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	// 사용한 DB 자원을 정리(닫기)하는 메서드
	public void close() {
		try {
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	// 도서 정보를 DB에 추가(입고)하는 메서드
	// 파라미터: BookDTO 객체(도서 정보)
	// 반환값: insert 성공 시 1, 실패 시 0
	public int insert(BookDTO dto) {
		String sql = "INSERT INTO Pro2_Book_Table VALUES (?, ?, ?, ?, ?, ?)"; // (BOOK_SEQ, BOOK_NAME, BOOK_AUTHOR, BOOK_PUBLISHER, BOOK_GENRE, BOOK_CHECK)
		int result = 0;
		conn = getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, dto.getBOOK_SEQ());         // 도서 고유번호
			pstmt.setString(2, dto.getBOOK_NAME());     // 도서명
			pstmt.setString(3, dto.getBOOK_AUTHOR());   // 저자
			pstmt.setString(4, dto.getBOOK_PUBLISHER());// 출판사
			pstmt.setString(5, dto.getBOOK_GENRE());    // 장르
			pstmt.setInt(6, dto.getBOOK_CHECK());       // 대출상태(1:대여가능, 2:대여중)
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return result;
	}

	// 전체 도서 목록을 조회하는 메서드
	public List<BookDTO> searchAll() {
		String sql = "SELECT * FROM Pro2_Book_Table ORDER BY BOOK_SEQ";
		List<BookDTO> list = new ArrayList<BookDTO>();
		conn = getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				BookDTO dto = new BookDTO();
				dto.setBOOK_SEQ(rs.getInt("BOOK_SEQ"));
				dto.setBOOK_NAME(rs.getString("BOOK_NAME"));
				dto.setBOOK_AUTHOR(rs.getString("BOOK_AUTHOR"));
				dto.setBOOK_PUBLISHER(rs.getString("BOOK_PUBLISHER"));
				dto.setBOOK_GENRE(rs.getString("BOOK_GENRE"));
				dto.setBOOK_CHECK(rs.getInt("BOOK_CHECK"));
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return list;
	}

	// 전체 도서 수를 반환하는 메서드
	public int countAllBooks() {
		String sql = "SELECT COUNT(*) FROM Pro2_Book_Table";
		try (Connection conn = getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql);
			 ResultSet rs = pstmt.executeQuery()) {
			if (rs.next()) return rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	// 대여 중 도서 수(BOOK_CHECK = 2)
	public int countBorrowedBooks() {
		String sql = "SELECT COUNT(*) FROM Pro2_Book_Table WHERE BOOK_CHECK = 2";
		try (Connection conn = getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql);
			 ResultSet rs = pstmt.executeQuery()) {
			if (rs.next()) return rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	// 대여 가능 도서 수(BOOK_CHECK = 1)
	public int countAvailableBooks() {
		String sql = "SELECT COUNT(*) FROM Pro2_Book_Table WHERE BOOK_CHECK = 1";
		try (Connection conn = getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql);
			 ResultSet rs = pstmt.executeQuery()) {
			if (rs.next()) return rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	// 도서명, 저자, 출판사로 부분 검색하는 메서드
	public List<BookDTO> searchByKeyword(String keyword) {
		String sql = "SELECT * FROM Pro2_Book_Table WHERE BOOK_NAME LIKE ? OR BOOK_AUTHOR LIKE ? OR BOOK_PUBLISHER LIKE ? ORDER BY BOOK_SEQ";
		List<BookDTO> list = new ArrayList<>();
		conn = getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			String kw = "%" + keyword + "%";
			pstmt.setString(1, kw);
			pstmt.setString(2, kw);
			pstmt.setString(3, kw);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				BookDTO dto = new BookDTO();
				dto.setBOOK_SEQ(rs.getInt("BOOK_SEQ"));
				dto.setBOOK_NAME(rs.getString("BOOK_NAME"));
				dto.setBOOK_AUTHOR(rs.getString("BOOK_AUTHOR"));
				dto.setBOOK_PUBLISHER(rs.getString("BOOK_PUBLISHER"));
				dto.setBOOK_GENRE(rs.getString("BOOK_GENRE"));
				dto.setBOOK_CHECK(rs.getInt("BOOK_CHECK"));
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return list;
	}

	// 장르별 도서 검색
	public List<BookDTO> searchByGenre(String genre) {
		String sql = "SELECT * FROM Pro2_Book_Table WHERE BOOK_GENRE = ? ORDER BY BOOK_SEQ";
		List<BookDTO> list = new ArrayList<>();
		conn = getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, genre);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				BookDTO dto = new BookDTO();
				dto.setBOOK_SEQ(rs.getInt("BOOK_SEQ"));
				dto.setBOOK_NAME(rs.getString("BOOK_NAME"));
				dto.setBOOK_AUTHOR(rs.getString("BOOK_AUTHOR"));
				dto.setBOOK_PUBLISHER(rs.getString("BOOK_PUBLISHER"));
				dto.setBOOK_GENRE(rs.getString("BOOK_GENRE"));
				dto.setBOOK_CHECK(rs.getInt("BOOK_CHECK"));
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return list;
	}

	// 전체 장르 목록을 반환 (콤보박스용)
	public List<String> getAllGenres() {
		List<String> genreList = new ArrayList<>();
		String sql = "SELECT DISTINCT BOOK_GENRE FROM PRO2_BOOK_TABLE ORDER BY BOOK_GENRE";
		conn = getConnection();

		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				genreList.add(rs.getString("BOOK_GENRE"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return genreList;
	}

	// 도서 대여 가능 여부 확인 (BOOK_CHECK=1: 대여가능, 2: 대여중)
	public boolean isBookAvailable(int bookSeq) {
		String sql = "SELECT BOOK_CHECK FROM Pro2_Book_Table WHERE BOOK_SEQ = ?";
		boolean available = false;
		conn = getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, bookSeq);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				available = (rs.getInt("BOOK_CHECK") == 1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return available;
	}

	// 도서 삭제
	public int deleteBook(int bookDel) {
		String sql = "DELETE FROM Pro2_Book_Table WHERE BOOK_SEQ = ?";
		int result = 0;
		conn = getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, bookDel);
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return result;
	}

	// 7. select --- 현재 대여 가능한 도서 목록 (BOOK_CHECK=1)
	public List<BookDTO> selectAvailableBooks() {
		String sql = "SELECT * FROM Pro2_Book_Table WHERE BOOK_CHECK = 1";
		List<BookDTO> list = new ArrayList<>();
		conn = getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				BookDTO dto = new BookDTO();
				dto.setBOOK_SEQ(rs.getInt("BOOK_SEQ"));
				dto.setBOOK_NAME(rs.getString("BOOK_NAME"));
				dto.setBOOK_AUTHOR(rs.getString("BOOK_AUTHOR"));
				dto.setBOOK_PUBLISHER(rs.getString("BOOK_PUBLISHER"));
				dto.setBOOK_GENRE(rs.getString("BOOK_GENRE"));
				dto.setBOOK_CHECK(rs.getInt("BOOK_CHECK"));
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return list;
	}

	// 8. select --- 도서코드로 도서 상세 정보 조회
	public BookDTO selectByBookSeq(int bookSeq) {
		String sql = "SELECT * FROM Pro2_Book_Table WHERE BOOK_SEQ = ?";
		BookDTO dto = null;
		conn = getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, bookSeq);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				dto = new BookDTO();
				dto.setBOOK_SEQ(rs.getInt("BOOK_SEQ"));
				dto.setBOOK_NAME(rs.getString("BOOK_NAME"));
				dto.setBOOK_AUTHOR(rs.getString("BOOK_AUTHOR"));
				dto.setBOOK_PUBLISHER(rs.getString("BOOK_PUBLISHER"));
				dto.setBOOK_GENRE(rs.getString("BOOK_GENRE"));
				dto.setBOOK_CHECK(rs.getInt("BOOK_CHECK"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return dto;
	}

	// 수정
	public int updateBook(BookDTO dto) {
		String sql = "UPDATE PRO2_BOOK_TABLE SET BOOK_NAME=?, BOOK_AUTHOR=?, BOOK_PUBLISHER=?, BOOK_GENRE=? ,BOOK_CHECK=? WHERE BOOK_SEQ=?";
		int result = 0;
		conn = getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getBOOK_NAME());
			pstmt.setString(2, dto.getBOOK_AUTHOR());
			pstmt.setString(3, dto.getBOOK_PUBLISHER());
			pstmt.setString(4, dto.getBOOK_GENRE());
			pstmt.setInt(5, dto.getBOOK_CHECK());
			pstmt.setInt(6, dto.getBOOK_SEQ());
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return result;
	}

	// 겹치지않는 가장 작은 seq 찾기
	public String nextSeq() {
		String nextSeq = "";

		conn = getConnection();

		String sql = """
				SELECT MIN(missing_seq) AS next_available_seq
				FROM (
				    SELECT LEVEL AS missing_seq
				    FROM dual
				    CONNECT BY LEVEL <= (SELECT NVL(MAX(BOOK_SEQ), 0) + 1 FROM Pro2_Book_Table)
				    MINUS
				    SELECT BOOK_SEQ FROM Pro2_Book_Table
				)
				""";

		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery()) {
			if (rs.next()) {
				nextSeq = rs.getString("next_available_seq");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return nextSeq;
	}
	
	// 대여 가능여부 변경
	public int updateBookCheck(int bookCheck, int bookSeq) {
	    String sql = "UPDATE Pro2_Book_Table SET BOOK_CHECK = ? WHERE BOOK_SEQ = ?";
	    int result = 0;
	    conn = getConnection();
	    try {
	        pstmt = conn.prepareStatement(sql);
	        pstmt.setInt(1, bookCheck); // 2: 대여중, 1: 대여가능
	        pstmt.setInt(2, bookSeq);
	        result = pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        close();
	    }
	    return result;
	}

}
