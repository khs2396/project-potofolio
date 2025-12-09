import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

// 대여/반납 관련 DB 처리 클래스
// 대여 등록, 반납 처리, 연체 처리, 이력 조회 등 DB 작업 담당
public class rentalDAO {
	Connection conn; // DB 연결 객체
	PreparedStatement pstmt; // SQL 실행 객체
	ResultSet rs; // SQL 결과 저장 객체

	// 생성자: 오라클 드라이버를 로드함
	public rentalDAO() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver"); // 오라클 드라이버 로드
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
			conn = DriverManager.getConnection(url, user, password); // DB 연결
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	// 사용한 DB 자원을 정리(닫기)하는 메서드
	public void close() {
		try {
			if (rs != null)
				rs.close(); // ResultSet 닫기
			if (pstmt != null)
				pstmt.close(); // PreparedStatement 닫기
			if (conn != null)
				conn.close(); // Connection 닫기
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 연체일수 계산 메서드
	// 파라미터: 반납예정일(Date)
	// 반환값: 연체일수(오늘이 반납예정일 이후면 차이 일수, 아니면 0)
	public int calculateOverdueDays(Date rentalReturnDate) {
	    LocalDate today = LocalDate.now(); // 오늘 날짜
	    LocalDate returnDate = rentalReturnDate.toLocalDate(); // 반납예정일

	    if (today.isAfter(returnDate)) { // 오늘이 반납예정일 이후면
	        long daysOverdue = ChronoUnit.DAYS.between(returnDate, today); // 연체일수 계산
	        return (int) daysOverdue;
	    } else {
	        return 0; // 연체 아님
	    }
	}
	
	// 대여 정보 DB에 추가(대여 등록)
	// 파라미터: RentalDTO(대여 정보)
	// 반환값: insert 성공 시 1, 실패 시 0
	public int insert(RentalDTO dto) {
		String sql = "INSERT INTO Pro2_rental_Table (RENTAL_SEQ, MEMBER_ID,MEMBER_NAME, MEMBER_TEL,BOOK_SEQ,BOOK_NAME,RENTAL_RENTAL,RENTAL_RETURN,RENTAL_CHECK, RENTAL_OVERDUE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		int result = 0;
		conn = getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, dto.getRENTAL_SEQ()); // 대여 고유번호
			pstmt.setString(2, dto.getMEMBER_ID()); // 회원ID
			pstmt.setString(3, dto.getMEMBER_NAME()); // 회원이름
			pstmt.setString(4, dto.getMEMBER_TEL()); // 회원전화번호
			pstmt.setInt(5, dto.getBOOK_SEQ()); // 도서코드
			pstmt.setString(6, dto.getBOOK_NAME()); // 도서명
			pstmt.setDate(7, dto.getRENTAL_RENTAL()); // 대여일
			pstmt.setDate(8, dto.getRENTAL_RETURN()); // 반납예정일
			pstmt.setInt(9, dto.getRENTAL_CHECK()); // 대여상태(1:대여중, 2:반납)
			pstmt.setInt(10, dto.getRENTAL_OVERDUE()); // 연체일수

			result = pstmt.executeUpdate(); // DB에 insert 실행
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return result;
	}
	
	// 대여 내역 삭제(반납 처리)
	// 파라미터: 도서코드(BOOK_SEQ)
	// 반환값: delete 성공 시 1, 실패 시 0
	public int deleterental(int seq) {
		String sql = "DELETE FROM Pro2_rental_Table WHERE BOOK_SEQ = ?";
		int result = 0;
		conn = getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, seq); // 도서코드
			result = pstmt.executeUpdate(); // DB에서 삭제 실행
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return result;
	}
	
