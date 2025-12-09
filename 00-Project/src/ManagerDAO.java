// 관리자(매니저) 관련 DB 처리 클래스
// 관리자 등록, 조회, 수정, 삭제 등 DB 작업 담당

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ManagerDAO {
	Connection conn; // DB 연결 객체
	PreparedStatement pstmt; // SQL 실행 객체
	ResultSet rs; // SQL 결과 저장 객체

	// 생성자: 오라클 드라이버를 로드함
	public ManagerDAO() {
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

	// 다음 매니저 시퀀스 번호 반환
	public int getNextSeq() {
		String sql = "SELECT NVL(MAX(MANAGER_SEQ), 0) + 1 FROM PRO2_MANAGER_TABLE";
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

	// 관리자 정보 DB에 추가(등록)
	// 파라미터: seq(순번), name(이름), tel(전화번호), code(코드)
	// 반환값: insert 성공 시 1, 실패 시 0
	public int insert(int seq, String name, String tel, int code) {
		String sql = "INSERT INTO PRO2_MANAGER_TABLE (MANAGER_SEQ, MANAGER_NAME, MANAGER_TEL, MANAGER_CODE) VALUES (?, ?, ?, ?)";
		int result = 0;

		conn = getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, seq); // 순번
			pstmt.setString(2, name); // 이름
			pstmt.setString(3, tel); // 전화번호
			pstmt.setInt(4, code); // 코드

			result = pstmt.executeUpdate(); // DB에 insert 실행
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return result;
	}

	// 전체 관리자 목록 조회
	// 반환값: ManagerDTO 리스트(전체 관리자 정보)
	public List<ManagerDTO> searchAll() {
		String sql = "SELECT * FROM Pro2_Manager_Table";
		List<ManagerDTO> list = new ArrayList<ManagerDTO>();
		conn = getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				ManagerDTO dto = new ManagerDTO();
				dto.setMANAGER_SEQ(rs.getInt("MANAGER_SEQ"));
				dto.setMANAGER_NAME(rs.getString("MANAGER_NAME"));
				dto.setMANAGER_TEL(rs.getString("MANAGER_TEL"));
				dto.setMANAGER_CODE(rs.getInt("MANAGER_CODE"));
				list.add(dto); // 리스트에 추가
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return list;
	}

	// 관리자 삭제(코드로 삭제)
	// 파라미터: managerCode(관리자 코드)
	// 반환값: delete 성공 시 1, 실패 시 0
	public int deleteManager(int managerCode) {
		String sql = "DELETE FROM Pro2_Manager_Table WHERE MANAGER_CODE = ?";
		int result = 0;
		conn = getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, managerCode); // 관리자 코드
			result = pstmt.executeUpdate(); // DB에서 삭제 실행
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return result;
	}

	// 관리자 코드로 관리자 정보 조회
	// 파라미터: managerCode(관리자 코드)
	// 반환값: ManagerDTO(관리자 정보, 없으면 null)
	public ManagerDTO selectByManagerCode(int managerCode) {
		String sql = "SELECT * FROM Pro2_Manager_Table WHERE MANAGER_CODE = ?";
		ManagerDTO dto = null;
		conn = getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, managerCode); // 관리자 코드
			rs = pstmt.executeQuery();
			if (rs.next()) {
				dto = new ManagerDTO();
				dto.setMANAGER_SEQ(rs.getInt("MANAGER_SEQ"));
				dto.setMANAGER_NAME(rs.getString("MANAGER_NAME"));
				dto.setMANAGER_TEL(rs.getString("MANAGER_TEL"));
				dto.setMANAGER_CODE(rs.getInt("MANAGER_CODE"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return dto;
	}

	// 관리자 정보 수정(SEQ 기준)
	// 파라미터: ManagerDTO(수정할 관리자 정보)
	// 반환값: update 성공 시 1, 실패 시 0
	public int update(ManagerDTO dto) {
		String sql = "UPDATE Pro2_Manager_Table " + "SET MANAGER_NAME = ?, MANAGER_TEL = ?, MANAGER_CODE = ? "
				+ "WHERE MANAGER_SEQ = ?";
		int result = 0;
		conn = getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getMANAGER_NAME()); // 이름
			pstmt.setString(2, dto.getMANAGER_TEL()); // 전화번호
			pstmt.setInt(3, dto.getMANAGER_CODE()); // 코드
			pstmt.setInt(4, dto.getMANAGER_SEQ()); // 순번(수정 대상)
			result = pstmt.executeUpdate(); // DB에서 update 실행
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return result;
	}

	// 이름으로 관리자 정보 검색(부분검색)
	// 파라미터: keyword(이름 키워드)
	// 반환값: ManagerDTO 리스트(이름 일치 관리자)
	public List<ManagerDTO> searchByName(String keyword) {
		List<ManagerDTO> list = new ArrayList<>();
		String sql = "SELECT * FROM Pro2_Manager_Table " + "WHERE MANAGER_NAME LIKE ? " + "ORDER BY MANAGER_NAME ASC";
		conn = getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, "%" + keyword + "%"); // 부분 문자열 검색
			rs = pstmt.executeQuery();
			while (rs.next()) {
				ManagerDTO dto = new ManagerDTO();
				dto.setMANAGER_SEQ(rs.getInt("MANAGER_SEQ"));
				dto.setMANAGER_NAME(rs.getString("MANAGER_NAME"));
				dto.setMANAGER_TEL(rs.getString("MANAGER_TEL"));
				dto.setMANAGER_CODE(rs.getInt("MANAGER_CODE"));
				list.add(dto); // 리스트에 추가
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return list;
	}

	// 전화번호로 관리자 정보 검색(부분검색)
	// 파라미터: telNum(전화번호 일부)
	// 반환값: ManagerDTO 리스트(전화번호 일치 관리자)
	public List<ManagerDTO> searchByTel(String telNum) {
		List<ManagerDTO> list = new ArrayList<>();
		String sql = "SELECT * FROM Pro2_Manager_Table WHERE MANAGER_TEL LIKE ?";

		conn = getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, "%" + telNum + "%"); // 부분 문자열 검색
			rs = pstmt.executeQuery();
			while (rs.next()) {
				ManagerDTO dto = new ManagerDTO();
				dto.setMANAGER_SEQ(rs.getInt("MANAGER_SEQ"));
				dto.setMANAGER_NAME(rs.getString("MANAGER_NAME"));
				dto.setMANAGER_TEL(rs.getString("MANAGER_TEL"));
				dto.setMANAGER_CODE(rs.getInt("MANAGER_CODE"));
				list.add(dto); // 리스트에 추가
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return list;
	}

	// 순번(SEQ)으로 관리자 정보 조회
	// 파라미터: managerCode(순번)
	// 반환값: ManagerDTO(관리자 정보, 없으면 null)
	public ManagerDTO selectByManagerSeq(int managerCode) {
		String sql = "SELECT * FROM Pro2_Manager_Table WHERE MANAGER_SEQ = ?";
		ManagerDTO dto = null;
		conn = getConnection();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, managerCode); // 순번
			rs = pstmt.executeQuery();
			if (rs.next()) {
				dto = new ManagerDTO();
				dto.setMANAGER_SEQ(rs.getInt("MANAGER_SEQ"));
				dto.setMANAGER_NAME(rs.getString("MANAGER_NAME"));
				dto.setMANAGER_TEL(rs.getString("MANAGER_TEL"));
				dto.setMANAGER_CODE(rs.getInt("MANAGER_CODE"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return dto;
	}
}
