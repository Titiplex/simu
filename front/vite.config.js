import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      // Alias pour faciliter l'importation de fichiers
      '@': '/src',
    }
  },
  build: {
    // Personnalisation de la configuration de build si nécessaire
    outDir: 'dist',  // Dossier de sortie de la build
    assetsDir: 'assets'  // Dossier des assets statiques (images, etc.)
  },
  server: {
    // Options de serveur de développement
    port: 3000, // Port sur lequel Vite servira le projet
    open: true, // Ouvre automatiquement le navigateur
  }
})
