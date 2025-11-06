import React, { useEffect, useState } from 'react'
import { api, Role } from './lib/api'
import Profile from './components/Profile'
import FeedbackList from './components/FeedbackList'
import AddFeedback from './components/AddFeedback'
import AbsenceForm from './components/AbsenceForm'

export default function App() {
  const [role, setRole] = useState<Role>('COWORKER')
  const [userId, setUserId] = useState<number>(2)
  const [employee, setEmployee] = useState<any>(null)
  const employeeId = 2 // Demo: focus on Bob (id=2) from seed

  useEffect(() => {
    api(`/api/v1/employees/${employeeId}`, {}, role, userId).then(setEmployee).catch(console.error)
  }, [role, userId])

  return (
    <>
      <header>
        <strong>NEWWORK Mini-App</strong>
        <div>
          <select value={role} onChange={e => setRole(e.target.value as Role)}>
            <option value="MANAGER">MANAGER</option>
            <option value="OWNER">OWNER</option>
            <option value="COWORKER">COWORKER</option>
          </select>
          {' '}
          <input style={{width:80}} type="number" value={userId} onChange={e => setUserId(Number(e.target.value))} />
          {' '}
          <a className="tag" href="http://localhost:8080/health" target="_blank">health</a>
        </div>
      </header>
      <div className="container">
        <div className="card">
          <h3>Employee Profile</h3>
          {employee ? <Profile employee={employee} role={role} /> : <p className="muted">Loading…</p>}
        </div>
        <div className="row">
          <div className="card" style={{flex:1}}>
            <h3>Feedback</h3>
            <AddFeedback employeeId={employeeId} onAdded={() => { /* no-op; list reloads below */ }} />
            <FeedbackList employeeId={employeeId} />
          </div>
          <div className="card" style={{flex:1}}>
            <h3>Absence Request</h3>
            <AbsenceForm employeeId={employeeId} />
          </div>
        </div>
        <p className="muted">Role-based redaction: coworkers do not see salary/DOB.</p>
      </div>
    </>
  )
}
