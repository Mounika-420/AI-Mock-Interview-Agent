const API_BASE = "http://localhost:8080/api";

const user = JSON.parse(localStorage.getItem("user") || "null");
if (!user) {
  window.location.href = "login.html";
}

document.getElementById("welcome-user").textContent = `Hi, ${user.fullName || user.username}`;

document.getElementById("logout-btn").addEventListener("click", () => {
  localStorage.removeItem("user");
  window.location.href = "login.html";
});

async function loadInterviews() {
  const listEl = document.getElementById("interview-list");
  listEl.innerHTML = "Loading...";

  try {
    const res = await fetch(`${API_BASE}/interview/user/${user.id}`);
    const interviews = await res.json();

    if (!interviews.length) {
      listEl.innerHTML = "<p>No interviews yet. Start one above!</p>";
      return;
    }

    listEl.innerHTML = "";
    interviews.forEach((interview) => {
      const item = document.createElement("div");
      item.className = "interview-item";
      item.innerHTML = `
        <div>
          <strong>${interview.role}</strong><br>
          <small>${interview.techStack} · ${interview.difficulty}</small>
        </div>
        <span class="status">${interview.status}</span>
      `;
      item.style.cursor = "pointer";
      item.addEventListener("click", () => {
        if (interview.status === "COMPLETED") {
          window.location.href = `result.html?interviewId=${interview.id}`;
        } else {
          window.location.href = `interview.html?interviewId=${interview.id}`;
        }
      });
      listEl.appendChild(item);
    });
  } catch (err) {
    listEl.innerHTML = "<p>Could not load interviews. Is the backend running?</p>";
  }
}

document.getElementById("new-interview-form").addEventListener("submit", async (e) => {
  e.preventDefault();
  const errorEl = document.getElementById("new-interview-error");
  errorEl.textContent = "";

  const role = document.getElementById("role").value;
  const techStack = document.getElementById("techStack").value;
  const difficulty = document.getElementById("difficulty").value;
  const numberOfQuestions = parseInt(document.getElementById("numberOfQuestions").value, 10);

  try {
    const res = await fetch(`${API_BASE}/interview/start`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ userId: user.id, role, techStack, difficulty, numberOfQuestions }),
    });
    const data = await res.json();

    if (!res.ok) {
      errorEl.textContent = data.error || "Could not start interview";
      return;
    }

    window.location.href = `interview.html?interviewId=${data.id}`;
  } catch (err) {
    errorEl.textContent = "Could not reach the server. Is the backend running?";
  }
});

loadInterviews();
