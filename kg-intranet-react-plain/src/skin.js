// skin.js :: v11.0.1 — load final overrides last and add the namespace class
import './styles/overrides.css'
if (typeof document !== 'undefined') {
  document.documentElement.classList.add('kg-skin') // namespace for overrides
}
