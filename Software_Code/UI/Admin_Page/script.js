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
    showConfirmModal(
        'Logout',
        'Are you sure you want to logout?',
        'info',
        function() {
            window.location.href = '../index.html';
        }
    );
});

// Modal functions
let modalCallback = null;

function showConfirmModal(title, message, type, onConfirm) {
    const modal = document.getElementById('confirmModal');
    const modalTitle = document.getElementById('modalTitle');
    const modalMessage = document.getElementById('modalMessage');
    const modalIcon = document.getElementById('modalIcon');
    const modalConfirmBtn = document.getElementById('modalConfirm');
    
    modalTitle.textContent = title;
    modalMessage.textContent = message;
    modalCallback = onConfirm;
    
    // Update icon and button style based on type
    if (type === 'warning') {
        modalIcon.className = 'modal-icon warning';
        modalIcon.innerHTML = '<svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z" fill="#dc2626"/></svg>';
        modalConfirmBtn.className = 'modal-btn confirm';
    } else {
        modalIcon.className = 'modal-icon info';
        modalIcon.innerHTML = '<svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z" fill="#667eea"/></svg>';
        modalConfirmBtn.className = 'modal-btn confirm info';
    }
    
    modal.classList.add('active');
}

function hideConfirmModal() {
    const modal = document.getElementById('confirmModal');
    modal.classList.remove('active');
    modalCallback = null;
}

document.getElementById('modalConfirm').addEventListener('click', function() {
    if (modalCallback) {
        modalCallback();
    }
    hideConfirmModal();
});

document.getElementById('modalCancel').addEventListener('click', function() {
    hideConfirmModal();
});

// Close modal when clicking outside
document.getElementById('confirmModal').addEventListener('click', function(e) {
    if (e.target === this) {
        hideConfirmModal();
    }
});

// Device management functions
function updateDeviceCounts() {
    const deviceCards = document.querySelectorAll('.device-card');
    let total = deviceCards.length;
    let online = 0;
    let offline = 0;

    deviceCards.forEach(card => {
        const status = card.querySelector('.status-badge');
        if (status && status.classList.contains('status-online')) {
            online++;
        } else if (status && status.classList.contains('status-offline')) {
            offline++;
        }
    });

    document.getElementById('totalDevices').textContent = total;
    document.getElementById('onlineDevices').textContent = online;
    document.getElementById('offlineDevices').textContent = offline;
}

function deleteDevice(button) {
    const col = button.closest('.col-12');
    const deviceName = col.querySelector('.device-name').textContent;
    
    showConfirmModal(
        'Delete Device',
        `Are you sure you want to delete "${deviceName}"? This action cannot be undone.`,
        'warning',
        function() {
            col.style.transition = 'opacity 0.3s ease, transform 0.3s ease';
            col.style.opacity = '0';
            col.style.transform = 'scale(0.8)';
            
            setTimeout(() => {
                col.remove();
                updateDeviceCounts();
            }, 300);
        }
    );
}

function addNewDevice() {
    const devicesGrid = document.getElementById('devicesGrid');
    const addDeviceCol = document.getElementById('addDeviceCol');
    
    const newCol = document.createElement('div');
    newCol.className = 'col-12 col-md-6 col-lg-4';
    newCol.style.opacity = '0';
    newCol.style.transform = 'scale(0.8)';
    
    newCol.innerHTML = `
        <div class="device-card">
            <button class="edit-btn" onclick="editDevice(this)">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" fill="currentColor"/>
                </svg>
            </button>
            <button class="delete-btn" onclick="deleteDevice(this)">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z" fill="currentColor"/>
                </svg>
            </button>
            <div class="device-name">New Device</div>
            <div class="device-info"><strong>ID:</strong> DEV-XXX</div>
            <div class="device-info"><strong>Position:</strong> To be assigned</div>
            <div class="device-info"><strong>Type:</strong> Pending configuration</div>
            <span class="status-badge status-offline">● Offline</span>
        </div>
    `;
    
    devicesGrid.insertBefore(newCol, addDeviceCol);
    
    setTimeout(() => {
        newCol.style.transition = 'opacity 0.3s ease, transform 0.3s ease';
        newCol.style.opacity = '1';
        newCol.style.transform = 'scale(1)';
    }, 10);
    
    updateDeviceCounts();
}