	// 전체 대여 내역 조회
	// 반환값: RentalDTO 리스트(전체 대여 정보)
	public List<RentalDTO> searchAll() {
		String sql = "SELECT * FROM Pro2_rental_Table";
		List<RentalDTO> list = new ArrayList<RentalDTO>();
		conn = getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				RentalDTO dto = new RentalDTO();
				dto.setBOOK_SEQ(rs.getInt("RENTAL_SEQ")); // 대여 고유번호
				dto.setMEMBER_ID(rs.getString("MEMBER_ID")); // 회원ID
				dto.setMEMBER_NAME(rs.getString("MEMBER_NAME")); // 회원이름
				dto.setMEMBER_TEL(rs.getString("MEMBER_TEL")); // 회원전화번호
				dto.setBOOK_SEQ(rs.getInt("BOOK_SEQ")); // 도서코드
				dto.setBOOK_NAME(rs.getString("BOOK_NAME")); // 도서명
				dto.setRENTAL_RENTAL(rs.getDate("RENTAL_RENTAL")); // 대여일
				dto.setRENTAL_RETURN(rs.getDate("RENTAL_RETURN")); // 반납예정일
				dto.setRENTAL_CHECK(rs.getInt("RENTAL_CHECK")); // 대여상태
				// 연체일수 계산
	            int overdue = 0;
	            if (dto.getRENTAL_CHECK() == 1 && dto.getRENTAL_RETURN() != null) {
	                overdue = calculateOverdueDays(dto.getRENTAL_RETURN());
	            }
	            dto.setRENTAL_OVERDUE(overdue);

	            list.add(dto); // 리스트에 추가
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return list;
	}

	// 특정 회원의 대여중인 도서 목록 조회 (RENTAL_CHECK=1: 대여중)
	// 파라미터: 회원ID
	// 반환값: RentalDTO 리스트(대여중 도서)
	public List<RentalDTO> rentalStayBook(String memberId) {
		String sql = "SELECT * FROM Pro2_rental_Table WHERE MEMBER_ID = ? AND RENTAL_CHECK = 1";
		List<RentalDTO> list = new ArrayList<>();
		conn = getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, memberId); // 회원ID
			rs = pstmt.executeQuery();
			while (rs.next()) {
				RentalDTO dto = new RentalDTO();
				dto.setRENTAL_SEQ(rs.getInt("RENTAL_SEQ"));
				dto.setMEMBER_ID(rs.getString("MEMBER_ID"));
				dto.setMEMBER_NAME(rs.getString("MEMBER_NAME"));
				dto.setMEMBER_TEL(rs.getString("MEMBER_TEL"));
				dto.setBOOK_SEQ(rs.getInt("BOOK_SEQ"));
				dto.setBOOK_NAME(rs.getString("BOOK_NAME"));
				dto.setRENTAL_RENTAL(rs.getDate("RENTAL_RENTAL"));
				dto.setRENTAL_RETURN(rs.getDate("RENTAL_RETURN"));
				dto.setRENTAL_CHECK(rs.getInt("RENTAL_CHECK"));
				dto.setRENTAL_OVERDUE(rs.getInt("RENTAL_OVERDUE"));
				list.add(dto); // 리스트에 추가
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return list;
	}

