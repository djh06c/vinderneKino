 (function(){
    const sel = document.getElementById('auditorium');
    const seats = document.getElementById('availableSeats');
    const info = document.getElementById('audInfo');

    async function sync(){
    const v = sel.value;
    if (!v) { seats.removeAttribute('max'); info && (info.textContent=''); return; }
    try {
    const res = await fetch(`/theaters/${v}/spec`);
    if (!res.ok) throw new Error('Spec ikke fundet');
    const t = await res.json();
    seats.setAttribute('max', t.capacity);
    if (!seats.value || (+seats.value > t.capacity)) seats.value = t.capacity;
    if (info) info.textContent = `Sal ${t.auditorium}: ${t.rows} rækker × ${t.seatsPerRow} = ${t.capacity} pladser.`;
} catch(e) {
    seats.removeAttribute('max'); if (info) info.textContent = '';
    console.error(e);
}
}
    sel.addEventListener('change', sync);
    window.addEventListener('load', sync);
})();

