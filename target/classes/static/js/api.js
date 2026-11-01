// ========================================================
// Campus Circular Market - API Client & Global Helpers
// ========================================================

const API_BASE = '/api';

/** Format a number as Indian Rupees */
function formatPrice(amount) {
  if (amount == null) return '₹0';
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 0
  }).format(amount);
}

/** Format ISO date into readable short date */
function formatDate(isoStr) {
  if (!isoStr) return '';
  const d = new Date(isoStr);
  return d.toLocaleDateString('en-IN', {
    day: 'numeric',
    month: 'short',
    year: 'numeric'
  });
}

/** Display unobtrusive toast notification */
function showToast(message, type = 'info') {
  let container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    document.body.appendChild(container);
  }

  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.textContent = message;

  container.appendChild(toast);

  setTimeout(() => {
    toast.style.transition = 'opacity 0.3s ease, transform 0.3s ease';
    toast.style.opacity = '0';
    toast.style.transform = 'translateY(10px)';
    setTimeout(() => toast.remove(), 300);
  }, 3500);
}

/** Generic fetch handler */
async function request(endpoint, options = {}) {
  const url = endpoint.startsWith('http') ? endpoint : `${API_BASE}${endpoint}`;
  
  options.credentials = 'same-origin'; // Maintain session cookie
  
  const headers = options.headers || {};
  if (!(options.body instanceof FormData) && !headers['Content-Type']) {
    headers['Content-Type'] = 'application/json';
  }
  options.headers = headers;

  try {
    const res = await fetch(url, options);
    
    // Handle 204 No Content
    if (res.status === 204) return null;

    const data = await res.json().catch(() => null);

    if (!res.ok) {
      const errMsg = (data && data.error) || (data && data.message) || `Request failed with status ${res.status}`;
      throw new Error(errMsg);
    }

    return data;
  } catch (err) {
    console.error(`API Error on [${options.method || 'GET'} ${url}]:`, err);
    throw err;
  }
}

const api = {
  get: (endpoint) => request(endpoint, { method: 'GET' }),
  
  post: (endpoint, body) => request(endpoint, {
    method: 'POST',
    body: JSON.stringify(body)
  }),

  postMultipart: (endpoint, formData) => request(endpoint, {
    method: 'POST',
    body: formData
  }),

  putMultipart: (endpoint, formData) => request(endpoint, {
    method: 'PUT',
    body: formData
  }),

  patch: (endpoint, params = {}) => {
    const query = new URLSearchParams(params).toString();
    const url = query ? `${endpoint}?${query}` : endpoint;
    return request(url, { method: 'PATCH' });
  },

  delete: (endpoint) => request(endpoint, { method: 'DELETE' })
};

