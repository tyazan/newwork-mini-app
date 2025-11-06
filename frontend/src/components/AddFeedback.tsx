import React, { useState } from 'react'
import { api } from '../lib/api'

export default function AddFeedback({ employeeId, onAdded }: { employeeId: number, onAdded: () => void }) {
  const [text, setText] = useState('')
  const [polish, setPolish] = useState(true)
  const [saving, setSaving] = useState(false)

  async function submit() {
    setSaving(true)
    try {
      await api(`/api/v1/employees/${employeeId}/feedback`, {
        method: 'POST',
        body: JSON.stringify({ text, polish })
      })
      setText('')
      onAdded()
    } finally {
      setSaving(false)
    }
  }

  return (
    <div style={{marginBottom:12}}>
      <textarea rows={3} placeholder="Write feedback…" value={text} onChange={e => setText(e.target.value)} />
      <div style={{display:'flex', alignItems:'center', gap:8}}>
        <label><input type="checkbox" checked={polish} onChange={e => setPolish(e.target.checked)} /> ✨ Polish with AI</label>
        <button className="primary" onClick={submit} disabled={saving || !text.trim()}>{saving ? 'Saving…' : 'Add'}</button>
      </div>
    </div>
  )
}
