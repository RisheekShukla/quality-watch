import { useQuery } from '@tanstack/react-query'
import { BuildHealthWidget } from '../components/dashboard/BuildHealthWidget'
import { CoverageChart } from '../components/dashboard/CoverageChart'
import { FlakyTestList } from '../components/dashboard/FlakyTestList'
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card'
import { useProjectContext } from '../contexts/ProjectContext'
import { fetchLatestCoverage } from '../services/api'

export default function Dashboard() {
  const { projectId, branchFilter } = useProjectContext()
  const branch = branchFilter.trim() || undefined

  const { data: latest } = useQuery({
    queryKey: ['coverage-latest', projectId, branch],
    queryFn: () => fetchLatestCoverage(projectId!, branch),
    enabled: !!projectId,
  })

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight text-white">Dashboard</h1>
        <p className="mt-1 text-sm text-slate-400">
          Coverage, flaky tests, and build health for your selected project.
        </p>
      </div>

      {projectId && latest && (
        <div className="grid gap-4 sm:grid-cols-3">
          <Card>
            <CardHeader>
              <CardTitle className="text-sm font-medium text-slate-400">Latest line coverage</CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-3xl font-semibold text-white">{latest.lineCoveragePercent?.toFixed(1) ?? '—'}%</p>
              <p className="mt-1 text-xs text-slate-500">Build {latest.buildNumber ?? '—'}</p>
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <CardTitle className="text-sm font-medium text-slate-400">Latest branch coverage</CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-3xl font-semibold text-white">{latest.branchCoveragePercent?.toFixed(1) ?? '—'}%</p>
              <p className="mt-1 text-xs text-slate-500">{latest.branch ?? 'all branches'}</p>
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <CardTitle className="text-sm font-medium text-slate-400">Last ingest</CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-lg font-medium text-white">
                {latest.buildTimestamp ? new Date(latest.buildTimestamp).toLocaleString() : '—'}
              </p>
              <p className="mt-1 text-xs text-slate-500">From latest coverage report</p>
            </CardContent>
          </Card>
        </div>
      )}

      <div className="grid gap-6 lg:grid-cols-2">
        <CoverageChart projectId={projectId} branch={branch} />
        <FlakyTestList projectId={projectId} />
      </div>

      <BuildHealthWidget projectId={projectId} />
    </div>
  )
}
