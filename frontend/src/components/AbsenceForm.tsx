import React, { useState } from 'react'
import { api } from '../lib/api'

export default function AbsenceForm({ employeeId }: { employeeId: number }) {
  const [startDate, setStart] = useState('')
  const [endDate, setEnd] = useState('')
  const [reason, setReason] = useState('')
  const [saving, setSaving] = useState(false)
  const [ok, setOk] = useState<string | null>(null)

  async function submit() {
    setSaving(true)
    setOk(null)
    try {
      await api(`/api/v1/employees/${employeeId}/absences`, {
        method: 'POST',
        body: JSON.stringify({ startDate, endDate, reason })
      })
      setOk('Request sent.')
      setStart(''); setEnd(''); setReason('')
    } finally {
      setSaving(false)
    }
  }

  return (
    <div>
      <div className="grid2">
        <div><label>Start</label><input type="date" value={startDate} onChange={e => setStart(e.target.value)} /></div>
        <div><label>End</label><input type="date" value={endDate} onChange={e => setEnd(e.target.value)} /></div>
      </div>
      <div><label>Reason</label><input value={reason} onChange={e => setReason(e.target.value)} placeholder="Optional" /></div>
      <div className="right" style={{marginTop:8}}>
        <button className="primary" onClick={submit} disabled={saving || !startDate || !endDate}>{saving ? 'Submitting…' : 'Submit'}</button>
      </div>
      {ok && <p className="muted">{ok}</p>}
    </div>
  )
}
