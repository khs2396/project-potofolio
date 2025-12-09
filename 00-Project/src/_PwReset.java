import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
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

// 비밀번호 재설정(초기화) 화면을 담당하는 클래스 (JFrame 상속)
public class _PwReset extends JFrame implements ActionListener, MouseListener {
	ImageIcon imageIcon = new ImageIcon("img/book.png");  // 이미지 관리 클래스
	private MemberDTO member; // 비밀번호를 변경할 회원 정보 DTO
	Container container = getContentPane(); // JFrame의 컨테이너

	// 라벨 선언
	JLabel labelReset = new JLabel("비밀번호 재설정", JLabel.CENTER); // 상단 타이틀 라벨
	JLabel labelPw = new JLabel("비밀번호        "); // 비밀번호 입력 라벨
	JLabel labelPwCheck = new JLabel("비밀번호 확인"); // 비밀번호 확인 입력 라벨
	JLabel labelPwCaution = new JLabel(""); // 비밀번호 입력 주의사항 라벨

	// 패널 선언 (UI 배치용)
	JPanel panelLabel = new JPanel();
	JPanel panelButton = new JPanel();
	JPanel panelCenter = new JPanel();
	
	// 비밀번호 입력 필드와 라벨을 분리해서 배치하기 위한 패널
	JPanel panelPasswordField1 = new JPanel(); // 비밀번호 입력 전체
	JPanel panelPasswordField2 = new JPanel(); // 비밀번호 라벨
	JPanel panelPasswordField3 = new JPanel(); // 비밀번호 입력 필드
	
	JPanel panelPasswordCheckField1 = new JPanel(); // 비밀번호 확인 전체
	JPanel panelPasswordCheckField2 = new JPanel(); // 비밀번호 확인 라벨
	JPanel panelPasswordCheckField3 = new JPanel(); // 비밀번호 확인 입력 필드
	
	JPanel panelPwCaution = new JPanel(); // 비밀번호 주의사항 라벨 패널
	
	// 버튼 선언
	JButton buttonModify = new JButton("변경"); // 비밀번호 변경 버튼
	JButton buttonCancle = new JButton("취소"); // 취소 버튼

	// 비밀번호 입력 필드
	JPasswordField passwordField = new JPasswordField(15); // 비밀번호 입력 필드
	JPasswordField passwordCheckField = new JPasswordField(15); // 비밀번호 확인 입력 필드

	// 생성자: 비밀번호 재설정 창 초기화 및 UI 세팅
	public _PwReset(MemberDTO member) {
		setIconImage(imageIcon.getImage());	// 아이콘 설정
		setTitle("비밀번호 재설정"); // 창 제목
		setSize(300, 300); // 창 크기
		setLocation(500, 200); // 창 위치
		init(); // UI 구성 메서드 호출
		start(); // 이벤트 연결 메서드 호출
		setVisible(true); // 창 보이기
		this.member = member; // 비밀번호를 변경할 회원 정보 저장
	}

	// UI 레이아웃 및 컴포넌트 배치 담당 메서드
	private void init() {
		container.setLayout(new BorderLayout()); // 전체 레이아웃 BorderLayout
		container.add("North", labelReset); // 상단 타이틀
		labelReset.setFont(new Font("듣음체", Font.BOLD, 25)); // 타이틀 폰트
		container.add("Center", panelCenter); // 중앙 입력부
		container.add("South", panelButton); // 하단 버튼부

		// 중앙 입력부 패널 (비밀번호, 비밀번호 확인, 주의사항)
		panelCenter.setLayout(new GridLayout(3, 1));
		panelCenter.add(panelPasswordField1); // 비밀번호 입력
		panelCenter.add(panelPasswordCheckField1); // 비밀번호 확인 입력
		panelCenter.add(panelPwCaution); // 주의사항 라벨
		
		// 비밀번호 입력 패널 구성
		panelPasswordField1.setLayout(new FlowLayout());
		panelPasswordField1.add(panelPasswordField2); // 라벨
		panelPasswordField1.add(panelPasswordField3); // 입력필드
		panelPasswordField2.setLayout(new FlowLayout());
		panelPasswordField2.add(labelPw);
		panelPasswordField3.setLayout(new FlowLayout());
		panelPasswordField3.add(passwordField);
		
		// 비밀번호 확인 입력 패널 구성
		panelPasswordCheckField1.setLayout(new FlowLayout());
		panelPasswordCheckField1.add(panelPasswordCheckField2); // 라벨
		panelPasswordCheckField1.add(panelPasswordCheckField3); // 입력필드
		panelPasswordCheckField2.setLayout(new FlowLayout());
		panelPasswordCheckField2.add(labelPwCheck);
		panelPasswordCheckField3.setLayout(new FlowLayout());
		panelPasswordCheckField3.add(passwordCheckField);
		
		// 비밀번호 주의사항 라벨 패널
		panelPwCaution.setLayout(new FlowLayout(FlowLayout.CENTER));
		panelPwCaution.add(labelPwCaution);

		// 하단 버튼 패널
		panelButton.setLayout(new FlowLayout(FlowLayout.CENTER,30,10));
		panelButton.add(buttonModify); // 변경 버튼
		panelButton.add(buttonCancle); // 취소 버튼
	}

