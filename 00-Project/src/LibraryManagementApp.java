// LibraryManagementApp.java (BookForm + OverdueForm 통합 툴바 기반)

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

// 도서관 관리 프로그램의 메인 프레임 클래스
// 도서 관리, 대여 관리, 연체 관리, 관리자 관리, 도서 현황 등 모든 주요 기능을 통합 제공
public class LibraryManagementApp extends JFrame implements ActionListener {
	// DAO 및 DTO 선언 (DB 연동용)
	static rentalDAO rentalDAO = new rentalDAO(); // 대여 관련 DAO (연체 등)
	RentalDTO rentalDTO = new RentalDTO(); // 대여 DTO
	static BookDAO bookDAO = new BookDAO(); // 도서 관련 DAO
	BookDTO bookDTO = new BookDTO(); // 도서 DTO
	ImageIcon imageIcon = new ImageIcon("img/book.png"); // 이미지 관리 클래스

	// 화면 전환용 CardLayout 및 메인 패널
	CardLayout cardLayout = new CardLayout(); // 여러 화면 전환용 레이아웃
	JPanel mainPanel = new JPanel(cardLayout); // 메인 패널(카드 레이아웃 적용)

	// 도서/대여/연체/현황 등 상태 정보를 담을 라벨들
	String[] labels = { "전체 도서 :", "전체 대여 도서 :", "대여 가능 도서 :", "연체 도서 :" };
	JLabel[] statusLabels = new JLabel[labels.length]; // 도서현황 패널에서 동적으로 값 표시

	// 각 화면별 패널 선언
	JPanel panelMainMenu = new JPanel(new FlowLayout()); // 메인 메뉴 패널
	JPanel panelBookForm = new JPanel(new BorderLayout()); // 도서 관리 패널
	JPanel panelOverdueForm = new JPanel(new BorderLayout()); // 연체 리스트 패널
	JPanel panelRentalManagement = new RentalManagementPanel(); // 대여 관리 패널
	JPanel panelManager = new _ManagerManagement(); // 관리자 관리 패널
	JPanel panelMonthlyStatus = new JPanel(); // 도서현황 패널

	// 메인 메뉴 이동 버튼들
	JButton btnToBook = new JButton("도서 관리");
	JButton btnToOverdue = new JButton("연체 리스트");
	JButton btnToManager = new JButton("매니저 관리");
	JButton btnToRental = new JButton("대여 관리");
	JButton btnToStatus = new JButton("도서현황");

	// 도서 관리(BookForm) 관련 컴포넌트
	JComboBox<String> cbFilter = new JComboBox<String>(); // 장르/필터 콤보박스
	static String[] book_list = { "코드번호", "도서명", "저자", "출판사", "장르", "대출여부" };
	static DefaultTableModel bookModel = new DefaultTableModel(book_list, 0); // 도서 테이블 모델
	JTextField tfSearch = new JTextField(20); // 검색어 입력 필드
	JButton btnSearch = new JButton("검색"); // 검색 버튼
	JButton btnAdd = new JButton("추가"); // 도서 추가 버튼
	JButton btnDelete = new JButton("삭제"); // 도서 삭제 버튼
	JButton btnUpdate = new JButton("수정"); // 도서 수정 버튼

	JTextField tfCode = new JTextField(10); // 도서코드 입력/표시 필드
	JTextField tfTitle = new JTextField(10); // 도서명 입력/표시 필드
	JTextField tfAuthor = new JTextField(10); // 저자 입력/표시 필드
	JTextField tfPublisher = new JTextField(10); // 출판사 입력/표시 필드
	JComboBox<String> cbGenre = new JComboBox<String>(new String[] {}); // 장르 콤보박스
	JTextField tfBorrow = new JTextField(10); // 대여여부 표시 필드
	JTable bookTable; // 도서 테이블
	DefaultTableModel bookModel2 = null; // (미사용)

	// 연체(Overdue) 관련 컴포넌트
	JLabel labelCount = new JLabel("연체 중"); // 연체 안내 라벨
	JButton btnSendMsg = new JButton("문자 보내기"); // 개별 문자 버튼
	JButton btnSendAll = new JButton("모두 보내기"); // 전체 문자 버튼
	JTable overdueTable; // 연체 테이블
	static DefaultTableModel overdueModel; // 연체 테이블 모델