	// 특정 회원의 대여중인 도서 목록 조회 (RENTAL_CHECK=1: 대여중)
	public List<RentalDTO> rentalStayBook(String memberId, int bookseq) {
		String sql = "SELECT * FROM Pro2_rental_Table WHERE MEMBER_ID = ? AND BOOK_SEQ = ? AND RENTAL_CHECK = 1";
		List<RentalDTO> list = new ArrayList<>();
		conn = getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, memberId);
			pstmt.setInt(2, bookseq);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				RentalDTO dto = new RentalDTO();
				dto.setRENTAL_SEQ(rs.getInt("RENTAL_SEQ"));
				dto.setMEMBER_ID(rs.getString("MEMBER_ID"));
				dto.setMEMBER_NAME(rs.getString("MEMBER_NAME"));
				dto.setMEMBER_TEL(rs.getString("MEMBER_TEL"));
				dto.setBOOK_SEQ(rs.getInt("BOOK_SEQ"));
				dto.setBOOK_NAME(rs.getString("BOOK_NAME"));
				dto.setRENTAL_RENTAL(rs.getDate("RENTAL_RENTAL"));
				dto.setRENTAL_RETURN(rs.getDate("RENTAL_RETURN"));
				dto.setRENTAL_CHECK(rs.getInt("RENTAL_CHECK"));
				dto.setRENTAL_OVERDUE(rs.getInt("RENTAL_OVERDUE"));
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return list;
	}
	
	// 연체 도서 목록 조회 (RENTAL_OVERDUE > 0 AND RENTAL_CHECK = 1)
	// 반환값: RentalDTO 리스트(연체 도서)
	public List<RentalDTO> rentalOver() {
		String sql = "SELECT * FROM Pro2_rental_Table WHERE RENTAL_OVERDUE > 0 AND RENTAL_CHECK = 1";
		List<RentalDTO> list = new ArrayList<>();
		conn = getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				RentalDTO dto = new RentalDTO();
				dto.setRENTAL_SEQ(rs.getInt("RENTAL_SEQ"));
				dto.setMEMBER_ID(rs.getString("MEMBER_ID"));
				dto.setMEMBER_NAME(rs.getString("MEMBER_NAME"));
				dto.setMEMBER_TEL(rs.getString("MEMBER_TEL"));
				dto.setBOOK_SEQ(rs.getInt("BOOK_SEQ"));
				dto.setBOOK_NAME(rs.getString("BOOK_NAME"));
				dto.setRENTAL_RENTAL(rs.getDate("RENTAL_RENTAL"));
				dto.setRENTAL_RETURN(rs.getDate("RENTAL_RETURN"));
				dto.setRENTAL_CHECK(rs.getInt("RENTAL_CHECK"));
				dto.setRENTAL_OVERDUE(rs.getInt("RENTAL_OVERDUE"));
				list.add(dto); // 리스트에 추가
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return list;
	}

	public int countOverdueBooks() {
	    String sql = "SELECT COUNT(*) FROM Pro2_rental_Table WHERE RENTAL_OVERDUE > 0 AND RENTAL_CHECK = 1";
	    int count = 0;

	    try (Connection conn = getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql);
	         ResultSet rs = pstmt.executeQuery()) {

	        if (rs.next()) {
	            count = rs.getInt(1);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return count;
	}

	// 회원별 대여중인 도서 수 반환 (3권 제한 체크용)
	// 파라미터: 회원ID
	// 반환값: 대여중 도서 수
	public int memberOverBook(String memberId) {
		String sql = "SELECT COUNT(*) FROM Pro2_rental_Table WHERE MEMBER_ID = ? AND RENTAL_CHECK = 1";
		int count = 0;
		conn = getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, memberId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return count;
	}

	// 회원의 대여/반납 이력 조회
	// 파라미터: 회원ID
	// 반환값: RentalDTO 리스트(이력)
	public List<RentalDTO> memberHistoryRental(String memberId) {
		String sql = "SELECT * FROM Pro2_rental_Table WHERE MEMBER_ID = ? ORDER BY RENTAL_RENTAL DESC";
		List<RentalDTO> list = new ArrayList<>();
		conn = getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, memberId);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				RentalDTO dto = new RentalDTO();
				dto.setRENTAL_SEQ(rs.getInt("RENTAL_SEQ"));
				dto.setMEMBER_ID(rs.getString("MEMBER_ID"));
				dto.setMEMBER_NAME(rs.getString("MEMBER_NAME"));
				dto.setMEMBER_TEL(rs.getString("MEMBER_TEL"));
				dto.setBOOK_SEQ(rs.getInt("BOOK_SEQ"));
				dto.setBOOK_NAME(rs.getString("BOOK_NAME"));
				dto.setRENTAL_RENTAL(rs.getDate("RENTAL_RENTAL"));
				dto.setRENTAL_RETURN(rs.getDate("RENTAL_RETURN"));
				dto.setRENTAL_CHECK(rs.getInt("RENTAL_CHECK"));
				dto.setRENTAL_OVERDUE(rs.getInt("RENTAL_OVERDUE"));
				list.add(dto); // 리스트에 추가
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return list;
	}

	// 연체 회원 목록 반환 (연체 도서가 있는 회원ID 리스트)
	public List<String> overMember() {
		List<String> list = new ArrayList<>();
		String sql = "SELECT DISTINCT MEMBER_ID FROM Pro2_rental_Table WHERE RENTAL_OVERDUE > 0 AND RENTAL_CHECK = 1";
		conn = getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				list.add(rs.getString("MEMBER_ID"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return list;
	}

	// 다음 대여 고유번호(시퀀스) 반환
	public int getNextRentalSeq() {
		String sql = "SELECT NVL(MAX(RENTAL_SEQ), 0) + 1 FROM Pro2_rental_Table";
		int seq = 1;
		conn = getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				seq = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return seq;
	}
}