import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import * as TasksApi from '../services/task.api.js'
import * as EmployeesService from '../services/employees.js'
import '../styles/detail-pages.css'

export default function TaskCreate() {
  const { user } = useAuth()
  const nav = useNavigate()
  const [form, setForm] = useState({
    subject: '',
    task_content: '',
    assignee_id: '',
    file_name: ''
  })
  const [file, setFile] = useState(null)
  const [saving, setSaving] = useState(false)
  const [employees, setEmployees] = useState([])
  const [employeesByDept, setEmployeesByDept] = useState({})

  // 직급 한글 변환
  const getTitleKorean = (title) => {
    const titleMap = {
      'ST': '사원',
      'AS': '주임',
      'AC': '대리',
      'MGR': '과장',
      'GM': '부장'
    }
    return titleMap[title] || title || '직급 없음'
  }

  useEffect(() => {
    const loadEmployees = async () => {
      const list = await EmployeesService.ensureLoaded()

      // 부장(GM) 제외 필터링
      const filteredList = list.filter(emp => {
        const role = (emp.role || emp.title || '').toUpperCase()
        return role !== 'GM'
      })

      setEmployees(filteredList)

      // 부서별로 그룹화
      const grouped = {}
      filteredList.forEach(emp => {
        const dept = emp.dept || '부서 미지정'
        if (!grouped[dept]) grouped[dept] = []
        grouped[dept].push(emp)
      })
      setEmployeesByDept(grouped)
    }
    loadEmployees()
  }, [])

  const onChange = (field) => (e) => {
    setForm({ ...form, [field]: e.target.value })
  }

  const onSubmit = async (e) => {
    e.preventDefault()
    if (!form.subject.trim()) {
      alert('제목을 입력해주세요.')
      return
    }
    if (!form.assignee_id.trim()) {
      alert('담당자를 입력해주세요.')
      return
    }
    setSaving(true)
    try {
      const payload = {
        request_id: user?.depno || user?.empNo || user?.id || '',
        assignee_id: form.assignee_id.trim(),
        subject: form.subject.trim(),
        task_content: form.task_content.trim(),
        file_name: form.file_name.trim() || null,
        file
      }
      const created = await TasksApi.create(payload)
      if (!created || created.rt === 'FAIL') {
        alert('업무 생성에 실패했습니다.')
      } else {
        alert('업무가 등록되었습니다.')
        nav(`/tasks/${created.taskSeq}`)
        return
      }
    } catch (err) {
      console.error(err)
      alert('업무 생성 중 오류가 발생했습니다.')
    } finally {
      setSaving(false)
    }
  }

  return (
    <section className="page">
      <div className="detail-head">
        <div>
          <p className="detail-chip">업무</p>
          <h1 className="page-title">업무 등록</h1>
          <p className="page-desc">요청자/담당자를 지정해 새로운 업무를 등록하세요</p>
        </div>
      </div>

      <form className="form" style={{ maxWidth: 720 }} onSubmit={onSubmit}>
        <label className="form-row">
          <span className="form-label">제목</span>
          <input className="input" value={form.subject} onChange={onChange('subject')} placeholder="업무 제목" />
        </label>
        <label className="form-row">
          <span className="form-label">담당자</span>
          <select className="input" value={form.assignee_id} onChange={onChange('assignee_id')}>
            <option value="">담당자를 선택하세요</option>
            {Object.entries(employeesByDept).map(([dept, emps]) => (
              <optgroup key={dept} label={dept}>
                {emps.map(emp => (
                  <option key={emp.empNo} value={emp.empNo}>
                    {emp.name} ({getTitleKorean(emp.title)})
                  </option>
                ))}
              </optgroup>
            ))}
          </select>
        </label>
        <label className="form-row">
          <span className="form-label">내용</span>
          <textarea className="input" rows="8" value={form.task_content} onChange={onChange('task_content')} placeholder="업무 내용" />
        </label>
        <label className="form-row">
          <span className="form-label">파일 첨부 (선택)</span>
          <input type="file" className="input" onChange={e => setFile(e.target.files?.[0] || null)} />
          {file && <div className="page-desc">{file.name}</div>}
        </label>
        <div className="form-row" style={{ display: 'flex', gap: 8 }}>
          <button className="btn" type="button" onClick={() => nav('/tasks')}>취소</button>
          <button className="btn primary" type="submit" disabled={saving}>등록</button>
        </div>
      </form>
    </section>
  )
}