// Edit device function
function editDevice(button) {
    const card = button.closest('.device-card');
    
    // Check if already editing
    if (card.classList.contains('editing')) {
        return;
    }
    
    // Store original content
    const deviceName = card.querySelector('.device-name').textContent;
    const deviceInfos = card.querySelectorAll('.device-info');
    const deviceId = deviceInfos[0].textContent.replace('ID:', '').trim();
    const devicePosition = deviceInfos[1].textContent.replace('Position:', '').trim();
    const deviceType = deviceInfos[2].textContent.replace('Type:', '').trim();
    const statusBadge = card.querySelector('.status-badge');
    const isOnline = statusBadge.classList.contains('status-online');
    
    // Create edit form
    card.classList.add('editing');
    const timestamp = Date.now();
    card.innerHTML = `
        <button class="edit-btn" style="opacity: 0.5; cursor: not-allowed;">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" fill="currentColor"/>
            </svg>
        </button>
        <button class="delete-btn" style="opacity: 0.5; cursor: not-allowed;">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z" fill="currentColor"/>
            </svg>
        </button>
        <input type="text" class="editable-field name-field" value="${deviceName}" placeholder="Device Name">
        <input type="text" class="editable-field" value="${deviceId}" placeholder="Device ID">
        <input type="text" class="editable-field" value="${devicePosition}" placeholder="Position">
        <input type="text" class="editable-field" value="${deviceType}" placeholder="Device Type">
        <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 8px;">
            <label style="font-size: 14px; color: #4a5568; font-weight: 600;">Status:</label>
            <label style="display: flex; align-items: center; gap: 6px; cursor: pointer;">
                <input type="radio" name="status-${timestamp}" value="online" ${isOnline ? 'checked' : ''}>
                <span style="font-size: 14px; color: #047857;">Online</span>
            </label>
            <label style="display: flex; align-items: center; gap: 6px; cursor: pointer;">
                <input type="radio" name="status-${timestamp}" value="offline" ${!isOnline ? 'checked' : ''}>
                <span style="font-size: 14px; color: #dc2626;">Offline</span>
            </label>
        </div>
        <div class="edit-actions">
            <button class="save-btn" onclick="saveDevice(this)">Save</button>
            <button class="cancel-btn" onclick="cancelEdit(this, '${deviceName.replace(/'/g, "\\'")}', '${deviceId.replace(/'/g, "\\'")}', '${devicePosition.replace(/'/g, "\\'")}', '${deviceType.replace(/'/g, "\\'")}', ${isOnline})">Cancel</button>
        </div>
    `;
}

function saveDevice(button) {
    const card = button.closest('.device-card');
    const inputs = card.querySelectorAll('.editable-field');
    const nameInput = inputs[0].value.trim();
    const idInput = inputs[1].value.trim();
    const positionInput = inputs[2].value.trim();
    const typeInput = inputs[3].value.trim();
    const statusRadio = card.querySelector('input[type="radio"]:checked');
    const isOnline = statusRadio.value === 'online';
    
    if (!nameInput || !idInput || !positionInput || !typeInput) {
        alert('Please fill in all fields');
        return;
    }
    
    card.classList.remove('editing');
    card.innerHTML = `
        <button class="edit-btn" onclick="editDevice(this)">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" fill="currentColor"/>
            </svg>
        </button>
        <button class="delete-btn" onclick="deleteDevice(this)">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z" fill="currentColor"/>
            </svg>
        </button>
        <div class="device-name">${nameInput}</div>
        <div class="device-info"><strong>ID:</strong> ${idInput}</div>
        <div class="device-info"><strong>Position:</strong> ${positionInput}</div>
        <div class="device-info"><strong>Type:</strong> ${typeInput}</div>
        <span class="status-badge ${isOnline ? 'status-online' : 'status-offline'}">● ${isOnline ? 'Online' : 'Offline'}</span>
    `;
    
    updateDeviceCounts();
}

function cancelEdit(button, name, id, position, type, isOnline) {
    const card = button.closest('.device-card');
    card.classList.remove('editing');
    card.innerHTML = `
        <button class="edit-btn" onclick="editDevice(this)">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" fill="currentColor"/>
            </svg>
        </button>
        <button class="delete-btn" onclick="deleteDevice(this)">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z" fill="currentColor"/>
            </svg>
        </button>
        <div class="device-name">${name}</div>
        <div class="device-info"><strong>ID:</strong> ${id}</div>
        <div class="device-info"><strong>Position:</strong> ${position}</div>
        <div class="device-info"><strong>Type:</strong> ${type}</div>
        <span class="status-badge ${isOnline ? 'status-online' : 'status-offline'}">● ${isOnline ? 'Online' : 'Offline'}</span>
    `;
}

// Initialize counts
updateDeviceCounts();