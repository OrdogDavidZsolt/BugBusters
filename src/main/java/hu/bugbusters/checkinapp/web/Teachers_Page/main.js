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

    // Segédváltozó a wrapper eléréséhez (a nyíl forgatásához szükséges)
    // A HTML módosítás alapján a select szülője a .select-wrapper
    const wrapper = selector.closest('.select-wrapper');

    // 1. Sessionök betöltése indításkor
    TeacherAPI.getSessions().then(sessions => {
        selector.innerHTML = '<option value="">Válasszon órát...</option>';
        sessions.forEach(s => {
            const opt = document.createElement('option');
            opt.value = s.id;
            opt.textContent = s.displayName;
            selector.appendChild(opt);
        });
    }).catch(console.error);

    // 2. Eseménykezelők a nyíl forgatásához (UX javítás)

    // Kattintáskor (vagy érintéskor) nyílik meg -> aktív
    selector.addEventListener('click', () => {
        if (wrapper) wrapper.classList.add('active');
    });

    // Ha elveszti a fókuszt (kattintás kívülre), záródik -> inaktív
    selector.addEventListener('blur', () => {
        if (wrapper) wrapper.classList.remove('active');
    });

    // Billentyűzet támogatás (Space, Enter, Alt+Le nyitja a menüt)
    selector.addEventListener('keydown', (e) => {
        if (e.key === ' ' || e.key === 'Enter' || (e.altKey && e.key === 'ArrowDown')) {
            if (wrapper) wrapper.classList.add('active');
        }
        // Escape-re zárjuk be vizuálisan is
        if (e.key === 'Escape') {
            if (wrapper) wrapper.classList.remove('active');
            selector.blur(); // Fókusz elvétele
        }
    });

    // 3. Kiválasztás (Change) eseménykezelő
    selector.addEventListener('change', async function() {
        // Sikeres választáskor levesszük az active class-t és a fókuszt is
        if (wrapper) wrapper.classList.remove('active');
        this.blur();

        if (!this.value) return;

        try {
            const data = await TeacherAPI.getSessionDetails(this.value);

            // State frissítése
            state.currentSessionId = data.sessionId;
            document.getElementById('classDateTime').textContent = data.dateTime;
            document.getElementById('classLocation').textContent = data.location;
            state.students = data.students;

            // UI Animáció kezelése
            if (!state.isClassSelected) {
                state.isClassSelected = true;

                // Felirat megváltoztatása
                const label = document.getElementById('classSelectorLabel');
                if(label) label.textContent = 'Change Class';

                // Animáció elindítása (compact class hozzáadása a fő wrapperhez)
                const mainWrapper = document.getElementById('classSelectorWrapper');
                if(mainWrapper) mainWrapper.classList.add('compact');

                // Tartalom megjelenítése késleltetéssel (hogy az animáció lefusson)
                setTimeout(() => {
                    document.getElementById('contentWrapper').classList.add('visible');
                    document.getElementById('timerBadge').classList.add('visible');
                    document.querySelector('.main-card').classList.add('expanded');
                    refreshList();
                    Timer.start();
                }, 100);
            } else {
                // Ha már volt kiválasztva óra, csak frissítjük a listát
                refreshList();
                Timer.start();
            }
        } catch (e) {
            alert('Error loading class details');
            console.error(e);
        }
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