let isIdChecked = false; // 아이디 중복체크 여부

// 로그인 화면
function inputCheck() {
	var frm = document.frm;


	if (!frm.id.value) {
		alert("아이디를 입력하세요.");
		frm.id.focus();
		return false;
	}

	if (!frm.pw.value) {
		alert("비밀번호를 입력하세요.");
		frm.pw.focus();
		return false;
	}

	frm.submit();
}

// 회원가입 화면 및 정보 변경
function checkInfo() {
	var frm = document.frm;
	var id = frm.id.value;
	var pw = frm.pw.value;
	var name = frm.name.value;


	if (!frm.name.value) {
		alert("이름을 입력하세요.");
		frm.name.focus();
		return false;
	}



	// 이름이 한글로만 되어 있는지 확인
	var koreanRegex = /^[가-힣]+$/;
	if (!koreanRegex.test(name)) {
		alert("이름은 한글로만 입력하세요.");
		frm.name.focus();
		return false;
	}

	if (!frm.id.value) {
		alert("아이디를 입력하세요.");
		frm.id.focus();
		return false;
	}

	// ✅ 중복체크 여부 확인
	if (!isIdChecked) {
		alert("아이디 중복체크를 해주세요.");
		frm.id.focus();
		return false;
	}


	// 아이디 한글 포함 여부 확인 (한글 체크 정규식)
	var hangul = /[ㄱ-ㅎㅏ-ㅣ가-힣]/;
	if (hangul.test(id)) {
		alert("아이디에 한글을 사용할 수 없습니다.");
		frm.id.focus();
		return false;
	}

	// 아이디 길이 체크 (5자 이상 15자 이하)
	if (id.length < 5 || id.length > 15) {
		alert("아이디는 5자 이상 15자 이하로 입력하세요.");
		frm.id.focus();
		return false;
	}


	// 비밀번호 길이 검사 (5~15자)
	if (pw.length < 5 || pw.length > 15) {
		alert("비밀번호는 5자 이상 15자 이하로 입력하세요.");
		frm.pw.focus();
		return false;
	}

	if (!frm.pw.value) {
		alert("비밀번호를 입력하세요.");
		frm.pw.focus();
		return false;
	}

	if (frm.pw.value != frm.repw.value) {
		alert("비밀번호가 맞지 않습니다.");
		frm.repw.value = "";
		frm.repw.focus();
		return false;
	}

	// 전화번호 숫자만 허용 (하이픈 금지)
	var telRegex = /^[0-9]+$/;
	if (!frm.tel.value || !telRegex.test(frm.tel.value)) {
		alert("전화번호를 -없이 숫자로 입력하세요.");
		frm.tel.focus();
		return false;
	}

	if (!frm.addr.value) {
		alert("지점주소를 입력하세요.");
		frm.addr.focus();
		return false;
	}

	frm.submit();
}

// 아이디 중복 검사 (간단하게 입력 여부만 확인)
function checkId() {
    var frm = document.frm;
    var sId = frm.id.value.trim();

    if (!sId) {
        alert("아이디를 입력하세요.");
        frm.id.focus();
        return false;
    }

    // 아이디 한글 포함 여부 확인
    var hangul = /[ㄱ-ㅎㅏ-ㅣ가-힣]/;
    if (hangul.test(sId)) {
        alert("아이디에 한글을 사용할 수 없습니다.");
        frm.id.focus();
        return false;
    }

    if (sId.length < 5 || sId.length > 15) {
        alert("아이디는 5자 이상 15자 이하로 입력하세요.");
        frm.id.focus();
        return false;
    }

	// fetch 요청 (백엔드 주소 맞게 변경)
	fetch('/checkId?id=' + encodeURIComponent(sId))
	    .then(response => response.json())
	    .then(isExist => {
	        if (isExist) {
	            alert("사용중인 아이디입니다.");
	            isIdChecked = false;
	            frm.id.focus();
	        } else {
	            alert("사용 가능한 아이디입니다.");
	            isIdChecked = true;
	        }
	    })
	    .catch(error => {
	        console.error('아이디 중복체크 오류:', error);
	        alert('서버와 통신 중 오류가 발생했습니다.');
	    });
}


// 정보수정
function modifyInfo() {
	var frm = document.frm;
	var pw = frm.pw.value;

	// 비밀번호 길이 검사 (5~15자)
	if (pw.length < 5 || pw.length > 15) {
		alert("비밀번호는 5자 이상 15자 이하로 입력하세요.");
		frm.pw.focus();
		return false;
	}

	if (!frm.pw.value) {
		alert("비밀번호를 입력하세요.");
		frm.pw.focus();
		return false;
	}

	if (frm.pw.value != frm.repw.value) {
		alert("비밀번호가 맞지 않습니다.");
		frm.repw.value = "";
		frm.repw.focus();
		return false;
	}

	// 전화번호 숫자만 허용 (하이픈 금지)
	var telRegex = /^[0-9]+$/;
	if (!frm.tel.value || !telRegex.test(frm.tel.value)) {
		alert("전화번호를 -없이 숫자로 입력하세요.");
		frm.tel.focus();
		return false;
	}

	if (!frm.addr.value) {
		alert("지점주소를 입력하세요.");
		frm.addr.focus();
		return false;
	}

	frm.submit();
}

