import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Field;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

// 회원가입 화면을 담당하는 클래스 (JFrame 상속)
public class _Signup extends JFrame implements ActionListener, MouseListener {
	ManagerDTO managerDTO = new ManagerDTO(); // 관리자 정보 저장용 DTO
	ManagerDAO managerDAO = new ManagerDAO(); // 관리자 DB 연동 DAO
	MemberDTO memberDTO = new MemberDTO(); // 회원 정보 저장용 DTO
	MemberDAO memberDAO = new MemberDAO(); // 회원 DB 연동 DAO
	Container container = getContentPane(); // JFrame의 컨테이너
	boolean idCheck = false; // 아이디 중복 확인 여부

	// 텍스트필드 선언 (아이디, 이름, 생년월일, 연락처)
	JTextField textFieldID = new JTextField(15); // 아이디 입력 필드
	JTextField textFieldName = new JTextField(15); // 이름 입력 필드
	JTextField textFieldBirth = new JTextField("ex) 960101", 15); // 생년월일 입력 필드(예시값)
	JTextField textFieldTel = new JTextField("ex) 01012345678", 15); // 연락처 입력 필드(예시값)

	// 비밀번호 입력 필드 (비밀번호, 비밀번호 확인)
	JPasswordField passwordField1 = new JPasswordField(15); // 비밀번호 입력 필드
	JPasswordField passwordField2 = new JPasswordField(15); // 비밀번호 확인 입력 필드

	// 라벨 선언 (아이디, 비밀번호, 이름, 생년월일, 연락처 등)
	JLabel labelID = new JLabel("아이디"); // 아이디 라벨
	JLabel labelPW1 = new JLabel("비밀번호"); // 비밀번호 라벨
	JLabel labelPW2 = new JLabel("비밀번호 확인"); // 비밀번호 확인 라벨

	JLabel labelIDCaution = new JLabel(""); // 아이디 입력 주의사항 라벨
	JLabel labelPWCantion = new JLabel(""); // 비밀번호 입력 주의사항 라벨

	JLabel labelName = new JLabel("이름"); // 이름 라벨
	JLabel labelBirth = new JLabel("생년월일"); // 생년월일 라벨
	JLabel labelTel = new JLabel("연락처"); // 연락처 라벨

	JLabel labelCaution = new JLabel(""); // 개인정보 입력 주의사항 라벨

	// 성별 및 관리자 선택 라디오버튼
	JRadioButton buttonMale = new JRadioButton("남성", true); // 남성 선택 라디오버튼(기본값)
	JRadioButton buttonFemale = new JRadioButton("여성"); // 여성 선택 라디오버튼
	JRadioButton buttonManager = new JRadioButton("관리자"); // 관리자 선택 라디오버튼

	ButtonGroup buttonGroup = new ButtonGroup(); // 성별 라디오버튼 그룹화

	// 버튼 선언 (중복확인, 가입, 취소)
	JButton buttonCheckDuplication = new JButton("중복 확인"); // 아이디 중복확인 버튼
	JButton buttonSignup = new JButton("가입"); // 회원가입 버튼
	JButton buttoncancle = new JButton("취소"); // 취소 버튼

	// 각종 패널 선언 (UI 배치용)
	JPanel panelOutID = new JPanel();
	JPanel panelInID = new JPanel();
	JPanel panelIDTextField = new JPanel();
	JPanel panelCheckButton = new JPanel();
	JPanel panelOutPW = new JPanel();
	JPanel panelInPW = new JPanel();
	JPanel panelPWTextField = new JPanel();
	JPanel panelOutPWCheck = new JPanel();
	JPanel panelInPWCheck = new JPanel();
	JPanel panelPWCheckTextField = new JPanel();
	JPanel panelRadioButton = new JPanel();
	JPanel panelName1 = new JPanel();
	JPanel panelName2 = new JPanel();
	JPanel panelNameTextField = new JPanel();
	JPanel panelBirth1 = new JPanel();
	JPanel panelBirth2 = new JPanel();
	JPanel panelBirthTextField = new JPanel();
	JPanel panelTel1 = new JPanel();
	JPanel panelTel2 = new JPanel();
	JPanel panelTelTextField = new JPanel();
	JPanel panelManager = new JPanel();
	JPanel panelLabel = new JPanel();
	JPanel panelRadioButtonManager = new JPanel();
	JPanel panelSignup = new JPanel();

	// 생성자: 회원가입 창 초기화 및 UI 세팅
	public _Signup() {
		setTitle("회원가입"); // 창 제목
		setSize(300, 500); // 창 크기
		setLocation(500, 200); // 창 위치
		init(); // UI 구성 메서드 호출
		start(); // 이벤트 연결 메서드 호출
		setVisible(true); // 창 보이기
	}

