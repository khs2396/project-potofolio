import { Link } from 'react-router-dom'
export default function NotFound(){return <section className='page notfound'><h1 className='page-title'>404</h1><p>페이지를 찾을 수 없습니다.</p><Link to='/' className='btn'>홈으로</Link></section>}
