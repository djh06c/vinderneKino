// Hent screeningId fra global eller data-attribut
function getScreeningId() {
    if (typeof screeningId !== "undefined") return screeningId;
    const el = document.getElementById("seating");
    const v = el?.dataset?.screeningId;
    return v ? parseInt(v, 10) : 1;
}

const state = { screeningId: getScreeningId(), needed: 2, seats: [] };
let busy = false; // simple klik/req-lås


async function loadSeats() {
    try {
        const res = await fetch(`/api/screenings/${state.screeningId}/seats`);
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        state.seats = await res.json(); // [{row, number, isAisle, status}]
    } catch (err) {
        console.error("loadSeats()", err);
        state.seats = [];
    }
    render();
}

async function toggleSeat(seat) {
    if (busy) return;
    if (seat.status === 'SOLD' || seat.status === 'HELD_OTHER') return;

    busy = true;
    try {
        const selecting = seat.status !== 'HELD_BY_ME';
        const body = JSON.stringify({ seats: [{ row: seat.row, number: seat.number }] });
        const path = selecting ? 'hold' : 'release';
        const res = await fetch(`/api/screenings/${state.screeningId}/${path}`, {
            method: 'POST', headers: {'Content-Type':'application/json'}, body
        });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        await loadSeats();
    } catch (err) {
        console.error("toggleSeat()", err);
        toast('Kunne ikke opdatere sæde');
    } finally {
        busy = false;
    }
}

async function suggestAndHold() {
    const suggestion = findBestBlock(state.seats, state.needed);
    if (!suggestion || suggestion.length === 0) {
        alert('Ingen sammenhængende pladser fundet');
        return;
    }
    try {
        const res = await fetch(`/api/screenings/${state.screeningId}/hold`, {
            method:'POST', headers:{'Content-Type':'application/json'},
            body: JSON.stringify({ seats: suggestion.map(s => ({ row: s.row, number: s.number })) })
        });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        await loadSeats();
    } catch (err) {
        console.error("suggestAndHold()", err);
        toast('Kunne ikke holde sæder');
    }
}

// ---------------- UI ----------------
function cssClass(status){
    return { AVAILABLE:'available', HELD_BY_ME:'mine', HELD_OTHER:'held', SOLD:'sold' }[status];
}

function updateNeeded() {
    const count = document.getElementById('count');
    if (count) count.textContent = state.needed;

    const chosen = state.seats.filter(x=>x.status==='HELD_BY_ME').length;
    const statusEl = document.getElementById('status');
    if (statusEl) statusEl.textContent = `Valgt: ${chosen} / ${state.needed}`;
}

function render() {
    const root = document.getElementById('seating');
    if (!root) return;

    if (!state.seats || state.seats.length === 0) {
        root.innerHTML = '';
        updateNeeded();
        return;
    }

    const gap  = 6;
    const maxNum = Math.max(...state.seats.map(s => s.number));

    // gør grid’et mindst ~600px bredt ved at øge seat-størrelsen lidt
    const targetMinPx = 600;
    let cell = 28;
    let gridPx = maxNum * cell + (maxNum - 1) * gap;
    if (gridPx < targetMinPx) {
        cell = Math.min(44, Math.floor((targetMinPx - (maxNum - 1) * gap) / maxNum));
        gridPx = maxNum * cell + (maxNum - 1) * gap;
    }

    // grid-opsætning + fast bredde (så centrerer det)
    root.style.display = 'grid';
    root.style.gridTemplateColumns = `repeat(${maxNum}, ${cell}px)`;
    root.style.gap = `${gap}px`;
    root.style.width = `${gridPx}px`;
    root.style.margin = '0 auto';

    // match “lærred” til samme bredde
    const screen = document.querySelector('.screen');
    if (screen) screen.style.width = `${gridPx}px`;

    root.innerHTML = '';

    const rows = Array.from(new Set(state.seats.map(s => s.row))).sort();
    const rowIndex = new Map(rows.map((r,i)=>[r,i]));

    for (const s of state.seats) {
        const div = document.createElement('div');
        div.className = `seat ${cssClass(s.status)}`;
        div.style.width = `${cell}px`;
        div.style.height = `${cell}px`;
        div.style.borderRadius = '6px';
        div.style.gridColumnStart = s.number;
        div.style.gridRowStart = (rowIndex.get(s.row) ?? 0) + 1;
        div.title = `Række ${s.row}, Sæde ${s.number}`;
        div.onclick = () => toggleSeat(s);
        root.appendChild(div);
    }

    updateNeeded();
}


// “Find bedste sæder”
function findBestBlock(seats, N) {
    const candidates = seats.filter(s => s.status==='AVAILABLE' || s.status==='HELD_BY_ME');

    const byRow = new Map();
    for (const s of candidates) {
        const arr = byRow.get(s.row) || [];
        arr.push(s); byRow.set(s.row, arr);
    }

    const maxNum = Math.max(...seats.map(s => s.number));
    const center = (maxNum + 1) / 2;

    let best = null, bestScore = Number.POSITIVE_INFINITY;

    for (const [row, arr] of byRow) {
        arr.sort((a,b)=>a.number-b.number);
        for (let i=0;i<=arr.length-N;i++) {
            const block = arr.slice(i,i+N);
            // sammenhængende?
            let contiguous = true;
            for (let j=1;j<block.length;j++){
                if (block[j].number !== block[j-1].number + 1) { contiguous = false; break; }
            }
            if (!contiguous) continue;

            const score = block.reduce((acc,s)=>acc+Math.abs(s.number-center),0)
                + Math.abs(rowCenterIndex(seats,row));
            if (score < bestScore) { bestScore = score; best = block; }
        }
    }
    return best;
}

function rowCenterIndex(seats, row) {
    const rows = Array.from(new Set(seats.map(s => s.row))).sort();
    const ideal = Math.floor((rows.length - 1) / 2);
    return rows.indexOf(row) - ideal;
}

// Toast helper (simpel)
function toast(msg){
    const t = document.getElementById('toast');
    if (!t) return;
    t.textContent = msg;
    t.style.display = 'block';
    setTimeout(()=> t.style.display='none', 2000);
}


// ---------------- Bindings & init ----------------
document.getElementById('inc')?.addEventListener('click', () => { state.needed++; updateNeeded(); });
document.getElementById('dec')?.addEventListener('click', () => { state.needed = Math.max(1, state.needed-1); updateNeeded(); });
document.getElementById('suggest')?.addEventListener('click', suggestAndHold);

document.addEventListener('DOMContentLoaded', () => {
    state.screeningId = getScreeningId();
    loadSeats();
    setInterval(loadSeats, 5000);
});


