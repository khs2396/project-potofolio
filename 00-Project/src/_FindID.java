import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

// 아이디 찾기 화면을 담당하는 클래스 (JFrame 상속)
public class _FindID extends JFrame implements ActionListener, MouseListener {
	MemberDAO memberDAO = new MemberDAO(); // 데이터베이스 접근을 위한 DAO 객체 생성
	Container container = getContentPane(); // JFrame의 기본 컨테이너

	// 라벨 정의 (텍스트 설명용 컴포넌트)
	JLabel labelFindID = new JLabel("아이디 찾기", JLabel.CENTER); // 상단 타이틀 라벨
	JLabel labelName = new JLabel("이름"); // 이름 라벨
	JLabel labelTel = new JLabel("휴대폰 번호"); // 휴대폰 번호 라벨
	JLabel labelTelCaution = new JLabel("※ 휴대폰 번호는 - 없이 입력해주세요", JLabel.CENTER); // 안내 라벨

	// 사용자 입력을 받을 텍스트 필드 정의
	TextField textFieldName = new TextField(7); // 이름 입력 필드
	TextField textFieldTel = new TextField("ex) 01012345678", 7); // 휴대폰 번호 입력 필드(예시값)

	// 버튼 컴포넌트 정의
	JButton buttonFind = new JButton("찾기"); // 찾기 버튼
	JButton buttonCancle = new JButton("취소"); // 취소 버튼

	// 패널 구성 (GUI 배치를 세분화)
	JPanel panelCenter = new JPanel(); 	// 중앙에 이름/전화번호 입력 필드를 담을 패널
	JPanel panelButton = new JPanel();	// 버튼 영역 전용 패널
	JPanel panelLabel = new JPanel();	// 각 입력 필드 앞의 라벨 패널
	JPanel panelTextField = new JPanel();	// 실제 텍스트필드가 있는 패널

	// 생성자: 아이디 찾기 창 초기화 및 UI 세팅
	public _FindID() {
		setTitle("아이디 찾기"); // 창 제목
		setSize(300, 300); // 창 크기
		setLocation(500, 200); // 창 위치
		init(); // UI 구성 메서드 호출
		start(); // 이벤트 연결 메서드 호출
		setVisible(true); // 창 보이기
		setResizable(false); // 창 크기 고정

	}

	// UI 레이아웃 및 컴포넌트 배치 담당 메서드
	private void init() {
		container.setLayout(new GridLayout(4, 1)); // 창 전체를 4행 1열 그리드로 나눔
		container.add(labelFindID); // 상단 아이디찾기 라벨
		labelFindID.setFont(new Font("듣음체", Font.BOLD, 20)); // 제목 폰트 설정
		container.add(panelCenter); // 중앙 입력부
		container.add(labelTelCaution); // 안내 라벨
		container.add(panelButton); // 하단 버튼부

		// 중앙 입력 영역 구성 (이름/전화번호 입력)
		panelCenter.setLayout(new FlowLayout()); // 수평 정렬
		panelCenter.add(panelLabel);			// 라벨 영역(PANEL)
		panelCenter.add(panelTextField);		// 입력 필드 영역(PANEL)

		// 왼쪽 라벨 세로 정렬
		panelLabel.setLayout(new GridLayout(2, 1, 20, 10));  // 2행 1열, 패딩 있음
		panelLabel.add(labelName);
		panelLabel.add(labelTel);

		// 오른쪽 입력 필드 세로 정렬
		panelTextField.setLayout(new GridLayout(2, 1, 20, 10)); // 2행 1열, 간격 포함
		panelTextField.add(textFieldName);
		panelTextField.add(textFieldTel);

		// panelButton  (가운데 정렬, 가로 간격 20)
		panelButton.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
		panelButton.add(buttonFind);
		panelButton.add(buttonCancle);

	}

	// 이벤트 리스너 등록 및 기본 설정 담당 메서드
	private void start() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE); // 창 닫아도 프로그램 종료하지 않음
		buttonFind.addActionListener(this); // 찾기 버튼 이벤트
		buttonCancle.addActionListener(this); // 취소 버튼 이벤트
		textFieldTel.addMouseListener(this); // 휴대폰 번호 입력 필드 클릭 이벤트
	}

	// 버튼 클릭 이벤트 처리
	@Override
	public void actionPerformed(ActionEvent e) {
		// 아이디찾기 창닫기
		if (e.getSource() == buttonCancle) {  // 취소 버튼을 클릭했을 때 창 닫기
			dispose();
		}
		// 찾기 버튼 클릭 시
		if (e.getSource() == buttonFind) { 	// 찾기 버튼 클릭: 이름, 전화번호로 검색
		    String name = textFieldName.getText().trim(); // 입력된 이름
		    String tel = textFieldTel.getText().trim(); // 입력된 전화번호
		    // DB에서 회원 정보 조회  이름 + 전화번호 검색
		    List<MemberDTO> results = memberDAO.searchByNameAndTel(name, tel);

		    if (results.isEmpty()) {// 검색 결과 없음
		        JOptionPane.showMessageDialog(this, "일치하는 회원 정보가 없습니다."); // 실패 안내
		    } else {
		        // 첫 번째 결과만 예시로 출력
		        MemberDTO dto = results.get(0);
		        String msg = String.format(
		            "회원 번호: %d\n아이디: %s\n이름: %s\n전화번호: %s\n성별: %s\n생년월일: %s",
		            dto.getMEMBER_SEQ(),
		            dto.getMEMBER_ID(),
		            dto.getMEMBER_NAME(),
		            dto.getMEMBER_TEL(),
		            (dto.getMEMBER_GENDER() == 1 ? "남" : "여"),
		            dto.getMEMBER_BIRTH()
		        );
		        JOptionPane.showMessageDialog(this, msg, "회원 정보 조회 결과", JOptionPane.INFORMATION_MESSAGE);
		    }
		}


	}

	// 마우스 클릭 이벤트 처리 (입력 필드 클릭 시 예시값 초기화)
	@Override
	public void mouseClicked(MouseEvent e) {
		// 텍스트필드 비우기
		if (e.getSource() == textFieldTel) {
			textFieldTel.setText(""); // 휴대폰 번호 입력 필드 클릭 시 예시값 삭제
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
