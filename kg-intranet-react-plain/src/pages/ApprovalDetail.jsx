import { useEffect, useMemo, useState } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import StatusBadge from '../components/StatusBadge.jsx'
import Stepper from '../components/Stepper.jsx'
import { FLOW } from '../data/approvals.js'
import * as ApprovalService from '../services/approvals.js'
import { useAuth } from '../context/AuthContext.jsx'

export default function ApprovalDetail() {
  const { id } = useParams()
  const approvalId = Number(id || 0)
  const nav = useNavigate()
  const { user } = useAuth()
  const [item, setItem] = useState(null)
  const [history, setHistory] = useState([])
  const [loading, setLoading] = useState(false)
  const [processing, setProcessing] = useState(false)

  useEffect(() => {
    let mounted = true
    const sync = async () => {
      setLoading(true)
      try {
        let it = null
        try {
          it = await ApprovalService.getById(approvalId)
        } catch (err) {
          console.warn('[ApprovalDetail] getById failed, fallback to list:', err)
        }
        if (!it) {
          try {
            const list = await ApprovalService.list()
            it = (list || []).find(a => Number(a.id) === approvalId) || null
          } catch (err) {
            console.warn('[ApprovalDetail] list fallback failed:', err)
          }
        }
        let hist = []
        try {
          hist = await ApprovalService.history(approvalId)
        } catch (err) {
          console.warn('[ApprovalDetail] history fetch failed:', err)
        }
        if (mounted) {
          setItem(it)
          setHistory(hist || [])
        }
      } finally {
        if (mounted) setLoading(false)
      }
    }
    sync()
    const unsub = ApprovalService.subscribe(sync)
    return () => { mounted = false; unsub && unsub() }
  }, [approvalId])

  const latestStatusId = useMemo(() => {
    if (!history || history.length === 0) return null
    const sorted = [...history].sort((a, b) => new Date(a.logtime || a.at || 0) - new Date(b.logtime || b.at || 0))
    return sorted[sorted.length - 1]?.statusId || null
  }, [history])

  const baseState = item?.state || 'IN_REVIEW'
  const displayState = latestStatusId === 3 ? 'APPROVED' : latestStatusId === 4 ? 'REJECTED' : baseState

  const currentStep = useMemo(() => {
    if (latestStatusId === 3) return 2
    if (latestStatusId === 4) return 1
    if (item?.state === 'APPROVED') return 2
    if (item?.state === 'IN_REVIEW') return 1
    return 0
  }, [item?.state, latestStatusId])

  const refreshDetail = async () => {
    const refreshed = await ApprovalService.getById(approvalId)
    setItem(refreshed)
    setHistory(await ApprovalService.history(approvalId))
  }

  const onApprove = async () => {
    if (!confirm('이 결재를 승인하시겠습니까?')) return
    setProcessing(true)
    try {
      await ApprovalService.approveStep(approvalId)
      await refreshDetail()
      alert('승인되었습니다.')
    } catch (err) {
      console.error(err)
      alert('승인 중 오류가 발생했습니다.')
    } finally {
      setProcessing(false)
    }
  }

  const onReject = async () => {
    if (!confirm('이 결재를 반려하시겠습니까?')) return
    setProcessing(true)
    try {
      await ApprovalService.rejectAll(approvalId)
      await refreshDetail()
      alert('반려되었습니다.')
    } catch (err) {
      console.error(err)
      alert('반려 중 오류가 발생했습니다.')
    } finally {
      setProcessing(false)
    }
  }

  const onResubmit = () => {
    // 기존 반려 건을 완료 목록에서 숨기기 위해 상태를 IN_REVIEW로 덮어씀(로컬)
    ApprovalService.overrideState(approvalId, 'IN_REVIEW')
    nav('/approvals/create', {
      state: {
        subject: item?.title || '',
        approval_content: item?.body || item?.approvalContent || '',
        task_seq: item?.taskId || '',
        file_name: item?.fileName || item?.file_name || ''
      }
    })
  }

  const handleDownload = () => {
    const fileName = item?.fileName || item?.file_name
    if (!fileName) return

    // 백엔드 다운로드 엔드포인트
    const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
    const downloadUrl = `${baseUrl}/download/approval/${approvalId}`

    // iframe을 사용한 다운로드 (새 창 열지 않음)
    const iframe = document.createElement('iframe')
    iframe.style.display = 'none'
    iframe.src = downloadUrl
    document.body.appendChild(iframe)

    // 다운로드 후 iframe 제거
    setTimeout(() => {
      document.body.removeChild(iframe)
    }, 1000)
  }

  const myId = user?.empNo || user?.id
  const isMyApproval = useMemo(() => {
    if (!item) return false
    if (item.approverId) return String(item.approverId) === String(myId)
    if (Array.isArray(item.approvers)) return item.approvers.some(a => String(a.id) === String(myId))
    return false
  }, [item, myId])
  const isPending = displayState === 'IN_REVIEW'
  const canHandle = isMyApproval && isPending
  const isRejected = displayState === 'REJECTED'
  const isRequester = item && String(item.requesterId || item.requester_id || item.requester) === String(myId)

  if (!item && !loading) {
    return (
      <section className="page">
        <h1 className="page-title">전자결재</h1>
        <p>결재 문서를 찾을 수 없습니다.</p>
        <button className="btn" onClick={() => nav('/approvals')}>목록</button>
      </section>
    )
  }

  if (loading && !item) {
    return (
      <section className="page">
        <p>불러오는 중...</p>
      </section>
    )
  }

  return (
    <section className="page">
      <div className="detail-head" style={{ marginBottom: 8 }}>
        <div>
          <p className="detail-chip">전자결재</p>
          <h1 className="page-title">결재 상세</h1>
        </div>
        {canHandle && (
          <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
            <button className="btn primary" onClick={onApprove} disabled={processing}>승인</button>
            <button className="btn danger" onClick={onReject} disabled={processing}>반려</button>
          </div>
        )}
      </div>
      <article className="card" style={{ maxWidth: 1000, display: 'grid', gap: 16 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: 12, flexWrap: 'wrap' }}>
          <div>
            <h2 style={{ fontSize: '1.25rem', marginBottom: 6 }}>{item?.title || ''}</h2>
            <div className="meta">
              상태 <StatusBadge state={displayState} /> · 기안자 {item?.requester || ''} · {item?.date || ''}
            </div>
            {item?.taskId && (
              <div className="meta" style={{ marginTop: 4 }}>
                관련 업무: <Link to={`/tasks/${item.taskId}`} className="link link-accent-1">#{item.taskId}</Link>
              </div>
            )}
          </div>
          <div className="detail-chip" style={{ fontSize: 14, alignSelf: 'center' }}>
            문서번호: #{item?.id || approvalId}
          </div>
        </div>

        <div className="content" style={{ marginTop: 6, lineHeight: 1.6 }}>
          {item?.body || '본문/첨부는 추후 백엔드 연동 시 교체됩니다.'}
        </div>

        {/* 첨부 파일 */}
        {(item?.fileName || item?.file_name) && (
          <div style={{ marginTop: 12 }}>
            <h3 style={{ marginBottom: 8, fontSize: 16, fontWeight: 600 }}>첨부 파일</h3>
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
                <span style={{ fontWeight: 500 }}>{item?.fileName || item?.file_name}</span>
              </div>
              <button className="btn sm primary" onClick={handleDownload}>다운로드</button>
            </div>
          </div>
        )}

        {Array.isArray(item?.approvers) && item.approvers.length > 0 && (
          <ApproverLine item={item} displayState={displayState} />
        )}

        {isRejected && isRequester && (
          <div className="card" style={{ borderColor: 'color-mix(in srgb, var(--accent-1, #f7c5d8), #9f1239 16%)' }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 12, flexWrap: 'wrap' }}>
              <div>
                <h3 style={{ marginBottom: 6 }}>반려됨 · 다시 상신할 수 있습니다</h3>
                <p className="page-desc" style={{ margin: 0 }}>내용을 보완한 후 재상신을 눌러주세요.</p>
              </div>
              <button className="btn primary" onClick={onResubmit} disabled={processing}>재상신</button>
            </div>
          </div>
        )}
      </article>

      <div className="detail-actions" style={{ justifyContent: 'flex-end', marginTop: 16 }}>
        <button className="btn" type="button" onClick={() => nav(-1)}>뒤로</button>
        <Link className="btn" to="/approvals">목록</Link>
      </div>

    </section>
  )
}

