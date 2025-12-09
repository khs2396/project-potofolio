import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

// 로그인 화면을 담당하는 클래스 (JFrame 상속)
public class _Login extends JFrame implements ActionListener, MouseListener {
	ImageIcon imageIcon = new ImageIcon("img/book.png"); // 이미지 관리 클래스
	MemberDTO memberDTO = new MemberDTO(); // 회원 정보 DTO
	MemberDAO memberDAO = new MemberDAO(); // 회원 DB 연동 DAO
	ManagerDTO managerDTO = new ManagerDTO(); // 관리자 정보 DTO
	ManagerDAO managerDAO = new ManagerDAO(); // 관리자 DB 연동 DAO
	Container container = getContentPane(); // JFrame의 컨테이너

	// 라벨 선언
	JLabel labelLogin = new JLabel("로그인", JLabel.CENTER); // 상단 타이틀 라벨
	JLabel labelID = new JLabel("ID"); // 아이디 라벨
	JLabel labelPW = new JLabel("PW"); // 비밀번호 라벨

	// 텍스트필드 선언
	JTextField textFieldID = new JTextField(20); // 아이디 입력 필드
	JTextField textFieldManagerLogin = new JTextField(""); // 관리자 코드 입력 필드

	// 비밀번호 입력 필드
	JPasswordField passwordField = new JPasswordField(20); // 비밀번호 입력 필드

	// 버튼 선언
	JButton buttonLogin = new JButton("로그인"); // 로그인 버튼
	JButton buttonMembership = new JButton("회원가입"); // 회원가입 버튼
	JButton buttonFindID = new JButton("ID 찾기"); // 아이디 찾기 버튼
	JButton buttonFindPW = new JButton("PW 찾기"); // 비밀번호 찾기 버튼
	JButton buttonManagerLogin = new JButton("관리자 로그인"); // 관리자 로그인 버튼

	// 라디오버튼 (관리자 로그인 전환용)
	JRadioButton buttonManagerLoginRadio = new JRadioButton("관리자 로그인", false); // 관리자 로그인 라디오버튼

	// 패널 선언 (UI 배치용)
	JPanel panelManager = new JPanel();
	JPanel panelBind1 = new JPanel();
	JPanel panelBind2 = new JPanel();
	JPanel panelButton = new JPanel();
	JPanel panelID = new JPanel();
	JPanel panelPW = new JPanel();
	JPanel panelLogin = new JPanel();

	// 카드 레이아웃 (관리자 로그인 패널, 빈 패널 전환용)
	CardLayout cardLayout = new CardLayout();

	// 생성자: 로그인 창 초기화 및 UI 세팅
	public _Login() {
		setTitle("로그인"); // 창 제목
		setIconImage(imageIcon.getImage());	// 아이콘 설정
		setSize(300, 500); // 창 크기
		setLocation(500, 200); // 창 위치
		init(); // UI 구성 메서드 호출
		start(); // 이벤트 연결 메서드 호출
		setVisible(true); // 창 보이기
		setResizable(false); // 창 크기 고정

	}

	// UI 레이아웃 및 컴포넌트 배치 담당 메서드
	private void init() {
		container.setLayout(new GridLayout(7, 1, 0, 5)); // 전체 레이아웃 7행 1열
		container.add(labelLogin); // 상단 타이틀
		labelLogin.setFont(new Font("듣음체", Font.BOLD, 25)); // 타이틀 폰트

		container.add(panelID); // 아이디 입력 패널
		container.add(panelPW); // 비밀번호 입력 패널
		container.add(panelLogin); // 로그인 버튼 패널
		container.add(buttonManagerLoginRadio); // 관리자 로그인 라디오버튼
		container.add(panelBind1); // 카드 레이아웃 패널
		container.add(panelButton); // 하단 버튼 패널

		// panelID
		panelID.setLayout(new FlowLayout());
		panelID.add(labelID);
		labelID.setFont(new Font("듣음체", Font.BOLD, 15));
		panelID.add(textFieldID);

		// panelPW
		panelPW.setLayout(new FlowLayout());
		panelPW.add(labelPW);
		labelPW.setFont(new Font("듣음체", Font.BOLD, 15));
		panelPW.add(passwordField);

		// panelLogin
		panelLogin.setLayout(new FlowLayout());
		panelLogin.add(buttonLogin);
		buttonLogin.setPreferredSize(new Dimension(225, 35));

		// panelBind1 (카드 레이아웃)
		panelBind1.setLayout(cardLayout);
		panelBind1.add(panelBind2, "bind"); // 일반 로그인용 빈 패널
		panelBind1.add(panelManager, "manager"); // 관리자 로그인 패널

		// panelManager (관리자 로그인 입력부)
		panelManager.setLayout(new FlowLayout());
		panelManager.add(textFieldManagerLogin);
		panelManager.add(buttonManagerLogin);

		// panelButton (하단 버튼들)
		panelButton.setLayout(new FlowLayout());
		panelButton.add(buttonMembership);
		panelButton.add(buttonFindID);
		panelButton.add(buttonFindPW);

	}

