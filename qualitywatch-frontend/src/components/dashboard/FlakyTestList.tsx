import { useQuery } from '@tanstack/react-query'
import { fetchFlakyTests } from '../../services/api'
import { Card, CardContent, CardHeader, CardTitle } from '../ui/card'

type Props = {
  projectId: string | null
}

export function FlakyTestList({ projectId }: Props) {
  const { data = [], isFetching } = useQuery({
    queryKey: ['flaky-tests', projectId],
    queryFn: () => fetchFlakyTests(projectId!),
    enabled: !!projectId,
  })

  return (
    <Card>
      <CardHeader>
        <CardTitle>Flaky tests</CardTitle>
      </CardHeader>
      <CardContent>
        {!projectId ? (
          <p className="text-sm text-slate-400">Select a project.</p>
        ) : isFetching && data.length === 0 ? (
          <p className="text-sm text-slate-400">Loading…</p>
        ) : data.length === 0 ? (
          <p className="text-sm text-slate-400">
            No flaky tests detected yet (needs mixed pass/fail history per test).
          </p>
        ) : (
          <ul className="space-y-3">
            {data.map((t) => (
              <li key={t.id} className="border-l-2 border-amber-500/80 pl-3">
                <p className="font-medium text-slate-100">
                  {t.testClass}.{t.testMethod}
                </p>
                <p className="text-xs text-slate-400">
                  Failure rate {t.failureRate?.toFixed(1) ?? '—'}% · {t.detectionConfidence ?? '—'} confidence ·{' '}
                  {t.totalExecutions ?? 0} runs
                </p>
              </li>
            ))}
          </ul>
        )}
      </CardContent>
    </Card>
  )
}
