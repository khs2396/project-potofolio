import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
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
import javax.swing.JTextField;

//비밀번호 찾기 창 클래스 정의
//JFrame을 상속하고 버튼 클릭(Action), 마우스 클릭(Mouse) 이벤트 처리 인터페이스를 구현
public class _FindPW extends JFrame implements ActionListener, MouseListener {
	MemberDAO memberDAO = new MemberDAO(); // 회원 DB 접근을 위한 DAO 객체
	Container container = getContentPane(); // JFrame의 컨테이너

	// 라벨 선언
	JLabel labelFindPW = new JLabel("비밀번호 찾기", JLabel.CENTER); // 상단 타이틀 라벨
	JLabel labelName = new JLabel("이름"); // 이름 라벨
	JLabel labelID = new JLabel("아이디"); // 아이디 라벨
	JLabel labelTel = new JLabel("휴대폰 번호"); // 휴대폰 번호 라벨
	JLabel labelTelCaution = new JLabel("※ 휴대폰 번호는 - 없이 입력해주세요", JLabel.CENTER); // 안내 라벨

	// 사용자 입력을 위한 텍스트 필드
	JTextField textFieldID = new JTextField(10); // 아이디 입력 필드
	JTextField textFieldName = new JTextField(10); // 이름 입력 필드
	JTextField textFieldTel = new JTextField(10); // 휴대폰 번호 입력 필드

	// 패널 선언 (UI 배치용) 각각의 입력 필드와 버튼을 배치할 패널 정의
	JPanel panelCenter = new JPanel(); // 전체 항목들을 포함할 중앙 패널
	JPanel panelName = new JPanel(); // 이름 입력 행
	JPanel panelID = new JPanel(); // ID 입력 행
	JPanel panelTel = new JPanel(); // 전화번호 입력 행
	JPanel panelButton = new JPanel(); // 하단 버튼 영역

	// 찾기 및 취소 버튼 정의
	JButton buttonFind = new JButton("찾기"); // 찾기 버튼
	JButton buttonCancle = new JButton("취소"); // 취소 버튼

	// 생성자: 비밀번호 찾기 창 초기화 및 UI 세팅
	public _FindPW() {
		setTitle("비밀번호 찾기"); // 창 제목
		setSize(300, 300); // 창 크기
		setLocation(500, 200); // 창 위치
		init(); // UI 구성 메서드 호출
		start(); // 이벤트 연결 메서드 호출
		setVisible(true); // 창 보이기
		setResizable(false); // 창 크기 고정
	}

	// UI 레이아웃 및 컴포넌트 배치 담당 메서드
	private void init() {
		container.setLayout(new BorderLayout()); // 전체 레이아웃 BorderLayout
		container.add("North", labelFindPW); // 상단 제목 배치
		labelFindPW.setFont(new Font("듣음체", Font.BOLD, 20)); // 타이틀 폰트
		container.add("Center", panelCenter); // 중앙 입력 필드 영역 패널
		container.add("South", panelButton); // 아래 버튼 영역

		// 중앙 입력부 패널 입력 영역은 4행 1열, 세로 간격 10픽셀 (아이디, 이름, 전화번호, 안내)
		panelCenter.setLayout(new GridLayout(4, 1, 0, 10));
		panelCenter.add(panelID); // 아이디 입력
		panelCenter.add(panelName); // 이름 입력
		panelCenter.add(panelTel); // 전화번호 입력
		panelCenter.add(labelTelCaution); // 안내 라벨

		// ID 입력 행 구성
		panelID.setLayout(new FlowLayout(FlowLayout.CENTER, 35, 10));// 가운데 정렬, 간격 조절
		panelID.add(labelID);
		panelID.add(textFieldID);

		// panelName 이름 입력 행 구성
		panelName.setLayout(new FlowLayout(FlowLayout.CENTER, 45, 10));
		panelName.add(labelName);
		panelName.add(textFieldName);

		// panelTel 전화번호 입력 행 구성
		panelTel.setLayout(new FlowLayout());
		panelTel.add(labelTel);
		panelTel.add(textFieldTel);

		// panelButton ( 버튼 영역 구성 (가운데 정렬, 버튼 사이 간격 20, 상단 여백 20))
		panelButton.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
		panelButton.add(buttonFind);
		panelButton.add(buttonCancle);
	}

	// 이벤트 리스너 등록 및 기본 설정 담당 메서드
	private void start() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE); // 창이 닫힐 때, 전체 프로그램 종료 X
		// 버튼 이벤트 리스너 등록
		buttonCancle.addActionListener(this); // 취소 버튼 이벤트
		buttonFind.addActionListener(this); // 찾기 버튼 이벤트
	}

	// 버튼 클릭 이벤트 처리
	@Override
	public void actionPerformed(ActionEvent e) {
		// 비밀번호찾기 창닫기
		if (e.getSource() == buttonCancle) {
			dispose();
		}
		// 찾기 버튼 클릭 시
		if (e.getSource() == buttonFind) {
			// 입력값 가져오기
			String id = textFieldID.getText(); // 입력된 아이디
			String name = textFieldName.getText(); // 입력된 이름
			String tel = textFieldTel.getText(); // 입력된 전화번호
			// DAO를 통해 id + name + 전화번호로 회원 정보 검색
			List<MemberDTO> result = memberDAO.searchByIdAndNameAndTel(id, name, tel); // DB에서 회원 정보 조회
			// 검색 결과가 없으면 경고 메시지
			if (result.isEmpty()) {
				JOptionPane.showMessageDialog(this, "일치하는 회원 정보가 없습니다.");
			} else {
				// 검색 결과가 있으면 비밀번호 재설정 창으로 연결
				MemberDTO member = result.get(0); // 첫 번째 일치 회원
				new _PwReset(member); // 비밀번호 재설정 창 오픈
				dispose(); // 현재 창 닫기
			}
		}

	}

	// 마우스 클릭 이벤트 처리 (사용하지 않음)
	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

}
