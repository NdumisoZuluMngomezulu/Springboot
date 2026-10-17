// js/common.js
const API_BASE_URL = 'http://localhost:8080';

const $ = (id) => document.getElementById(id);

let token = sessionStorage.getItem('novabank_item') || null;

const api = async (url, options = {}) => {
    const fullUrl = url.startsWith('/api') ? `${API_BASE_URL}${url}` : url;

    const response = await fetch(fullUrl, {
        ...options,
        headers: {
            'Content-Type' : 'application/json',
            ...(token ? { Authorization: `Bearer ${token}` } : {}),
            ...(options.headers || {})
        }
    });

    const data = response.status === 204 ? null : await response.json().catch(() => null);

    if (!response.ok) {
        //Spring boot validation errors often come in data.message or data.error 
        const message = data?.message || data?.error || (response.status === 401 ? 'Please sign in again.' : 'Request failed.');
        throw new Error(message);
    }

    return data;
};
//   if (!response.ok) {
//     // Spring Boot Validation errors often come in data.message or data.error
//     const message = data?.message || data?.error || (response.status === 401 ? 'Please sign in again.' : 'Request failed.');
//     throw new Error(message);
//   }

//   return data;
// };

// ... setMessage helper remains the same