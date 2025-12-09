import { useEffect, useState } from 'react'

export default function SearchBar({ onSearch, defaultValue = '', placeholder = '검색' }) {
  const [value, setValue] = useState(defaultValue)

  useEffect(() => { setValue(defaultValue) }, [defaultValue])

  const submit = (e) => {
    e.preventDefault()
    onSearch?.(value.trim())
  }

  return (
    <form className="searchbar" onSubmit={submit}>
      <input
        className="input sm search-input"
        type="search"
        value={value}
        onChange={e => setValue(e.target.value)}
        placeholder={placeholder}
      />
      <button className="btn sm" type="submit">검색</button>
    </form>
  )
}
