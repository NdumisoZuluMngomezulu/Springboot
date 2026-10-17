document.addEventListener('DOMContentLoaded', () => {
  const container = $('cards-container');
  const cards = [
    { type: 'Visa Platinum', number: '•••• •••• •••• 4892', exp: '08/28', name: 'ALEX MORGAN', gradient: 'from-indigo-600 via-purple-600 to-pink-600' },
    { type: 'Mastercard World', number: '•••• •••• •••• 9102', exp: '11/29', name: 'ALEX MORGAN', gradient: 'from-slate-800 via-slate-900 to-slate-950' }
  ];

  container.innerHTML = cards.map(card => `
    <div class="bank-card bg-gradient-to-tr ${card.gradient} p-6 rounded-2xl border border-white/10 text-white flex flex-col justify-between h-52">
      <div class="flex justify-between items-start">
        <span class="font-bold tracking-wider text-sm">${card.type}</span>
        <i class="fa-solid fa-wifi opacity-75"></i>
      </div>
      <div class="text-xl font-mono tracking-widest my-2">${card.number}</div>
      <div class="flex justify-between items-end text-xs opacity-90">
        <div>
          <p class="text-[10px] uppercase tracking-wider opacity-75">Card Holder</p>
          <p class="font-semibold">${card.name}</p>
        </div>
        <div>
          <p class="text-[10px] uppercase tracking-wider opacity-75">Expires</p>
          <p class="font-semibold">${card.exp}</p>
        </div>
      </div>
    </div>
  `).join('');
});
