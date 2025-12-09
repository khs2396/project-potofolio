import { useEffect, useMemo, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import Table from '../components/Table.jsx'
import SearchBar from '../components/SearchBar.jsx'
import StatusBadge from '../components/StatusBadge.jsx'
import FlowStatus from '../components/FlowStatus.jsx'
import * as ApprovalService from '../services/approvals.js'
import { useAuth } from '../context/AuthContext.jsx'
import '../styles/detail-pages.css'

const STATE_OPTIONS = [
  { value: 'ALL', label: '전체 상태' },
  { value: 'DRAFT', label: '기안' },
  { value: 'IN_REVIEW', label: '결재중' },
]

export default function ApprovalOutbox() {
  const [params, setParams] = useSearchParams()
  const qParam = params.get('q') || ''
  const stateParam = params.get('state') || 'ALL'
  const { user } = useAuth()

  const [q, setQ] = useState(qParam)
  const [state, setState] = useState(stateParam)
  const [data, setData] = useState([])

  const mapStatus = (hist = []) => {
    if (!hist || hist.length === 0) return { state: 'IN_REVIEW', statusName: '진행중' }
    const sorted = [...hist].sort((a, b) => new Date(a.logtime || a.at || 0) - new Date(b.logtime || b.at || 0))
    const latest = sorted[sorted.length - 1]
    const statusId = latest.statusId
    if (statusId === 3) return { state: 'APPROVED', statusName: '승인' }
    if (statusId === 4) return { state: 'REJECTED', statusName: '반려' }
    return { state: 'IN_REVIEW', statusName: '진행중' }
  }

  useEffect(() => {
    setQ(qParam)
    setState(stateParam)
  }, [qParam, stateParam])

  useEffect(() => {
    let mounted = true
    const fetchData = async () => {
      const list = await ApprovalService.list()
      const withStatus = await Promise.all((list || []).map(async (item) => {
        const hist = await ApprovalService.history(item.id)
        const status = mapStatus(hist)
        return { ...item, ...status }
      }))

      // 내가 올린 결재 요청 + 진행중(DRAFT/IN_REVIEW)만 유지
      const myRequests = (withStatus || []).filter(item => {
        const myId = user?.empNo || user?.id
        const isMine = item.requesterId === myId || String(item.requesterId) === String(myId)
        const isPending = item.state !== 'APPROVED' && item.state !== 'REJECTED'
        return isMine && isPending
      })

      if (mounted) setData(myRequests)
    }
    fetchData()
    const unsub = ApprovalService.subscribe(fetchData)
    return () => { mounted = false; unsub && unsub() }
  }, [user])

  const filtered = useMemo(() => {
    const term = q.toLowerCase()
    return data.filter(a =>
      (state === 'ALL' || a.state === state) &&
      ((a.title || '').toLowerCase().includes(term) ||
        (a.approver || '').toLowerCase().includes(term))
    )
  }, [data, q, state])

  const onSearch = (text) => {
    setQ(text)
    setParams({ q: text, state })
  }
  const onState = (val) => {
    setState(val)
    setParams({ q, state: val })
  }

  return (
    <section className="page">
      <div className="detail-head">
        <div>
          <p className="detail-chip">전자결재</p>
          <h1 className="page-title">결재 요청함</h1>
          <p className="page-desc">내가 올린 결재 요청의 진행 상황을 확인하세요.</p>
        </div>
      </div>

      <div className="card" style={{ marginBottom: 16 }}>
        <div className="toolbar" style={{ gap: 12, flexWrap: 'wrap' }}>
          <SearchBar onSearch={onSearch} defaultValue={q} placeholder="결재 문서 검색" />
          <select className="input sm" value={state} onChange={e => onState(e.target.value)} style={{ maxWidth: 180 }}>
            {STATE_OPTIONS.map(opt => <option key={opt.value} value={opt.value}>{opt.label}</option>)}
          </select>
        </div>
      </div>

      <Table className="modern approval-center" wrapClassName="modern approval-center" cols={[90, null, 180, 220, 150, 120]}>
        <thead>
          <tr>
            <th className="col-appr-id">번호</th>
            <th className="col-appr-title">제목</th>
            <th className="col-appr-approver">결재자</th>
            <th className="col-appr-state">진행</th>
            <th className="col-appr-date">요청일</th>
            <th>상세</th>
          </tr>
        </thead>
        <tbody>
          {filtered.length === 0
            ? <tr><td colSpan="6" style={{ textAlign: 'center' }}>진행중인 결재 요청이 없습니다.</td></tr>
            : filtered.map(d => (
              <tr key={d.id}>
                <td className="col-appr-id">{d.id}</td>
                <td className="col-appr-title">
                  <span className="cell-ellipsis">{d.title}</span>
                </td>
                <td className="col-appr-approver">{d.approver || '-'}</td>
                <td className="col-appr-state">
                  <div style={{ display: 'flex', flexDirection: 'column', gap: 4, alignItems: 'center' }}>
                    <FlowStatus state={d.state} compact />
                    <StatusBadge state={d.state} />
                  </div>
                </td>
                <td className="col-appr-date">{d.date}</td>
                <td><Link className="btn sm primary" to={`/approvals/${d.id}`}>상세</Link></td>
              </tr>
            ))
          }
        </tbody>
      </Table>
    </section>
  )
}
