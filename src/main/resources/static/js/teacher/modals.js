export function setupModal(btnId, modalId, cancelId, confirmId, onConfirm) {
    const btn = document.getElementById(btnId);
    const modal = document.getElementById(modalId);
    const cancel = document.getElementById(cancelId);
    const confirm = document.getElementById(confirmId);

    if (btn && modal) btn.addEventListener('click', () => modal.classList.add('active'));

    const closeModal = () => modal.classList.remove('active');

    if (cancel && modal) cancel.addEventListener('click', closeModal);

    if (confirm && modal) {
        // Remove old listeners to prevent duplicates if called multiple times
        const newConfirm = confirm.cloneNode(true);
        confirm.parentNode.replaceChild(newConfirm, confirm);
        newConfirm.addEventListener('click', async () => {
            await onConfirm();
            closeModal();
        });
    }

    if (modal) modal.addEventListener('click', (e) => {
        if (e.target === modal) closeModal();
    });

    return closeModal; // Return function to close programmatically
}