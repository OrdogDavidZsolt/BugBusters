let timerInterval = null;
let timeRemaining = 1200;

export const Timer = {
    start() {
        if (timerInterval) clearInterval(timerInterval);
        timeRemaining = 1200;
        timerInterval = setInterval(this.update, 1000);
        this.update();
    },

    update() {
        const minutes = Math.floor(timeRemaining / 60);
        const seconds = timeRemaining % 60;
        const timerValue = document.getElementById('timerValue');
        const timerBadge = document.getElementById('timerBadge');

        if (timerValue) {
            timerValue.textContent = `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
        }

        if (timerBadge) {
            timerBadge.classList.remove('warning', 'danger');
            if (timeRemaining <= 60) timerBadge.classList.add('danger');
            else if (timeRemaining <= 300) timerBadge.classList.add('warning');
        }

        if (timeRemaining > 0) {
            timeRemaining--;
        } else {
            clearInterval(timerInterval);
            if (timerValue) timerValue.textContent = '00:00';
        }
    }
};