	// 이벤트 리스너 등록 및 기본 설정 담당 메서드
	private void start() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE); // 창 닫기 동작 설정
		passwordField.addMouseListener(this); // 비밀번호 입력 필드 클릭 이벤트
		passwordCheckField.addMouseListener(this); // 비밀번호 확인 입력 필드 클릭 이벤트
		buttonCancle.addActionListener(this); // 취소 버튼 이벤트
		buttonModify.addActionListener(this); // 변경 버튼 이벤트
	}

	// 버튼 클릭 이벤트 처리
	@Override
	public void actionPerformed(ActionEvent e) {
		// 취소버튼 클릭 시 창 닫기
		if(e.getSource() == buttonCancle) dispose();
		
		// 변경버튼 클릭 시
		if (e.getSource() == buttonModify) {
		    // 입력값 가져오기
		    String pw = new String(passwordField.getPassword()).trim(); // 새 비밀번호
		    String pwCheck = new String(passwordCheckField.getPassword()).trim(); // 비밀번호 확인

		    // 비밀번호 입력값 검증
		    if (pw.isEmpty()) {
		        labelPwCaution.setText("비밀번호를 입력해주세요");
		        labelPwCaution.setForeground(Color.RED);
		        return;
		    }

		    if (pwCheck.isEmpty()) {
		        labelPwCaution.setText("비밀번호 확인을 입력해주세요");
		        labelPwCaution.setForeground(Color.RED);
		        return;
		    }

		    if (!pw.equals(pwCheck)) {
		        labelPwCaution.setText("비밀번호가 일치하지 않습니다.");
		        labelPwCaution.setForeground(Color.RED);
		        return;
		    }

		    if (!pw.matches("^(?=.*[A-Za-z])(?=.*[0-9]).+$")) {
		        labelPwCaution.setText("비밀번호는 영문과 숫자를 포함해야 합니다.");
		        labelPwCaution.setForeground(Color.RED);
		        return;
		    }

		    if (pw.length() < 8 || pw.length() > 16) {
		        labelPwCaution.setText("비밀번호는 8~16자 사이여야 합니다.");
		        labelPwCaution.setForeground(Color.RED);
		        return;
		    }

		    // 검증 통과 → DB 업데이트
		    member.setMEMBER_PW(pw);  // ✅ DTO 객체에 새 비밀번호 저장
		    int result = new MemberDAO().updateMember(member);  // ✅ DB 반영

		    if (result > 0) {
		    	JOptionPane.showMessageDialog(this, "변경되었습니다.");
		        dispose(); // 창 닫기
		    } else {
		        labelPwCaution.setText("비밀번호 변경 실패. 다시 시도해주세요.");
		        labelPwCaution.setForeground(Color.RED);
		    }
		}
	}

	// 마우스 클릭 이벤트 처리 (입력 필드 클릭 시 주의사항 문구 초기화)
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource() == passwordField) labelPwCaution.setText(""); // 비밀번호 입력 필드 클릭 시 주의사항 초기화
		if(e.getSource() == passwordCheckField) labelPwCaution.setText(""); // 비밀번호 확인 입력 필드 클릭 시 주의사항 초기화
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
