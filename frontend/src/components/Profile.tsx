import React from 'react'

export default function Profile({ employee, role }: { employee: any, role: string }) {
  return (
    <div className="grid2">
      <div><label>Name</label><div>{employee.name}</div></div>
      <div><label>Email</label><div>{employee.email}</div></div>
      <div><label>Department</label><div>{employee.department}</div></div>
      <div><label>Title</label><div>{employee.title}</div></div>
      <div><label>Salary</label><div>{employee.salary ?? <em className='muted'>(redacted)</em>}</div></div>
      <div><label>Date of Birth</label><div>{employee.dob ?? <em className='muted'>(redacted)</em>}</div></div>
    </div>
  )
}
