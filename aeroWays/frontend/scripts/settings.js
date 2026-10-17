document.addEventListener('DOMContentLoaded', () => {
  $('settings-form')?.addEventListener('submit', (e) => {
    e.preventDefault();
    alert('Settings saved successfully!');
  });
});
