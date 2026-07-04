const API_BASE = "http://localhost:8080/api";

const params = new URLSearchParams(window.location.search);
const interviewId = params.get("interviewId");

if (!interviewId) {
  window.location.href = "dashboard.html";
}

let questions = [];
let answers = [];
let currentIndex = 0;

const questionText = document.getElementById("question-text");
const answerBox = document.getElementById("answer-box");
const progressEl = document.getElementById("question-progress");
const prevBtn = document.getElementById("prev-btn");
const nextBtn = document.getElementById("next-btn");
const submitBtn = document.getElementById("submit-btn");
const errorEl = document.getElementById("interview-error");

function renderQuestion() {
  questionText.textContent = questions[currentIndex];
  answerBox.value = answers[currentIndex] || "";
  progressEl.textContent = `Question ${currentIndex + 1} of ${questions.length}`;

  prevBtn.disabled = currentIndex === 0;

  if (currentIndex === questions.length - 1) {
    nextBtn.classList.add("hidden");
    submitBtn.classList.remove("hidden");
  } else {
    nextBtn.classList.remove("hidden");
    submitBtn.classList.add("hidden");
  }
}

function saveCurrentAnswer() {
  answers[currentIndex] = answerBox.value;
}

prevBtn.addEventListener("click", () => {
  saveCurrentAnswer();
  if (currentIndex > 0) {
    currentIndex--;
    renderQuestion();
  }
});

nextBtn.addEventListener("click", () => {
  saveCurrentAnswer();
  if (currentIndex < questions.length - 1) {
    currentIndex++;
    renderQuestion();
  }
});

submitBtn.addEventListener("click", async () => {
  saveCurrentAnswer();
  errorEl.textContent = "";
  submitBtn.disabled = true;
  submitBtn.textContent = "Evaluating your answers...";

  try {
    const res = await fetch(`${API_BASE}/interview/${interviewId}/submit`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(answers),
    });
    const data = await res.json();

    if (!res.ok) {
      errorEl.textContent = data.error || "Could not submit answers";
      submitBtn.disabled = false;
      submitBtn.textContent = "Submit Interview";
      return;
    }

    window.location.href = `result.html?interviewId=${interviewId}`;
  } catch (err) {
    errorEl.textContent = "Could not reach the server. Is the backend running?";
    submitBtn.disabled = false;
    submitBtn.textContent = "Submit Interview";
  }
});

async function loadInterview() {
  try {
    const res = await fetch(`${API_BASE}/interview/${interviewId}`);
    const interview = await res.json();

    if (!res.ok) {
      errorEl.textContent = interview.error || "Could not load interview";
      return;
    }

    document.getElementById("interview-title").textContent =
      `${interview.role} · ${interview.techStack}`;

    questions = JSON.parse(interview.questions);
    answers = new Array(questions.length).fill("");
    renderQuestion();
  } catch (err) {
    errorEl.textContent = "Could not reach the server. Is the backend running?";
  }
}

loadInterview();
