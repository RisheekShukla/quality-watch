import { Navigate, Route, Routes } from 'react-router-dom'
import { Layout } from './components/common/Layout'
import BuildHealth from './pages/BuildHealth'
import Coverage from './pages/Coverage'
import Dashboard from './pages/Dashboard'
import Tests from './pages/Tests'

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Layout />}>
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="dashboard" element={<Dashboard />} />
        <Route path="coverage" element={<Coverage />} />
        <Route path="tests" element={<Tests />} />
        <Route path="builds" element={<BuildHealth />} />
      </Route>
    </Routes>
  )
}