function History({ items }) {
  if (!items || items.length === 0) {
    return <div className="page-desc">변경 이력이 없습니다.</div>
  }
  return (
    <ul className="stack" style={{ listStyle: 'none', padding: 0 }}>
      {items.slice().reverse().map((r, i) => (
        <li key={`${r.at}-${i}`} className="card" style={{ padding: '8px 12px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
            <strong>{r.action === 'APPROVED' ? '승인' : r.action === 'REJECTED' ? '반려' : r.action}</strong>
            <span style={{ color: 'var(--fg-muted)' }}>{new Date(r.at || r.logtime || Date.now()).toLocaleString()}</span>
          </div>
        </li>
      ))}
    </ul>
  )
}

function ApproverLine({ item, displayState }) {
  const currentIdx = item.approvers.findIndex(a => a.status === 'PENDING')
  const isFinal = displayState === 'APPROVED' || displayState === 'REJECTED'
  return (
    <div className="card" style={{ marginTop: 8 }}>
      <h3 style={{ marginBottom: 8 }}>결재선</h3>
      <ol style={{ listStyle: 'none', padding: 0, margin: 0 }}>
        {item.approvers.map((a, i) => {
          const status = isFinal ? displayState : a.status
          const label = status === 'APPROVED' ? '승인' : status === 'REJECTED' ? '반려' : (i === currentIdx ? '검토중' : '대기')
          return (
            <li
              key={a.id ?? i}
              style={{ display: 'flex', justifyContent: 'space-between', padding: '8px 0', borderBottom: '1px solid var(--line)' }}
            >
              <span>{i + 1}. {a.name} {a.dept ? <span style={{ color: 'var(--fg-muted)' }}>({a.dept})</span> : null}</span>
              <span className={`status-badge ${status === 'APPROVED' ? 'is-approved' : status === 'REJECTED' ? 'is-rejected' : 'is-review'}`}>
                {label}
              </span>
            </li>
          )
        })}
      </ol>
    </div>
  )
}
