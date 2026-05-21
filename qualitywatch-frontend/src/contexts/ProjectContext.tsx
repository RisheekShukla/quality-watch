import { useQuery } from '@tanstack/react-query'
import { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from 'react'
import { fetchProjects, type Project } from '../services/api'

type ProjectContextValue = {
  projects: Project[]
  projectId: string | null
  setProjectId: (id: string | null) => void
  branchFilter: string
  setBranchFilter: (b: string) => void
  isLoading: boolean
}

const ProjectContext = createContext<ProjectContextValue | null>(null)

export function ProjectProvider({ children }: { children: ReactNode }) {
  const { data: projects = [], isLoading } = useQuery({
    queryKey: ['projects'],
    queryFn: fetchProjects,
  })
  const [projectId, setProjectId] = useState<string | null>(null)
  const [branchFilter, setBranchFilter] = useState('')

  useEffect(() => {
    if (!projectId && projects.length > 0) {
      setProjectId(projects[0].id)
    }
  }, [projects, projectId])

  const value = useMemo(
    () => ({
      projects,
      projectId,
      setProjectId,
      branchFilter,
      setBranchFilter,
      isLoading,
    }),
    [projects, projectId, branchFilter, isLoading],
  )

  return <ProjectContext.Provider value={value}>{children}</ProjectContext.Provider>
}

export function useProjectContext() {
  const ctx = useContext(ProjectContext)
  if (!ctx) {
    throw new Error('useProjectContext must be used within ProjectProvider')
  }
  return ctx
}
