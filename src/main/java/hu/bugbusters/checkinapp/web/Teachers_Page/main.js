import { TeacherAPI } from './api.js';
import { Renderer } from './render.js';
import { Timer } from './timer.js';
import { setupModal } from './modals.js';

// State
let state = {
    students: [],
    sort: { field: 'name', order: 'asc' },
    editingId: null,
    deleteId: null,
    isClassSelected: false,
    currentUserEmail: '', // ÚJ MEZŐ
    currentSessionId: null // Ezt is tároljuk el, hogy tudjuk mit exportálunk
};

// --- Initialization ---
document.addEventListener('DOMContentLoaded', async () => {
    loadUser();
    initClassSelector();
    initSortButtons();
    initModals();

    // Event Delegation for Student List (Edit/Save/Delete buttons)
    document.getElementById('attendanceList').addEventListener('click', handleListClick);

    const homeBtn = document.getElementById('homeBtn');
    if(homeBtn) homeBtn.addEventListener('click', () => window.location.href = '../index.html');
});

async function loadUser() {
    try {
        const user = await TeacherAPI.getCurrentUser();
        const display = document.getElementById('teacherNameDisplay');
        if(display) display.textContent = user.name;
        state.currentUserEmail = user.email;
    } catch (e) { console.error(e); }
}

/*
function initClassSelector() {
    const selector = document.getElementById('classSelector');

    TeacherAPI.getCourses().then(courses => {
        selector.innerHTML = '<option value="">Válasszon órát...</option>';
        courses.forEach(c => {
            const opt = document.createElement('option');
            opt.value = c.id;
            opt.textContent = c.name;
            selector.appendChild(opt);
        });
    }).catch(console.error);

    selector.addEventListener('change', async function() {
        if (!this.value) return;

        try {
            const data = await TeacherAPI.getCourseDetails(this.value);
            state.currentSessionId = data.sessionId;
            document.getElementById('classDateTime').textContent = data.dateTime;
            document.getElementById('classLocation').textContent = data.location;

            state.students = data.students;

            if (!state.isClassSelected) {
                state.isClassSelected = true;
                setTimeout(() => {
                    document.getElementById('contentWrapper').classList.add('visible');
                    document.getElementById('timerBadge').classList.add('visible');
                    document.querySelector('.main-card').classList.add('expanded');
                    refreshList();
                    Timer.start();
                }, 100);
            } else {
                refreshList();
                Timer.start();
            }
        } catch (e) { alert('Error loading class'); console.error(e); }
    });
}
*/

function initClassSelector() {
    const selector = document.getElementById('classSelector');

    // MÓDOSÍTÁS: getCourses helyett getSessions
    TeacherAPI.getSessions().then(sessions => {
        selector.innerHTML = '<option value="">Válasszon órát...</option>';
        sessions.forEach(s => {
            const opt = document.createElement('option');
            opt.value = s.id; // Ez most már a SESSION ID, nem a Course ID
            opt.textContent = s.displayName; // Pl: "Webfejlesztés - 2025.11.26 14:00"
            selector.appendChild(opt);
        });
    }).catch(console.error);

    // main.js - initClassSelector függvényen belül

    selector.addEventListener('change', async function() {

        this.blur(); // Ez veszi el a fókuszt, így a nyíl azonnal visszafordul

        if (!this.value) return;

        try {
            const data = await TeacherAPI.getSessionDetails(this.value);

            state.currentSessionId = data.sessionId;
            document.getElementById('classDateTime').textContent = data.dateTime;
            document.getElementById('classLocation').textContent = data.location;

            state.students = data.students;

            if (!state.isClassSelected) {
                state.isClassSelected = true;

                // --- EZT A RÉSZT FRISSÍTSD/ADZD HOZZÁ ---

                // 1. Felirat megváltoztatása
                const label = document.getElementById('classSelectorLabel');
                if(label) label.textContent = 'Change Class'; // Vagy 'Óra cseréje' magyarul

                // 2. Animáció elindítása (compact class hozzáadása)
                const wrapper = document.getElementById('classSelectorWrapper');
                if(wrapper) wrapper.classList.add('compact');

                // --- EDDIG TART A MÓDOSÍTÁS ---

                setTimeout(() => {
                    document.getElementById('contentWrapper').classList.add('visible');
                    document.getElementById('timerBadge').classList.add('visible');
                    document.querySelector('.main-card').classList.add('expanded');
                    refreshList();
                    Timer.start();
                }, 100);
            } else {
                refreshList();
                Timer.start();
            }
        } catch (e) { alert('Error loading class details'); console.error(e); }
    });
}

