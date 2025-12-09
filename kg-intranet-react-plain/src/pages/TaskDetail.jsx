import { useEffect, useState } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import StatusBadge from '../components/StatusBadge.jsx'
import * as TasksApi from '../services/task.api.js'
import '../styles/detail-pages.css'

export default function TaskDetail() {
  const { id } = useParams()
  const nav = useNavigate()
  const { user } = useAuth()
  const [item, setItem] = useState(null)
  const [loading, setLoading] = useState(false)

  // 부장인지 확인
  const roleText = (user?.role || user?.title || '').toLowerCase()
  const isManager = (user?.roleId === 5) || ['gm', 'manager', 'lead', 'leader', '부장', '관리자', '책임'].some(k => roleText.includes(k))

  useEffect(() => {
    if (!id || id === 'undefined') {
      console.error('유효하지 않은 task ID:', id)
      return
    }

    let mounted = true
    const fetchOne = async () => {
      setLoading(true)
      try {
        const data = await TasksApi.getOne(id)
        if (mounted) setItem(data)
      } catch (err) {
        console.error('TaskDetail - 에러:', err)
      } finally {
        if (mounted) setLoading(false)
      }
    }
    fetchOne()
    return () => { mounted = false }
  }, [id])

  if (loading) {
    return (
      <section className="page">
        <p>불러오는 중...</p>
      </section>
    )
  }

  if (!item) {
    return (
      <section className="page">
        <h1 className="page-title">업무 상세</h1>
        <p>업무를 찾을 수 없습니다.</p>
        <Link to="/tasks" className="btn">목록</Link>
      </section>
    )
  }

  const taskId = item.task_seq || item.taskSeq || item.id
  const requester =
    (item.request_name || item.requestName) ||
    (item.requestId && item.requestId.name) ||
    (typeof item.requestId === 'string' ? item.requestId : '') ||
    '-'
  const assignee =
    (item.assignee_name || item.assigneeName) ||
    (item.assigneeId && item.assigneeId.name) ||
    (typeof item.assigneeId === 'string' ? item.assigneeId : '') ||
    '-'
  const createdAt = item.create_at || item.createAt || item.logtime || ''

  // 업무 상태 매핑 (statusSeq: 1=작성중, 2=진행중, 3=승인, 4=반려)
  const getStatusState = (statusSeq) => {
    if (statusSeq === 1) return 'DRAFT'
    if (statusSeq === 2) return 'IN_REVIEW'
    if (statusSeq === 3) return 'APPROVED'
    if (statusSeq === 4) return 'REJECTED'
    return 'DRAFT'
  }

  const getStatusText = (statusSeq) => {
    if (statusSeq === 1) return '작성중'
    if (statusSeq === 2) return '진행중'
    if (statusSeq === 3) return '승인됨'
    if (statusSeq === 4) return '반려됨'
    return '알 수 없음'
  }

  const handleDownload = () => {
    const fileName = item.fileName || item.file_name
    if (!fileName) return

    // 백엔드 다운로드 엔드포인트
    const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
    const downloadUrl = `${baseUrl}/download/task/${taskId}`

    // a 태그로 다운로드 트리거 (X-Frame-Options: DENY 대응)
    const link = document.createElement('a')
    link.href = downloadUrl
    link.style.display = 'none'
    document.body.appendChild(link)
    link.click()
    setTimeout(() => {
      document.body.removeChild(link)
    }, 500)
  }

  return (
    <section className="page">
      <div className="detail-head">
        <div>
          <p className="detail-chip">업무</p>
          <h1 className="page-title">{item.subject || '제목 없음'}</h1>
        </div>
        <div style={{ display: 'flex', gap: 8 }}>
          <Link to="/tasks" className="btn">목록</Link>
          {!isManager && (
            <button className="btn primary" onClick={() => nav(`/approvals/create?taskSeq=${taskId}`)}>결재 요청</button>
          )}
        </div>
      </div>

      {/* 업무 정보 카드 */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: 16, marginBottom: 16 }}>
        <div className="card" style={{ padding: '24px' }}>
          <div style={{ fontSize: 16, color: 'var(--text-muted)', marginBottom: 10 }}>업무 번호</div>
          <div style={{ fontSize: 32, fontWeight: 600 }}>#{taskId}</div>
        </div>
        <div className="card" style={{ padding: '24px' }}>
          <div style={{ fontSize: 16, color: 'var(--text-muted)', marginBottom: 10 }}>상태</div>
          <div style={{ fontSize: 24, fontWeight: 600 }}>
            <StatusBadge state={getStatusState(item.statusSeq)} />
          </div>
        </div>
        <div className="card" style={{ padding: '24px' }}>
          <div style={{ fontSize: 16, color: 'var(--text-muted)', marginBottom: 10 }}>작성일</div>
          <div style={{ fontSize: 24, fontWeight: 500 }}>{String(createdAt).slice(0, 10)}</div>
        </div>
      </div>

      {/* 담당자 정보 */}
      <div className="card" style={{ marginBottom: 16 }}>
        <h3 style={{ marginBottom: 16, fontSize: 18, fontWeight: 600 }}>담당자 정보</h3>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: 16 }}>
          <div>
            <div style={{ fontSize: 14, color: 'var(--text-muted)', marginBottom: 4 }}>지시자</div>
            <div style={{ fontSize: 16, fontWeight: 500 }}>{requester}</div>
          </div>
          <div>
            <div style={{ fontSize: 14, color: 'var(--text-muted)', marginBottom: 4 }}>담당자</div>
            <div style={{ fontSize: 16, fontWeight: 500 }}>{assignee}</div>
          </div>
        </div>
      </div>

      {/* 업무 내용 */}
      <div className="card" style={{ marginBottom: 16 }}>
        <h3 style={{ marginBottom: 16, fontSize: 18, fontWeight: 600 }}>업무 내용</h3>
        <div style={{
          padding: '16px',
          backgroundColor: 'var(--bg-secondary, #f8f9fa)',
          borderRadius: '8px',
          lineHeight: '1.6',
          whiteSpace: 'pre-wrap',
          minHeight: '120px'
        }}>
          {item.taskContent || '내용이 없습니다.'}
        </div>
      </div>

      {/* 첨부 파일 */}
      {item.fileName && (
        <div className="card">
          <h3 style={{ marginBottom: 16, fontSize: 18, fontWeight: 600 }}>첨부 파일</h3>
          <div style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            padding: '12px 16px',
            backgroundColor: 'var(--bg-secondary, #f8f9fa)',
            borderRadius: '8px',
            gap: 8
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <span style={{ fontSize: 20 }}>📎</span>
              <span style={{ fontWeight: 500 }}>{item.fileName || item.file_name}</span>
            </div>
            <button className="btn sm primary" onClick={handleDownload}>다운로드</button>
          </div>
        </div>
      )}
    </section>
  )
}
