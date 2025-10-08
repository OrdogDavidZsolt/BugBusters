const modeToggle = document.getElementById('modeToggle');
const body = document.body;
const modeLabel = document.querySelector('.mode-label');

function setFavicon(isLight) {
    let link = document.getElementById('faviconLink') || document.querySelector("link[rel~='icon']");
    if (!link) {
        link = document.createElement('link');
        link.id = 'faviconLink';
        link.rel = 'icon';
        document.head.appendChild(link);
    }
    const target = isLight ? 'favicon-light.ico' : 'favicon-dark.ico';
    link.setAttribute('href', target + '?v=' + Date.now());
    link.setAttribute('type', 'image/x-icon');
}

if (body.classList.contains('light-mode')) {
    modeToggle.checked = false;
    modeLabel.textContent = 'Light Mode';
    setFavicon(true);
} else {
    modeToggle.checked = true;
    modeLabel.textContent = 'Dark Mode';
    setFavicon(false);
}

modeToggle.addEventListener('change', function() {
    if (this.checked) {
        body.classList.remove('light-mode');
        modeLabel.textContent = 'Dark Mode';
        setFavicon(false);
    } else {
        body.classList.add('light-mode');
        modeLabel.textContent = 'Light Mode';
        setFavicon(true);
    }
});

document.getElementById('showPassword').addEventListener('change', function() {
    const passwordInput = document.getElementById('password');
    passwordInput.type = this.checked ? 'text' : 'password';
});

document.getElementById('loginForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const username = document.getElementById('username').value;
    const mode = document.querySelector('input[name="loginMode"]:checked').value;
    alert('Login submitted!\nUsername: ' + username + '\nMode: ' + (mode === 'admin' ? 'Admin' : 'Normal'));
});

const helpBtn = document.getElementById('helpBtn');
const helpOverlay = document.getElementById('helpOverlay');
const helpCloseBtn = document.getElementById('helpCloseBtn');

function lockScroll() { document.documentElement.style.overflow = 'hidden'; document.body.style.overflow = 'hidden'; }
function unlockScroll() { document.documentElement.style.overflow = ''; document.body.style.overflow = ''; }

function openHelp() {
    helpOverlay.removeEventListener('transitionend', onHideTransitionEnd);
    helpOverlay.classList.add('is-open');
    helpOverlay.setAttribute('aria-hidden', 'false');
    lockScroll();
    window.setTimeout(() => helpCloseBtn.focus(), 80);
}

function closeHelp() {
    if (!helpOverlay.classList.contains('is-open')) return;
    helpOverlay.classList.remove('is-open');
    helpOverlay.addEventListener('transitionend', onHideTransitionEnd);
}

function onHideTransitionEnd(e) {
    if (e.propertyName && (e.propertyName === 'opacity' || e.propertyName === 'transform')) {
        helpOverlay.setAttribute('aria-hidden', 'true');
        unlockScroll();
        helpBtn.focus();
        helpOverlay.removeEventListener('transitionend', onHideTransitionEnd);
    }
}

helpBtn.addEventListener('click', openHelp);
helpCloseBtn.addEventListener('click', closeHelp);
helpOverlay.addEventListener('click', function(e) { if (e.target === helpOverlay) closeHelp(); });
document.addEventListener('keydown', function(e) { if (e.key === 'Escape' && helpOverlay.classList.contains('is-open')) closeHelp(); });
document.addEventListener('focus', function(event) {
    if (!helpOverlay.classList.contains('is-open')) return;
    if (!helpOverlay.contains(event.target)) {
        event.stopPropagation();
        helpCloseBtn.focus();
    }
}, true);

// FULLSCREEN toggle functionality (változatlan)
const fullscreenBtn = document.getElementById('fullscreenBtn');
const fullscreenIcon = document.getElementById('fullscreenIcon');

const expandIcon = '<path d="M7 14H5v5h5v-2H7v-3zm-2-4h2V7h3V5H5v5zm12 7h-3v2h5v-5h-2v3zM14 5v2h3v3h2V5h-5z" fill="#1e293b"/>';
const compressIcon = '<path d="M5 16h3v3h2v-5H5v2zm3-8H5v2h5V5H8v3zm6 11h2v-3h3v-2h-5v5zm2-11V5h-2v5h5V8h-3z" fill="#1e293b"/>';

function toggleFullscreen() {
    if (!document.fullscreenElement) {
        document.documentElement.requestFullscreen().catch(err => {
            console.log(`Error attempting to enable fullscreen: ${err.message}`);
        });
    } else {
        document.exitFullscreen();
    }
}

function updateFullscreenIcon() {
    if (document.fullscreenElement) {
        fullscreenIcon.innerHTML = compressIcon;
        fullscreenBtn.setAttribute('title', 'Exit fullscreen');
    } else {
        fullscreenIcon.innerHTML = expandIcon;
        fullscreenBtn.setAttribute('title', 'Fullscreen');
    }
}

fullscreenBtn.addEventListener('click', toggleFullscreen);
document.addEventListener('fullscreenchange', updateFullscreenIcon);

// Kiegészítés: ha a felhasználó választja az "admin" módot, frissítünk egy kis hintet és a címkét
const modeUserRadio = document.getElementById('modeUser');
const modeAdminRadio = document.getElementById('modeAdmin');
const modeHint = document.getElementById('modeHint');
const pageTitle = document.getElementById('pageTitle');
const pageSubtitle = document.getElementById('pageSubtitle');

function updateModeUI() {
    const selected = document.querySelector('input[name="loginMode"]:checked').value;
    if (selected === 'admin') {
        pageSubtitle.textContent = 'Use your admin credentials to sign in';
    } else {
        pageSubtitle.textContent = 'Please enter your credentials to continue';
    }

    // frissítjük az aria-selected attribútumokat (jobb accessibility)
    modeUserRadio.nextElementSibling.setAttribute('aria-selected', selected === 'user');
    modeAdminRadio.nextElementSibling.setAttribute('aria-selected', selected === 'admin');
}

modeUserRadio.addEventListener('change', updateModeUI);
modeAdminRadio.addEventListener('change', updateModeUI);

// inicializálás
updateModeUI();