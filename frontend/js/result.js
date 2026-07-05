const API_BASE = "https://ai-mock-interview-backend-nlrj.onrender.com/api";

const params = new URLSearchParams(window.location.search);
const interviewId = params.get("interviewId");

const errorEl = document.getElementById("result-error");

if (!interviewId) {
  window.location.href = "dashboard.html";
}

function renderList(elementId, items) {
  const el = document.getElementById(elementId);
  el.innerHTML = "";
  (items || []).forEach((item) => {
    const li = document.createElement("li");
    li.textContent = item;
    el.appendChild(li);
  });
}

async function loadFeedback() {
  try {
    const res = await fetch(`${API_BASE}/feedback/${interviewId}`);
    const data = await res.json();

    if (!res.ok) {
      errorEl.textContent = data.error || "Could not load feedback";
      return;
    }

    document.getElementById("score-badge").textContent = data.score;
    renderList("strengths-list", data.strengths);
    renderList("improvements-list", data.improvements);
    document.getElementById("detailed-feedback").textContent = data.detailedFeedback;
  } catch (err) {
    errorEl.textContent = "Could not reach the server. Is the backend running?";
  }
}

loadFeedback();
