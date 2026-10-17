document.addEventListener('DOMContentLoaded', () => {
  const tbody = $('tx-table-body');
  const transactions = [
    { name: 'Apple Store', category: 'Electronics', date: 'Oct 16, 2026', amount: '-$999.00', isExpense: true },
    { name: 'Payroll Direct Deposit', category: 'Income', date: 'Oct 15, 2026', amount: '+$4,250.00', isExpense: false },
    { name: 'Starbucks Coffee', category: 'Dining', date: 'Oct 14, 2026', amount: '-$6.45', isExpense: true },
    { name: 'Uber Ride', category: 'Transport', date: 'Oct 12, 2026', amount: '-$24.50', isExpense: true }
  ];

  tbody.innerHTML = transactions.map(tx => `
    <tr class="hover:bg-slate-900/40">
      <td class="p-4 font-medium text-slate-200">${tx.name}</td>
      <td class="p-4 text-slate-400">${tx.category}</td>
      <td class="p-4 text-slate-400">${tx.date}</td>
      <td class="p-4 text-right font-semibold ${tx.isExpense ? 'text-slate-200' : 'text-emerald-400'}">${tx.amount}</td>
    </tr>
  `).join('');
});
