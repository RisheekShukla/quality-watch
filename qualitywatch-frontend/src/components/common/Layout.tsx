import { Outlet } from 'react-router-dom'
import { ProjectProvider } from '../../contexts/ProjectContext'
import { Navigation } from './Navigation'

export function Layout() {
  return (
    <ProjectProvider>
      <div className="min-h-screen bg-slate-950 text-slate-100">
        <Navigation />
        <main className="mx-auto max-w-6xl px-6 py-8">
          <Outlet />
        </main>
      </div>
    </ProjectProvider>
  )
}