// 아이디 찾기
function checkNameAndTel() {
	var frm = document.frm;
	var name = frm.name.value;

	if (!frm.name.value) {
		alert("이름을 입력하세요.");
		frm.name.focus();
		return false;
	}
	
	// 이름이 한글로만 되어 있는지 확인
	var koreanRegex = /^[가-힣]+$/;
	if (!koreanRegex.test(name)) {
		alert("이름은 한글로만 입력하세요.");
		frm.name.focus();
		return false;
	}

	// 전화번호 숫자만 허용 (하이픈 금지)
	var telRegex = /^[0-9]+$/;
	if (!frm.tel.value || !telRegex.test(frm.tel.value)) {
		alert("전화번호를 -없이 숫자로 입력하세요.");
		frm.tel.focus();
		return false;
	}

	frm.submit();
}

// 비밀번호 찾기
function checkIdAndNameAndTel() {
	var frm = document.frm;
	var id = frm.id.value;
	var name = frm.name.value;

	if (!frm.id.value) {
		alert("아이디를 입력하세요.");
		frm.id.focus();
		return false;
	}
	
	// 아이디 한글 포함 여부 확인 (한글 체크 정규식)
	var hangul = /[ㄱ-ㅎㅏ-ㅣ가-힣]/;
	if (hangul.test(id)) {
		alert("아이디에 한글을 사용할 수 없습니다.");
		frm.id.focus();
		return false;
	}

	// 아이디 길이 체크 (5자 이상 15자 이하)
	if (id.length < 5 || id.length > 15) {
		alert("아이디는 5자 이상 15자 이하로 입력하세요.");
		frm.id.focus();
		return false;
	}

	if (!frm.name.value) {
		alert("이름을 입력하세요.");
		frm.name.focus();
		return false;
	}
	
	// 이름이 한글로만 되어 있는지 확인
	var koreanRegex = /^[가-힣]+$/;
	if (!koreanRegex.test(name)) {
		alert("이름은 한글로만 입력하세요.");
		frm.name.focus();
		return false;
	}

	// 전화번호 숫자만 허용 (하이픈 금지)
	var telRegex = /^[0-9]+$/;
	if (!frm.tel.value || !telRegex.test(frm.tel.value)) {
		alert("전화번호를 -없이 숫자로 입력하세요.");
		frm.tel.focus();
		return false;
	}
	frm.submit();
}

// 비밀번호 변경
function inputPw() {
	var frm = document.frm;
	var pw = frm.pw.value;

	if (!frm.pw.value) {
		alert("비밀번호를 입력하세요.");
		frm.pw.focus();
		return false;
	}
	
	// 비밀번호 길이 검사 (5~15자)
	if (pw.length < 5 || pw.length > 15) {
		alert("비밀번호는 5자 이상 15자 이하로 입력하세요.");
		frm.pw.focus();
		return false;
	}

	if (frm.pw.value != frm.repw.value) {
		alert("비밀번호가 맞지 않습니다.");
		frm.repw.value = "";
		frm.repw.focus();
		return false;
	}
	

	frm.submit();
}

// 정보변경을 위한 비밀번호,전화번호 확인
function checkPwAndTel() {
	var frm = document.frm;

	if (!frm.pw.value) {
		alert("비밀번호를 입력하세요.");
		frm.pw.focus();
		return false;
	}

	// 전화번호 숫자만 허용 (하이픈 금지)
	var telRegex = /^[0-9]+$/;
	if (!frm.tel.value || !telRegex.test(frm.tel.value)) {
		alert("전화번호를 -없이 숫자로 입력하세요.");
		frm.tel.focus();
		return false;
	}

	frm.submit();
}

// 탈퇴를 위한 비밀번호 확인
function checkPw() {
	if (!frm.pw.value) {
		alert("비밀번호를 입력하세요.");
		frm.pw.focus();
		return false;
	}

	frm.submit();
}

function checkDelete() {
	const id = document.getElementById("managerId").value;

	window.open(
		'/checkDelete?id=' + encodeURIComponent(id),  // 팝업에 띄울 URL (Controller 매핑 경로)
		'회원탈퇴확인',               // 창 이름
		'width=700,height=500,left=300,top=200'  // 팝업 크기 및 위치
	);
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

