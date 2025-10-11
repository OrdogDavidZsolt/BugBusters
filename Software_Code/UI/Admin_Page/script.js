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

const logoutBtn = document.getElementById('logoutBtn');
logoutBtn.addEventListener('click', function() {
    if (confirm('Are you sure you want to logout?')) {
        window.location.href = '../Login_Page/index.html';
    }
});