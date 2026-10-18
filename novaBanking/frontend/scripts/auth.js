// js/auth.js
let authMode = 'login';

function setAuthMode(mode) {
  authMode = mode;
  const isLogin = mode === 'login';
  
  const tabLogin = $('tab-login');
  const tabReg = $('tab-register');

  if (isLogin) {
    tabLogin.className = 'flex-1 py-2 text-sm font-medium rounded-lg transition-all duration-200 text-white bg-indigo-600 shadow-md';
    tabReg.className = 'flex-1 py-2 text-sm font-medium rounded-lg transition-all duration-200 text-slate-400 hover:text-slate-200';
  } else {
    tabReg.className = 'flex-1 py-2 text-sm font-medium rounded-lg transition-all duration-200 text-white bg-indigo-600 shadow-md';
    tabLogin.className = 'flex-1 py-2 text-sm font-medium rounded-lg transition-all duration-200 text-slate-400 hover:text-slate-200';
  }

  $('name-field').classList.toggle('hidden', isLogin);
  $('auth-submit').innerHTML = isLogin 
    ? '<span>Sign in securely</span> <i class="fa-solid fa-arrow-right text-xs"></i>' 
    : '<span>Create demo account</span> <i class="fa-solid fa-arrow-right text-xs"></i>';
  
  $('password').autocomplete = isLogin ? 'current-password' : 'new-password';
  setMessage('auth-message', '');
}

$('auth-form')?.addEventListener('submit', async (e) => {
  e.preventDefault();
  const btn = $('auth-submit');
  btn.disabled = true;
  setMessage('auth-message', '');

  try {
    const email = $('email').value.trim();
    const password = $('password').value;

    if (authMode === 'register') {
      const fullName = $('fullName').value.trim();
      // Calls Spring Boot @PostMapping("/api/auth/register")
      await api('/api/auth/register', {
        method: 'POST',
        body: JSON.stringify({ fullName, email, password })
      });
    }

    // Calls Spring Boot @PostMapping("/api/auth/login")
    const result = await api('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password })
    });

    // Expecting Spring Boot to return { token: "...", fullName: "..." }
    token = result.token;
    sessionStorage.setItem('novabank_token', token);
    sessionStorage.setItem('novabank_user_name', result.fullName || 'User');

    setMessage('auth-message', 'Signed in successfully! Redirecting...', true);
    setTimeout(() => window.location.href = 'accounts.html', 800);
  } catch (err) {
    setMessage('auth-message', err.message);
  } finally {
    btn.disabled = false;
  }
});