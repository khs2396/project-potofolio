import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import SearchBar from '../components/SearchBar.jsx'
import Table from '../components/Table.jsx'
import StatusBadge from '../components/StatusBadge.jsx'
import * as ApprovalService from '../services/approvals.js'
import * as AnnService from '../services/announcements.js'
import * as EmpService from '../services/employees.js'
import * as BoardService from '../services/boards.api.js'
import * as TasksApi from '../services/task.api.js'
import * as AttendanceApi from '../services/attendance.api.js'
import '../styles/home.css'

const SEARCH_OPTIONS = [
  { value: 'APPROVALS', label: '결재' },
  { value: 'PEOPLE', label: '사람' },
  { value: 'ANN', label: '공지' },
]

export default function Home() {
  const nav = useNavigate()
  const { user } = useAuth()
  const roleText = (user?.role || user?.title || '').toLowerCase()
  const isManager = (user?.roleId === 5) || ['gm', 'manager', 'lead', 'leader', '부장', '관리자', '책임'].some(k => roleText.includes(k))

  const [approvals, setApprovals] = useState([])
  const [anns, setAnns] = useState(() => AnnService.list())
  const [emps, setEmps] = useState(() => EmpService.list())
  const [boardTotal, setBoardTotal] = useState(0)
  const [tasks, setTasks] = useState([])
  const [searchCategory, setSearchCategory] = useState('APPROVALS')

  useEffect(() => {
    let mounted = true
    const sync = async () => {
      const list = await ApprovalService.list()
      if (!mounted) return

      const empNo = user?.empNo || user?.id
      const userName = user?.name || user?.email || ''

      const mapStatus = (hist = []) => {
        if (!hist || hist.length === 0) return { state: 'IN_REVIEW', statusName: '검토중' }
        const sorted = [...hist].sort((a, b) => new Date(a.logtime || a.at || 0) - new Date(b.logtime || b.at || 0))
        const latest = sorted[sorted.length - 1]
        const statusId = latest.statusId
        if (statusId === 3) return { state: 'APPROVED', statusName: '승인' }
        if (statusId === 4) return { state: 'REJECTED', statusName: '반려' }
        return { state: 'IN_REVIEW', statusName: '검토중' }
      }

      const withStatus = await Promise.all((list || []).map(async (item) => {
        const hist = await ApprovalService.history(item.id)
        const status = mapStatus(hist)
        return { ...item, ...status }
      }))

      if (isManager) {
        // 부장: 내가 결재해야 하고 대기 중인 건 (결재함 기준)
        const myPending = withStatus.filter(item => {
          const isMine = String(item.approverId || '').toLowerCase() === String(empNo || '').toLowerCase()
          const isPending = item.state !== 'APPROVED' && item.state !== 'REJECTED'
          return isMine && isPending
        })
        setApprovals(myPending || [])
      } else {
        // 직원: 내가 올린 대기 건 (결재 요청함 기준)
        const myRequests = withStatus.filter(item => {
          const isMine = String(item.requesterId || '').toLowerCase() === String(empNo || '').toLowerCase() ||
                         String(item.requester || '').toLowerCase() === String(userName).toLowerCase()
          const isPending = item.state !== 'APPROVED' && item.state !== 'REJECTED'
          return isMine && isPending
        })
        setApprovals(myRequests || [])
      }
    }
    sync()
    const unsub = ApprovalService.subscribe(sync)
    return () => { mounted = false; unsub && unsub() }
  }, [user, isManager])

  useEffect(() => {
    let mounted = true
    const fetchTasks = async () => {
      const empNo = user?.empNo || user?.id
      try {
        const params = isManager ? {} : (empNo ? { assignee_id: empNo } : {})
        const list = await TasksApi.list(params)
        const filtered = (list || []).filter(t => !t.statusSeq || t.statusSeq === 1)
        if (mounted) setTasks(filtered)
      } catch {
        if (mounted) setTasks([])
      }
    }
    fetchTasks()
    return () => { mounted = false }
  }, [user, isManager])

  useEffect(() => {
    let mounted = true
    const sync = async () => {
      try {
        const list = await AnnService.ensureLoaded()
        if (mounted) setAnns(list || [])
      } catch {
        if (mounted) setAnns([])
      }
    }
    sync()
    const unsub = AnnService.subscribe(sync)
    return () => { mounted = false; unsub && unsub() }
  }, [])

  useEffect(() => {
    let mounted = true
    const sync = async () => {
      try {
        const list = await EmpService.ensureLoaded()
        if (mounted) setEmps(list || [])
      } catch {
        if (mounted) setEmps([])
      }
    }
    sync()
    const unsub = EmpService.subscribe(sync)
    return () => { mounted = false; unsub && unsub() }
  }, [])

  useEffect(() => {
    let mounted = true
    const fetchMyBoardCount = async () => {
      if (!user?.depno) {
        setBoardTotal(0)
        return
      }
      try {
        let page = 1
        let totalPages = 1
        let myCount = 0
        do {
          const res = await BoardService.listBoards(page)
          totalPages = res?.totalP || 1
          const items = res?.items || []
          myCount += items.filter(it => it.depno === user.depno).length
          page += 1
        } while (page <= totalPages)
        if (mounted) setBoardTotal(myCount)
      } catch {
        if (mounted) setBoardTotal(0)
      }
    }
    fetchMyBoardCount()
    return () => { mounted = false }
  }, [user?.depno])

  const kpi = {
    requests: approvals.length,
  }

  const boardCard = { key: 'board', title: '게시판', value: boardTotal, link: '/boards', linkText: '게시판 바로가기', accent: 'link-accent-1' }
  const taskCard = isManager
    ? { key: 'tasks', title: '업무 생성', value: tasks.length, link: '/tasks/create', linkText: '업무 등록', accent: 'link-accent-1' }
    : { key: 'tasks', title: '내 업무', value: tasks.length, link: '/tasks', linkText: '업무 확인', accent: 'link-accent-1' }

  const kpiCards = isManager
    ? [
        { key: 'inbox', title: '내 결재', value: kpi.requests, link: '/approvals/inbox', linkText: '결재함', accent: 'link-accent-1' },
        taskCard,
        boardCard,
      ]
    : [
        { key: 'requests', title: '내 결재요청', value: kpi.requests, link: '/approvals/outbox', linkText: '결제 요청함', accent: 'link-accent-1' },
        taskCard,
        boardCard,
      ]

  const handleSearch = (text) => {
    const term = text?.trim()
    if (!term) return
    if (searchCategory === 'APPROVALS') nav(`/approvals?q=${encodeURIComponent(term)}`)
    else if (searchCategory === 'PEOPLE') nav(`/directory?q=${encodeURIComponent(term)}`)
    else if (searchCategory === 'ANN') nav(`/announcements?q=${encodeURIComponent(term)}`)
  }

  const topApprovals = approvals.slice(0, 5)
  const topTasks = tasks.slice(0, 5)
  const recentAnns = anns.slice(0, 5)
  const people = emps.slice(0, 6)

  return (
    <section className="home-page">
      <div className="hero">
        <div className="hero-left">
          <div className="hero-text">
            <div className="brand-badge">KG</div>
            <div>
              <h1 className="hero-title">
                {user?.name ? <><span>{user.name}</span>님 환영합니다</> : '인트라넷 홈'}
              </h1>
              <p className="hero-copy">바로 검색해서 필요한 정보를 찾아보세요.</p>
            </div>
          </div>
        </div>
        <div className="hero-right">
          <div className="hero-search-row">
            <select
              className="input sm hero-search-select"
              value={searchCategory}
              onChange={e => setSearchCategory(e.target.value)}
            >
              {SEARCH_OPTIONS.map(opt => (
                <option key={opt.value} value={opt.value}>{opt.label}</option>
              ))}
            </select>
            <SearchBar onSearch={handleSearch} placeholder="검색어를 입력하세요" />
          </div>
          <div className="quick-actions">
            {isManager && (
              <Link className="btn sm primary" to="/tasks/create">업무 요청</Link>
            )}
          </div>
        </div>
      </div>

      <div className="kpi-grid">
        {kpiCards.map(card => (
          <div className="kpi" key={card.key}>
            <div className="kpi-title">{card.title}</div>
            <div className="kpi-value">{card.value}</div>
            <div className="kpi-foot"><Link to={card.link} className={`link ${card.accent}`}>{card.linkText}</Link></div>
          </div>
        ))}
      </div>

      <div className="main-grid">
        <div className="card card--approval">
          <div className="card-head">
            <h2>최근 결재</h2>
            {isManager ? (
              <Link to="/approvals/inbox" className="link link-accent-1">결재함</Link>
            ) : (
              <Link to="/approvals/outbox" className="link link-accent-1">결제 요청함</Link>
            )}
          </div>
          <Table className="modern approvals-table" wrapClassName="modern" cols={[60, null, 120, 120, 170]}>
            <thead>
              <tr>
                <th className="col-id">번호</th>
                <th>제목</th>
                <th>요청자</th>
                <th>상태</th>
                <th className="col-date">요청일</th>
              </tr>
            </thead>
            <tbody>
              {topApprovals.length === 0
                ? <tr><td colSpan="5" style={{ textAlign: 'center' }}>표시할 결재가 없습니다.</td></tr>
                : topApprovals.map(d => (
                  <tr key={d.id}>
                    <td className="col-id">{d.id}</td>
                    <td>
                      <span className="cell-ellipsis">
                        <Link className="link link-accent-1" to={`/approvals/${d.id}`}>{d.title}</Link>
                      </span>
                    </td>
                    <td>{d.requester}</td>
                    <td><StatusBadge state={d.state} /></td>
                    <td className="col-date"><span className="date-text">{d.date}</span></td>
                  </tr>
                ))
              }
            </tbody>
          </Table>
        </div>

        <div className="card card--board">
          <div className="card-head">
            <h2>최근 업무</h2>
          </div>
          <Table className="modern approvals-table" wrapClassName="modern" cols={[60, null, 160, 170]}>
            <thead>
              <tr>
                <th className="col-id">번호</th>
                <th>제목</th>
                <th>요청자</th>
                <th className="col-date">요청일</th>
              </tr>
            </thead>
            <tbody>
              {topTasks.length === 0
                ? <tr><td colSpan="4" style={{ textAlign: 'center' }}>표시할 업무가 없습니다.</td></tr>
                : topTasks.map(t => (
                  <tr key={t.id}>
                    <td className="col-id">{t.taskSeq || t.id}</td>
                    <td>
                      <span className="cell-ellipsis">{t.subject || '(제목 없음)'}</span>
                    </td>
                    <td>{t.requestName || '-'}</td>
                    <td className="col-date"><span className="date-text">{String(t.createAt || '').slice(0, 10)}</span></td>
                  </tr>
                ))
              }
            </tbody>
          </Table>
        </div>

        <div className="card card--announcement">
          <div className="card-head">
            <h2>최근 공지</h2>
            <Link to="/announcements" className="link link-accent-3">전체보기</Link>
          </div>
          <ul className="ann-list">
            {recentAnns.length === 0 ? (
              <li className="empty">등록된 공지가 없습니다.</li>
            ) : recentAnns.map(a => (
              <li key={a.id}>
                <Link to={`/announcements/${a.id}`} className="link link-accent-3">{a.title}</Link>
                <span className="ann-date">{a.date}</span>
              </li>
            ))}
          </ul>
        </div>

        <div className="card card--employee">
          <div className="card-head">
            <h2>직원</h2>
            <Link to="/directory" className="link link-accent-4">전체 보기</Link>
          </div>
          <div className="people-grid">
            {people.length === 0 ? (
              <div className="empty">표시할 구성원이 없습니다.</div>
            ) : people.map(p => (
              <Link
                key={p.id}
                to={p.empNo || p.id ? `/directory/${p.empNo || p.id}` : `/directory?q=${encodeURIComponent(p.name)}`}
                className="person link link-accent-4"
              >
                <div className="avatar">{(p.name || '유저').slice(0, 2)}</div>
                <div className="info">
                  <div className="name">{p.name || '이름 없음'}</div>
                  <div className="meta">{p.dept || '부서 없음'}</div>
                </div>
              </Link>
            ))}
          </div>
        </div>
      </div>
    </section>
  )
}
