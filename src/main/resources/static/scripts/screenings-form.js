(function() {
    const salSelect = document.getElementById('auditorium');
    const availInput = document.getElementById('availableSeats');
    if (!salSelect || !availInput) return;

    function getCapacity() {
        const opt = salSelect.options[salSelect.selectedIndex];
        return opt ? parseInt(opt.getAttribute('data-capacity') || '0', 10) : 0;
    }
    function initOrUpdate(isInit) {
        const cap = getCapacity();
        if (isInit) {
            const current = parseInt(availInput.value || '0', 10);
            availInput.value = current > 0 ? current : (cap || 0);
        } else {
            availInput.value = cap || 0;
        }
    }

    salSelect.addEventListener('change', () => initOrUpdate(false));
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => initOrUpdate(true));
    } else {
        initOrUpdate(true);
    }
})();
