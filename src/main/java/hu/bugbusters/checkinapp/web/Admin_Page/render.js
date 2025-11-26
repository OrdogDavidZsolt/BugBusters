export const Renderer = {
    renderDevices(devicesArray) {
        // 1. Get the container
        const container = document.getElementById('deviceListContainer');
        if (!container) {
            console.error("Critical Error: #deviceListContainer not found in DOM");
            return;
        }

        // 2. Clear previous content
        container.innerHTML = '';

        let onlineCount = 0;
        let offlineCount = 0;

        // 3. Loop and Create Elements
        devicesArray.forEach(device => {
            if(device.isOnline) onlineCount++; else offlineCount++;

            // Create Column Wrapper
            const col = document.createElement('div');
            col.className = 'col-12 col-md-6 col-lg-4';

            // Store data for editing
            col.dataset.json = JSON.stringify(device);

            // Set Inner HTML
            col.innerHTML = this.getCardHTML(device);

            // 4. APPEND TO CONTAINER (Crucial Step)
            container.appendChild(col);
        });

        this.updateCounts(devicesArray.length, onlineCount, offlineCount);
    },

    getCardHTML(device) {
        return `
            <div class="device-card">
                <button class="edit-btn" data-action="edit" title="Edit">
                    <svg style="pointer-events: none;" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" fill="currentColor"/></svg>
                </button>
                <div class="device-name">${device.name}</div>
                <div class="device-info"><strong>ID:</strong> ${device.id}</div>
                <div class="device-info"><strong>Position:</strong> ${device.position}</div>
                <div class="device-info"><strong>Type:</strong> ${device.type}</div>
                <span class="status-badge ${device.isOnline ? 'status-online' : 'status-offline'}">
                    ● ${device.isOnline ? 'Online' : 'Offline'}
                </span>
            </div>
        `;
    },

    renderEditForm(card, device) {
        card.classList.add('editing');
        card.innerHTML = `
            <input type="text" class="editable-field name-field" value="${device.name}" placeholder="Device Name">
            <input type="text" class="editable-field" value="${device.id}" placeholder="ID">
            <input type="text" class="editable-field" value="${device.position}" placeholder="Position">
            <input type="text" class="editable-field" value="${device.type}" placeholder="Type">
            <span class="status-badge ${device.isOnline ? 'status-online' : 'status-offline'}" style="opacity: 0.5">
                ● ${device.isOnline ? 'Online' : 'Offline'}
            </span>
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