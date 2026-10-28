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


function orderKeyFor(payload) {
    const sig = hashStr(JSON.stringify(payload));
    let a = null;
    try {
        a = JSON.parse(localStorage.getItem(ATTEMPT) || "null");
    } catch {/** ignore*/}
    if (!a || a.sig !== sig || typeof a.key !== "string") {
        a = { key: newKey(), sig};
        localStorage.setItem(ATTEMPT, JSON.stringify(a));
    }
    return a.key;
}
const clearAttempt = () => localStorage.removeItem(ATTEMPT);

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


function renderFilters() {
    const cats = ["All", ...new Set(allProducts.map((p) => p.category).filter(Boolean))];
    if (!cats.includes(category)) category = "All";
    $("#filters").innerHTML = cats.map((c) => `<button data-cat="${esc(c)}" aria-pressed="${c === category}">${esc(c)}</button>`).join("");
}


// grid rendering

function renderGrid() {
    const list = products.filter((p) => category === "ALL" || p.category === category);
    if (!list.length) { $("#grid").innerHTML = "<p>No products match. Try a different search.</p>"; return; }
    $("#grid").innerHTML = list.map((p) => {
        const img = safeUrl(p.imageUrl);
        return `
        <article class="">
            ${img ? `<img src="${esc(img)}" alt="${esc(p.name)}" loading = "lazy">` : ""}
            <div>
                <h3>${esc(p.name)}</h3><p>${esc(p.description)}</p>
                <span class="price">${money(p.price)}</span>
                ${p.stock > 0 && p.stock < 10 ? `<span class="low">Only ${p.stock} left</span>` : ""}
                <button data-add="${esc(p.id)}" ${p.stock < 1 ? "disabled" : ""}>${p.stock < 1 ? "Sold out" : "Add to cart"}</button>
            </div>
        </article>
        `;
    }).join("");
}

/**Reload the full catalogue and fix the cart to match it. Returns true if the cart had to change. */
async function syncCatalog() {
    allProducts = await api("/products");
    const byId = new Map(allProducts.map((p) => [String(p.id), p]));
    let changed = false;
    for (const [id, q] of Object.entries(cart)) {
        if (!p || p.stock < 1) { 
            delete cart[id];
            changed = true;
        } else if (q > p.stock) {
            cart[id] = p.stock;
            changed = true;
        }
    }
    if (changed) saveCart();
    return changed;
}

/**Re-sync catalogue + grid (after an order or a failed order) */
async function refresh() {
    let changed = false;
    try {
        changed = await syncCatalog();
    } catch {/**ignore */}
    await loadProducts(query);
    renderCart();
    return changed;
}

function renderCart() {
    const rows = Object.entries(cart).map(([id, q]) => ({
        p: findProduct(id),
        q
    })).filter((r) => r.p);
    $("#cartCount").textContext = rows.reduce((n, r) => n + r.q, 0);
    $("#cartTotal").textContext = money(rows.reduce((t, r) => t + r.p.price * r.q, 0));
    $("#cartItems").innerHTML = rows.length ? rows.map(({ p, q}) => `
    <li>
        <span>${esc(p.name)}</span><span>${money(p.price * q)}</span>
        <span class="qty"><button data-dec="${esc(p.id)}" aria-label="Remove one">-</button>${q}<button data-inc="${esc(p.id)}" aira-label="Add one">+</button></span>
    </li>`).join("")
    : "<li>Your cart is empty. Add something from the shop.</li>";
    $("#toCheckout").hidden = !rows.length;
    if (!rows.length) $("#checkout").hidden = true;
}



