import { useQuery } from '@tanstack/react-query'
import {
  CartesianGrid,
  Legend,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'
import { fetchCoverageTrends } from '../../services/api'
import { Card, CardContent, CardHeader, CardTitle } from '../ui/card'

type Props = {
  projectId: string | null
  branch?: string
  days?: number
}

export function CoverageChart({ projectId, branch, days = 30 }: Props) {
  const { data = [], isFetching } = useQuery({
    queryKey: ['coverage-trends', projectId, branch, days],
    queryFn: () => fetchCoverageTrends(projectId!, branch || undefined, days),
    enabled: !!projectId,
  })

  const chartData = data.map((d) => ({
    date: d.date,
    line: d.avgLineCoverage ?? 0,
    branch: d.avgBranchCoverage ?? 0,
  }))

  return (
    <Card>
      <CardHeader>
        <CardTitle>Coverage trends</CardTitle>
      </CardHeader>
      <CardContent>
        {!projectId ? (
          <p className="text-sm text-slate-400">Select a project to view coverage.</p>
        ) : isFetching && chartData.length === 0 ? (
          <p className="text-sm text-slate-400">Loading chart…</p>
        ) : chartData.length === 0 ? (
          <p className="text-sm text-slate-400">No coverage data yet. Upload telemetry from your build pipeline.</p>
        ) : (
          <div className="h-72 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#334155" />
                <XAxis dataKey="date" stroke="#94a3b8" fontSize={12} />
                <YAxis domain={[0, 100]} stroke="#94a3b8" fontSize={12} />
                <Tooltip
                  contentStyle={{ background: '#0f172a', border: '1px solid #334155' }}
                  labelStyle={{ color: '#e2e8f0' }}
                />
                <Legend />
                <Line type="monotone" dataKey="line" name="Line %" stroke="#818cf8" strokeWidth={2} dot={false} />
                <Line type="monotone" dataKey="branch" name="Branch %" stroke="#34d399" strokeWidth={2} dot={false} />
              </LineChart>
            </ResponsiveContainer>
          </div>
        )}
      </CardContent>
    </Card>
  )
}
