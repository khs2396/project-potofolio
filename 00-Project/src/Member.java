
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

// 회원 도서 검색/조회 화면 클래스
// 회원이 도서를 검색하고 대출 가능 여부를 확인할 수 있는 UI 및 로직 담당
class Member extends JFrame implements ActionListener { // JFrame 상속, ActionListener 구현
	BookDAO bookDAO = new BookDAO(); // 도서 DB 처리 객체 (검색, 장르 등)
	Container container = getContentPane(); // 프레임 컨테이너
	ImageIcon imageIcon = new ImageIcon("img/book.png"); // 이미지 관리 클래스
	JLabel label_searchBook = new JLabel("도서 검색", JLabel.CENTER); // 상단 타이틀 라벨
	JLabel label_bookList = new JLabel("도서 목록"); // 도서 목록 라벨

	Font font1 = new Font("듣음체", Font.BOLD, 20); // 타이틀용 폰트
	Font font2 = new Font("듣음체", Font.BOLD, 15); // 소제목용 폰트

	JTextField textField_search = new JTextField(20); // 도서 검색어 입력 필드

	JButton button_search = new JButton("검색"); // 검색 버튼

	JPanel panel_booklList = new JPanel(); // 도서 목록 라벨 패널
	JPanel panel_search = new JPanel(); // 검색 입력/버튼 패널
	JPanel panel_bind = new JPanel(); // 상단 전체 묶음 패널

	JPanel panel_west = new JPanel(); // 좌측 여백 패널
	JPanel panel_east = new JPanel(); // 우측 여백 패널

	JComboBox<String> comboBox = new JComboBox<String>(); // 장르 선택 콤보박스

	String[] book_list = { "코드번호", "도서명", "저자", "출판사", "장르", "대출여부" }; // 테이블 컬럼명
	DefaultTableModel tableModel = new DefaultTableModel(book_list, 0); // 테이블 데이터 모델
	JTable table = new JTable(tableModel); // 도서 목록 테이블
	JScrollPane scrollPane = new JScrollPane(table); // 테이블 스크롤

	// 생성자: 회원 도서 검색 화면 초기화 및 데이터 로딩
	public Member() {
		setTitle("도서 검색"); // 프레임 타이틀
		setIconImage(imageIcon.getImage());	// 아이콘 설정
		setSize(800, 400); // 프레임 크기
		setLocation(500, 300); // 프레임 위치
		init(); // UI 배치
		loadGenres(); // 장르 목록 로딩
		start(); // 이벤트 연결
		loadBookList(); // 전체 도서 목록 로딩
		setVisible(true); // 화면 표시
	}

	// UI 컴포넌트 배치 메서드
	private void init() {
		container.setLayout(new BorderLayout()); // 프레임 레이아웃 설정
		container.add("North", panel_bind); // 상단에 panel_bind 추가
		container.add("Center", scrollPane); // 중앙에 테이블 추가
		container.add("West", panel_west); // 좌측 여백
		container.add("East", panel_east); // 우측 여백

		panel_bind.setLayout(new BorderLayout()); // 상단 묶음 패널
		panel_bind.add("North", label_searchBook); // 타이틀 추가
		label_searchBook.setFont(font1); // 타이틀 폰트 적용

		panel_bind.add("Center", panel_search); // 검색 입력/버튼 패널 추가
		panel_bind.add("South", panel_booklList); // 도서 목록 라벨 패널 추가

		panel_search.setLayout(new FlowLayout()); // 검색 입력/버튼 패널
		panel_search.add(comboBox); // 장르 콤보박스
		panel_search.add(textField_search); // 검색어 입력 필드
		panel_search.add(button_search); // 검색 버튼

		panel_booklList.setLayout(new FlowLayout(FlowLayout.LEFT)); // 도서 목록 라벨 패널
		panel_booklList.add(label_bookList); // 도서 목록 라벨
		label_bookList.setFont(font2); // 소제목 폰트 적용
	}

