const modeToggle = document.getElementById('modeToggle');
        const body = document.body;
        const modeLabel = document.querySelector('.mode-label');

        // Inicializálás
        if (body.classList.contains('light-mode')) {
            modeToggle.checked = false;
            modeLabel.textContent = 'Light Mode';
        } else {
            modeToggle.checked = true;
            modeLabel.textContent = 'Dark Mode';
        }

        modeToggle.addEventListener('change', function() {
            if (this.checked) {
                body.classList.remove('light-mode');
                modeLabel.textContent = 'Dark Mode';
            } else {
                body.classList.add('light-mode');
                modeLabel.textContent = 'Light Mode';
            }
        });

        // Show/hide password
        document.getElementById('showPassword').addEventListener('change', function() {
            const passwordInput = document.getElementById('password');
            passwordInput.type = this.checked ? 'text' : 'password';
        });

        // Login form submission (demo)
        document.getElementById('loginForm').addEventListener('submit', function(e) {
            e.preventDefault();
            const username = document.getElementById('username').value;
            alert('Login submitted!\nUsername: ' + username);
        });


        // HELP modal functionality (smooth open/close with transitions)
        const helpBtn = document.getElementById('helpBtn');
        const helpOverlay = document.getElementById('helpOverlay');
        const helpCloseBtn = document.getElementById('helpCloseBtn');

        // Accessibility / state helpers
        function lockScroll() {
            document.documentElement.style.overflow = 'hidden';
            document.body.style.overflow = 'hidden';
        }
        function unlockScroll() {
            document.documentElement.style.overflow = '';
            document.body.style.overflow = '';
        }

        // Open immediately makes the panel visible (aria-hidden false),
        // and CSS animates opacity/transform.
        function openHelp() {
            // stop any pending hide cleanup
            helpOverlay.removeEventListener('transitionend', onHideTransitionEnd);

            helpOverlay.classList.add('is-open');
            helpOverlay.setAttribute('aria-hidden', 'false');
            lockScroll();

            // focus the close button for keyboard users
            // small timeout to allow focusable element to become visible (optional)
            window.setTimeout(() => helpCloseBtn.focus(), 80);
        }

        // When closing: remove the "is-open" class which triggers the CSS hide transition.
        // Wait for transitionend before finishing cleanup (aria-hidden true + restore focus/scroll).
        function closeHelp() {
            // if already hidden, do nothing
            if (!helpOverlay.classList.contains('is-open')) return;

            // start hide transition
            helpOverlay.classList.remove('is-open');

            // add listener to finalize after transition(s)
            helpOverlay.addEventListener('transitionend', onHideTransitionEnd);
        }

        function onHideTransitionEnd(e) {
            // we only care about overlay's opacity transition end
            if (e.propertyName && (e.propertyName === 'opacity' || e.propertyName === 'transform')) {
                // mark aria-hidden and restore scrolling and focus
                helpOverlay.setAttribute('aria-hidden', 'true');
                unlockScroll();
                helpBtn.focus();

                // remove this handler
                helpOverlay.removeEventListener('transitionend', onHideTransitionEnd);
            }
        }

        helpBtn.addEventListener('click', openHelp);
        helpCloseBtn.addEventListener('click', closeHelp);

        helpOverlay.addEventListener('click', function(e) {
            if (e.target === helpOverlay) closeHelp();
        });

        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape' && helpOverlay.classList.contains('is-open')) {
                closeHelp();
            }
        });

        // Keep focus inside modal while open
        document.addEventListener('focus', function(event) {
            if (!helpOverlay.classList.contains('is-open')) return;
            if (!helpOverlay.contains(event.target)) {
                event.stopPropagation();
                helpCloseBtn.focus();
            }
        }, true);