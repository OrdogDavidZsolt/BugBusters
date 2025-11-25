export const AdminAPI = {
    async getDatabaseLink() {
        const res = await fetch('/api/admin/db-link');
        if (!res.ok) throw new Error(`Error ${res.status}`);
        return res.json();
    },

    async getReaders() {
        const res = await fetch('/api/admin/readers');
        if (!res.ok) throw new Error(`Error ${res.status}`);
        return res.json();
    }
};