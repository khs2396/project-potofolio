export default function Alert({ tone='info', children }){return <div className={`alert ${tone}`} role='alert'>{children}</div>}
