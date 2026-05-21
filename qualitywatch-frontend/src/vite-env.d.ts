/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_BASE_URL?: string
  readonly VITE_DASHBOARD_USER?: string
  readonly VITE_DASHBOARD_PASSWORD?: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
