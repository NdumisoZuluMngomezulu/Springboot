// // js/accounts.js
document.addEventListener('DOMContentLoaded', async () => {
  const container = $('accounts-container');

  try {
    //calls spring boot @GetMapping("/api/accounts") protected by Spring Secuirty JWT
    const accounts = await api('/api/accounts');

    if (!accounts || accounts.length === 0){
      container.innerHTML = `<p class="text-slate-400">No active accounts found.</p>`;
      return;

    }

    container.innerHTML = accounts.map(acc => `
//       <div class="account-card bg-slate-900/60 border border-slate-800 rounded-2xl p-6 backdrop-blur-md flex flex-col justify-between">
//         <div>
//           <div class="flex items-center justify-between mb-4">
//             <span class="text-xs font-semibold px-2.5 py-1 bg-indigo-500/10 text-indigo-400 border border-indigo-500/20 rounded-full">${acc.type}</span>
//             <span class="text-xs text-slate-500">•••• ${acc.accountNumber.slice(-4)}</span>
//           </div>
//           <h3 class="text-lg font-semibold text-white mb-1">${acc.title}</h3>
//           <p class="text-2xl font-bold text-slate-100 mb-4">$${acc.balance.toLocaleString(undefined, {minimumFractionDigits: 2})}</p>
//         </div>
//         <div class="pt-4 border-t border-slate-800 flex justify-between items-center text-xs text-slate-400">
//           <span>Status: <strong class="text-emerald-400 font-medium">${acc.status}</strong></span>
//           <a href="transactions.html?accountId=${acc.id}" class="text-indigo-400 hover:text-indigo-300 font-medium">View Transactions →</a>
//         </div>
//       </div>
//     `).join('');
  } catch (err) {
    container.innerHTML = `<p class="text-rose-400 text-sm">Failed to load accounts: ${err.message}</p>`;
  }

  
});

//     container.innerHTML = accounts.map(acc => `
//       <div class="account-card bg-slate-900/60 border border-slate-800 rounded-2xl p-6 backdrop-blur-md flex flex-col justify-between">
//         <div>
//           <div class="flex items-center justify-between mb-4">
//             <span class="text-xs font-semibold px-2.5 py-1 bg-indigo-500/10 text-indigo-400 border border-indigo-500/20 rounded-full">${acc.type}</span>
//             <span class="text-xs text-slate-500">•••• ${acc.accountNumber.slice(-4)}</span>
//           </div>
//           <h3 class="text-lg font-semibold text-white mb-1">${acc.title}</h3>
//           <p class="text-2xl font-bold text-slate-100 mb-4">$${acc.balance.toLocaleString(undefined, {minimumFractionDigits: 2})}</p>
//         </div>
//         <div class="pt-4 border-t border-slate-800 flex justify-between items-center text-xs text-slate-400">
//           <span>Status: <strong class="text-emerald-400 font-medium">${acc.status}</strong></span>
//           <a href="transactions.html?accountId=${acc.id}" class="text-indigo-400 hover:text-indigo-300 font-medium">View Transactions →</a>
//         </div>
//       </div>
//     `).join('');
//   } catch (err) {
//     container.innerHTML = `<p class="text-rose-400 text-sm">Failed to load accounts: ${err.message}</p>`;
//   }
// });