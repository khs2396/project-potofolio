import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

// 도서 추가 다이얼로그 창 클래스
// 사용자가 도서 정보를 입력하면 DB에 저장하고, 추가 이벤트를 발생시킴
public class AddBookForm extends JDialog {
	// 도서 DB 처리 객체 (DB 연동용)
	BookDAO bookDAO = new BookDAO();
	// 도서 데이터 저장 객체 (입력값 임시 저장)
	BookDTO bookDTO = new BookDTO();
	ImageIcon imageIcon = new ImageIcon("img/book.png");  // 이미지 관리 클래스

	// 도서 코드 입력 필드 (자동생성, 수정불가)
	private JTextField tfCode = new JTextField(15);
	// 도서명 입력 필드
	private JTextField tfTitle = new JTextField(15);
	// 저자 입력 필드
	private JTextField tfAuthor = new JTextField(15);
	// 출판사 입력 필드
	private JTextField tfPublisher = new JTextField(15);
	// 장르 선택 콤보박스
	private JComboBox<String> cbGenre = new JComboBox<String>();

	// 도서 추가 완료 시 호출되는 콜백 인터페이스
	public interface BookAddListener {
		void onBookAdded(String code, String title, String author, String publisher, String genre);
	}

	// 생성자: 도서 추가 폼 UI 구성 및 이벤트 연결
	public AddBookForm(BookAddListener listener) {
		setTitle("도서 추가");
		setIconImage(imageIcon.getImage());	// 아이콘 설정
		setSize(400, 300);
		setLocationRelativeTo(null);
		setModal(true);
		setLayout(new BorderLayout());

		// 입력 폼 패널 (도서 정보 입력)
		JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
		formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

		formPanel.add(new JLabel("도서 코드"));
		formPanel.add(tfCode);
		tfCode.setEnabled(false); // 코드 직접 입력 불가
		tfCode.setText(bookDAO.nextSeq()); // 자동생성 코드 표시
		formPanel.add(new JLabel("도서명"));
		formPanel.add(tfTitle);
		formPanel.add(new JLabel("저자"));
		formPanel.add(tfAuthor);
		formPanel.add(new JLabel("출판사"));
		formPanel.add(tfPublisher);
		formPanel.add(new JLabel("장르"));
		formPanel.add(cbGenre);

		// 버튼 패널 (확인/취소)
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton btnConfirm = new JButton("확인");
		JButton btnCancel = new JButton("취소");
		btnPanel.add(btnCancel);
		btnPanel.add(btnConfirm);

		add(formPanel, BorderLayout.CENTER);
		add(btnPanel, BorderLayout.SOUTH);

		// 확인 버튼 클릭 시 도서 정보 입력값 검증 및 DB 저장
		btnConfirm.addActionListener(e -> {
			String code = tfCode.getText().trim();
			String title = tfTitle.getText().trim();
			String author = tfAuthor.getText().trim();
			String publisher = tfPublisher.getText().trim();
			String genre = cbGenre.getSelectedItem().toString();

			// 입력값 누락 체크
			if (code.isEmpty() || title.isEmpty() || author.isEmpty() || publisher.isEmpty()) {
				JOptionPane.showMessageDialog(this, "모든 항목을 입력해주세요.", "오류", JOptionPane.WARNING_MESSAGE);
				return;
			}
			// DTO에 값 세팅
			bookDTO.setBOOK_SEQ(Integer.parseInt(tfCode.getText()));
			bookDTO.setBOOK_NAME(tfTitle.getText());
			bookDTO.setBOOK_AUTHOR(tfAuthor.getText());
			bookDTO.setBOOK_PUBLISHER(tfPublisher.getText());
			bookDTO.setBOOK_GENRE(cbGenre.getSelectedItem().toString());
			bookDTO.setBOOK_CHECK(1); // 신규 도서는 대여 가능 상태로 등록

			// DB에 도서 정보 저장
			int result = bookDAO.insert(bookDTO);
			if (result > 0) {
				JOptionPane.showMessageDialog(this, "등록 성공.");
			} else {
				JOptionPane.showMessageDialog(this, "등록 실패.");
			}
			// 콜백 호출(상위 화면에 알림)
			listener.onBookAdded(code, title, author, publisher, genre);
			dispose(); // 창 닫기
		});

		// 취소 버튼 클릭 시 창 닫기
		btnCancel.addActionListener(e -> dispose());
		// 장르 목록 로딩
		loadGenres();
		setVisible(true);
	}

	// DB에서 장르 목록을 불러와 콤보박스에 추가
	private void loadGenres() {
		cbGenre.removeAllItems();

		List<String> genres = bookDAO.getAllGenres();
		for (String genre : genres) {
			cbGenre.addItem(genre);
		}
	}

}
