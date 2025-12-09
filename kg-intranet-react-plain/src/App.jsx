import { Routes, Route, Navigate } from "react-router-dom"
import Layout from "./components/layout/Layout.jsx"

import Home from "./pages/Home.jsx"
import NotFound from "./pages/NotFound.jsx"

import Profile from "./pages/Profile.jsx"
import ProfileEdit from "./pages/ProfileEdit.jsx"

import Directory from "./pages/Directory.jsx"
import DirectoryDetail from "./pages/DirectoryDetail.jsx"
import DirectoryEdit from "./pages/DirectoryEdit.jsx"

import Terms from "./pages/Terms.jsx"
import Privacy from "./pages/Privacy.jsx"

import Announcements from "./pages/Announcements.jsx"
import AnnouncementDetail from "./pages/AnnouncementDetail.jsx"
import AnnouncementWrite from "./pages/AnnouncementWrite.jsx"

import Boards from "./pages/BoardList.jsx"
import BoardDetail from "./pages/BoardDetail.jsx"
import BoardWrite from "./pages/BoardWrite.jsx"
import Approvals from "./pages/Approvals.jsx"
import ApprovalDetail from "./pages/ApprovalDetail.jsx"
import ApprovalCreate from "./pages/ApprovalCreate.jsx"
import ApprovalInbox from "./pages/ApprovalInbox.jsx"
import ApprovalOutbox from "./pages/ApprovalOutbox.jsx"
import Task from "./pages/Task.jsx"
import TaskDetail from "./pages/TaskDetail.jsx"
import TaskCreate from "./pages/TaskCreate.jsx"

import RequireAuth from "./components/RequireAuth.jsx"
import { useAuth } from "./context/AuthContext.jsx"

import Login from "./pages/auth/Login.jsx"
import Signup from "./pages/auth/Signup.jsx"
import FindPassword from "./pages/auth/FindPassword.jsx"
import SignupComplete from "./pages/auth/SignupComplete.jsx"

export default function App() {
  const { user } = useAuth()
  const roleText = (user?.role || user?.title || "").toLowerCase()
  const isManager = (user?.roleId === 5) || ["gm", "manager", "lead", "leader", "부장", "관리자", "책임"].some(k => roleText.includes(k))

  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<Home />} />

        {/* public */}
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/signup/complete" element={<SignupComplete />} />
        <Route path="/find-password" element={<FindPassword />} />
        <Route path="/terms" element={<Terms />} />
        <Route path="/privacy" element={<Privacy />} />

        {/* protected - profile */}
        <Route path="/settings/profile" element={<RequireAuth><Profile /></RequireAuth>} />
        <Route path="/settings/profile/edit" element={<RequireAuth><ProfileEdit /></RequireAuth>} />

        {/* legacy redirects */}
        <Route path="/profile" element={<Navigate to="/settings/profile" replace />} />
        <Route path="/profile/edit" element={<Navigate to="/settings/profile/edit" replace />} />

        {/* directory */}
        <Route path="/directory" element={<RequireAuth><Directory /></RequireAuth>} />
        <Route path="/directory/:id" element={<RequireAuth><DirectoryDetail /></RequireAuth>} />
        <Route path="/directory/:id/edit" element={<RequireAuth><DirectoryEdit /></RequireAuth>} />

        {/* announcements */}
        <Route path="/announcements" element={<Announcements />} />
        <Route path="/announcements/:id" element={<AnnouncementDetail />} />
        <Route path="/announcements/write" element={<RequireAuth><AnnouncementWrite /></RequireAuth>} />

        {/* tasks */}
        <Route path="/tasks" element={<RequireAuth><Task /></RequireAuth>} />
        <Route
          path="/tasks/create"
          element={
            <RequireAuth>
              {isManager ? <TaskCreate /> : <Navigate to="/tasks" replace />}
            </RequireAuth>
          }
        />
        <Route path="/tasks/:id" element={<RequireAuth><TaskDetail /></RequireAuth>} />

        {/* boards */}
        <Route path="/boards" element={<RequireAuth><Boards /></RequireAuth>} />
        <Route path="/boards/write" element={<RequireAuth><BoardWrite /></RequireAuth>} />
        <Route path="/boards/:id" element={<RequireAuth><BoardDetail /></RequireAuth>} />
        <Route path="/boards/:id/edit" element={<RequireAuth><BoardWrite /></RequireAuth>} />

        {/* approvals */}
        <Route path="/approvals" element={<Approvals />} />
        <Route path="/approvals/:id" element={<ApprovalDetail />} />
        <Route
          path="/approvals/create"
          element={
            <RequireAuth>
              {!isManager ? <ApprovalCreate /> : <Navigate to="/" replace />}
            </RequireAuth>
          }
        />
        <Route
          path="/approvals/inbox"
          element={
            <RequireAuth>
              {isManager ? <ApprovalInbox /> : <Navigate to="/" replace />}
            </RequireAuth>
          }
        />
        <Route
          path="/approvals/outbox"
          element={
            <RequireAuth>
              {!isManager ? <ApprovalOutbox /> : <Navigate to="/" replace />}
            </RequireAuth>
          }
        />
      </Route>

      <Route path="/home" element={<Navigate to="/" replace />} />
      <Route path="*" element={<NotFound />} />
    </Routes>
  )
}
