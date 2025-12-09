
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

// 도서 대여/반납 및 회원-도서 검색, 테이블 관리 등
// 도서관 대여 관리의 모든 UI와 로직을 담당하는 패널 클래스
class RentalManagementPanel extends JPanel implements ActionListener { // JPanel 상속, ActionListener 구현
	rentalDAO rentalDAO = new rentalDAO(); // 대여 관련 DB 처리 객체 (대여/반납/연체 등 DB 작업)
	RentalDTO rentalDTO = new RentalDTO(); // 대여 정보 임시 저장용 DTO
	MemberDAO memberDAO = new MemberDAO(); // 회원 관련 DB 처리 객체 (회원 검색 등)
	MemberDTO memberDTO = new MemberDTO(); // 회원 정보 임시 저장용 DTO
	BookDAO bookDAO = new BookDAO(); // 도서 관련 DB 처리 객체 (도서 검색, 상태 변경 등)
	BookDTO bookDTO = new BookDTO(); // 도서 정보 임시 저장용 DTO
	boolean check1 = false; // 회원 검색 성공 여부 플래그
	boolean check2 = false; // 도서 검색 성공 여부 플래그

	Font font1 = new Font("듣음체", Font.BOLD, 20); // 타이틀용 폰트
	Font font2 = new Font("듣음체", Font.BOLD, 15); // 소제목용 폰트

	JLabel label_rentalManagement = new JLabel("대여 관리", JLabel.CENTER); // 상단 타이틀 라벨
	JLabel label_memberList = new JLabel("회원 목록"); // 회원 목록 라벨
	JLabel label_memberID = new JLabel("회원 ID"); // 회원ID 입력 라벨
	JLabel label_Name = new JLabel("이름"); // 이름 라벨
	JLabel label_birth = new JLabel("생년월일"); // 생년월일 라벨
	JLabel label_tel = new JLabel("핸드폰 번호"); // 전화번호 라벨
	JLabel label_bookInfo = new JLabel("도서 정보"); // 도서 정보 라벨
	JLabel label_bookCode = new JLabel("도서 코드"); // 도서코드 라벨
	JLabel label_bookName = new JLabel("도서명"); // 도서명 라벨
	JLabel label_author = new JLabel("저자"); // 저자 라벨
	JLabel label_publisher = new JLabel("출판사"); // 출판사 라벨

	JButton button_searchID = new JButton("검색"); // 회원ID 검색 버튼
	JButton button_searchCode = new JButton("검색"); // 도서코드 검색 버튼
	JButton button_rental = new JButton("대여하기"); // 대여 버튼
	JButton button_return = new JButton("반납하기"); // 반납 버튼
	JButton button_delete = new JButton("지우기"); // 입력값 초기화 버튼

	JTextField textField_memberID = new JTextField(10); // 회원ID 입력 필드
	JTextField textField_name = new JTextField(10); // 회원이름 표시(수정불가)
	JTextField textField_birth = new JTextField(10); // 생년월일 표시(수정불가)
	JTextField textField_tel = new JTextField(10); // 전화번호 표시(수정불가)
	JTextField textField_bookCode = new JTextField(10); // 도서코드 입력 필드
	JTextField textField_bookName = new JTextField(10); // 도서명 표시(수정불가)
	JTextField textField_author = new JTextField(10); // 저자 표시(수정불가)
	JTextField textField_publisher = new JTextField(10); // 출판사 표시(수정불가)

	JPanel panelBind = new JPanel(); // 전체 묶음 패널
	JPanel panelMemberID = new JPanel(); // 회원ID 입력 패널
	JPanel panelMemberInfo = new JPanel(); // 회원정보 표시 패널
	JPanel panelBookCode = new JPanel(); // 도서코드 입력 패널
	JPanel panelBookInfo = new JPanel(); // 도서정보 표시 패널
	JPanel panelButton = new JPanel(); // 버튼 묶음 패널

	String[] rental = { "회원ID", "이름", "책코드", "책제목", "대여일", "반납일" }; // 테이블 컬럼명
	DefaultTableModel tableModel = new DefaultTableModel(rental, 0); // 테이블 데이터 모델
	JTable table = new JTable(tableModel); // 대여/반납 테이블
	JScrollPane scrollPane = new JScrollPane(table); // 테이블 스크롤

