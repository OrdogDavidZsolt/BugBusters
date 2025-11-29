import { AdminAPI } from './api.js';
import { Renderer } from './render.js';
import { Modal } from './modals.js';

// Define your exact HTML devices here as a fallback
const DUMMY_DEVICES = [
    { name: "Scanner Unit A1", id: "DEV-001", position: "Main Entrance", type: "QR Scanner", isOnline: true },
    { name: "Scanner Unit B2", id: "DEV-002", position: "Building B - Floor 2", type: "RFID Reader", isOnline: true },
    { name: "Terminal C3", id: "DEV-003", position: "Reception Desk", type: "Touch Terminal", isOnline: false },
    { name: "Mobile Unit M1", id: "DEV-004", position: "Parking Lot", type: "Mobile Scanner", isOnline: true },
    { name: "Kiosk K1", id: "DEV-005", position: "Cafeteria", type: "Self-Service Kiosk", isOnline: true }
];

document.addEventListener('DOMContentLoaded', () => {
    initData();
    initEvents();
    Modal.initListeners();

    // EVENT DELEGATION: This makes the Edit/Save buttons work!
    const container = document.getElementById('deviceListContainer');
    if (container) {
        container.addEventListener('click', handleCardActions);
    }
});

async function initData() {
    // 1. Load DB Link
    try {
        const data = await AdminAPI.getDatabaseLink();
        console.log("DB Link:", data.url);

        // Update the Manage Database button link dynamically
        const dbBtn = document.querySelector('.db-btn'); // Based on your HTML class
        if(dbBtn) {
            dbBtn.href = data.url;
        }
    } catch (e) { console.error(e); }

    // 2. Load Readers
    loadReaders();
}

async function loadReaders() {
    try {
        const apiReadersMap = await AdminAPI.getReaders();
        let devicesToRender = [];

        // Check if API returned data
        if (Object.keys(apiReadersMap).length > 0) {
            devicesToRender = Object.entries(apiReadersMap).map(([ip, deviceId]) => ({
                name: "RFID Olvasó",
                id: deviceId,
                position: ip,
                type: "RFID Reader",
                isOnline: true
            }));
        } else {
            // API is empty? Use your specific Dummy Data
            console.warn("No devices from API. Loading Dummy Data.");
            devicesToRender = DUMMY_DEVICES;
        }

        Renderer.renderDevices(devicesToRender);
        animateCards();

    } catch (e) {
        console.error("API Error, using dummy data:", e);
        // Fallback to dummy data on error too
        Renderer.renderDevices(DUMMY_DEVICES);
        animateCards();
    }
}

function initEvents() {
    // Refresh Button Logic
    const refreshBtn = document.getElementById('refreshBtn');
    if (refreshBtn) {
        refreshBtn.addEventListener('click', () => {
            // 1. Spin Icon
            refreshBtn.classList.add('spinning');
            setTimeout(() => refreshBtn.classList.remove('spinning'), 600);

            // 2. Reload Data
            loadReaders();
        });
    }

    // Logout Button Logic
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => {
            Modal.show('Logout', 'Are you sure you want to logout?', 'info', () => {
                window.location.href = '../index.html';
            });
        });
    }
}

// --- CARD ACTION HANDLER (Edit/Save/Cancel) ---
function handleCardActions(e) {
    const btn = e.target.closest('button');
    if (!btn) return; // Clicked somewhere else

    const action = btn.dataset.action;
    const cardDiv = btn.closest('.device-card');
    const colDiv = btn.closest('.col-12'); // The container holding the data

    if (!action || !cardDiv || !colDiv) return;

    // Get current device data stored in the DOM
    const currentDevice = JSON.parse(colDiv.dataset.json);

    if (action === 'edit') {
        // Switch to Input Fields
        Renderer.renderEditForm(cardDiv, currentDevice);
    }
    else if (action === 'cancel') {
        // Revert HTML using stored JSON
        cardDiv.classList.remove('editing');
        colDiv.innerHTML = Renderer.getCardHTML(currentDevice);
    }
    else if (action === 'save') {
        // 1. Gather new values from inputs
        const inputs = cardDiv.querySelectorAll('input');
        // Note: Input order based on renderEditForm HTML
        const newName = inputs[0].value;
        const newId = inputs[1].value;
        const newPos = inputs[2].value;
        const newType = inputs[3].value;

        // 2. Validation
        if (!newName || !newId || !newPos || !newType) {
            alert("Please fill all fields");
            return;
        }

        // 3. Update Data Object
        const updatedDevice = {
            ...currentDevice,
            name: newName,
            id: newId,
            position: newPos,
            type: newType
        };

        // 4. Update JSON storage
        colDiv.dataset.json = JSON.stringify(updatedDevice);

        // 5. Re-render View Mode
        colDiv.innerHTML = Renderer.getCardHTML(updatedDevice);

        // TODO: Send PUT request to API here to save changes permanently
        // AdminAPI.updateDevice(updatedDevice)...
    }
}

function animateCards() {
    const deviceCards = document.querySelectorAll('.device-card');
    deviceCards.forEach((card, index) => {
        const col = card.parentElement;
        if(col) {
            col.classList.remove('device-card-animated');
            void col.offsetWidth; // Trigger reflow
            setTimeout(() => col.classList.add('device-card-animated'), index * 80);
        }
    });
}