// --- Core Logic ---
function refreshList() {
    // Sort
    state.students.sort((a, b) => {
        let valA = a[state.sort.field];
        let valB = b[state.sort.field];
        if (state.sort.field === 'time') {
            valA = valA.replace(/:/g, '');
            valB = valB.replace(/:/g, '');
        }
        return state.sort.order === 'asc'
            ? (valA > valB ? 1 : -1)
            : (valA < valB ? 1 : -1);
    });

    Renderer.renderStudents(state.students, 'attendanceList');
}

// --- Event Delegation (The "Pro" way to handle dynamic buttons) ---
async function handleListClick(e) {
    const btn = e.target.closest('button');
    if (!btn) return;

    const card = btn.closest('.student-card');
    const id = parseInt(card.dataset.id);
    const action = btn.dataset.action;

    if (action === 'edit') {
        if (state.editingId) return; // Already editing someone else
        state.editingId = id;
        const student = state.students.find(s => s.attendanceId === id);
        Renderer.renderEditMode(card, student);
    }

    else if (action === 'cancel') {
        state.editingId = null;
        refreshList();
    }

    else if (action === 'save') {
        const newNote = card.querySelector('.note-edit-field').value.trim();
        try {
            await TeacherAPI.updateNote(id, newNote);
            const student = state.students.find(s => s.attendanceId === id);
            if (student) student.note = newNote;
            state.editingId = null;
            refreshList();
        } catch (e) { alert('Save failed'); }
    }

    else if (action === 'delete') {
        state.deleteId = id;
        document.getElementById('deleteModal').classList.add('active');
    }
}

// --- Sort Buttons ---
function initSortButtons() {
    document.querySelectorAll('.sort-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            const field = btn.dataset.sort;
            state.sort.order = (state.sort.field === field && state.sort.order === 'asc') ? 'desc' : 'asc';
            state.sort.field = field;

            // UI Updates
            document.querySelectorAll('.sort-btn').forEach(b => {
                b.classList.remove('active');
                b.querySelector('.sort-arrow').textContent = '▲';
            });
            btn.classList.add('active');
            btn.querySelector('.sort-arrow').textContent = state.sort.order === 'asc' ? '▲' : '▼';

            refreshList();
        });
    });
}

function initModals() {
    // Clear All
    setupModal('clearAllBtn', 'clearAllModal', 'clearAllCancel', 'clearAllConfirm', () => {
        state.students = [];
        refreshList();
    });

    // --- EXPORT RÉSZ JAVÍTÁSA ---

    // 1. Előkészítés: Amikor rákattint a gombra, írjuk be az emailt a mezőbe
    const exportBtn = document.getElementById('exportBtn');
    if (exportBtn) {
        exportBtn.addEventListener('click', () => {
            const emailInput = document.getElementById('exportEmail');
            // Ha van elmentett email címünk a betöltésből, írjuk be
            if (emailInput && state.currentUserEmail) {
                emailInput.value = state.currentUserEmail;
            }
        });
    }

    // 2. A Modal logikája: Küldés a backendnek
    setupModal('exportBtn', 'exportModal', 'exportCancel', 'exportConfirm', async () => {
        const emailInput = document.getElementById('exportEmail');
        const email = emailInput ? emailInput.value.trim() : '';

        // Validáció
        if (!email || !email.includes('@')) {
            alert('Kérlek adj meg egy érvényes email címet!');
            throw new Error('Invalid email'); // Ez megakadályozza a modal bezárását (ha a setupModal így van írva)
        }

        if (!state.currentSessionId) {
            alert('Nincs kiválasztott óra, amit exportálhatnánk!');
            return;
        }

        try {
            // API hívás a backend felé
            await TeacherAPI.exportCsv(state.currentSessionId, email);
            alert(`Sikeres exportálás! A jelentést elküldtük a(z) ${email} címre.`);
            // A setupModal automatikusan bezárja a modalt ezután
        } catch (e) {
            console.error(e);
            alert('Hiba történt az e-mail küldésekor.');
        }
    });

    // --- EXPORT RÉSZ VÉGE ---

    // Logout
    setupModal('logoutBtn', 'logoutModal', 'logoutCancel', 'logoutConfirm', () => {
        window.location.href = '../index.html';
    });

    // Delete
    setupModal(null, 'deleteModal', 'deleteCancel', 'deleteConfirm', async () => {
        if (state.deleteId) {
            try {
                await TeacherAPI.deleteAttendance(state.deleteId);
                state.students = state.students.filter(s => s.attendanceId !== state.deleteId);
                state.deleteId = null;
                refreshList();
            } catch (e) { alert('Delete failed'); }
        }
    });
}