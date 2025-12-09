// 부서별 파스텔 색상 (카카오톡 스타일)
export const getDeptColor = (dept) => {
  const deptStr = String(dept || '').toLowerCase()

  if (deptStr.includes('인사') || deptStr.includes('hr')) {
    return { bg: '#D1E7F8', text: '#0369A1' } // 파란 파스텔
  }
  if (deptStr.includes('총무') || deptStr.includes('ga')) {
    return { bg: '#D4F4DD', text: '#047857' } // 초록 파스텔
  }
  if (deptStr.includes('개발') || deptStr.includes('dev')) {
    return { bg: '#E0D4F7', text: '#6B21A8' } // 보라 파스텔
  }
  if (deptStr.includes('재무') || deptStr.includes('fid')) {
    return { bg: '#FFE8CC', text: '#C2410C' } // 주황 파스텔
  }
  if (deptStr.includes('영업') || deptStr.includes('sd') || deptStr.includes('sales')) {
    return { bg: '#FFD4E5', text: '#BE123C' } // 핑크 파스텔
  }

  // 기본 색상
  return { bg: '#E5E7EB', text: '#4B5563' } // 회색 파스텔
}
