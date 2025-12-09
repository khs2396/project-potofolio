export default function Stepper({ steps=[], current=0 }){
  return (
    <ol style={{display:'flex',gap:12,flexWrap:'wrap',listStyle:'none',padding:0,margin:'12px 0'}}>
      {steps.map((s, i)=>{
        const active = i <= current
        return (
          <li key={i} style={{display:'flex',alignItems:'center',gap:8}}>
            <span style={{display:'grid',placeItems:'center',width:26,height:26,borderRadius:999,
              background: active ? 'var(--brand)' : 'var(--surface-2)',
              color: active ? 'var(--brand-contrast)' : 'var(--fg-muted)',
              border: '1px solid var(--line)'}}>{i+1}</span>
            <span style={{color: active ? 'var(--fg)' : 'var(--fg-muted)'}}>{s}</span>
            {i < steps.length-1 && <span aria-hidden="true" style={{width:24,height:1,background:'var(--line)'}}/>}
          </li>
        )
      })}
    </ol>
  )
}
