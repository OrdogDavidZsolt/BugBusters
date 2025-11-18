document.addEventListener('DOMContentLoaded', () => {

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

            fetch('/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
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