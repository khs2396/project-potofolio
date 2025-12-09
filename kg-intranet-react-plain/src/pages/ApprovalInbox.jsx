import { useEffect, useMemo, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import Table from '../components/Table.jsx'
import SearchBar from '../components/SearchBar.jsx'
import * as ApprovalService from '../services/approvals.js'
import { useAuth } from '../context/AuthContext.jsx'
import '../styles/detail-pages.css'

export default function ApprovalInbox() {
  const [params, setParams] = useSearchParams()
  const qParam = params.get('q') || ''
  const { user } = useAuth()

  const [q, setQ] = useState(qParam)
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
  }, [qParam])

  useEffect(() => {
    let mounted = true
    const fetchData = async () => {
      const list = await ApprovalService.list()
      const withStatus = await Promise.all((list || []).map(async (item) => {
        const hist = await ApprovalService.history(item.id)
        const status = mapStatus(hist)
        return { ...item, ...status }
      }))

      // 내 결재이면서 진행중(IN_REVIEW) 상태만 필터링
      const myApprovals = (withStatus || []).filter(item => {
        const myId = user?.empNo || user?.id
        const isMyApproval = item.approverId === myId || String(item.approverId) === String(myId)
        const isPending = item.state === 'IN_REVIEW'
        return isMyApproval && isPending
      })

      if (mounted) setData(myApprovals)
    }
    fetchData()
    const unsub = ApprovalService.subscribe(fetchData)
    return () => { mounted = false; unsub && unsub() }
  }, [user])

  const filtered = useMemo(() => {
    const term = q.toLowerCase()
    return data.filter(a =>
      ((a.title || '').toLowerCase().includes(term) ||
        (a.requester || '').toLowerCase().includes(term))
    )
  }, [data, q])

  const onSearch = (text) => {
    setQ(text)
    setParams({ q: text })
  }

  return (
    <section className="page">
      <div className="detail-head">
        <div>
          <p className="detail-chip">전자결재</p>
          <h1 className="page-title">결재함</h1>
          <p className="page-desc">내게 올라온 결재를 확인하고 처리하세요.</p>
        </div>
      </div>

      <div className="card" style={{ marginBottom: 16, padding: '16px 20px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 12 }}>
          <SearchBar onSearch={onSearch} defaultValue={q} placeholder="결재 문서 검색" />
          <div className="detail-chip" style={{ fontSize: 16, padding: '8px 16px' }}>
            대기중: <strong>{data.length}건</strong>
          </div>
        </div>
      </div>

      <Table className="modern approval-center" wrapClassName="modern approval-center" cols={[90, null, 180, 150, 180]}>
        <thead>
          <tr>
            <th className="col-appr-id">번호</th>
            <th className="col-appr-title">제목</th>
            <th className="col-appr-requester">기안자</th>
            <th className="col-appr-date">요청일</th>
            <th>상세</th>
          </tr>
        </thead>
        <tbody>
          {filtered.length === 0
            ? <tr><td colSpan="5" style={{ textAlign: 'center' }}>처리 대기중인 결재가 없습니다.</td></tr>
            : filtered.map(d => (
              <tr key={d.id}>
                <td className="col-appr-id">{d.id}</td>
                <td className="col-appr-title">
                  <span className="cell-ellipsis">{d.title}</span>
                </td>
                <td className="col-appr-requester">{d.requester}</td>
                <td className="col-appr-date">{d.date}</td>
                <td>
                  <Link className="btn sm primary" to={`/approvals/${d.id}`}>상세</Link>
                </td>
              </tr>
            ))
          }
        </tbody>
      </Table>
    </section>
  )
}
