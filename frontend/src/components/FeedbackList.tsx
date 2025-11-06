import React, { useEffect, useState } from 'react'
import { api } from '../lib/api'

export default function FeedbackList({ employeeId }: { employeeId: number }) {
  const [items, setItems] = useState<any[]>([])
  useEffect(() => {
    api(`/api/v1/employees/${employeeId}/feedback`).then(setItems).catch(console.error)
  }, [employeeId])
  return (
    <ul className="list">
      {items.map((f, i) => (
        <li key={i}>
          <div><strong>Raw:</strong> {f.text}</div>
          <div><strong>Polished:</strong> <em>{f.polishedText ?? '—'}</em></div>
        </li>
      ))}
    </ul>
  )
}
