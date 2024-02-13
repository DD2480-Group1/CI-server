/**
 * main.js
 *
 * Bootstraps Vuetify and other plugins then mounts the App`
 */

// Plugins
import { registerPlugins } from '@/plugins'
import { createRouter, createWebHistory } from 'vue-router'

const Home = { template: '<div>Home</div>' }
const routes =[
    { path: '/', component: Home },
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// Components
import App from './App.vue'

// Composables
import { createApp } from 'vue'

const app = createApp(App)
app.use(router)

registerPlugins(app)

app.mount('#app')
