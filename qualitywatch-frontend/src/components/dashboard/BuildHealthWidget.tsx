import { useQuery } from '@tanstack/react-query'
import { fetchBuildHealth } from '../../services/api'
import { cn } from '../../lib/utils'
import { Card, CardContent, CardHeader, CardTitle } from '../ui/card'

type Props = {
  projectId: string | null
}

export function BuildHealthWidget({ projectId }: Props) {
  const { data = [], isFetching } = useQuery({
    queryKey: ['build-health', projectId],
    queryFn: () => fetchBuildHealth(projectId!, 12),
    enabled: !!projectId,
  })

  return (
    <Card>
      <CardHeader>
        <CardTitle>Recent builds</CardTitle>
      </CardHeader>
      <CardContent>
        {!projectId ? (
          <p className="text-sm text-slate-400">Select a project.</p>
        ) : isFetching && data.length === 0 ? (
          <p className="text-sm text-slate-400">Loading…</p>
        ) : data.length === 0 ? (
          <p className="text-sm text-slate-400">No builds recorded yet.</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="border-b border-slate-800 text-slate-400">
                <tr>
                  <th className="pb-2 pr-4 font-medium">Build</th>
                  <th className="pb-2 pr-4 font-medium">Branch</th>
                  <th className="pb-2 pr-4 font-medium">Tests</th>
                  <th className="pb-2 font-medium">Status</th>
                </tr>
              </thead>
              <tbody>
                {data.map((b) => (
                  <tr key={b.buildId} className="border-b border-slate-800/80">
                    <td className="py-2 pr-4 font-mono text-xs text-slate-200">{b.buildNumber}</td>
                    <td className="py-2 pr-4 text-slate-300">{b.branch}</td>
                    <td className="py-2 pr-4 text-slate-300">
                      {b.totalTests} total ·{' '}
                      <span className={b.failedTests > 0 ? 'text-red-400' : 'text-emerald-400'}>{b.failedTests}</span>{' '}
                      failed
                    </td>
                    <td className="py-2">
                      <span
                        className={cn(
                          'rounded-full px-2 py-0.5 text-xs font-medium',
                          b.failedTests > 0 ? 'bg-red-500/20 text-red-300' : 'bg-emerald-500/20 text-emerald-300',
                        )}
                      >
                        {b.status}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </CardContent>
    </Card>
  )
}