	// 생성자: 전체 UI/이벤트/데이터 초기화 및 화면 표시
	public LibraryManagementApp() {
		setTitle("도서관리 프로그램"); // 창 제목
		setSize(1000, 600); // 창 크기
		setDefaultCloseOperation(EXIT_ON_CLOSE); // 닫기 동작
		setLocationRelativeTo(null); // 화면 중앙 배치
		//
		setIconImage(imageIcon.getImage());	// 아이콘 설정
		Dimension dimension = getSize();				// 프레임 크기		
		int x = (int)(dimension.getHeight()/2 -dimension.getHeight()/2);
		setLocation(x);
		//
		initMenuBar(); // 메뉴바(상단 화면 전환) 초기화
		initMainMenu(); // 메인 메뉴 패널 초기화
		loadGenres(); // 장르 목록 로딩
		initBookForm(); // 도서 관리 패널 초기화
		initOverdueForm(); // 연체 리스트 패널 초기화
		initMonthlyStatusPanel(); // 도서현황 패널 초기화
		loadBookList(); // 도서 목록 로딩
		initPanels(); // 각 화면 패널을 카드에 등록
		start(); // 기타 이벤트 연결

		add(mainPanel); // 메인 패널 추가
		setVisible(true); // 창 보이기
	}
	private void setLocation(int x) {
		
	}
		

