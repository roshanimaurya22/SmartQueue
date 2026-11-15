// Backend base URL (Railway). Use "" instead if you use the Netlify proxy or serve pages from Railway.
const API = "https://smartqueue-production-db1ad54b.up.railway.app";

const $ = id => document.getElementById(id);

async function request(url, options = {}) {
    const r = await fetch(API + url, {
        ...options,
        headers: { 'Content-Type': 'application/json', ...(options.headers || {}) }
    });
    let data;
    try { data = await r.json(); } catch { data = {}; }
    if (!r.ok) throw new Error(data.error || 'Request failed');
    return data;
}

function notify(msg) {
    const t = $('toast');
    if (!t) return;
    t.textContent = msg;
    t.classList.add('show');
    setTimeout(() => t.classList.remove('show'), 2800);
}

function esc(s) {
    return String(s ?? '').replace(/[&<>"']/g, c => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    }[c]));
}

async function loadData() {
    if (!$('tickets')) return;
    try {
        const [rows, s] = await Promise.all([request('/api/tickets'), request('/api/stats')]);
        ['total', 'waiting', 'serving', 'completed'].forEach(k => $(k).textContent = s[k] ?? 0);
        $('currentToken').textContent = s.current ? s.current.tokenNumber : 'No customer being served';
        $('currentName').textContent = s.current ? s.current.customerName : 'Call the next token to begin.';
        $('tickets').innerHTML = rows.length
            ? rows.map(t => `<tr>
          <td class="token">${esc(t.tokenNumber)}</td>
          <td>${esc(t.customerName)}</td>
          <td>${new Date(t.createdAt).toLocaleString()}</td>
          <td><span class="status ${t.status.toLowerCase()}">${t.status}</span></td>
          <td>${['WAITING', 'SERVING'].includes(t.status)
                ? `<button class="mini" onclick="changeStatus(${t.id},'COMPLETED')">Complete</button>
               <button class="mini danger" onclick="changeStatus(${t.id},'SKIPPED')">Skip</button>
               <button class="mini danger" onclick="changeStatus(${t.id},'CANCELLED')">Cancel</button>`
                : '—'}</td>
        </tr>`).join('')
            : '<tr><td colspan="5" class="empty">No tokens yet. Create the first token.</td></tr>';
    } catch (e) {
        notify(e.message);
    }
}

async function callNext() {
    try {
        const t = await request('/api/call-next', { method: 'POST' });
        notify('Now serving ' + t.tokenNumber);
        loadData();
    } catch (e) {
        notify(e.message);
    }
}

async function changeStatus(id, status) {
    try {
        await request(`/api/tickets/${id}/status`, {
            method: 'PATCH',
            body: JSON.stringify({ status })
        });
        notify('Ticket updated');
        loadData();
    } catch (e) {
        notify(e.message);
    }
}

if ($('joinForm')) {
    $('joinForm').addEventListener('submit', async e => {
        e.preventDefault();
        try {
            const t = await request('/api/tickets', {
                method: 'POST',
                body: JSON.stringify({ customerName: $('customerName').value })
            });
            $('result').classList.remove('hidden');
            $('result').innerHTML = `<span class="eyebrow">YOUR TOKEN NUMBER</span>
        <strong>${esc(t.tokenNumber)}</strong>
        <p>Name: ${esc(t.customerName)}</p>
        <p>Status: <span class="status waiting">WAITING</span></p>
        <a href="/track?token=${encodeURIComponent(t.tokenNumber)}">Track this token →</a>`;
            $('joinForm').reset();
            notify('Token generated successfully');
        } catch (e) {
            notify(e.message);
        }
    });
}

if ($('trackForm')) {
    const q = new URLSearchParams(location.search).get('token');
    if (q) $('token').value = q;
}