import { FlakyTestList } from '../components/dashboard/FlakyTestList'
import { useProjectContext } from '../contexts/ProjectContext'

export default function Tests() {
  const { projectId } = useProjectContext()

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight text-white">Tests</h1>
        <p className="mt-1 text-sm text-slate-400">
          Flaky test candidates inferred from mixed pass/fail outcomes over recent runs.
        </p>
      </div>
      <FlakyTestList projectId={projectId} />
    </div>
  )
}
