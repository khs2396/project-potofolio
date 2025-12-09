import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

// 관리자 회원가입 화면을 담당하는 클래스 (JFrame 상속)
public class _ManagerSingup extends JFrame implements ActionListener, MouseListener {
	Random random = new Random(); // 랜덤 객체(코드 생성용)
	Container container = getContentPane(); // JFrame의 컨테이너
	
	// 2. 랜덤 코드 생성 (중복 허용)
    int code = (int)(Math.random() * 90000) + 10000; // 5자리 랜덤 관리자 코드 생성

	// 라벨 선언
	JLabel labelManagerSingup = new JLabel("매니저 회원가입", JLabel.CENTER); // 상단 타이틀 라벨
	JLabel labelManagerCode = new JLabel("매니저 코드 : " + code); // 관리자 코드 표시 라벨
	JLabel labelManagerName = new JLabel("이름"); // 이름 라벨
	JLabel labelManagerTel = new JLabel("휴대폰 번호"); // 휴대폰 번호 라벨
	JLabel labelTelCaution = new JLabel("휴대폰 번호는 - 없이 입력해주세요", JLabel.CENTER); // 안내 라벨

	// 텍스트 필드 선언
	JTextField textFieldName = new JTextField("이름", 15); // 이름 입력 필드
	JTextField textFieldTel = new JTextField("휴대폰 번호", 15); // 휴대폰 번호 입력 필드

	// 버튼 선언
	JButton buttonSignup = new JButton("가입"); // 가입 버튼
	JButton buttonCancle = new JButton("취소"); // 취소 버튼

	// 패널 선언 (UI 배치용)
	JPanel panelCenter = new JPanel();
	JPanel panelCode = new JPanel();
	JPanel panelCodeLabel = new JPanel();
	JPanel panelCodeNumber = new JPanel(); // 코드 들어갈 패널
	JPanel panelName = new JPanel();
	JPanel panelTel = new JPanel();
	JPanel panelLabel1 = new JPanel();
	JPanel panelLabel2 = new JPanel();
	JPanel panelButton = new JPanel();

	// 생성자: 관리자 회원가입 창 초기화 및 UI 세팅
	public _ManagerSingup(ManagerDTO managerDTO) {
		setTitle("매니저 회원가입"); // 창 제목
		setSize(300, 300); // 창 크기
		setLocation(500, 200); // 창 위치
		init(); // UI 구성 메서드 호출
		start(); // 이벤트 연결 메서드 호출
		setVisible(true); // 창 보이기
	}

	// UI 레이아웃 및 컴포넌트 배치 담당 메서드
	private void init() {
		container.setLayout(new BorderLayout()); // 전체 레이아웃 BorderLayout
		container.add("North", labelManagerSingup); // 상단 타이틀
		labelManagerSingup.setFont(new Font("듣음체", Font.BOLD, 25)); // 타이틀 폰트
		container.add("Center", panelCenter); // 중앙 입력부
		container.add("South", panelLabel1); // 하단 안내/버튼부

		// 중앙 입력부 패널 (코드, 이름, 전화번호)
		panelCenter.setLayout(new GridLayout(3, 1));
		panelCenter.add(panelCode); // 관리자 코드
		panelCenter.add(panelName); // 이름
		panelCenter.add(panelTel); // 전화번호

		// 관리자 코드 패널
		panelCode.setLayout(new FlowLayout());
		panelCode.add(panelCodeLabel);
		panelCode.add(panelCodeNumber);
		panelCodeLabel.setLayout(new FlowLayout());
		panelCodeLabel.add(labelManagerCode);
		labelManagerCode.setFont(new Font("듣음체", Font.BOLD, 15));

		// 이름 입력 패널
		panelName.setLayout(new FlowLayout());
		panelName.add(textFieldName);

		// 전화번호 입력 패널
		panelTel.setLayout(new FlowLayout());
		panelTel.add(textFieldTel);

		// 하단 안내/버튼 패널
		panelLabel1.setLayout(new GridLayout(2, 1));
		panelLabel1.add(panelLabel2); // 안내
		panelLabel1.add(panelButton); // 버튼
		panelLabel2.setLayout(new FlowLayout());
		panelLabel2.add(labelTelCaution);
		panelButton.setLayout(new FlowLayout());
		panelButton.add(buttonSignup);
		panelButton.add(buttonCancle);
	}

	// 이벤트 리스너 등록 및 기본 설정 담당 메서드
	private void start() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE); // 창 닫기 동작 설정
		textFieldName.addMouseListener(this); // 이름 입력 필드 클릭 이벤트
		textFieldTel.addMouseListener(this); // 전화번호 입력 필드 클릭 이벤트
		buttonCancle.addActionListener(this); // 취소 버튼 이벤트
		buttonSignup.addActionListener(this); // 가입 버튼 이벤트
	}

	// 버튼 클릭 이벤트 처리
	@Override
	public void actionPerformed(ActionEvent e) {
		// 취소버튼 클릭 시 창 닫기
		if (e.getSource() == buttonCancle) {
			dispose();
		}
		// 가입 버튼 클릭 시
		if (e.getSource() == buttonSignup) {
		    String name = textFieldName.getText().trim(); // 입력된 이름
		    String tel = textFieldTel.getText().trim(); // 입력된 전화번호

		    // 입력값 체크
		    if (name.isEmpty() || tel.isEmpty()) {
		        JOptionPane.showMessageDialog(this, "이름과 전화번호를 입력해주세요.");
		        return;
		    }

		    // 1. SEQ 얻기 (DB에서 다음 관리자 번호)
		    ManagerDAO dao = new ManagerDAO();
		    int seq = dao.getNextSeq();

		    // 3. DB에 관리자 정보 저장
		    int result = dao.insert(seq, name, tel, code);

		    // 4. 결과 안내
		    if (result > 0) {
		        JOptionPane.showMessageDialog(this, "관리자 등록 완료!\n관리자 코드: " + code);
		        dispose();
		    } else {
		        JOptionPane.showMessageDialog(this, "등록 실패. 다시 시도해주세요.");
		    }
		}

	}

	// 마우스 클릭 이벤트 처리 (입력 필드 클릭 시 예시값 초기화)
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == textFieldName)
			textFieldName.setText(""); // 이름 입력 필드 클릭 시 초기화
		if (e.getSource() == textFieldTel)
			textFieldTel.setText(""); // 전화번호 입력 필드 클릭 시 초기화

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
