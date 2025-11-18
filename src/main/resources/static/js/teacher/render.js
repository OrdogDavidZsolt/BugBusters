export const Renderer = {
    renderStudents(students, containerId) {
        const list = document.getElementById(containerId);
        if (!list) return;
        list.innerHTML = '';

        students.forEach(student => {
            const noteClass = student.note ? '' : ' empty';
            const noteText = student.note || 'No notes yet';

            const div = document.createElement('div');
            div.className = 'student-card';
            div.dataset.id = student.attendanceId;

            div.innerHTML = `
                <div class="student-card-header">
                    <div class="student-info">
                        <div class="student-name">${student.name}</div>
                        <div class="student-code">${student.code}</div>
                        <div class="student-note${noteClass}">${noteText}</div>
                    </div>
                    <div class="student-actions">
                        <button class="edit-btn" data-action="edit"><svg style="pointer-events:none" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" fill="currentColor"/></svg></button>
                        <button class="delete-btn" data-action="delete"><svg style="pointer-events:none" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z" fill="currentColor"/></svg></button>
                        <div class="student-time">${student.time}</div>
                    </div>
                </div>`;
            list.appendChild(div);
        });

        const counter = document.getElementById('studentCounter');
        if (counter) counter.textContent = students.length;
    },

    renderEditMode(card, student) {
        const currentNote = student.note || '';
        card.classList.add('editing');
        card.innerHTML = `
            <div class="student-card-header">
                <div class="student-info">
                    <div class="student-name">${student.name}</div>
                    <div class="student-code">${student.code}</div>
                </div>
                <div class="student-actions">
                     <div class="student-time">${student.time}</div>
                </div>
            </div>
            <textarea class="note-edit-field" placeholder="Enter notes...">${currentNote}</textarea>
            <div class="edit-actions">
                <button class="save-btn" data-action="save">Save</button>
                <button class="cancel-btn" data-action="cancel">Cancel</button>
            </div>`;
    }
};