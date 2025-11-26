document.addEventListener('DOMContentLoaded', () => {

    // --- Server Status Check ---
    const serverCard = document.getElementById('serverCard');
    const statusText = document.querySelector('.server-status');
    const statusDot = document.querySelector('.status-dot');

    const API_PORT = 8080;
    const API_BASE = `${window.location.protocol}//${window.location.hostname}:${API_PORT}`;

    async function checkServerStatus() {
        
        try {
            // Create a simple timeout promise so we don't wait forever
            const timeout = new Promise((_, reject) =>
                setTimeout(() => reject(new Error('Timeout')), 2000)
            );

            // A /login helyett az /api/health végpontot hívjuk GET-tel
            const fetchPromise = fetch(`${API_BASE}/api/health`, {
                method: 'GET', // HEAD helyett GET (biztosabb)
                cache: 'no-store'
            });

            await Promise.race([fetchPromise, timeout]);

            // If we get here, the server responded
            updateServerUI(true);
        } catch (error) {
            // Server is down or unreachable
            updateServerUI(false);
        }
    }

    function updateServerUI(isOnline) {
        if (isOnline) {
            statusText.textContent = 'Online';
            statusText.style.color = 'var(--success-text)'; // Defined in your new variables
            statusDot.style.backgroundColor = '#10b981'; // Green
            statusDot.style.boxShadow = '0 0 8px rgba(16, 185, 129, 0.4)';
            statusDot.classList.remove('pulse'); // Stop pulsing red

            // Optional: Add a gentle green pulse animation
            statusDot.style.animation = 'none';
            serverCard.setAttribute('aria-label', 'Server status: Online');
        } else {
            statusText.textContent = 'Offline';
            statusText.style.color = '#64748b';
            statusDot.style.backgroundColor = '#ef4444'; // Red
            statusDot.style.boxShadow = '0 0 8px rgba(239, 68, 68, 0.28)';
            statusDot.classList.add('pulse');
            serverCard.setAttribute('aria-label', 'Server status: Offline');
        }
    }

    // Check immediately on load
    checkServerStatus();

    // Then check every 10 seconds
    setInterval(checkServerStatus, 10000);

    // --- Password Toggle ---
    const showPassCheckbox = document.getElementById('showPassword');
    if(showPassCheckbox) {
        showPassCheckbox.addEventListener('change', function() {
            const passwordInput = document.getElementById('password');
            passwordInput.type = this.checked ? 'text' : 'password';
        });
    }

    // --- Login Form Submission ---
    const loginForm = document.getElementById('loginForm');
    if(loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            const mode = document.querySelector('input[name="loginMode"]:checked').value;

            const data = {
                username: username,
                password: password,
                mode: mode
            };

            fetch(`${API_BASE}/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify(data)
            })
                .then(response => response.json())
                .then(result => {
                    console.log('Server response:', result);
                    if (result.success) {
                        if (mode === 'admin') {
                            window.location.href = '../Admin_Page/index.html';
                        } else {
                            window.location.href = '../Teachers_Page/index.html';
                        }
                    } else {
                        alert(result.message || 'Invalid credentials!');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                });
        });
    }

    // --- UI Updates based on Mode (Admin/Teacher) ---
    const modeUserRadio = document.getElementById('modeUser');
    const modeAdminRadio = document.getElementById('modeAdmin');
    const pageSubtitle = document.getElementById('pageSubtitle');

    function updateModeUI() {
        const selected = document.querySelector('input[name="loginMode"]:checked').value;
        if (selected === 'admin') {
            pageSubtitle.textContent = 'Use your admin credentials to sign in';
        } else {
            pageSubtitle.textContent = 'Please enter your credentials to continue';
        }

        modeUserRadio.nextElementSibling.setAttribute('aria-selected', selected === 'teacher');
        modeAdminRadio.nextElementSibling.setAttribute('aria-selected', selected === 'admin');
    }

    if(modeUserRadio && modeAdminRadio) {
        modeUserRadio.addEventListener('change', updateModeUI);
        modeAdminRadio.addEventListener('change', updateModeUI);
        updateModeUI(); // Initial call
    }
});