
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

// 관리자 목록 조회 및 삭제 기능을 제공하는 패널 클래스
public class _ManagerManagement extends JPanel implements ActionListener {
	ManagerDAO managerDao = new ManagerDAO(); // 관리자 DB 연동 DAO
	ImageIcon imageIcon = new ImageIcon("img/book.png"); // 이미지 관리 클래스
	// 라벨 선언
	JLabel labelManagerManagement = new JLabel("매니저관리", JLabel.CENTER); // 상단 타이틀 라벨
	JLabel labelManagerList = new JLabel("매니저 목록", JLabel.LEFT); // 목록 안내 라벨

	// 테이블 관련 선언
	String[] managerList = { "번호", "이름", "전화번호", "코드" }; // 테이블 헤더
	DefaultTableModel tableModel = new DefaultTableModel(managerList, 0); // 테이블 데이터 모델
	JTable table = new JTable(tableModel); // 관리자 목록 테이블
	JScrollPane scrollPane = new JScrollPane(table); // 스크롤 지원

	// 패널 선언 (UI 배치용)
	JPanel panelManagerManagememt = new JPanel();
	JPanel panelButton = new JPanel();
	JPanel panelLabel = new JPanel();
	JPanel panelBind = new JPanel();

	// 버튼 선언
	JButton buttonDelete = new JButton("삭제"); // 삭제 버튼

	// 생성자: 관리자 관리 패널 초기화 및 UI 세팅
	public _ManagerManagement() {
		setIconImage(imageIcon.getImage());	// 아이콘 설정
		Dimension dimension = getSize();				// 프레임 크기		
		int x = (int)(dimension.getHeight()/2 -dimension.getHeight()/2);
		setLocation(x);
		
		init(); // UI 구성 메서드 호출
		start(); // 이벤트 연결 메서드 호출
		loadManagerList(); // 관리자 목록 불러오기
	}
	private void setLocation(int x) {
		// TODO Auto-generated method stub
		
	}

	private void setIconImage(Image image) {
		// TODO Auto-generated method stub
		
	}
	// UI 레이아웃 및 컴포넌트 배치 담당 메서드
	private void init() {
		setLayout(new BorderLayout()); // JPanel에 직접 레이아웃 설정
		add(panelManagerManagememt, BorderLayout.NORTH); // 상단 타이틀/버튼
		add(scrollPane, BorderLayout.CENTER); // 중앙 테이블
		scrollPane.setPreferredSize(new Dimension(500, 200)); // 테이블 크기

		// 상단 패널(타이틀+버튼)
		panelManagerManagememt.setLayout(new GridLayout(2, 1));
		panelManagerManagememt.add(labelManagerManagement);
		labelManagerManagement.setFont(new Font("맑은 고딕", Font.BOLD, 25));
		panelManagerManagememt.add(panelBind);

		// 상단 패널 내부(좌:라벨, 우:버튼)
		panelBind.setLayout(new GridLayout(1, 2));
		panelBind.add(panelLabel);
		panelBind.add(panelButton);

		// 좌측 라벨
		panelLabel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelLabel.add(labelManagerList);
		labelManagerList.setFont(new Font("맑은 고딕", Font.BOLD, 15));

		// 우측 버튼
		panelButton.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panelButton.add(buttonDelete);
	}

	// DB에서 관리자 목록을 불러와 테이블에 표시하는 메서드
	private void loadManagerList() {
		List<ManagerDTO> managers = managerDao.searchAll(); // 전체 관리자 목록 조회
		tableModel.setRowCount(0); // 테이블 초기화
		int number = 1;
		for (ManagerDTO m : managers) {
			Object[] row = { number++, m.getMANAGER_NAME(), m.getMANAGER_TEL(), m.getMANAGER_CODE() };
			tableModel.addRow(row); // 한 행씩 추가
		}
	}

	// 이벤트 리스너 등록 및 기본 설정 담당 메서드
	private void start() {
		buttonDelete.addActionListener(this); // 삭제 버튼 이벤트 연결
	}

	// 버튼 클릭 이벤트 처리
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonDelete) {
			// 삭제 로직
			int row = table.getSelectedRow(); // 선택된 행 인덱스
			if (row == -1) {
				JOptionPane.showMessageDialog(this, "삭제할 행을 선택해주세요.", "삭제", JOptionPane.WARNING_MESSAGE);
				return;
			}
			int confirm = JOptionPane.showConfirmDialog(this, "삭제하시겠습니까?", "삭제", JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {

				String Str = tableModel.getValueAt(row, 3).toString(); // 선택된 행의 관리자 코드
				int managercode = Integer.parseInt(Str);

				managerDao.deleteManager(managercode); // DB에서 삭제
				tableModel.removeRow(row); // 테이블에서 삭제
				JOptionPane.showMessageDialog(this, "삭제되었습니다.", "삭제", JOptionPane.INFORMATION_MESSAGE);

				loadManagerList(); // 목록 새로고침
			}
		}
	}
}
