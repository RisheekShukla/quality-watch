import axios from 'axios'

const baseURL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

export const client = axios.create({ baseURL })

const dashboardUser = import.meta.env.VITE_DASHBOARD_USER
const dashboardPassword = import.meta.env.VITE_DASHBOARD_PASSWORD

if (dashboardUser && dashboardPassword) {
  client.defaults.auth = {
    username: dashboardUser,
    password: dashboardPassword,
  }
}

export type Project = {
  id: string
  name: string
}

export type CoverageTrend = {
  date: string
  avgLineCoverage: number | null
  avgBranchCoverage: number | null
  buildCount: number | null
}

export type LatestCoverage = {
  lineCoveragePercent: number | null
  branchCoveragePercent: number | null
  instructionCoveragePercent: number | null
  buildTimestamp: string | null
  buildNumber: string | null
  branch: string | null
  status: string | null
}

export type FlakyTest = {
  id: string
  testClass: string
  testMethod: string
  failureRate: number | null
  totalExecutions: number | null
  failedExecutions: number | null
  lastFailedAt: string | null
  detectionConfidence: string | null
}

export type BuildHealth = {
  buildId: string
  buildNumber: string
  branch: string
  status: string
  buildTimestamp: string
  totalTests: number
  failedTests: number
}

export async function fetchProjects(): Promise<Project[]> {
  const { data } = await client.get<Project[]>('/api/v1/projects')
  return data
}

export async function fetchCoverageTrends(
  projectId: string,
  branch: string | undefined,
  days: number,
): Promise<CoverageTrend[]> {
  const { data } = await client.get<Record<string, unknown>[]>('/api/v1/analytics/coverage/trends', {
    params: { projectId, branch, days },
  })
  return data.map((row) => ({
    date: String(row.date),
    avgLineCoverage: row.avgLineCoverage != null ? Number(row.avgLineCoverage) : null,
    avgBranchCoverage: row.avgBranchCoverage != null ? Number(row.avgBranchCoverage) : null,
    buildCount: row.buildCount != null ? Number(row.buildCount) : null,
  }))
}

export async function fetchLatestCoverage(
  projectId: string,
  branch: string | undefined,
): Promise<LatestCoverage | null> {
  const { data } = await client.get<LatestCoverage | null>('/api/v1/analytics/coverage/latest', {
    params: { projectId, branch },
  })
  return data
}

export async function fetchFlakyTests(projectId: string): Promise<FlakyTest[]> {
  const { data } = await client.get<Record<string, unknown>[]>('/api/v1/analytics/tests/flaky', {
    params: { projectId },
  })
  return data.map((row) => ({
    id: String(row.id),
    testClass: String(row.testClass),
    testMethod: String(row.testMethod),
    failureRate: row.failureRate != null ? Number(row.failureRate) : null,
    totalExecutions: row.totalExecutions != null ? Number(row.totalExecutions) : null,
    failedExecutions: row.failedExecutions != null ? Number(row.failedExecutions) : null,
    lastFailedAt: row.lastFailedAt != null ? String(row.lastFailedAt) : null,
    detectionConfidence: row.detectionConfidence != null ? String(row.detectionConfidence) : null,
  }))
}

export async function fetchBuildHealth(projectId: string, limit = 20): Promise<BuildHealth[]> {
  const { data } = await client.get<Record<string, unknown>[]>('/api/v1/analytics/builds/health', {
    params: { projectId, limit },
  })
  return data.map((row) => ({
    buildId: String(row.buildId),
    buildNumber: String(row.buildNumber),
    branch: String(row.branch),
    status: String(row.status),
    buildTimestamp: String(row.buildTimestamp),
    totalTests: Number(row.totalTests ?? 0),
    failedTests: Number(row.failedTests ?? 0),
  }))
}
