let modalCallback = null;
const confirmModal = document.getElementById('confirmModal');

export const Modal = {
    show(title, message, type, onConfirm) {
        if (!confirmModal) return;

        const modalTitle = document.getElementById('modalTitle');
        const modalMessage = document.getElementById('modalMessage');
        const modalIcon = document.getElementById('modalIcon');
        const modalConfirmBtn = document.getElementById('modalConfirm');

        if (modalTitle) modalTitle.textContent = title;
        if (modalMessage) modalMessage.textContent = message;
        modalCallback = onConfirm;

        if (type === 'warning') {
            modalIcon.className = 'modal-icon warning';
            modalIcon.innerHTML = '<svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z" fill="#dc2626"/></svg>';
            modalConfirmBtn.className = 'modal-btn confirm';
        } else {
            modalIcon.className = 'modal-icon info';
            modalIcon.innerHTML = '<svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z" fill="#667eea"/></svg>';
            modalConfirmBtn.className = 'modal-btn confirm info';
        }
        confirmModal.classList.add('active');
    },

    hide() {
        if (confirmModal) confirmModal.classList.remove('active');
        modalCallback = null;
    },

    initListeners() {
        const confirmBtn = document.getElementById('modalConfirm');
        const cancelBtn = document.getElementById('modalCancel');

        if (confirmBtn) {
            // Prevent duplicate listeners by cloning or just ensuring single init
            confirmBtn.onclick = () => {
                if (modalCallback) modalCallback();
                this.hide();
            };
        }

        if (cancelBtn) cancelBtn.onclick = () => this.hide();

        if (confirmModal) {
            confirmModal.onclick = (e) => {
                if (e.target === confirmModal) this.hide();
            };
        }
    }
};