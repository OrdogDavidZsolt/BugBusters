const API_PORT = 8080;
const API_BASE = `${window.location.protocol}//${window.location.hostname}:${API_PORT}`;

export const TeacherAPI = {


    async getCourses() {
        const res = await fetch(`${API_BASE}/api/teacher/courses`, {
        credentials: 'include'
    });
        if (!res.ok) throw new Error('Failed to load courses');
        return res.json();
    },

    async getCourseDetails(classId) {
        const res = await fetch(`${API_BASE}/api/teacher/course-details/${classId}`, {
        credentials: 'include'
    });
        if (!res.ok) throw new Error('Failed to load details');
        return res.json();
    },

    async getCurrentUser() {
    const res = await fetch(`${API_BASE}/api/user/me`, {
        credentials: 'include'
    });
    if (!res.ok) throw new Error('User error');
    return res.json();
    },

    async updateNote(attendanceId, note) {
        const res = await fetch(`${API_BASE}/api/teacher/attendance/${attendanceId}/note`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ note }),
            credentials: 'include',
        });
        if (!res.ok) throw new Error('Update failed');
        return res;
    },

    async deleteAttendance(attendanceId) {
        const res = await fetch(`${API_BASE}/api/teacher/attendance/${attendanceId}`, {
            method: 'DELETE',
            credentials: 'include',
        });
        if (!res.ok) throw new Error('Delete failed');
        return res;
    },

    async exportCsv(sessionId, email) {
        const res = await fetch(`${API_BASE}/api/teacher/export/${sessionId}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(email), // Csak sima stringként küldjük, vagy JSON objektumként
            credentials: 'include',
        });
        if (!res.ok) throw new Error('Export failed');
        return res;
    }
};