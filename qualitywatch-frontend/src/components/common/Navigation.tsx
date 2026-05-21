import { Activity, BarChart3, FlaskConical, LayoutDashboard } from 'lucide-react'
import { NavLink } from 'react-router-dom'
import { useProjectContext } from '../../contexts/ProjectContext'
import { cn } from '../../lib/utils'

const links = [
  { to: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
  { to: '/coverage', label: 'Coverage', icon: BarChart3 },
  { to: '/tests', label: 'Tests', icon: FlaskConical },
  { to: '/builds', label: 'Build health', icon: Activity },
]

export function Navigation() {
  const { projects, projectId, setProjectId, branchFilter, setBranchFilter, isLoading } = useProjectContext()

  return (
    <header className="border-b border-slate-800 bg-slate-950/80 backdrop-blur">
      <div className="mx-auto flex max-w-6xl flex-wrap items-center gap-4 px-6 py-4">
        <div className="flex items-center gap-2 font-semibold tracking-tight text-slate-100">
          <Activity className="h-5 w-5 text-emerald-400" />
          QualityWatch
        </div>
        <nav className="flex flex-1 flex-wrap gap-1">
          {links.map(({ to, label, icon: Icon }) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) =>
                cn(
                  'flex items-center gap-2 rounded-md px-3 py-2 text-sm font-medium text-slate-400 hover:bg-slate-800 hover:text-slate-100',
                  isActive && 'bg-slate-800 text-white',
                )
              }
            >
              <Icon className="h-4 w-4" />
              {label}
            </NavLink>
          ))}
        </nav>
        <div className="flex flex-wrap items-center gap-2">
          <label className="sr-only" htmlFor="project">
            Project
          </label>
          <select
            id="project"
            className="h-9 rounded-md border border-slate-700 bg-slate-900 px-3 text-sm text-slate-100"
            disabled={isLoading || projects.length === 0}
            value={projectId ?? ''}
            onChange={(e) => setProjectId(e.target.value || null)}
          >
            {projects.length === 0 ? (
              <option value="">No projects</option>
            ) : (
              projects.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.name}
                </option>
              ))
            )}
          </select>
          <input
            type="text"
            placeholder="Branch filter"
            value={branchFilter}
            onChange={(e) => setBranchFilter(e.target.value)}
            className="h-9 w-36 rounded-md border border-slate-700 bg-slate-900 px-3 text-sm text-slate-100 placeholder:text-slate-500"
          />
        </div>
      </div>
    </header>
  )
}
