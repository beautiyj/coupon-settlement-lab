const API_BASE_URL = '/api'

export async function issueCoupon(payload) {
  const response = await fetch(`${API_BASE_URL}/coupons`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })
  return response.json()
}