	// UI 레이아웃 및 컴포넌트 배치 담당 메서드
	private void init() {
		container.setLayout(new GridLayout(12, 1)); // 전체 레이아웃 12행 1열
		container.add(panelOutID); // 아이디 입력 패널
		container.add(panelCheckButton); // 중복확인 버튼 패널
		container.add(labelIDCaution); // 아이디 주의사항 라벨
		container.add(panelOutPW); // 비밀번호 입력 패널
		container.add(panelOutPWCheck); // 비밀번호 확인 입력 패널
		container.add(labelPWCantion); // 비밀번호 주의사항 라벨
		container.add(panelRadioButton); // 성별 라디오버튼 패널
		container.add(panelName1); // 이름 입력 패널
		container.add(panelBirth1); // 생년월일 입력 패널
		container.add(panelTel1); // 연락처 입력 패널
		container.add(panelManager); // 관리자/주의사항 패널
		container.add(panelSignup); // 가입/취소 버튼 패널

		// 각 패널별로 세부 레이아웃 및 컴포넌트 배치 (아래 생략)
		// ... 기존 코드 동일 ...
		panelOutID.setLayout(new BorderLayout());
		panelOutID.add("Center", panelInID);
		panelOutID.add("East", panelIDTextField);
		panelInID.setLayout(new FlowLayout());
		panelInID.add(labelID);
		panelIDTextField.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panelIDTextField.add(textFieldID);
		panelCheckButton.setLayout(new FlowLayout());
		panelCheckButton.add(buttonCheckDuplication);
		buttonCheckDuplication.setPreferredSize(new Dimension(200, 30)); // 버튼 크기
		panelOutPW.setLayout(new BorderLayout());
		panelOutPW.add("Center", panelInPW);
		panelOutPW.add("East", panelPWTextField);
		panelInPW.setLayout(new FlowLayout());
		panelInPW.add(labelPW1);
		panelPWTextField.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panelPWTextField.add(passwordField1);
		passwordField1.setEchoChar('*'); // 비밀번호 * 표시
		panelOutPWCheck.setLayout(new BorderLayout());
		panelOutPWCheck.add("Center", panelInPWCheck);
		panelOutPWCheck.add("East", panelPWCheckTextField);
		panelInPWCheck.setLayout(new FlowLayout());
		panelInPWCheck.add(labelPW2);
		panelPWCheckTextField.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panelPWCheckTextField.add(passwordField2);
		passwordField2.setEchoChar('*'); // 비밀번호 확인 * 표시
		panelRadioButton.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 0));
		panelRadioButton.add(buttonMale);
		panelRadioButton.add(buttonFemale);
		buttonGroup.add(buttonMale);
		buttonGroup.add(buttonFemale);
		panelName1.setLayout(new BorderLayout());
		panelName1.add("Center", panelName2);
		panelName1.add("East", panelNameTextField);
		panelName2.setLayout(new FlowLayout());
		panelName2.add(labelName);
		panelNameTextField.setLayout(new FlowLayout());
		panelNameTextField.add(textFieldName);
		panelBirth1.setLayout(new BorderLayout());
		panelBirth1.add("Center", panelBirth2);
		panelBirth1.add("East", panelBirthTextField);
		panelBirth2.setLayout(new FlowLayout());
		panelBirth2.add(labelBirth);
		panelBirthTextField.setLayout(new FlowLayout());
		panelBirthTextField.add(textFieldBirth);
		panelTel1.setLayout(new BorderLayout());
		panelTel1.add("Center", panelTel2);
		panelTel1.add("East", panelTelTextField);
		panelTel2.setLayout(new FlowLayout());
		panelTel2.add(labelTel);
		panelTelTextField.setLayout(new FlowLayout());
		panelTelTextField.add(textFieldTel);
		panelManager.setLayout(new GridLayout(1, 2));
		panelManager.add(panelLabel);
		panelManager.add(panelRadioButtonManager);
		panelLabel.setLayout(new FlowLayout());
		panelLabel.add(labelCaution);
		panelRadioButtonManager.setLayout(new FlowLayout());
		panelRadioButtonManager.add(buttonManager);
		panelSignup.setLayout(new FlowLayout());
		panelSignup.add(buttonSignup);
		panelSignup.add(buttoncancle);
	}

	// 이벤트 리스너 등록 및 기본 설정 담당 메서드
	private void start() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE); // 창 닫기 동작 설정
		textFieldBirth.addMouseListener(this); // 생년월일 필드 클릭 이벤트
		textFieldTel.addMouseListener(this); // 연락처 필드 클릭 이벤트
		buttonManager.addActionListener(this); // 관리자 라디오버튼 이벤트
		buttoncancle.addActionListener(this); // 취소 버튼 이벤트
		buttonSignup.addActionListener(this); // 가입 버튼 이벤트
		buttonCheckDuplication.addActionListener(this); // 중복확인 버튼 이벤트
	}

	// 버튼 및 라디오버튼 클릭 이벤트 처리
	@Override
	public void actionPerformed(ActionEvent e) {
		// 관리자 라디오버튼 선택 시 관리자 코드 입력 및 관리자 회원가입 창 이동
		if (buttonManager.isSelected()) {
			String result = JOptionPane.showInternalInputDialog(container, " 관리자 번호를 입력하세요 ");
			if (result == null || result.trim().isEmpty()) {
				JOptionPane.showMessageDialog(this, "입력된 내용이 없습니다.");
			} else {
				int code = Integer.parseInt(result);
				managerDTO = managerDAO.selectByManagerSeq(code); // DB에서 관리자 코드로 조회
				if(managerDTO != null) {
					new _ManagerSingup(managerDTO); // 관리자 회원가입 창 이동
					dispose(); // 현재 창 닫기
				}else {
					JOptionPane.showMessageDialog(this, "없는 코드입니다.");
				}
			}
			buttonManager.setSelected(false); // 라디오버튼 해제
		}
		// 취소 버튼 클릭 시 창 닫기
		if (e.getSource() == buttoncancle) {
			dispose();
		}
		// 아이디 중복확인 버튼 클릭 시
		if (e.getSource() == buttonCheckDuplication) {
			String str = textFieldID.getText(); // 입력된 아이디
			boolean result = memberDAO.isIdDuplicate(str); // DB에서 중복 체크
			if (str.equals("")) {
				JOptionPane.showMessageDialog(this, "아이디를 입력해주세요.");
				return;
			}
			if (result) {
				JOptionPane.showMessageDialog(this, "중복된 아이디입니다.");
				idCheck = false;
			} else {
				JOptionPane.showMessageDialog(this, "사용가능한 아이디입니다.");
				idCheck = true;
			}
		}
		// 회원가입 버튼 클릭 시
		if (e.getSource() == buttonSignup)
		{
			// 아이디 중복 체크 여부 확인
			if (idCheck == false) {
				JOptionPane.showMessageDialog(this, "아이디 중복 확인을 해주세요.");
				return;
			}
			// 입력값 변수에 저장
			String id = textFieldID.getText().trim(); // 아이디
			String pw1 = passwordField1.getText().trim(); // 비밀번호
			String pw2 = passwordField2.getText().trim(); // 비밀번호 확인
			String name = textFieldName.getText().trim(); // 이름
			String birth = textFieldBirth.getText().trim(); // 생년월일
			String tel = textFieldTel.getText().trim(); // 연락처
			int gender = buttonMale.isSelected() ? 1 : 2; // 성별(1:남, 2:여)

			// 비밀번호 일치 여부 체크
			if (!pw1.equals(pw2)) {
				labelPWCantion.setText("비밀번호가 일치하지 않습니다.");
				labelPWCantion.setForeground(Color.RED);
				return;
			}
			// 비밀번호 영문+숫자 포함 여부 체크
			if (!pw1.matches("^(?=.*[A-Za-z])(?=.*[0-9]).+$")) {
				labelPWCantion.setText("비밀번호는 영문과 숫자를 포함해야 합니다.");
				labelPWCantion.setForeground(Color.RED);
		        return;
		    }
		    // 비밀번호 길이 체크
		    if (pw1.length() < 8 || pw1.length() > 16) {
		    	labelPWCantion.setText("비밀번호는 8~16자 사이여야 합니다.");
		    	labelPWCantion.setForeground(Color.RED);
		        return;
		    }
			// 필수 입력값 체크
			if (name.isEmpty() || birth.isEmpty() || tel.isEmpty()) {
				JOptionPane.showMessageDialog(this, "모든 항목을 입력해주세요.");
				return;
			}
			// 회원번호 자동생성(마지막 회원번호+1)
			int newSeq = 1;
			List<MemberDTO> list = memberDAO.searchAllAsc();
			if (!list.isEmpty()) {
				newSeq = list.get(list.size() - 1).getMEMBER_SEQ() + 1;
			}
			// DTO 객체 생성 및 값 세팅
			MemberDTO memberDTO = new MemberDTO(
					newSeq, // MEMBER_SEQ
					id, // MEMBER_ID
					pw1, // MEMBER_PW
					name, // MEMBER_NAME
					tel, // MEMBER_TEL
					gender, // MEMBER_GENDER (1:남, 2:여)
					birth // MEMBER_BIRTH
			);
			// DB에 회원정보 저장
			int result = memberDAO.insert(memberDTO);
			if (result > 0) {
				JOptionPane.showMessageDialog(this, "회원가입이 완료되었습니다.");
				dispose(); // 창 닫기
			} else {
				JOptionPane.showMessageDialog(this, "회원가입 실패. 다시 시도해주세요.");
			}
		}
	}

	// 마우스 클릭 이벤트 처리 (입력 예시값 지우기)
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == textFieldBirth) {
			textFieldBirth.setText(""); // 생년월일 필드 클릭 시 예시값 삭제
		}
		if (e.getSource() == textFieldTel) {
			textFieldTel.setText(""); // 연락처 필드 클릭 시 예시값 삭제
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
