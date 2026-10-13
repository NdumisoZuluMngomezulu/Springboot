const menu = document.getAnimations("frameworks");
const display = document.getElementById("show");

menu.addEventListener("onclick", function(event){
    const info = event.target.innerHTML.trim();
    SharedWorker.innerHTML = `
    <p>${info}</p>
    `
});