	// 생성자: 패널 초기화 및 전체 대여 목록 로딩
	public RentalManagementPanel() {
		setLayout(new BorderLayout()); // 패널 레이아웃 설정
		init(); // UI 초기화 및 이벤트 연결
		rentalsearchall(); // 전체 대여 목록 테이블에 표시
	}

	// UI 컴포넌트 배치 및 이벤트 연결 메서드
	private void init() {
		add("North", panelBind); // 상단에 panelBind 추가
		add("Center", scrollPane); // 중앙에 테이블 추가
		scrollPane.setPreferredSize(new Dimension(400, 200)); // 테이블 크기 지정

		panelBind.setLayout(new GridLayout(8, 1)); // panelBind를 8행 1열로 배치
		panelBind.add(label_rentalManagement); // 타이틀 추가
		label_rentalManagement.setFont(font1); // 타이틀 폰트 적용

		panelBind.add(label_memberList); // 회원 목록 라벨 추가
		label_memberList.setFont(font2); // 소제목 폰트 적용
		panelBind.add(panelMemberID); // 회원ID 입력 패널 추가
		panelBind.add(panelMemberInfo); // 회원정보 표시 패널 추가
		panelBind.add(label_bookInfo); // 도서 정보 라벨 추가
		label_bookInfo.setFont(font2); // 소제목 폰트 적용

		panelBind.add(panelBookCode); // 도서코드 입력 패널 추가
		panelBind.add(panelBookInfo); // 도서정보 표시 패널 추가
		panelBind.add(panelButton); // 버튼 패널 추가

		// 회원ID 입력 패널 구성
		panelMemberID.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelMemberID.add(label_memberID); // 회원ID 라벨
		panelMemberID.add(textField_memberID); // 회원ID 입력 필드
		panelMemberID.add(button_searchID); // 회원ID 검색 버튼

		// 회원정보 표시 패널 구성
		panelMemberInfo.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelMemberInfo.add(label_Name); // 이름 라벨
		panelMemberInfo.add(textField_name); // 이름 표시 필드
		textField_name.setEnabled(false); // 수정불가
		panelMemberInfo.add(label_birth); // 생년월일 라벨
		panelMemberInfo.add(textField_birth); // 생년월일 표시 필드
		textField_birth.setEnabled(false); // 수정불가
		panelMemberInfo.add(label_tel); // 전화번호 라벨
		panelMemberInfo.add(textField_tel); // 전화번호 표시 필드
		textField_tel.setEnabled(false); // 수정불가

		// 도서코드 입력 패널 구성
		panelBookCode.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelBookCode.add(label_bookCode); // 도서코드 라벨
		panelBookCode.add(textField_bookCode); // 도서코드 입력 필드
		panelBookCode.add(button_searchCode); // 도서코드 검색 버튼

		// 도서정보 표시 패널 구성
		panelBookInfo.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelBookInfo.add(label_bookName); // 도서명 라벨
		panelBookInfo.add(textField_bookName); // 도서명 표시 필드
		textField_bookName.setEnabled(false); // 수정불가
		panelBookInfo.add(label_author); // 저자 라벨
		panelBookInfo.add(textField_author); // 저자 표시 필드
		textField_author.setEnabled(false); // 수정불가
		panelBookInfo.add(label_publisher); // 출판사 라벨
		panelBookInfo.add(textField_publisher); // 출판사 표시 필드
		textField_publisher.setEnabled(false); // 수정불가

		// 버튼 패널 구성
		panelButton.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panelButton.add(button_rental); // 대여 버튼
		panelButton.add(button_return); // 반납 버튼
		panelButton.add(button_delete); // 입력값 초기화 버튼

		// 버튼 이벤트 연결
		button_searchID.addActionListener(this);
		button_searchCode.addActionListener(this);
		button_rental.addActionListener(this);
		button_return.addActionListener(this);
		button_delete.addActionListener(this);
	}

