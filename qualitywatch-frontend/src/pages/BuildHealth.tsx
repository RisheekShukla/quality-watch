import { BuildHealthWidget } from '../components/dashboard/BuildHealthWidget'
import { useProjectContext } from '../contexts/ProjectContext'

export default function BuildHealth() {
  const { projectId } = useProjectContext()

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight text-white">Build health</h1>
        <p className="mt-1 text-sm text-slate-400">Recent builds with aggregated test outcomes.</p>
      </div>
      <BuildHealthWidget projectId={projectId} />
    </div>
  )
}
