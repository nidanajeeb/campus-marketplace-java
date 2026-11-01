// ========================================================
// Campus Circular Market - Authentication State & Header UI
// ========================================================

let currentUser = null;

async function checkAuthStatus() {
  try {
    const res = await api.get('/auth/me');
    if (res && res.userId) {
      currentUser = res;
    } else {
      currentUser = null;
    }
  } catch {
    currentUser = null;
  }
  updateNavHeader();
  return currentUser;
}

function updateNavHeader() {
  const authNav = document.getElementById('header-auth-section');
  const mobileAuth = document.getElementById('mobile-auth-section');
  const myListingsNav = document.getElementById('nav-my-listings');

  if (currentUser) {
    const roleBadge = currentUser.role === 'ROLE_ADMIN' ? ' <span class="badge" style="background:#fef3c7;color:#92400e;font-size:0.7rem;padding:0.1rem 0.4rem;">Admin</span>' : '';
    
    // Update My Listings link text for Admin
    if (myListingsNav) {
      myListingsNav.style.display = 'inline-block';
      if (currentUser.role === 'ROLE_ADMIN') {
        myListingsNav.textContent = 'Manage All Listings';
      } else {
        myListingsNav.textContent = 'My Listings';
      }
    }

    if (authNav) {
      authNav.innerHTML = `
        <span style="font-size:0.85rem;font-weight:600;color:var(--text-main);display:flex;align-items:center;gap:0.35rem;">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
          ${escapeHtml(currentUser.fullName)}${roleBadge}
        </span>
        <button class="btn btn-ghost btn-sm" onclick="handleLogout()">Sign out</button>
      `;
    }

    if (mobileAuth) {
      mobileAuth.innerHTML = `
        <div style="padding:0.5rem 1rem;font-size:0.875rem;font-weight:600;color:var(--text-main);">
          Signed in as: ${escapeHtml(currentUser.fullName)}${roleBadge}
        </div>
        <button class="btn btn-outline btn-sm" style="margin-left:1rem;margin-top:0.25rem;" onclick="handleLogout()">Sign out</button>
      `;
    }
  } else {
    if (myListingsNav) {
      myListingsNav.style.display = 'none'; // Only visible to logged in users
    }

    if (authNav) {
      authNav.innerHTML = `
        <a href="auth.html" class="btn btn-primary btn-sm">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
          Sign in
        </a>
      `;
    }

    if (mobileAuth) {
      mobileAuth.innerHTML = `
        <a href="auth.html" class="btn btn-primary btn-sm" style="margin-left:1rem;margin-top:0.5rem;display:inline-block;">Sign in</a>
      `;
    }
  }
}

async function handleLogout() {
  try {
    await api.post('/auth/logout');
    currentUser = null;
    showToast('Signed out successfully', 'info');
    setTimeout(() => {
      window.location.href = 'index.html';
    }, 400);
  } catch (err) {
    showToast('Error signing out', 'error');
  }
}

function escapeHtml(str) {
  if (!str) return '';
  return str.replace(/[&<>'"]/g, 
    tag => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;' }[tag] || tag)
  );
}

// Mobile drawer toggle
function toggleMobileMenu() {
  const drawer = document.getElementById('mobile-drawer');
  if (drawer) {
    drawer.classList.toggle('open');
  }
}

// Initialise auth state on script execution
document.addEventListener('DOMContentLoaded', () => {
  checkAuthStatus();
});

