const API_URL = "http://localhost:8080/api/tasks";

const taskForm = document.getElementById("taskForm");
const taskInput = document.getElementById("taskInput");
const taskList = document.getElementById("taskList");

async function loadTasks() {
    const response = await fetch(API_URL);
    const tasks = await response.json();

    taskList.innerHTML = "";

    tasks.forEach(task => {
        const li = document.createElement("li");
        li.innerHTML = `
            <span class="${task.completed ? "completed" : ""}">${task.title}</span>
            <div>
                <button onclick="completeTask(${task.id}, ${task.completed})">
                ${task.completed ? "Undo" : "Complete"}</button>

                <button onclick="deleteTask(${task.id})">Delete</button>
            </div>
            `;
        taskList.appendChild(li);
    });
}

taskForm.addEventListener("submit", async function(event) {
    event.preventDefault();
    //This is crucial. By default, submitting a form reloads the whole browser page. 
    // This line stops 
    // that default behaviour so the JavaScript can handle the data smoothly in the background
    const title = taskInput.value;

    await fetch(API_URL, {
        method: "POST",
        headers: {"Content-Type":"application/json"},

        body: JSON.stringify({
            title: title,
            completed: false
        })

    });

    taskInput.value = "";

    loadTasks();
})

async function completeTask(id, currentStatus) {
    await fetch(`${API_URL}/${id}`, {
        method: "PUT",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({
            completed: !currentStatus
        })       
    });

    loadTasks();
}

async function deleteTask(id) {
    await fetch(`${API_URL}/${id}`, {
        method: "DELETE"
    });
}

/**
async function loadTasks() {

    const response = await fetch(API_URL);

    const tasks = await response.json();

    taskList.innerHTML = "";

    tasks.forEach(task => {

        const li = document.createElement("li");

        li.innerHTML = `
            <span class="${task.completed ? "completed" : ""}">
                ${task.title}
            </span>

            <div>
                <button onclick="completeTask(${task.id}, ${task.completed})">
                    ${task.completed ? "Undo" : "Complete"}
                </button>

                <button onclick="deleteTask(${task.id})">
                    Delete
                </button>
            </div>
        `;

        taskList.appendChild(li);
    });
}


taskForm.addEventListener("submit", async function(event) {

    event.preventDefault();

    const title = taskInput.value;

    await fetch(API_URL, {

        method: "POST",

        headers: {
            "Content-Type": "application/json"
        },

        body: JSON.stringify({
            title: title,
            completed: false
        })

    });

    taskInput.value = "";

    loadTasks();
});


async function completeTask(id, currentStatus) {

    await fetch(`${API_URL}/${id}`, {

        method: "PUT",

        headers: {
            "Content-Type": "application/json"
        },

        body: JSON.stringify({
            completed: !currentStatus
        })

    });

    loadTasks();
}


async function deleteTask(id) {

    await fetch(`${API_URL}/${id}`, {
        method: "DELETE"
    });

    loadTasks();
}


loadTasks();
 */