	// 기타 이벤트 연결 (예: 콤보박스 장르 선택 시 도서 목록 필터링)
	private void start() {
		// 장르 콤보박스 선택 이벤트 처리
		cbFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedGenre = (String) cbFilter.getSelectedItem();
				if (selectedGenre.equals("전체")) {
					loadBookList(); // 전체 목록 로딩
				} else {
					loadBookListByGenre(selectedGenre); // 장르별 목록 로딩
				}
			}
		});
	}

	// DB에서 장르 목록을 불러와 콤보박스에 세팅
	private void loadGenres() {
		cbFilter.removeAllItems(); // 필터 콤보박스 초기화
		cbFilter.addItem("전체"); // 전체 항목 추가
		cbGenre.removeAllItems(); // 장르 콤보박스 초기화

		List<String> genres = bookDAO.getAllGenres(); // DB에서 장르 목록 조회
		for (String genre : genres) {
			cbFilter.addItem(genre);
			cbGenre.addItem(genre);
		}
	}

	// 전체 도서 목록을 DB에서 불러와 테이블에 표시
	public static void loadBookList() {
		List<BookDTO> book = bookDAO.searchAll(); // 전체 도서 목록 조회

		// 테이블 초기화
		bookModel.setRowCount(0);

		for (BookDTO b : book) {

			String checkStatus = (b.getBOOK_CHECK() == 1) ? "대여 가능" : "대여 불가능";

			Object[] row = { b.getBOOK_SEQ(), b.getBOOK_NAME(), b.getBOOK_AUTHOR(), b.getBOOK_PUBLISHER(),
					b.getBOOK_GENRE(), checkStatus };
			bookModel.addRow(row);
		}
	}

	// 특정 장르의 도서 목록만 테이블에 표시
	private void loadBookListByGenre(String genre) {
		List<BookDTO> bookList = bookDAO.searchByGenre(genre);

		bookModel.setRowCount(0);

		for (BookDTO b : bookList) {

			String checkStatus = (b.getBOOK_CHECK() == 1) ? "대여 가능" : "대여 불가능";

			Object[] row = { b.getBOOK_SEQ(), b.getBOOK_NAME(), b.getBOOK_AUTHOR(), b.getBOOK_PUBLISHER(),
					b.getBOOK_GENRE(), checkStatus };
			bookModel.addRow(row);
		}
	}

	// 도서 리스트를 받아 테이블에 표시 (검색 등에서 사용)
	private void loadBookTable(List<BookDTO> bookList) {
		bookModel.setRowCount(0); // 테이블 초기화

		for (BookDTO b : bookList) {
			String checkStatus = (b.getBOOK_CHECK() == 1) ? "대여 가능" : "대여 불가능";
			Object[] row = { b.getBOOK_SEQ(), b.getBOOK_NAME(), b.getBOOK_AUTHOR(), b.getBOOK_PUBLISHER(),
					b.getBOOK_GENRE(), checkStatus };
			bookModel.addRow(row);
		}
	}

	// 상단 메뉴바(화면 전환 메뉴) 초기화
	private void initMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("화면 전환");

		JMenuItem menuMain = new JMenuItem("메인 메뉴");
		JMenuItem menuBook = new JMenuItem("도서 관리");
		JMenuItem menuRental = new JMenuItem("대여 관리");
		JMenuItem menuOverdue = new JMenuItem("연체 리스트");
		JMenuItem menuStatus = new JMenuItem("도서현황");
		JMenuItem menuManager = new JMenuItem("매니저 관리");

		// 각 메뉴 클릭 시 해당 화면으로 전환
		menuMain.addActionListener(e -> cardLayout.show(mainPanel, "MAIN"));
		menuBook.addActionListener(e -> cardLayout.show(mainPanel, "BOOK"));
		menuOverdue.addActionListener(e -> cardLayout.show(mainPanel, "OVERDUE"));
		menuStatus.addActionListener(e -> cardLayout.show(mainPanel, "STATUS"));
		menuRental.addActionListener(e -> cardLayout.show(mainPanel, "RENTAL"));
		menuManager.addActionListener(e -> cardLayout.show(mainPanel, "MANAGER"));

		menu.add(menuMain);
		menu.add(menuBook);
		menu.add(menuOverdue);
		menu.add(menuStatus);
		menu.add(menuRental);
		menu.add(menuManager);

		menuBar.add(menu);
		setJMenuBar(menuBar);
	}

	// 메인 메뉴 패널(첫 화면) 초기화
	private void initMainMenu() {
		panelMainMenu.add(new JLabel("📚 도서관리 시스템에 오신 것을 환영합니다!"));
		panelMainMenu.add(btnToBook);
		panelMainMenu.add(btnToOverdue);
		panelMainMenu.add(btnToRental);
		panelMainMenu.add(btnToManager);
		panelMainMenu.add(btnToStatus);

		// 각 버튼 클릭 시 해당 화면으로 전환
		btnToBook.addActionListener(e -> cardLayout.show(mainPanel, "BOOK"));
		btnToRental.addActionListener(e -> cardLayout.show(mainPanel, "RENTAL"));
		btnToOverdue.addActionListener(e -> cardLayout.show(mainPanel, "OVERDUE"));
		btnToManager.addActionListener(e -> cardLayout.show(mainPanel, "MANAGER"));
		btnToStatus.addActionListener(e -> cardLayout.show(mainPanel, "STATUS"));
	}

	// 도서 관리(추가/수정/삭제/검색) 패널 초기화
	private void initBookForm() {

		JPanel panelTop = new JPanel(new BorderLayout());
		JLabel lblTitle = new JLabel("📚 도서 관리", SwingConstants.CENTER);
		lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 22));
		panelTop.add(lblTitle, BorderLayout.NORTH);

		JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panelSearch.add(cbFilter); // 장르/필터 콤보박스
		panelSearch.add(tfSearch); // 검색어 입력
		panelSearch.add(btnSearch); // 검색 버튼
		panelSearch.add(btnAdd); // 추가 버튼
		panelSearch.add(btnDelete); // 삭제 버튼
		panelTop.add(panelSearch, BorderLayout.SOUTH);

		panelBookForm.add(panelTop, BorderLayout.NORTH);

		String[] cols = { "도서코드", "도서명", "저자", "출판사", "장르", "대여여부" };
		bookModel = new DefaultTableModel(cols, 0);
		bookTable = new JTable(bookModel);
		panelBookForm.add(new JScrollPane(bookTable), BorderLayout.CENTER);

		JPanel panelForm = new JPanel(new GridLayout(2, 6, 10, 10));
		panelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
		panelForm.add(new JLabel("도서코드"));
		panelForm.add(new JLabel("출판사"));
		panelForm.add(new JLabel("장르"));
		panelForm.add(new JLabel("도서명"));
		panelForm.add(new JLabel("저자"));
		panelForm.add(new JLabel("대여여부"));
		panelForm.add(tfCode);
		panelForm.add(tfPublisher);
		panelForm.add(cbGenre);
		panelForm.add(tfTitle);
		panelForm.add(tfAuthor);
		panelForm.add(tfBorrow);
		tfCode.setEnabled(false); // 도서코드(자동생성) 비활성화
		tfBorrow.setEnabled(false); // 대여여부(자동) 비활성화

		JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panelBottom.add(btnUpdate); // 수정 버튼

		JPanel bottomWrapper = new JPanel(new BorderLayout());
		bottomWrapper.add(panelForm, BorderLayout.CENTER);
		bottomWrapper.add(panelBottom, BorderLayout.SOUTH);

		panelBookForm.add(bottomWrapper, BorderLayout.SOUTH);

		// 도서 추가 버튼 클릭 시 AddBookForm 팝업
		btnAdd.addActionListener(e -> new AddBookForm((code, title, author, publisher, genre) -> {
			bookModel.addRow(new Object[] { String.valueOf(code), title, author, publisher, genre, "대여가능" });
		}));
		btnDelete.addActionListener(this); // 삭제 버튼 이벤트
		btnUpdate.addActionListener(this); // 수정 버튼 이벤트
		btnSearch.addActionListener(this); // 검색 버튼 이벤트

		// 테이블 행 선택 시 상세정보 폼에 값 표시
		bookTable.getSelectionModel().addListSelectionListener(e -> {
			int selectedRow = bookTable.getSelectedRow();
			if (selectedRow != -1) {
				tfCode.setText(bookModel.getValueAt(selectedRow, 0).toString());
				tfTitle.setText(bookModel.getValueAt(selectedRow, 1).toString());
				tfAuthor.setText(bookModel.getValueAt(selectedRow, 2).toString());
				tfPublisher.setText(bookModel.getValueAt(selectedRow, 3).toString());
				cbGenre.setSelectedItem(bookModel.getValueAt(selectedRow, 4).toString());
				tfBorrow.setText(bookModel.getValueAt(selectedRow, 5).toString());
			}
		});
		
	}
	
	// 연체 리스트(연체자 목록, 문자 발송) 패널 초기화
	private void initOverdueForm() {
		JPanel panelTop = new JPanel(new BorderLayout());
		JLabel lblTitle = new JLabel("📌 연체 리스트 관리", SwingConstants.CENTER);
		lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 22));
		panelTop.add(lblTitle, BorderLayout.CENTER);
		labelCount.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 0));
		panelTop.add(labelCount, BorderLayout.WEST);
		panelOverdueForm.add(panelTop, BorderLayout.NORTH);

		String[] cols = { "ID", "이름", "연락처", "도서 코드", "도서 이름", "연체일" };
		overdueModel = new DefaultTableModel(cols, 0);
		overdueTable = new JTable(overdueModel);
		panelOverdueForm.add(new JScrollPane(overdueTable), BorderLayout.CENTER);

		JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panelBottom.add(btnSendMsg); // 개별 문자 버튼
		panelBottom.add(btnSendAll); // 전체 문자 버튼
		panelOverdueForm.add(panelBottom, BorderLayout.SOUTH);

		overmemberlist(); // 연체자 목록 로딩

		btnSendMsg.addActionListener(this); // 문자 보내기 버튼 이벤트
		btnSendAll.addActionListener(this); // 모두 보내기 버튼 이벤트

	}

	// 연체자 목록을 DB에서 불러와 테이블에 표시
	public static void overmemberlist() {
		List<RentalDTO> rental = rentalDAO.rentalOver(); // 연체자만 가져오는 DAO 메서드

		// 테이블 초기화
		overdueModel.setRowCount(0);

		for (RentalDTO r : rental) {
			Object[] row = { r.getMEMBER_ID(), // 회원 ID
					r.getMEMBER_NAME(), // 회원 이름
					r.getMEMBER_TEL(), // 회원 전화번호
					r.getBOOK_SEQ(), // 도서 코드
					r.getBOOK_NAME(), // 도서 이름
					r.getRENTAL_OVERDUE() + "일" // 연체일수 표시
			};

			overdueModel.addRow(row);
		}
	}

	// 도서현황(전체/대여/가능/연체) 패널 초기화
	private void initMonthlyStatusPanel() {

		panelMonthlyStatus.setLayout(new BoxLayout(panelMonthlyStatus, BoxLayout.Y_AXIS));
		panelMonthlyStatus.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel lblTitle = new JLabel("📅 도서현황");
		lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 26));
		topPanel.add(lblTitle);

		panelMonthlyStatus.add(topPanel);

		JPanel statusPanel = new JPanel();
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
		panelMonthlyStatus.add(statusPanel);

		for (int i = 0; i < labels.length; i++) {
			JPanel row = new JPanel(new FlowLayout());
			JLabel label = new JLabel(labels[i]);
			label.setFont(new Font("맑은 고딕", Font.BOLD, 20));

			statusLabels[i] = new JLabel(); // 동적으로 값 변경
			statusLabels[i].setFont(new Font("맑은 고딕", Font.BOLD, 20));

			row.add(label);
			row.add(statusLabels[i]);
			statusPanel.add(row);
		}

		// 각 현황 값 DB에서 조회하여 표시
		statusLabels[0].setText(String.valueOf(bookDAO.countAllBooks()) + "권");
		statusLabels[1].setText(String.valueOf(bookDAO.countBorrowedBooks()) + "권");
		statusLabels[2].setText(String.valueOf(bookDAO.countAvailableBooks()) + "권");
		statusLabels[3].setText(String.valueOf(rentalDAO.countOverdueBooks()) + "권");

	}

	// 도서 상세정보 입력폼 초기화
	private void clearBookForm() {
		tfCode.setText("");
		tfTitle.setText("");
		tfAuthor.setText("");
		tfPublisher.setText("");
		cbGenre.setSelectedIndex(0);
		tfBorrow.setText("");
	}

	// 버튼/이벤트 처리 (도서 삭제/수정, 연체 문자, 검색 등)
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();

		if (src == btnDelete) {
			int row = bookTable.getSelectedRow();
			if (row == -1) {
				JOptionPane.showMessageDialog(this, "삭제할 행을 선택해주세요.", "삭제", JOptionPane.WARNING_MESSAGE);
				return;
			}
			int confirm = JOptionPane.showConfirmDialog(this, "삭제하시겠습니까?", "삭제", JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {
				bookModel.removeRow(row);
				int bookSeq = Integer.parseInt(tfCode.getText());

				bookDAO.deleteBook(bookSeq);
				clearBookForm();
				JOptionPane.showMessageDialog(this, "삭제되었습니다.", "삭제", JOptionPane.INFORMATION_MESSAGE);
			}

		} else if (src == btnUpdate) {
			int row = bookTable.getSelectedRow();
			if (row == -1) {
				JOptionPane.showMessageDialog(this, "수정할 행을 선택해주세요.", "수정", JOptionPane.WARNING_MESSAGE);
				return;
			}
			int confirm = JOptionPane.showConfirmDialog(this, "수정하시겠습니까?", "수정", JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {
				bookModel.setValueAt(tfTitle.getText(), row, 1);
				bookModel.setValueAt(tfAuthor.getText(), row, 2);
				bookModel.setValueAt(tfPublisher.getText(), row, 3);
				bookModel.setValueAt(cbGenre.getSelectedItem(), row, 4);

				bookDTO.setBOOK_SEQ(Integer.parseInt(tfCode.getText()));
				bookDTO.setBOOK_NAME(tfTitle.getText());
				bookDTO.setBOOK_AUTHOR(tfAuthor.getText());
				bookDTO.setBOOK_PUBLISHER(tfPublisher.getText());
				bookDTO.setBOOK_GENRE(cbGenre.getSelectedItem().toString());

				String status = (String) bookModel.getValueAt(row, 5);
				int check = status.equals("대여 가능") ? 1 : 2;
				bookDTO.setBOOK_CHECK(check);

				bookDAO.updateBook(bookDTO);

				JOptionPane.showMessageDialog(this, "수정되었습니다.", "수정", JOptionPane.INFORMATION_MESSAGE);
			}

		} else if (src == btnSendMsg) {
			int row = overdueTable.getSelectedRow();
			if (row == -1) {
				JOptionPane.showMessageDialog(this, "문자를 보낼 대상을 선택해주세요.", "문자 보내기", JOptionPane.WARNING_MESSAGE);
				return;
			}
			int confirm = JOptionPane.showConfirmDialog(this, "책을 반납해 주세요. 문자를 보내시겠습니까?", "문자 보내기",
					JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {
				JOptionPane.showMessageDialog(this, "문자를 보냈습니다.", "안내", JOptionPane.INFORMATION_MESSAGE);
			}

		} else if (src == btnSendAll) {
			int confirm = JOptionPane.showConfirmDialog(this, "책을 반납해 주세요. 단체 문자를 보내시겠습니까?", "단체 문자 보내기",
					JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {
				JOptionPane.showMessageDialog(this, "모든 연체자에게 문자를 보냈습니다.", "안내", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (src == btnSearch) {
			String keyword = tfSearch.getText().trim();
			String selectedGenre = (String) cbFilter.getSelectedItem();

			// 모든 도서 검색 (장르가 전체일 경우)
			if (selectedGenre.equals("전체")) {
				List<BookDTO> result = bookDAO.searchByKeyword(keyword);
				loadBookTable(result);
			} else {
				// 장르 조건 포함 검색
				List<BookDTO> fullList = bookDAO.searchByKeyword(keyword);
				List<BookDTO> filteredList = new ArrayList<>();
				for (BookDTO b : fullList) {
					if (b.getBOOK_GENRE().equals(selectedGenre)) {
						filteredList.add(b);
					}
				}
				loadBookTable(filteredList);
			}
		}
	}

	// 각 화면별 패널을 카드 레이아웃에 등록
	private void initPanels() {
		mainPanel.add(panelMainMenu, "MAIN"); // 메인 메뉴
		mainPanel.add(panelBookForm, "BOOK"); // 도서 관리
		mainPanel.add(panelRentalManagement, "RENTAL"); // 대여 관리
		mainPanel.add(panelOverdueForm, "OVERDUE"); // 연체 리스트
		mainPanel.add(panelManager, "MANAGER"); // 관리자 관리
		mainPanel.add(panelMonthlyStatus, "STATUS"); // 도서현황
	}
}