	// 이벤트 리스너 등록 및 기본 설정 담당 메서드
	private void start() {
		setDefaultCloseOperation(EXIT_ON_CLOSE); // 창 닫기 동작 설정
		buttonManagerLoginRadio.addActionListener(this); // 관리자 로그인 라디오버튼 이벤트
		textFieldManagerLogin.addMouseListener(this); // 관리자 코드 입력 필드 클릭 이벤트
		buttonMembership.addActionListener(this); // 회원가입 버튼 이벤트
		buttonFindID.addActionListener(this); // 아이디 찾기 버튼 이벤트
		buttonFindPW.addActionListener(this); // 비밀번호 찾기 버튼 이벤트
		buttonLogin.addActionListener(this); // 로그인 버튼 이벤트
		buttonManagerLogin.addActionListener(this); // 관리자 로그인 버튼 이벤트
	}

	// 버튼 및 라디오버튼 클릭 이벤트 처리
	@Override
	public void actionPerformed(ActionEvent e) {
		// 라디오버튼 이벤트 처리 (관리자 로그인 전환)
		if (e.getSource() == buttonManagerLoginRadio) {
			cardLayout.show(panelBind1, "manager"); // 관리자 로그인 패널로 전환
			textFieldManagerLogin.setText("관리자 번호를 입력하세요");
		}
		if (!buttonManagerLoginRadio.isSelected()) {
			cardLayout.show(panelBind1, "bind"); // 일반 로그인 패널로 전환
		}
		// 버튼 이벤트 처리
		// 회원가입 전환
		if (e.getSource() == buttonMembership) {
			new _Signup(); // 회원가입 창 오픈
		}
		if (e.getSource() == buttonFindID) {
			new _FindID(); // 아이디 찾기 창 오픈
		}
		if (e.getSource() == buttonFindPW) {
			new _FindPW(); // 비밀번호 찾기 창 오픈
		}
		if (e.getSource() == buttonLogin) {
			String id = textFieldID.getText(); // 입력된 아이디
			String pw = passwordField.getText(); // 입력된 비밀번호
			boolean result = memberDAO.loginCheck(id, pw); // DB에서 로그인 체크
			if (result) {
					new Member(); // 회원 메인 화면 이동
					dispose(); // 로그인 창 닫기
			} else {
				JOptionPane.showMessageDialog(this, "로그인 실패"); // 실패 안내
			}
		}
		if (e.getSource() == buttonManagerLogin) {
			String managerCode = textFieldManagerLogin.getText(); // 입력된 관리자 코드
			int code = Integer.parseInt(managerCode); // 정수 변환
			ManagerDTO result = managerDAO.selectByManagerCode(code); // DB에서 관리자 코드로 조회
			if (result != null) {
				new LibraryManagementApp(); // 관리자 메인 화면 이동
				dispose(); // 로그인 창 닫기
			} else {
				JOptionPane.showMessageDialog(this, "없는 번호입니다"); // 실패 안내
			}
		}
	}

	// 마우스 클릭 이벤트 처리 (입력 필드 클릭 시 예시값 초기화)
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == textFieldManagerLogin) {
			textFieldManagerLogin.setText(""); // 관리자 코드 입력 필드 클릭 시 초기화
		}

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// 사용하지 않음
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// 사용하지 않음
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// 사용하지 않음
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// 사용하지 않음
	}

}
