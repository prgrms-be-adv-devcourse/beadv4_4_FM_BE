import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig(({ mode }) => {
  // 현재 작업 디렉토리에서 'VITE_'로 시작하는 환경 변수를 불러옵니다.
  const env = loadEnv(mode, process.cwd(), '');

  return {
    plugins: [react(), tailwindcss()],
    server: {
      port: 5173,
      strictPort: true,
      proxy: {
        '/api': {
          target: env.VITE_API_BASE_URL, 
          changeOrigin: true,
          secure: false,
        }
      }
    }
  }
})