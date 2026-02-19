import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react-swc';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: process.env.VITE_BACKEND_URL || 'http://localhost:8680',
        changeOrigin: true
      },
      '/auth': {
        target: process.env.VITE_BACKEND_URL || 'http://localhost:8680',
        changeOrigin: true
      }
    }
  }
});

