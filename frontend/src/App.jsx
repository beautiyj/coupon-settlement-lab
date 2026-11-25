import IssuePage from './pages/IssuePage.jsx'
import AdminPage from './pages/AdminPage.jsx'

export default function App() {
  const isAdmin = window.location.pathname === '/admin'
  return isAdmin ? <AdminPage /> : <IssuePage />
}
