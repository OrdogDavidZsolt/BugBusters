const API_PORT = 8080;
const API_BASE = `${window.location.protocol}//${window.location.hostname}:${API_PORT}`;

export const AdminAPI = {
    async getDatabaseLink() {
        const res = await fetch(`${API_BASE}/api/admin/db-link`, { credentials: 'include' });
        if (!res.ok) throw new Error(`Error ${res.status}`);
        return res.json();
    },

    async getReaders() {
        const res = await fetch(`${API_BASE}/api/admin/readers`, { credentials: 'include' });
        if (!res.ok) throw new Error(`Error ${res.status}`);
        return res.json();
    },

    async updateDevice(device) {
        // A device.id itt már csak a számot tartalmazza (pl. "001")
        const res = await fetch(`${API_BASE}/api/admin/device/${device.id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(device),
            credentials: 'include'
        });
        if (!res.ok) throw new Error('Update failed');
        return res;
    }
};