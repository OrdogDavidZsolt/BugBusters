export const Renderer = {
    renderDevices(devicesArray) {
        const container = document.getElementById('deviceListContainer');
        if (!container) {
            console.error("Critical Error: #deviceListContainer not found in DOM");
            return;
        }

        container.innerHTML = '';

        // Mivel csak online eszközök jönnek a backendről, mindenki online
        let onlineCount = devicesArray.length;
        let offlineCount = 0; // Ez így konstans 0 lesz a listában lévők alapján

        devicesArray.forEach(device => {
            const col = document.createElement('div');
            col.className = 'col-12 col-md-6 col-lg-4';
            col.dataset.json = JSON.stringify(device);
            col.innerHTML = this.getCardHTML(device);
            container.appendChild(col);
        });

        this.updateCounts(devicesArray.length, onlineCount, offlineCount);
    },

    getCardHTML(device) {
        // Az ID-t a frontend egészíti ki "DEV-" előtaggal
        // A típus fixen RFID Reader
        // Nincs online/offline badge, mert csak online van
        return `
            <div class="device-card">
                <button class="edit-btn" data-action="edit" title="Edit">
                    <svg style="pointer-events: none;" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" fill="currentColor"/></svg>
                </button>
                <div class="device-name">${device.name}</div>
                <div class="device-info"><strong>ID:</strong> DEV-${device.id}</div>
                <div class="device-info"><strong>Type:</strong> RFID Reader</div>
                <div class="device-info" style="font-size: 12px; color: #94a3b8;">IP: ${device.ip}</div>
            </div>
        `;
    },

    renderEditForm(card, device) {
        card.classList.add('editing');
        // Csak a név szerkeszthető
        card.innerHTML = `
            <label style="font-size: 12px; font-weight: 600; color: #64748b; margin-bottom: 4px; display:block;">Device Name</label>
            <input type="text" class="editable-field name-field" value="${device.name}" placeholder="Device Name">
            
            <div style="margin-top: 12px; font-size: 14px; color: #64748b;">
                <strong>ID:</strong> DEV-${device.id}
            </div>
            
            <div class="edit-actions">
                <button class="save-btn" data-action="save">Save</button>
                <button class="cancel-btn" data-action="cancel">Cancel</button>
            </div>
        `;
    },

    updateCounts(total, online, offline) {
        const totalEl = document.getElementById('totalDevices');
        const onlineEl = document.getElementById('onlineDevices');
        const offlineEl = document.getElementById('offlineDevices');

        if (totalEl) totalEl.textContent = total;
        if (onlineEl) onlineEl.textContent = online;
        if (offlineEl) offlineEl.textContent = offline;
    }
};