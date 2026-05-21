import { CoverageChart } from '../components/dashboard/CoverageChart'
import { useProjectContext } from '../contexts/ProjectContext'

export default function Coverage() {
  const { projectId, branchFilter } = useProjectContext()
  const branch = branchFilter.trim() || undefined

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight text-white">Coverage</h1>
        <p className="mt-1 text-sm text-slate-400">Line and branch coverage trends from JaCoCo uploads.</p>
      </div>
      <CoverageChart projectId={projectId} branch={branch} days={60} />
    </div>
  )
}
