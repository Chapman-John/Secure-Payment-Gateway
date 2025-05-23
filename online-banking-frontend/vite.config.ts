import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      },
      '/ws': {
        target: 'http://localhost:8080',
        ws: true,
        changeOrigin: true
      }
    },
    hmr: {
      overlay: false
    }
  },
  define: {
    global: 'window',
  }
})