	// 이벤트 연결 및 종료 설정
	private void start() {
		setDefaultCloseOperation(EXIT_ON_CLOSE); // 창 닫기 시 종료

		// 장르 콤보박스 선택 이벤트 처리
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedGenre = (String) comboBox.getSelectedItem(); // 선택된 장르
				if (selectedGenre.equals("전체")) {
					loadBookList(); // 전체 목록 로딩
				} else {
					loadBookListByGenre(selectedGenre); // 장르별 목록 로딩
				}
			}
		});

		// 검색 버튼 이벤트 연결
		button_search.addActionListener(this);
	}

	// 장르 목록을 콤보박스에 로딩
	private void loadGenres() {
		comboBox.removeAllItems(); // 콤보박스 초기화
		comboBox.addItem("전체"); // 전체 항목 추가

		List<String> genres = bookDAO.getAllGenres(); // DB에서 장르 목록 조회
		for (String genre : genres) {
			comboBox.addItem(genre); // 콤보박스에 장르 추가
		}
	}

	// 전체 도서 목록을 테이블에 표시
	private void loadBookList() {
		List<BookDTO> book = bookDAO.searchAll(); // DB에서 전체 도서 조회

		tableModel.setRowCount(0); // 테이블 초기화

		for (BookDTO b : book) {
			String checkStatus = (b.getBOOK_CHECK() == 1) ? "대여 가능" : "대여 불가능"; // 대출여부 표시
			Object[] row = { b.getBOOK_SEQ(), b.getBOOK_NAME(), b.getBOOK_AUTHOR(), b.getBOOK_PUBLISHER(),
					b.getBOOK_GENRE(), checkStatus };
			tableModel.addRow(row); // 테이블에 한 행씩 추가
		}
	}

	// 특정 장르의 도서 목록을 테이블에 표시
	private void loadBookListByGenre(String genre) {
		List<BookDTO> bookList = bookDAO.searchByGenre(genre); // DB에서 장르별 도서 조회

		tableModel.setRowCount(0); // 테이블 초기화

		for (BookDTO b : bookList) {
			String checkStatus = (b.getBOOK_CHECK() == 1) ? "대여 가능" : "대여 불가능";
			Object[] row = { b.getBOOK_SEQ(), b.getBOOK_NAME(), b.getBOOK_AUTHOR(), b.getBOOK_PUBLISHER(),
					b.getBOOK_GENRE(), checkStatus };
			tableModel.addRow(row); // 테이블에 한 행씩 추가
		}
	}

	// 검색 버튼 클릭 시 이벤트 처리
	@Override
	public void actionPerformed(ActionEvent e) {
		// 도서 검색
		if (e.getSource() == button_search) {
			String keyword = textField_search.getText().trim(); // 검색어
			String selectedGenre = (String) comboBox.getSelectedItem(); // 선택된 장르

			// 모든 도서 검색 (장르가 전체일 경우)
			if (selectedGenre.equals("전체")) {
				List<BookDTO> result = bookDAO.searchByKeyword(keyword); // 키워드로 검색
				loadBookTable(result); // 결과 테이블에 표시
			} else {
				// 장르 조건 포함 검색
				List<BookDTO> fullList = bookDAO.searchByKeyword(keyword); // 키워드로 전체 검색
				List<BookDTO> filteredList = new ArrayList<>();
				for (BookDTO b : fullList) {
					if (b.getBOOK_GENRE().equals(selectedGenre)) {
						filteredList.add(b); // 장르 일치하는 도서만 추가
					}
				}
				loadBookTable(filteredList); // 결과 테이블에 표시
			}

		}
	}

	// 도서 리스트를 테이블에 표시하는 메서드
	private void loadBookTable(List<BookDTO> bookList) {
		tableModel.setRowCount(0); // 테이블 초기화

		for (BookDTO b : bookList) {
			String checkStatus = (b.getBOOK_CHECK() == 1) ? "대여 가능" : "대여 불가능";
			Object[] row = { b.getBOOK_SEQ(), b.getBOOK_NAME(), b.getBOOK_AUTHOR(), b.getBOOK_PUBLISHER(),
					b.getBOOK_GENRE(), checkStatus };
			tableModel.addRow(row); // 테이블에 한 행씩 추가
		}
	}
}

