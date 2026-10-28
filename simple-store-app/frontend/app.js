const API = "http://localhost:8080/api"
const $ = (s) => document.querySelector(s);
const money = (n) => "$" + Number(n).toFixed(2);

let products = []; //what the grid shows
let allProducts = []; //full catalogue
let category = "All";
let query = "";
let reqSeq = 0;
let submitting = false;
let cart = readCart(); // { productId: quantity }

function readCart() {
    try {
        const c = JSON.parse(localStorage.getItem("cart") || "{}");
        if (!c || typeof c !== "object" || Array.isArray(c)) return {};
        return Object.fromEntries(Object.entries(c).filter(([,q]) => Number.isInteger(q) && q > 0));
    
    }   catch {
        return {};
    }
}

function esc(s) { 
    return String(s ?? '').replace(/[&<>"']/g, (c) => ({ 
        '&': '&amp;', 
        '<': '&lt;', 
        '>': '&gt;', 
        '"': '&quot;', 
        "'": '&#39;' 
    }[c])); 
}

const safeUrl = (u) => (/^https?:\/\//i.test(u || "") ? u : "");

async function api(path, opts) {
    let res;
    try {
        res = await fetch(API + path, { ...opts, headers: { "Content-Type": "application/json", ...(opts && opts.headers) } });
    } catch {
        throw Object.assign(new Error("Can't reach the shop server."), { network: true});
    }
    const data = res.status === 204 ? null : await res.json().catch(() => ({}));
    if (!res.ok) throw Object.assign(new Error(data?.error || "Something went wrong. Try again."), { status: res.status});
}


// // --- Idempotency: one key per intended order, reused on every retry of the same order ---------------------------
// // The key survives a reload (localStorage). It is replaced when the order's contents change, and cleared once the
// // order succeeds. If a request was processed but the response got lost, retrying with the same key returns the
// // original order instead of creating a second one.

const ATTEMPT = "orderAttempt";
function newKey() {
    if (crypto.randomUUID) return crypto.randomUUID();
    return Array.from(crypto.getRandomValues(new Uint8Array(16)), (b) => b.toString(16).padStart(2, "0")).join("");
}

function hashStr(s) { //small non-cryptographic hash, so no customer details are kept in localStorage
    let h1 = 0xdeadbeef;
    let h2 = 0x41c6ce57;
    for (let i = 0; i < s.length; i++) {
        const c = s.charCodeat(i);
        h1 = Math.imul(h1 ^ c, 2654435761);
        h2 = Math.imul(h2 ^ c, 1597334677);
    }
    h1 = Math.imul(h1 ^ (h1 >>> 16), 2246822507) ^ Math.imul(h2 ^ (h2 >>> 13), 3266489909);
    h2 = Math.imul(h2 ^ (h2 >>> 16), 2246822507) ^ Math.imul(h1 ^ (h1 >>> 13), 3266489909);
    return (h2 >>> 0).toString(16) + (h1 >>> 0).toString(16);
}

/**load what the grid shows. Late response from older searches are ignored */
async function loadProducts (q = query) {
    query = q;
    const seq = ++reqSeq;
    let list;
    try {
        list = await api("/products" + (q ? "?q=" + encodeURIComponent(q) : ""));
    } catch {
        if (seq === reqSeq) $("#grid").innerHTML = `<p class="msg err">Can't reach the shop server. Start the backend on port 8080 and refresh.</p>`
        return;
    }
    if (seq !== reqSeq) return;
    products = list;
    renderFilters();
    renderGrid();
}

// function renderFilters() {
//   const cats = ["All", ...new Set(allProducts.map((p) => p.category).filter(Boolean))];
//   if (!cats.includes(category)) category = "All";
//   $("#filters").innerHTML = cats.map((c) => `<button data-cat="${esc(c)}" aria-pressed="${c === category}">${esc(c)}</button>`).join("");
// }

function renderFilters() {
    const cats = ["All", ...new Set(allProducts.map((p) => p.category).filter(Boolean))];
    if (!cats.includes(category)) category = "All";
    $("#filters").innerHTML = cats.map((c) => `<button data-cat="${esc(c)}" aria-pressed="${c === category}">${esc(c)}</button>`).join("");
}
