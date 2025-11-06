import React, { useEffect, useState } from 'react'
import { api, Role } from '../lib/api'

type Props = { employee: any; role: Role; userId?: number }

export default function Profile({ employee, role, userId = 2 }: Props) {
  const editable = role === 'MANAGER' || (role === 'OWNER' /* + optionally ensure userId === employee.id */)
  const [form, setForm] = useState({ ...employee })
  const [saving, setSaving] = useState(false)
  const [msg, setMsg] = useState<string | null>(null)

  useEffect(() => { setForm({ ...employee }) }, [employee])

  async function save() {
    setSaving(true); setMsg(null)
    try {
      const body = {
        name: form.name,
        email: form.email,
        department: form.department,
        title: form.title,
        salary: form.salary,
        dob: form.dob
      }
      const updated = await api(`/api/v1/employees/${employee.id}`, {
        method: 'PUT',
        body: JSON.stringify(body)
      }, role, userId)
      setForm(updated)
      setMsg('Saved ✔')
    } catch (e:any) {
      setMsg(e.message || 'Save failed')
    } finally {
      setSaving(false)
    }
  }

  function field(label: string, key: keyof typeof form, type: string = 'text') {
    return (
      <div>
        <label>{label}</label>
        {editable
          ? <input type={type} value={form[key] ?? ''} onChange={e => setForm({ ...form, [key]: e.target.value })} />
          : <div>{form[key] ?? <em className="muted">(redacted)</em>}</div>}
      </div>
    )
  }

  return (
    <>
      <div className="grid2">
        {field('Name', 'name')}
        {field('Email', 'email')}
        {field('Department', 'department')}
        {field('Title', 'title')}
        {field('Salary', 'salary')}
        {field('Date of Birth', 'dob', 'date')}
      </div>
      {editable && (
        <div style={{ marginTop: 12, textAlign: 'right' }}>
          <button className="primary" onClick={save} disabled={saving}>{saving ? 'Saving…' : 'Save'}</button>
        </div>
      )}
      {msg && <p className="muted">{msg}</p>}
    </>
  )
}
