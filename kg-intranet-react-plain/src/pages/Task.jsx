import { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import Table from '../components/Table.jsx'
import SearchBar from '../components/SearchBar.jsx'
import { useAuth } from '../context/AuthContext.jsx'
import * as TasksApi from '../services/tasks.api.js'
import '../styles/detail-pages.css'

export default function Tasks() {
  const { user } = useAuth()
  const roleText = (user?.role || user?.title || '').toLowerCase()
  const isManager = ['manager', '부장', '관리자', '팀장', '리더', 'lead', 'gm'].some(k => roleText.includes(k))

  // 부장: 지시한 업무 / 전체, 직원: 내 업무함 / 전체
  const defaultFilter = isManager ? 'requester_id' : 'assignee_id'
  const [filter, setFilter] = useState(defaultFilter)
  const [items, setItems] = useState([])
  const [q, setQ] = useState('')
  const [loading, setLoading] = useState(false)

  const fetchList = async (nextFilter = filter) => {
    setLoading(true)
    try {
      const params = {}
      const userId = user?.empNo || user?.id
      if (nextFilter === 'assignee_id' && userId) params.assignee_id = userId
      if (nextFilter === 'requester_id' && userId) params.requester_id = userId
      const data = await TasksApi.list(params)
      setItems(data || [])
    } catch (err) {
      console.error('업무 목록 조회 실패:', err)
      setItems([])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { fetchList(filter) }, [filter, user])

  const filtered = useMemo(() => {
    const term = q.toLowerCase()
    return items.filter(t => {
      // 결재 요청된 업무 제외 (statusSeq: 1=작성중만 표시, 2=진행중/3=승인됨/4=반려됨 제외)
      if (t.statusSeq && t.statusSeq !== 1) return false

      return (t.subject || '').toLowerCase().includes(term) ||
        (t.requestName || '').toLowerCase().includes(term) ||
        (t.assigneeName || '').toLowerCase().includes(term)
    })
  }, [items, q])

  return (
    <section className="page">
      <div className="detail-head">
        <div>
          <p className="detail-chip">업무</p>
          <h1 className="page-title">업무함</h1>
          <p className="page-desc">담당/지시한 업무를 확인하세요.</p>
        </div>
        <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
          {isManager ? (
            <>
              <Link to="/tasks/create" className="btn">업무 등록</Link>
              <Link to="/approvals/inbox" className="btn primary">결재함</Link>
            </>
          ) : (
            <Link to="/approvals/outbox" className="btn primary">결재 요청함</Link>
          )}
        </div>
      </div>

      <div className="card" style={{ marginBottom: 12 }}>
        <div className="toolbar" style={{ gap: 12, flexWrap: 'wrap' }}>
          <SearchBar placeholder="업무 검색" onSearch={setQ} />
          <div style={{ display: 'flex', gap: 8 }}>
            <button
              className={`btn sm ${filter === (isManager ? 'requester_id' : 'assignee_id') ? 'primary' : ''}`}
              onClick={() => setFilter(isManager ? 'requester_id' : 'assignee_id')}
            >
              {isManager ? '내가 지시한 업무' : '내 업무함'}
            </button>
            <button
              className={`btn sm ${filter === 'all' ? 'primary' : ''}`}
              onClick={() => setFilter('all')}
            >
              전체 보기
            </button>
          </div>
          <button className="btn sm" type="button" onClick={() => fetchList(filter)} disabled={loading}>
            새로고침
          </button>
        </div>
      </div>

      <Table className="modern approvals-table" wrapClassName="modern" cols={[60, null, 140, 140, 140, 100]}>
        <thead>
          <tr>
            <th className="col-id">번호</th>
            <th>제목</th>
            <th>지시자</th>
            <th>담당자</th>
            <th className="col-date">요청일</th>
            <th>상세</th>
          </tr>
        </thead>
        <tbody>
          {loading
            ? <tr><td colSpan="6" style={{ textAlign: 'center' }}>불러오는 중...</td></tr>
            : filtered.length === 0
              ? <tr><td colSpan="6" style={{ textAlign: 'center' }}>업무가 없습니다.</td></tr>
              : filtered.map(t => (
                <tr key={t.taskSeq || t.id}>
                  <td className="col-id">{t.taskSeq}</td>
                  <td>
                    <span className="cell-ellipsis">{t.subject}</span>
                  </td>
                  <td>{t.requestName || '-'}</td>
                  <td>{t.assigneeName || '-'}</td>
                  <td className="col-date"><span className="date-text">{String(t.createAt || '').slice(0, 10)}</span></td>
                  <td><Link className="btn sm primary" to={`/tasks/${t.taskSeq}`}>상세</Link></td>
                </tr>
              ))
          }
        </tbody>
      </Table>
    </section>
  )
}
