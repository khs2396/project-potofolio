import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import Table from '../components/Table.jsx'
import * as Boards from '../services/boards.api.js'
import '../styles/boards.css'

export default function BoardList() {
  const [page, setPage] = useState(1)
  const [data, setData] = useState({ items: [], total: 0, totalP: 1, startPage: 1, endPage: 1 })
  const [loading, setLoading] = useState(false)
  const nav = useNavigate()

  useEffect(() => {
    let active = true
    setLoading(true)
    Boards.listBoards(page, 10)
      .then(res => {
        if (!active) return
        setData({
          items: res?.items || [],
          total: res?.total || 0,
          totalP: res?.totalP || 1,
          startPage: res?.startPage || 1,
          endPage: res?.endPage || 1
        })
      })
      .catch(() => active && setData(prev => ({ ...prev, items: [] })))
      .finally(() => active && setLoading(false))
    return () => { active = false }
  }, [page])

  const onWrite = () => nav('/boards/write')

  return (
    <section className="page board-list kg-skin">
      <div className="board-actions">
        <div>
          <h1 className="page-title">자유 게시판</h1>
          <p className="page-desc">모든 직원이 자유롭게 대화하는 공간입니다.</p>
        </div>
        <button className="btn primary" type="button" onClick={onWrite}>글 작성</button>
      </div>

      <Table className="modern" wrapClassName="modern board-table" cols={[80, null, 200, 160, 90, 100]}>
        <thead>
          <tr>
            <th>번호</th>
            <th>제목</th>
            <th>작성자</th>
            <th>작성일</th>
            <th>조회</th>
            <th>상세</th>
          </tr>
        </thead>
        <tbody>
          {loading ? (
            <tr><td colSpan="6" style={{ textAlign: 'center' }}>불러오는 중...</td></tr>
          ) : data.items.length === 0 ? (
            <tr><td colSpan="6" style={{ textAlign: 'center' }}>게시글이 없습니다.</td></tr>
          ) : (
            data.items.map(item => (
              <tr key={item.boardseq}>
                <td>{item.boardseq}</td>
                <td>
                  <span className="cell-ellipsis">{item.subject}</span>
                </td>
                <td>{item.authorName} ({item.depno})</td>
                <td>{new Date(item.logtime).toLocaleDateString()}</td>
                <td>{item.hit}</td>
                <td><Link className="btn sm primary" to={`/boards/${item.boardseq}`}>상세</Link></td>
              </tr>
            ))
          )}
        </tbody>
      </Table>

      <div className="board-pagination" style={{ justifyContent: 'center', gap: 12 }}>
        <button
          type="button"
          className="btn sm"
          disabled={page <= 1}
          onClick={() => setPage(p => Math.max(1, p - 1))}
        >
          이전
        </button>
        <span style={{ alignSelf: 'center' }}>{page} / {Math.max(1, data.totalP || 1)}</span>
        <button
          type="button"
          className="btn sm"
          disabled={page >= (data.totalP || 1)}
          onClick={() => setPage(p => Math.min((data.totalP || 1), p + 1))}
        >
          다음
        </button>
      </div>
    </section>
  )
}
