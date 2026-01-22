const apiBaseUrl = window.location.origin;
const storageKeyToken = "lab10_jwt_token";
const storageKeySelectedNoteId = "lab10_selected_note_id";
function getToken() {
return localStorage.getItem(storageKeyToken);
}
function setToken(token) {
if (token) {
localStorage.setItem(storageKeyToken, token);
} else {
localStorage.removeItem(storageKeyToken);
}
}
function clearSessionAndRedirect() {
setToken(null);
localStorage.removeItem(storageKeySelectedNoteId);
alert("Your session has expired or you are not authorized. Please log in again.");
window.location.href = "/login.html";
}
async function apiFetch(path, options) {
const token = getToken();
const headers = options && options.headers ? { ...options.headers } : {};
if (token) {
headers["Authorization"] = "Bearer " + token;
}
if (options && options.body && !headers["Content-Type"]) {
headers["Content-Type"] = "application/json";
}
const config = { ...options, headers };
let response;
try {
response = await fetch(apiBaseUrl + path, config);
} catch (e) {
alert("Network error. Please check your connection.");
throw e;
}
if (response.status === 401 || response.status === 403) {
clearSessionAndRedirect();
throw new Error("Unauthorized or forbidden");
}
if (!response.ok) {
let message = "Request failed with status " + response.status;
try {
const data = await response.json();
if (data && data.message) {
message = data.message;
}
} catch (e) {
}
alert(message);
throw new Error(message);
}
const contentType = response.headers.get("Content-Type") || "";
if (contentType.includes("application/json")) {
return response.json();
}
return response.text();
}
async function handleRegisterSubmit(event) {
event.preventDefault();
const username = document.getElementById("register-username").value.trim();
const email = document.getElementById("register-email").value.trim();
const password = document.getElementById("register-password").value;
const payload = { username, email, password };
try {
const data = await apiFetch("/auth/register", {
method: "POST",
body: JSON.stringify(payload)
});
if (data && data.token) {
setToken(data.token);
window.location.href = "/dashboard.html";
}
} catch (e) {
}
}
async function handleLoginSubmit(event) {
event.preventDefault();
const email = document.getElementById("login-email").value.trim();
const password = document.getElementById("login-password").value;
const payload = { email, password };
try {
const data = await apiFetch("/auth/login", {
method: "POST",
body: JSON.stringify(payload)
});
if (data && data.token) {
setToken(data.token);
window.location.href = "/dashboard.html";
}
} catch (e) {
}
}
let notesCache = [];
let selectedNoteId = null;
function setSelectedNoteId(id) {
selectedNoteId = id;
if (id === null) {
localStorage.removeItem(storageKeySelectedNoteId);
} else {
localStorage.setItem(storageKeySelectedNoteId, String(id));
}
}
function getSelectedNoteFromStorage() {
const value = localStorage.getItem(storageKeySelectedNoteId);
if (!value) {
return null;
}
const num = Number(value);
return Number.isNaN(num) ? null : num;
}
function renderNotesList() {
const listEl = document.getElementById("notes-list");
if (!listEl) {
return;
}
listEl.innerHTML = "";
if (!notesCache || notesCache.length === 0) {
const emptyItem = document.createElement("li");
emptyItem.className = "notes-list-empty";
emptyItem.textContent = "No notes yet";
listEl.appendChild(emptyItem);
return;
}
notesCache.forEach(note => {
const li = document.createElement("li");
li.className = "notes-list-item";
if (note.id === selectedNoteId) {
li.classList.add("active");
}
li.dataset.id = note.id;
li.textContent = note.title || "Untitled note";
li.addEventListener("click", () => {
loadNoteIntoEditor(note.id);
});
listEl.appendChild(li);
});
}
function loadNoteIntoEditor(id) {
const note = notesCache.find(n => n.id === id);
const titleInput = document.getElementById("note-title-input");
const contentInput = document.getElementById("note-content-input");
if (!note || !titleInput || !contentInput) {
return;
}
titleInput.value = note.title || "";
contentInput.value = note.content || "";
setSelectedNoteId(note.id);
renderNotesList();
}
function clearEditor() {
const titleInput = document.getElementById("note-title-input");
const contentInput = document.getElementById("note-content-input");
if (titleInput) {
titleInput.value = "";
}
if (contentInput) {
contentInput.value = "";
}
setSelectedNoteId(null);
renderNotesList();
}
async function fetchNotes() {
try {
const data = await apiFetch("/notes", {
method: "GET"
});
notesCache = Array.isArray(data) ? data : [];
const storedId = getSelectedNoteFromStorage();
if (storedId) {
const exists = notesCache.some(n => n.id === storedId);
if (exists) {
selectedNoteId = storedId;
} else {
setSelectedNoteId(null);
}
}
renderNotesList();
if (selectedNoteId) {
loadNoteIntoEditor(selectedNoteId);
} else if (notesCache.length > 0) {
loadNoteIntoEditor(notesCache[0].id);
}
} catch (e) {
}
}
async function handleSaveNote() {
const titleInput = document.getElementById("note-title-input");
const contentInput = document.getElementById("note-content-input");
if (!titleInput || !contentInput) {
return;
}
const title = titleInput.value.trim();
const content = contentInput.value.trim();
if (!title && !content) {
alert("Please enter a title or content before saving.");
return;
}
const payload = { title: title || "Untitled note", content };
try {
if (selectedNoteId == null) {
const created = await apiFetch("/notes", {
method: "POST",
body: JSON.stringify(payload)
});
await fetchNotes();
if (created && created.id) {
loadNoteIntoEditor(created.id);
}
} else {
await apiFetch("/notes/" + selectedNoteId, {
method: "PUT",
body: JSON.stringify(payload)
});
await fetchNotes();
if (selectedNoteId) {
loadNoteIntoEditor(selectedNoteId);
}
}
} catch (e) {
}
}
async function handleDeleteNote() {
if (selectedNoteId == null) {
alert("Select a note to delete.");
return;
}
const confirmed = window.confirm("Are you sure you want to delete this note?");
if (!confirmed) {
return;
}
try {
await apiFetch("/notes/" + selectedNoteId, {
method: "DELETE"
});
setSelectedNoteId(null);
await fetchNotes();
clearEditor();
} catch (e) {
}
}
function handleLogout() {
setToken(null);
localStorage.removeItem(storageKeySelectedNoteId);
window.location.href = "/login.html";
}
function ensureAuthenticatedForDashboard() {
const token = getToken();
if (!token) {
window.location.href = "/login.html";
}
}
function initAuthPages() {
const loginForm = document.getElementById("login-form");
if (loginForm) {
loginForm.addEventListener("submit", handleLoginSubmit);
}
const registerForm = document.getElementById("register-form");
if (registerForm) {
registerForm.addEventListener("submit", handleRegisterSubmit);
}
}
function initDashboardPage() {
ensureAuthenticatedForDashboard();
const newNoteBtn = document.getElementById("new-note-btn");
const saveNoteBtn = document.getElementById("save-note-btn");
const deleteNoteBtn = document.getElementById("delete-note-btn");
const logoutBtn = document.getElementById("logout-btn");
if (newNoteBtn) {
newNoteBtn.addEventListener("click", () => {
clearEditor();
});
}
if (saveNoteBtn) {
saveNoteBtn.addEventListener("click", () => {
handleSaveNote();
});
}
if (deleteNoteBtn) {
deleteNoteBtn.addEventListener("click", () => {
handleDeleteNote();
});
}
if (logoutBtn) {
logoutBtn.addEventListener("click", () => {
handleLogout();
});
}
fetchNotes();
}
document.addEventListener("DOMContentLoaded", () => {
const path = window.location.pathname;
if (path.endsWith("/login.html")) {
initAuthPages();
} else if (path.endsWith("/register.html")) {
initAuthPages();
} else if (path.endsWith("/dashboard.html")) {
initDashboardPage();
} else if (path === "/" || path === "") {
window.location.href = "/login.html";
}
});

