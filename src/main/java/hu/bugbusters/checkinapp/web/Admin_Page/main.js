import { AdminAPI } from './api.js';
import { Renderer } from './render.js';
import { Modal } from './modals.js';

const DUMMY_DEVICES = [
    { name: "Demo Scanner", id: "DEMO-01", position: "Front Desk", type: "RFID", isOnline: false }
];

document.addEventListener('DOMContentLoaded', () => {
    initData();
    initEvents();
    Modal.initListeners();

    const container = document.getElementById('deviceListContainer');
    if (container) {
        container.addEventListener('click', handleCardActions);
    }
});

async function initData() {
    try {
        const data = await AdminAPI.getDatabaseLink();
        const dbBtn = document.querySelector('.db-btn');
        if(dbBtn) dbBtn.href = data.url;
    } catch (e) { console.error(e); }

    loadReaders();
}

async function loadReaders() {
    try {
        // Now returns an array of ReaderDeviceDTO
        const devices = await AdminAPI.getReaders();

        if (devices && devices.length > 0) {
            Renderer.renderDevices(devices);
        } else {
            console.warn("No devices found. Waiting for connection...");
            Renderer.renderDevices([]); // Or render DUMMY_DEVICES if you prefer
        }
        animateCards();

    } catch (e) {
        console.error("API Error:", e);
        Renderer.renderDevices(DUMMY_DEVICES);
        animateCards();
    }
}

function initEvents() {
    const refreshBtn = document.getElementById('refreshBtn');
    if (refreshBtn) {
        refreshBtn.addEventListener('click', () => {
            refreshBtn.classList.add('spinning');
            setTimeout(() => refreshBtn.classList.remove('spinning'), 600);
            loadReaders();
        });
    }

    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => {
            Modal.show('Logout', 'Are you sure you want to logout?', 'info', () => {
                window.location.href = '../index.html';
            });
        });
    }
}

async function handleCardActions(e) {
    const btn = e.target.closest('button');
    if (!btn) return;

    const action = btn.dataset.action;
    const cardDiv = btn.closest('.device-card');
    const colDiv = btn.closest('.col-12');

    if (!action || !cardDiv || !colDiv) return;

    const currentDevice = JSON.parse(colDiv.dataset.json);

    if (action === 'edit') {
        Renderer.renderEditForm(cardDiv, currentDevice);
    }
    else if (action === 'cancel') {
        cardDiv.classList.remove('editing');
        colDiv.innerHTML = Renderer.getCardHTML(currentDevice);
    }
    else if (action === 'save') {
        const inputs = cardDiv.querySelectorAll('input');
        const newName = inputs[0].value;
        const newId = inputs[1].value; // Read-only ideally, but kept for structure
        const newPos = inputs[2].value;
        const newType = inputs[3].value;

        if (!newName || !newPos || !newType) {
            alert("Please fill all fields");
            return;
        }

        const updatedDevice = {
            ...currentDevice,
            name: newName,
            position: newPos,
            type: newType
        };

        try {
            // CALL API TO SAVE
            await AdminAPI.updateDevice(updatedDevice);

            // Update UI on success
            colDiv.dataset.json = JSON.stringify(updatedDevice);
            colDiv.innerHTML = Renderer.getCardHTML(updatedDevice);
        } catch (err) {
            alert("Failed to save changes to server.");
            console.error(err);
        }
    }
}

function animateCards() {
    const deviceCards = document.querySelectorAll('.device-card');
    deviceCards.forEach((card, index) => {
        const col = card.parentElement;
        if(col) {
            col.classList.remove('device-card-animated');
            void col.offsetWidth;
            setTimeout(() => col.classList.add('device-card-animated'), index * 80);
        }
    });
}