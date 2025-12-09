// 공지사항 추가를 위한 함수
function checkInputNotice() {
	var frm = document.frm;

	if (!frm.subject.value) {
		alert("제목을 입력해주세요.");
		frm.subject.focus();
		return false;
	}

	if (!frm.content.value) {
		alert("내용을 입력해주세요.");
		frm.content.focus();
		return false;
	}

	if (!frm.img1.value) {
		alert("파일을 입력해주세요.");
		frm.img1.focus();
		return false;
	}

	frm.submit();
}

// 공지사항 수정을 위한 함수
function checkInputNoticeModify() {
	var frm = document.frm;
	
	if(!frm.subject.value) {
		alert("제목을 입력해주세요.");
		frm.subject.focus();
		return false;
	}
	
	if(!frm.content.value) {
		alert("내용을 입력해주세요.");
		frm.content.focus();
		return false;
	}
	
	frm.submit();
}

function isLogin1() {
	if (managerId == null) {
		alert("먼저 로그인 해주세요.");
	} else {
		location.href = "mypage?id=" + managerId;
	}
}

function isLogin2() {
	if (managerId == null) {
		alert("먼저 로그인 해주세요.");
	} else {
		location.href = "orderlist";
	}
}

function isLogin3() {
	if (managerId == null) {
		alert("먼저 로그인 해주세요.");
	} else {
		location.href = "productList";
	}
}

function isLogin4() {
	if (managerId == null) {
		alert("먼저 로그인 해주세요.");
	} else {
		location.href = "orderingList";
	}
}