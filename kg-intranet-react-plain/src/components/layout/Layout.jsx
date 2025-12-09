import Header from './Header.jsx'
import Footer from './Footer.jsx'
import { Outlet } from 'react-router-dom'
import MessagePanel from '../messages/MessagePanel.jsx'

export default function Layout() {
  return (
    <div className="app-root">
      <Header />
      <main className="container" role="main">
        <Outlet />
      </main>
      <Footer />
      <MessagePanel />
    </div>
  )
}
