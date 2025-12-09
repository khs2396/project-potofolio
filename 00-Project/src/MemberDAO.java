// 회원 관련 DB 처리 클래스
// 회원 가입, 로그인, 정보 조회/수정/삭제 등 DB 작업 담당
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {
    Connection conn; // DB 연결 객체
    PreparedStatement pstmt; // SQL 실행 객체
    ResultSet rs; // SQL 결과 저장 객체

    // 생성자: 오라클 드라이버를 로드함
    public MemberDAO() {
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
            if (rs != null) rs.close(); // ResultSet 닫기
            if (pstmt != null) pstmt.close(); // PreparedStatement 닫기
            if (conn != null) conn.close(); // Connection 닫기
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 회원 정보 DB에 추가(회원 가입)
    // 파라미터: MemberDTO(회원 정보)
    // 반환값: insert 성공 시 1, 실패 시 0
    public int insert(MemberDTO dto) {
        String sql = "INSERT INTO Pro2_Member_Table (MEMBER_SEQ, MEMBER_ID, MEMBER_PW, MEMBER_NAME, MEMBER_TEL, MEMBER_GENDER, MEMBER_BIRTH) VALUES (?, ?, ?, ?, ?, ?, ?)";
        int result = 0;
        conn = getConnection();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, dto.getMEMBER_SEQ()); // 회원 고유번호
            pstmt.setString(2, dto.getMEMBER_ID()); // 회원ID
            pstmt.setString(3, dto.getMEMBER_PW()); // 비밀번호
            pstmt.setString(4, dto.getMEMBER_NAME()); // 이름
            pstmt.setString(5, dto.getMEMBER_TEL()); // 전화번호
            pstmt.setInt(6, dto.getMEMBER_GENDER()); // 성별
            pstmt.setString(7, dto.getMEMBER_BIRTH()); // 생년월일
            result = pstmt.executeUpdate(); // DB에 insert 실행
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return result;
    }

    // 전체 회원 목록을 조회(오름차순)
    // 반환값: MemberDTO 리스트(전체 회원 정보)
    public List<MemberDTO> searchAllAsc() {
        String sql = "SELECT * FROM PRO2_MEMBER_TABLE ORDER BY MEMBER_SEQ ASC";
        List<MemberDTO> list = new ArrayList<>();
        conn = getConnection();
        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                MemberDTO dto = new MemberDTO();
                dto.setMEMBER_SEQ(rs.getInt("MEMBER_SEQ"));
                dto.setMEMBER_ID(rs.getString("MEMBER_ID"));
                dto.setMEMBER_PW(rs.getString("MEMBER_PW"));
                dto.setMEMBER_NAME(rs.getString("MEMBER_NAME"));
                dto.setMEMBER_TEL(rs.getString("MEMBER_TEL"));
                dto.setMEMBER_GENDER(rs.getInt("MEMBER_GENDER"));
                dto.setMEMBER_BIRTH(rs.getString("MEMBER_BIRTH"));
                list.add(dto); // 리스트에 추가
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return list;
    }

    // 회원ID로 회원 정보 조회
    // 파라미터: id(회원ID)
    // 반환값: MemberDTO(회원 정보, 없으면 null)
    public MemberDTO searchID(String id) {
        String sql = "SELECT * FROM PRO2_MEMBER_TABLE WHERE MEMBER_ID=?";
        MemberDTO dto = null;
        conn = getConnection();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id); // 회원ID
            rs = pstmt.executeQuery();
            if (rs.next()) {
                dto = new MemberDTO();
                dto.setMEMBER_SEQ(rs.getInt("MEMBER_SEQ"));
                dto.setMEMBER_ID(rs.getString("MEMBER_ID"));
                dto.setMEMBER_PW(rs.getString("MEMBER_PW"));
                dto.setMEMBER_NAME(rs.getString("MEMBER_NAME"));
                dto.setMEMBER_TEL(rs.getString("MEMBER_TEL"));
                dto.setMEMBER_GENDER(rs.getInt("MEMBER_GENDER"));
                dto.setMEMBER_BIRTH(rs.getString("MEMBER_BIRTH"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return dto;
    }

    // 이름으로 회원 정보 조회
    // 파라미터: name(이름)
    // 반환값: MemberDTO 리스트(동명이인 모두 반환)
    public List<MemberDTO> searchName(String name) {
        String sql = "SELECT * FROM PRO2_MEMBER_TABLE WHERE MEMBER_NAME=?";
        List<MemberDTO> list = new ArrayList<>();
        conn = getConnection();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name); // 이름
            rs = pstmt.executeQuery();
            while (rs.next()) {
                MemberDTO dto = new MemberDTO();
                dto.setMEMBER_SEQ(rs.getInt("MEMBER_SEQ"));
                dto.setMEMBER_ID(rs.getString("MEMBER_ID"));
                dto.setMEMBER_PW(rs.getString("MEMBER_PW"));
                dto.setMEMBER_NAME(rs.getString("MEMBER_NAME"));
                dto.setMEMBER_TEL(rs.getString("MEMBER_TEL"));
                dto.setMEMBER_GENDER(rs.getInt("MEMBER_GENDER"));
                dto.setMEMBER_BIRTH(rs.getString("MEMBER_BIRTH"));
                list.add(dto); // 리스트에 추가
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return list;
    }

    // 이름+전화번호로 회원 정보 조회 (ID/PW 찾기용)
    // 파라미터: name(이름), tel(전화번호)
    // 반환값: MemberDTO 리스트(일치 회원 모두 반환)
    public List<MemberDTO> searchByNameAndTel(String name, String tel) {
        String sql = "SELECT * FROM PRO2_MEMBER_TABLE WHERE MEMBER_NAME = ? AND MEMBER_TEL = ?";
        List<MemberDTO> list = new ArrayList<>();
        conn = getConnection();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name); // 이름
            pstmt.setString(2, tel); // 전화번호
            rs = pstmt.executeQuery();
            while (rs.next()) {
                MemberDTO dto = new MemberDTO();
                dto.setMEMBER_SEQ(rs.getInt("MEMBER_SEQ"));
                dto.setMEMBER_ID(rs.getString("MEMBER_ID"));
                dto.setMEMBER_PW(rs.getString("MEMBER_PW"));
                dto.setMEMBER_NAME(rs.getString("MEMBER_NAME"));
                dto.setMEMBER_TEL(rs.getString("MEMBER_TEL"));
                dto.setMEMBER_GENDER(rs.getInt("MEMBER_GENDER"));
                dto.setMEMBER_BIRTH(rs.getString("MEMBER_BIRTH"));
                list.add(dto); // 리스트에 추가
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return list;
    }

    // ID+이름+전화번호로 회원 정보 조회 (PW 찾기용)
    // 파라미터: id(회원ID), name(이름), tel(전화번호)
    // 반환값: MemberDTO 리스트(일치 회원 모두 반환)
    public List<MemberDTO> searchByIdAndNameAndTel(String id, String name, String tel) {
        String sql = "SELECT * FROM PRO2_MEMBER_TABLE WHERE MEMBER_ID = ? AND MEMBER_NAME = ? AND MEMBER_TEL = ?";
        List<MemberDTO> list = new ArrayList<>();
        conn = getConnection();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id); // 회원ID
            pstmt.setString(2, name); // 이름
            pstmt.setString(3, tel); // 전화번호
            rs = pstmt.executeQuery();
            while (rs.next()) {
                MemberDTO dto = new MemberDTO();
                dto.setMEMBER_SEQ(rs.getInt("MEMBER_SEQ"));
                dto.setMEMBER_ID(rs.getString("MEMBER_ID"));
                dto.setMEMBER_PW(rs.getString("MEMBER_PW"));
                dto.setMEMBER_NAME(rs.getString("MEMBER_NAME"));
                dto.setMEMBER_TEL(rs.getString("MEMBER_TEL"));
                dto.setMEMBER_GENDER(rs.getInt("MEMBER_GENDER"));
                dto.setMEMBER_BIRTH(rs.getString("MEMBER_BIRTH"));
                list.add(dto); // 리스트에 추가
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return list;
    }

    // 회원ID 중복 체크
    // 파라미터: id(회원ID)
    // 반환값: true(중복), false(사용 가능)
    public boolean isIdDuplicate(String id) {
        String sql = "SELECT COUNT(*) FROM PRO2_MEMBER_TABLE WHERE MEMBER_ID = ?";
        boolean isDuplicate = false;
        conn = getConnection();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id); // 회원ID
            rs = pstmt.executeQuery();
            if (rs.next()) {
                isDuplicate = rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return isDuplicate;
    }

    // 회원 탈퇴(삭제)
    // 파라미터: id(회원ID)
    // 반환값: delete 성공 시 1, 실패 시 0
    public int deleteMember(String id) {
        String sql = "DELETE FROM PRO2_MEMBER_TABLE WHERE MEMBER_ID = ?";
        int result = 0;
        conn = getConnection();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id); // 회원ID
            result = pstmt.executeUpdate(); // DB에서 삭제 실행
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return result;
    }

    // 로그인 체크
    // 파라미터: id(회원ID), pw(비밀번호)
    // 반환값: true(로그인 성공), false(실패)
    public boolean loginCheck(String id, String pw) {
        String sql = "SELECT * FROM PRO2_MEMBER_TABLE WHERE MEMBER_ID = ? AND MEMBER_PW = ?";
        boolean loginSuccess = false;
        conn = getConnection();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id); // 회원ID
            pstmt.setString(2, pw); // 비밀번호
            rs = pstmt.executeQuery();
            if (rs.next()) {
                loginSuccess = true; // 로그인 성공
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return loginSuccess;
    }

    // 회원 정보 수정
    // 파라미터: MemberDTO(수정할 회원 정보)
    // 반환값: update 성공 시 1, 실패 시 0
    public int updateMember(MemberDTO dto) {
        String sql = "UPDATE PRO2_MEMBER_TABLE SET MEMBER_PW=?, MEMBER_NAME=?, MEMBER_TEL=?, MEMBER_GENDER=?, MEMBER_BIRTH=? WHERE MEMBER_ID=?";
        int result = 0;
        conn = getConnection();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, dto.getMEMBER_PW()); // 비밀번호
            pstmt.setString(2, dto.getMEMBER_NAME()); // 이름
            pstmt.setString(3, dto.getMEMBER_TEL()); // 전화번호
            pstmt.setInt(4, dto.getMEMBER_GENDER()); // 성별
            pstmt.setString(5, dto.getMEMBER_BIRTH()); // 생년월일
            pstmt.setString(6, dto.getMEMBER_ID()); // 회원ID(수정 대상)
            result = pstmt.executeUpdate(); // DB에서 update 실행
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return result;
    }
}
