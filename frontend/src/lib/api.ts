const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';
export type Role = 'MANAGER' | 'OWNER' | 'COWORKER';

export async function api(path: string, opts: RequestInit = {}, role: Role = 'COWORKER', userId = 2) {
  const res = await fetch(`${BASE_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      'X-Demo-Role': role,
      'X-Demo-UserId': String(userId),
      ...(opts.headers || {}),
    },
    ...opts,
  });
  if (!res.ok) throw new Error(`API ${res.status}`);
  return res.json();
}