	// 버튼 클릭 등 이벤트 처리 메서드
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == button_searchID) { // 회원ID 검색 버튼 클릭 시
			// 입력값으로 회원 정보 검색
			MemberDTO id = memberDAO.searchID(textField_memberID.getText());

			if (id != null) { // 검색 성공 시
				textField_name.setText(id.getMEMBER_NAME()); // 이름 표시
				textField_birth.setText(id.getMEMBER_BIRTH()); // 생년월일 표시
				textField_tel.setText(id.getMEMBER_TEL()); // 전화번호 표시
				check1 = true; // 회원 검색 성공 플래그
				loadRentalList(); // 해당 회원의 대여중 도서 목록 표시
				return;
			} else { // 검색 실패 시
				JOptionPane.showMessageDialog(this, "해당 ID를 찾을 수 없습니다.", "검색 실패", JOptionPane.WARNING_MESSAGE);
			}
		} else if (e.getSource() == button_searchCode) { // 도서코드 검색 버튼 클릭 시
			if (textField_bookCode.getText().equals("")) { // 입력값 없을 때
				JOptionPane.showMessageDialog(this, "해당 책을 찾을 수 없습니다.", "검색 실패", JOptionPane.WARNING_MESSAGE);
				return;
			} else {
				String bcode = textField_bookCode.getText();
				int bookcode = Integer.parseInt(bcode);
				BookDTO bookseq = bookDAO.selectByBookSeq(bookcode); // 도서코드로 도서 검색

				if (bookseq != null) { // 검색 성공 시
					textField_bookName.setText(bookseq.getBOOK_NAME()); // 도서명 표시
					textField_publisher.setText(bookseq.getBOOK_PUBLISHER()); // 출판사 표시
					textField_author.setText(bookseq.getBOOK_AUTHOR()); // 저자 표시
					check2 = true; // 도서 검색 성공 플래그
				} else { // 검색 실패 시
					JOptionPane.showMessageDialog(this, "해당 책을 찾을 수 없습니다.", "검색 실패", JOptionPane.WARNING_MESSAGE);
				}
				return;
			}
		} else if (e.getSource() == button_rental) { // 대여 버튼 클릭 시
			// 입력값 및 대여 가능 여부 체크
			if (textField_memberID.getText().isEmpty()) {
				JOptionPane.showMessageDialog(this, "아이디를 입력해주세요.", "대여 실패", JOptionPane.WARNING_MESSAGE);
				return;
			} else if (textField_bookCode.getText().equals("")) {
				JOptionPane.showMessageDialog(this, "도서 코드를 입력해주세요.", "대여 실패", JOptionPane.WARNING_MESSAGE);
				return;
			} else if (rentalDAO.memberOverBook(textField_memberID.getText()) >= 3) { // 3권 이상 대여 불가
				JOptionPane.showMessageDialog(this, "3권까지만 대여 가능합니다..", "대여 실패", JOptionPane.WARNING_MESSAGE);
				return;
			}

			String bcode = textField_bookCode.getText();
			int bookseq = Integer.parseInt(bcode);
			BookDTO bookcheck = bookDAO.selectByBookSeq(bookseq); // 도서 상태 확인

			if (bookcheck.getBOOK_CHECK() == 2) { // 이미 대여중인 도서
				JOptionPane.showMessageDialog(this, "해당 책은 대여중입니다.", "대여 실패", JOptionPane.WARNING_MESSAGE);
				return;
			} else {
				// 대여 정보 DTO에 값 세팅
				rentalDTO.setRENTAL_SEQ(rentalDAO.getNextRentalSeq()); // 대여 고유번호
				rentalDTO.setMEMBER_ID(textField_memberID.getText()); // 회원ID
				rentalDTO.setMEMBER_NAME(textField_name.getText()); // 회원이름
				rentalDTO.setMEMBER_TEL(textField_tel.getText()); // 회원전화번호
				rentalDTO.setBOOK_SEQ(bookseq); // 도서코드
				rentalDTO.setBOOK_NAME(textField_bookName.getText()); // 도서명
				rentalDTO.setRENTAL_RENTAL(Date.valueOf(LocalDate.now())); // 대여일(오늘)
				rentalDTO.setRENTAL_RETURN(Date.valueOf(LocalDate.now().plusDays(7))); // 반납예정일(7일 후)
				rentalDTO.setRENTAL_CHECK(1); // 대여중 상태
				rentalDTO.setRENTAL_OVERDUE(0); // 연체 없음

				int result = rentalDAO.insert(rentalDTO); // DB에 대여 내역 저장

				if (result > 0) { // 대여 성공 시
					bookDAO.updateBookCheck(2, bookcheck.getBOOK_SEQ()); // 도서 상태를 대여중(2)으로 변경
					JOptionPane.showMessageDialog(this, "대여가 완료되었습니다.", "대여 성공", JOptionPane.WARNING_MESSAGE);
					check1 = false;
					check2 = false;
					loadRentalList(); // 테이블 갱신
					delete(); // 입력값 초기화
					LibraryManagementApp.loadBookList(); // 전체 도서 테이블 갱신
					LibraryManagementApp.overmemberlist(); // 연체 회원 목록 갱신
				} else { // 대여 실패 시
					JOptionPane.showMessageDialog(this, "대여 등록에 실패했습니다.", "대여 실패", JOptionPane.WARNING_MESSAGE);
					return;
				}
			}
			return;
		} else if (e.getSource() == button_return) { // 반납 버튼 클릭 시
			int selectedRow = table.getSelectedRow(); // 테이블에서 선택된 행

			if (selectedRow == -1) { // 아무 행도 선택되지 않음
				JOptionPane.showMessageDialog(this, "반납할 책을 선택해주세요.", "반납", JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			// 테이블 모델에서 값 추출
			int rentalSeq = (int) table.getValueAt(selectedRow, 2); // 책코드(BOOK_SEQ)
			String bookName = (String) table.getValueAt(selectedRow, 3); // 책제목

			int confirm = JOptionPane.showConfirmDialog(this, "정말로 '" + bookName + "' 을/를 반납하시겠습니까?", "반납 확인",
					JOptionPane.YES_NO_OPTION);

			if (confirm == JOptionPane.YES_OPTION) { // 반납 확인 시
				rentalDAO.deleterental(rentalSeq); // 대여 내역 삭제(또는 RENTAL_CHECK=0)
				bookDAO.updateBookCheck(1, rentalSeq); // 도서 상태를 대여 가능(1)로 변경

				JOptionPane.showMessageDialog(this, "반납되었습니다.");
				loadRentalList(); // 테이블 갱신
				LibraryManagementApp.loadBookList(); // 전체 도서 테이블 갱신
				LibraryManagementApp.overmemberlist(); // 연체 회원 목록 갱신
			}
		} else if (e.getSource() == button_delete) { // 입력값 초기화 버튼 클릭 시
			delete(); // 입력값 초기화
			rentalsearchall(); // 전체 대여 목록 표시
		}
	}

	// 전체 대여 목록을 테이블에 표시하는 메서드
	private void rentalsearchall() {
		List<RentalDTO> rental = rentalDAO.searchAll(); // DB에서 전체 대여 내역 조회

		// 테이블 초기화
		tableModel.setRowCount(0);

		for (RentalDTO r : rental) {
			Object[] row = { r.getMEMBER_ID(), r.getMEMBER_NAME(), r.getBOOK_SEQ(), r.getBOOK_NAME(),
					r.getRENTAL_RENTAL(), r.getRENTAL_RETURN() };
			tableModel.addRow(row); // 테이블에 한 행씩 추가
		}
	}

	// 특정 회원의 대여중인 도서 목록을 테이블에 표시하는 메서드
	private void loadRentalList() {
		List<RentalDTO> rental = rentalDAO.rentalStayBook(textField_memberID.getText()); // 해당 회원의 대여중 도서 조회

		// 테이블 초기화
		tableModel.setRowCount(0);

		for (RentalDTO r : rental) {
			Object[] row = { r.getMEMBER_ID(), r.getMEMBER_NAME(), r.getBOOK_SEQ(), r.getBOOK_NAME(),
					r.getRENTAL_RENTAL(), r.getRENTAL_RETURN() };
			tableModel.addRow(row); // 테이블에 한 행씩 추가
		}
	}

	// 입력값(회원/도서 정보) 초기화 메서드
	void delete() {
		textField_memberID.setText(""); // 회원ID 초기화
		textField_name.setText(""); // 이름 초기화
		textField_birth.setText(""); // 생년월일 초기화
		textField_tel.setText(""); // 전화번호 초기화
		textField_bookCode.setText(""); // 도서코드 초기화
		textField_bookName.setText(""); // 도서명 초기화
		textField_author.setText(""); // 저자 초기화
		textField_publisher.setText(""); // 출판사 초기화
	}
}
