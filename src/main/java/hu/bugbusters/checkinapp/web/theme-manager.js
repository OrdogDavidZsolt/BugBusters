document.addEventListener('DOMContentLoaded', () => {
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
        // We use a relative path check or absolute path to ensure it works from subfolders
        const baseUrl = window.location.pathname.includes('_Page') ? '../' : '';
        const target = isLight ? baseUrl + 'favicon-light.ico' : baseUrl + 'favicon-dark.ico';

        link.setAttribute('href', target + '?v=' + Date.now());
        link.setAttribute('type', 'image/x-icon');
    }

    // Initialize
    if (body.classList.contains('light-mode')) {
        if(modeToggle) modeToggle.checked = false;
        if(modeLabel) modeLabel.textContent = 'Light Mode';
        setFavicon(true);
    } else {
        if(modeToggle) modeToggle.checked = true;
        if(modeLabel) modeLabel.textContent = 'Dark Mode';
        setFavicon(false);
    }

    // Listener
    if(modeToggle) {
        modeToggle.addEventListener('change', function() {
            if (this.checked) {
                body.classList.remove('light-mode');
                if(modeLabel) modeLabel.textContent = 'Dark Mode';
                setFavicon(false);
            } else {
                body.classList.add('light-mode');
                if(modeLabel) modeLabel.textContent = 'Light Mode';
                setFavicon(true);
            }
        });
    }
});