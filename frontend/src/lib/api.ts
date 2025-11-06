export type Role = 'MANAGER' | 'OWNER' | 'COWORKER'
const BASE_URL = 'http://localhost:8080'; // direct

export async function api(path: string, opts: RequestInit = {}, role: Role = 'COWORKER', userId = 2) {
  const res = await fetch(`${BASE_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      'X-Demo-Role': role,
      'X-Demo-UserId': String(userId),
      ...(opts.headers || {}),
    },
    ...opts,
  })
  return res.json()
}
