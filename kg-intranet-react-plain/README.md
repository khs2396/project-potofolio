# KG Intranet — React/Vite + Plain CSS (v5)

## 설치
```bash
npm i
npm run dev
```

## 주요 페이지
- Home `/`
- Auth: `/login`, `/signup`, `/forgot-password`, `/profile`(보호), `/settings`(보호)
- 공지: `/announcements`, `/announcements/:id`, `/announcements/write`(보호)
- 게시판: `/boards`, `/boards/:id`, `/boards/write`(보호)
- 전자결재: `/approvals`, `/approvals/:id`, `/approvals/create`(보호), `/approvals/inbox`, `/approvals/outbox`
- 업무: `/tasks`, `/tasks/:id`, `/tasks/create`
- 임직원: `/directory`, `/directory/:id`, `/directory/:id/edit`(보호)

## 테마
- 라이트/다크 토글(헤더 우측). `localStorage.kg_theme`에 저장.
- 초기 로딩 스크립트로 FOUC 방지.

## 페이지 추가 방법
1) `src/pages/Example.jsx` 생성
2) `src/App.jsx`에서 import + `<Route>` 추가

## v7
- 테마: 라이트/다크 2종 전환(헤더 토글), 컬러/타이포 토큰 정리
- 전자결재: 상세/진행 스텝 추가, 승인·반려 로컬 상태 반영, 히스토리 기록(localStorage)
- 목록/카드: 로컬 상태 캐싱으로 표시 유지

## v8.1
- AuthContext 새로고침 시 구문 오류 수정(로그인 상태 초기화 보완)
- 전자결재: 결재함 필터/정렬 추가, 로컬 결재 흐름(승인/반려/대기) 분리 저장

## v9
- 더미 데이터 세트 추가(announcements/documents/approvals/employees): 현재는 localStorage 기반, 추후 REST로 교체 예정
- 공지/문서 페이지가 더미 데이터로 우선 동작(향후 API로 교체)
- 직원 디렉터리 상세 페이지 `/directory/:id` 추가
