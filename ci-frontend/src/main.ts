import './assets/main.css'
import { BootstrapVue, IconsPlugin } from 'bootstrap-vue'


import { createApp} from 'vue'
import App from './App.vue'

// createApp(App).mount('#app')
let app = createApp(App)
app.use(BootstrapVue)
app.use(IconsPlugin)

app.mount('#app')

