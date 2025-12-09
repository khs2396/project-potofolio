// 발주 주문번호 클릭시
function iscode(seq) {
	console.log("code:", code);
	if(!code || code == 0 || code === "0") {
		alert("접근권한이 없습니다.");
		return;
	} else {
		location.href = "orderingView?seq=" + seq;
	}
}

function isDelete(seq) {
	if(confirm("정말로 취소하시겠습니까?")) {
		location.href = "orderingDelete?seq=" + seq;
	} 
}