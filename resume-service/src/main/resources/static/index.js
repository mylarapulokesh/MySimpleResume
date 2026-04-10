// index.js  — v3 (cache-bust fix)

(function checkAuth() {
    if (localStorage.getItem('isLoggedIn') !== 'true') {
        window.location.href = "login.html";
    }
})();

/* ── Base URL — change this one line when deploying ── */
const BASE_URL = "http://localhost:8080";

/* ── API endpoints ── */
const SAVE_URL = `${BASE_URL}/save/information`;
const PDF_URL  = `${BASE_URL}/get/resume`;
const GET_URL  = (email) => `${BASE_URL}/get/information/${encodeURIComponent(email)}`;

/* ── Per-user localStorage keys ── */
const userEmail  = localStorage.getItem("userEmail") ?? "";

/* ── DRAFT VERSION — bump this number whenever you change JS/HTML ── */
const DRAFT_VERSION = "3";   // ← change to 4, 5, 6... on every deploy

const DRAFT_KEY    = `resumeDraft_v${DRAFT_VERSION}_${userEmail}`;
const LOADED_KEY   = `dataLoaded_v${DRAFT_VERSION}_${userEmail}`;

/* ── Wipe drafts from older versions automatically ── */
(function clearOldDrafts() {
    Object.keys(localStorage).forEach(key => {
        if (
            (key.startsWith("resumeDraft_") || key.startsWith("dataLoaded_")) &&
            !key.includes(`_v${DRAFT_VERSION}_`)
        ) {
            localStorage.removeItem(key);
        }
    });
})();

/* ── App state ── */
const state = {
    skills:      [""],
    languages:   [""],
    projects:    [{ name: "", desc: "" }],
    internships: [{ name: "", desc: "" }],
    education:   [{ key: "", instituteName: "", courseName: "", courseStartYear: "", courseEndYear: "" }],
};

/* ── Helpers ── */
function qs(id) { return document.getElementById(id); }

function escapeHtml(s) {
    return String(s ?? "")
        .replaceAll("&", "&amp;").replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;").replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

function autoResize(el) {
    el.style.height = "auto";
    el.style.height = el.scrollHeight + "px";
}

/* ────────────────────────────────────
   AUTO-SAVE STATUS INDICATOR
   ──────────────────────────────────── */
let autoSaveTimer  = null;
let autoSaveStatus = null;

function getAutoSaveEl() {
    if (!autoSaveStatus) {
        autoSaveStatus = document.createElement("div");
        autoSaveStatus.id = "autoSaveIndicator";
        autoSaveStatus.style.cssText = `
            position: fixed; bottom: 20px; right: 20px;
            background: rgba(20,20,35,0.92);
            border: 1px solid rgba(255,255,255,0.1);
            color: #8b8fa8; font-size: 0.75rem; font-weight: 600;
            padding: 7px 14px; border-radius: 999px;
            backdrop-filter: blur(10px);
            transition: opacity 0.4s ease, transform 0.3s ease;
            opacity: 0; transform: translateY(6px);
            pointer-events: none; z-index: 9999;
            font-family: 'Inter', sans-serif;
        `;
        document.body.appendChild(autoSaveStatus);
    }
    return autoSaveStatus;
}

function showAutoSave(msg, color = "#8b8fa8") {
    const el = getAutoSaveEl();
    el.textContent = msg;
    el.style.color = color;
    el.style.opacity = "1";
    el.style.transform = "translateY(0)";
}

function hideAutoSave() {
    const el = getAutoSaveEl();
    el.style.opacity = "0";
    el.style.transform = "translateY(6px)";
}

/* ────────────────────────────────────
   LOCAL DRAFT — localStorage
   ──────────────────────────────────── */

function saveDraft() {
    const draft = {
        fields: {
            name:      qs("name").value,
            email:     qs("email").value,
            phone:     qs("phone").value,
            address:   qs("address").value,
            summary:   qs("summary").value,
            linkedin:  qs("linkedin").value,
            github:    qs("github").value,
            portfolio: qs("portfolio").value,
        },
        state: {
            skills:      state.skills,
            languages:   state.languages,
            projects:    state.projects,
            internships: state.internships,
            education:   state.education,
        }
    };
    localStorage.setItem(DRAFT_KEY, JSON.stringify(draft));
    scheduleAutoSave();
}

function restoreDraft(draft) {
    const f = draft.fields ?? {};
    qs("name").value      = f.name      ?? "";
    qs("email").value     = f.email     ?? "";
    qs("phone").value     = f.phone     ?? "";
    qs("address").value   = f.address   ?? "";
    qs("summary").value   = f.summary   ?? "";
    qs("linkedin").value  = f.linkedin  ?? "";
    qs("github").value    = f.github    ?? "";
    qs("portfolio").value = f.portfolio ?? "";

    const s = draft.state ?? {};
    state.skills      = s.skills?.length      ? s.skills      : [""];
    state.languages   = s.languages?.length   ? s.languages   : [""];
    state.projects    = s.projects?.length    ? s.projects    : [{ name: "", desc: "" }];
    state.internships = s.internships?.length ? s.internships : [{ name: "", desc: "" }];
    state.education   = s.education?.length   ? s.education   : [{ key: "", instituteName: "", courseName: "", courseStartYear: "", courseEndYear: "" }];

    renderAll();
}

/* ────────────────────────────────────
   DEBOUNCED AUTO-SAVE TO API
   ──────────────────────────────────── */

function scheduleAutoSave() {
    clearTimeout(autoSaveTimer);
    showAutoSave("● Unsaved changes", "#f59e0b");
    autoSaveTimer = setTimeout(async () => {
        try {
            showAutoSave("↑ Saving...", "#8b8fa8");
            const res = await fetch(SAVE_URL, {
                method:  "POST",
                headers: { "Content-Type": "application/json" },
                body:    JSON.stringify(buildResumePayload()),
            });
            if (!res.ok) throw new Error();
            showAutoSave("✓ Saved", "#10b981");
            setTimeout(hideAutoSave, 2000);
        } catch {
            showAutoSave("✕ Auto-save failed", "#ef4444");
            setTimeout(hideAutoSave, 3000);
        }
    }, 2000);
}

/* ────────────────────────────────────
   RENDERERS
   ──────────────────────────────────── */

function renderSkills() {
    qs("skills").innerHTML = state.skills.map((v, i) => `
        <div class="inline" style="margin-bottom:12px;">
            <input style="flex:1;" placeholder="e.g., Java" value="${escapeHtml(v)}"
                oninput="state.skills[${i}] = this.value; saveDraft();" />
            <button class="remove-btn" type="button" onclick="removeSkill(${i})">Remove</button>
        </div>
    `).join("");
}

function renderLanguages() {
    qs("languages").innerHTML = state.languages.map((v, i) => `
        <div class="inline" style="margin-bottom:12px;">
            <input style="flex:1;" placeholder="e.g., Spanish" value="${escapeHtml(v)}"
                oninput="state.languages[${i}] = this.value; saveDraft();" />
            <button class="remove-btn" type="button" onclick="removeLanguage(${i})">Remove</button>
        </div>
    `).join("");
}

function renderProjects() {
    qs("projects").innerHTML = state.projects.map((p, i) => `
        <div class="block">
            <div class="grid">
                <div class="row"><label>Project Title</label>
                    <textarea
                        oninput="state.projects[${i}].name = this.value; autoResize(this); saveDraft();"
                        placeholder="e.g., E-Commerce Backend"
                        style="resize:none; overflow:hidden; min-height:42px;"
                    >${escapeHtml(p.name)}</textarea></div>
                <div class="row"><label>Description</label>
                    <textarea
                        oninput="state.projects[${i}].desc = this.value; autoResize(this); saveDraft();"
                        placeholder="Describe your project..."
                        style="resize:none; overflow:hidden; min-height:42px;"
                    >${escapeHtml(p.desc)}</textarea></div>
            </div>
            <button class="remove-btn" type="button" onclick="removeProject(${i})" style="margin-top:15px;">Remove Project</button>
        </div>
    `).join("");
    qs("projects").querySelectorAll("textarea").forEach(autoResize);
}

function renderInternships() {
    qs("internships").innerHTML = state.internships.map((p, i) => `
        <div class="block">
            <div class="grid">
                <div class="row"><label>Company/Role</label>
                    <textarea
                        oninput="state.internships[${i}].name = this.value; autoResize(this); saveDraft();"
                        placeholder="e.g., Backend Developer Intern at Google"
                        style="resize:none; overflow:hidden; min-height:42px;"
                    >${escapeHtml(p.name)}</textarea></div>
                <div class="row"><label>Description</label>
                    <textarea
                        oninput="state.internships[${i}].desc = this.value; autoResize(this); saveDraft();"
                        placeholder="Describe your role and responsibilities..."
                        style="resize:none; overflow:hidden; min-height:42px;"
                    >${escapeHtml(p.desc)}</textarea></div>
            </div>
            <button class="remove-btn" type="button" onclick="removeInternship(${i})" style="margin-top:15px;">Remove Internship</button>
        </div>
    `).join("");
    qs("internships").querySelectorAll("textarea").forEach(autoResize);
}

function renderEducation() {
    qs("education").innerHTML = state.education.map((e, i) => `
        <div class="block">
            <div class="grid">
                <div class="row"><label>Level (e.g. B.Tech)</label>
                    <input value="${escapeHtml(e.key)}" oninput="state.education[${i}].key = this.value; saveDraft();" /></div>
                <div class="row"><label>Institute</label>
                    <input value="${escapeHtml(e.instituteName)}" oninput="state.education[${i}].instituteName = this.value; saveDraft();" /></div>
                <div class="row"><label>Course</label>
                    <input value="${escapeHtml(e.courseName)}" oninput="state.education[${i}].courseName = this.value; saveDraft();" /></div>
                <div class="row">
                    <div class="inline" style="gap:5px;">
                        <div style="flex:1"><label>Start</label>
                            <input value="${escapeHtml(e.courseStartYear)}" oninput="state.education[${i}].courseStartYear = this.value; saveDraft();" /></div>
                        <div style="flex:1"><label>End</label>
                            <input value="${escapeHtml(e.courseEndYear)}" oninput="state.education[${i}].courseEndYear = this.value; saveDraft();" /></div>
                    </div>
                </div>
            </div>
            <button class="remove-btn" type="button" onclick="removeEducation(${i})" style="margin-top:15px;">Remove Education</button>
        </div>
    `).join("");
}

function renderAll() {
    renderSkills();
    renderLanguages();
    renderProjects();
    renderInternships();
    renderEducation();
}

/* ── Attach saveDraft to static fields ── */
function attachFieldListeners() {
    ["name","email","phone","address","summary","linkedin","github","portfolio"]
        .forEach(id => { const el = qs(id); if (el) el.addEventListener("input", saveDraft); });
}

/* ────────────────────────────────────
   ADD / REMOVE
   ──────────────────────────────────── */

function addSkill()           { state.skills.push("");                             renderSkills();      saveDraft(); }
function removeSkill(i)       { state.skills.splice(i,1); if (!state.skills.length) state.skills.push(""); renderSkills(); saveDraft(); }

function addLanguage()        { state.languages.push("");                          renderLanguages();   saveDraft(); }
function removeLanguage(i)    { state.languages.splice(i,1); if (!state.languages.length) state.languages.push(""); renderLanguages(); saveDraft(); }

function addProject()         { state.projects.push({name:"",desc:""});            renderProjects();    saveDraft(); }
function removeProject(i)     { state.projects.splice(i,1); if (!state.projects.length) state.projects.push({name:"",desc:""}); renderProjects(); saveDraft(); }

function addInternship()      { state.internships.push({name:"",desc:""});         renderInternships(); saveDraft(); }
function removeInternship(i)  { state.internships.splice(i,1); if (!state.internships.length) state.internships.push({name:"",desc:""}); renderInternships(); saveDraft(); }

function addEducation()       { state.education.push({key:"",instituteName:"",courseName:"",courseStartYear:"",courseEndYear:""}); renderEducation(); saveDraft(); }
function removeEducation(i)   { state.education.splice(i,1); if (!state.education.length) state.education.push({key:"",instituteName:"",courseName:"",courseStartYear:"",courseEndYear:""}); renderEducation(); saveDraft(); }

/* ────────────────────────────────────
   BUILD PAYLOAD
   ──────────────────────────────────── */

function buildResumePayload() {
    const projectMap = {};
    state.projects.forEach(p => { if (p.name) projectMap[p.name] = p.desc; });

    const internshipMap = {};
    state.internships.forEach(p => { if (p.name) internshipMap[p.name] = p.desc; });

    const eduMap = {};
    state.education.forEach(e => {
        if (e.key) eduMap[e.key] = {
            instituteName:   e.instituteName,
            courseName:      e.courseName,
            courseStartYear: e.courseStartYear,
            courseEndYear:   e.courseEndYear,
        };
    });

    return {
        userName:              userEmail,
        puppeteerTemplatePath: localStorage.getItem("selectedTemplate"),
        personalDetails: {
            name:        qs("name").value,
            address:     qs("address").value,
            phoneNumber: qs("phone").value,
            summary:     qs("summary").value,
            gmail:       qs("email").value,
        },
        socialMediaURLs: {
            linkedInUrl: qs("linkedin").value,
            gitHubUrl:   qs("github").value,
            portfolio:   qs("portfolio").value,
        },
        skillSet:         state.skills.filter(Boolean),
        languagesKnown:   state.languages.filter(Boolean),
        projects:         { projectNameAndDescription: projectMap },
        internships:      { internshipNameAndDescription: internshipMap },
        educationDetails: { educationDetails: eduMap },
    };
}

/* ────────────────────────────────────
   LOAD DATA
   ──────────────────────────────────── */

async function loadSavedData() {
    /* 1. Draft exists → restore and stop */
    try {
        const raw = localStorage.getItem(DRAFT_KEY);
        if (raw) {
            restoreDraft(JSON.parse(raw));
            return;
        }
    } catch { /* corrupted draft, fall through */ }

    /* 2. Already fetched from DB this session → stop */
    if (localStorage.getItem(LOADED_KEY) === "true") return;

    /* 3. First ever load → fetch from DB */
    try {
        if (!userEmail) return;
        const r = await fetch(GET_URL(userEmail));
        if (!r.ok) return;

        const data = await r.json();

        qs("name").value      = data.personalDetails?.name        ?? "";
        qs("address").value   = data.personalDetails?.address     ?? "";
        qs("phone").value     = data.personalDetails?.phoneNumber ?? "";
        qs("summary").value   = data.personalDetails?.summary     ?? "";
        qs("email").value     = data.personalDetails?.gmail       ?? "";
        qs("linkedin").value  = data.socialMediaURLs?.linkedInUrl  ?? "";
        qs("github").value    = data.socialMediaURLs?.gitHubUrl    ?? "";
        qs("portfolio").value = data.socialMediaURLs?.portfolioUrl ?? "";

        state.skills = data.skillSet?.length ? data.skillSet : [""];
        state.languages = data.languagesKnown?.length ? data.languagesKnown : [""];

        state.projects = Object.entries(data.projects?.projectNameAndDescription || {})
            .map(([name, desc]) => ({ name, desc }));
        if (!state.projects.length) state.projects = [{ name: "", desc: "" }];

        state.internships = Object.entries(data.internships?.internshipNameAndDescription || {})
            .map(([name, desc]) => ({ name, desc }));
        if (!state.internships.length) state.internships = [{ name: "", desc: "" }];

        state.education = Object.entries(data.educationDetails?.educationDetails || {})
            .map(([key, val]) => ({
                key,
                instituteName:   val.instituteName   ?? "",
                courseName:      val.courseName      ?? "",
                courseStartYear: val.courseStartYear ?? "",
                courseEndYear:   val.courseEndYear   ?? "",
            }));
        if (!state.education.length) state.education = [{ key: "", instituteName: "", courseName: "", courseStartYear: "", courseEndYear: "" }];

        renderAll();
        localStorage.setItem(LOADED_KEY, "true");
        saveDraft();

    } catch {
        // Silent fail — user fills manually
    }
}

/* ────────────────────────────────────
   GENERATE RESUME
   ──────────────────────────────────── */

async function generateResume() {
    const statusEl = qs("status");
    try {
        const selectedTemplatePath = localStorage.getItem("selectedTemplate");
        if (!selectedTemplatePath) {
            statusEl.textContent = "Please select a template first ❌";
            return;
        }

        clearTimeout(autoSaveTimer);
        hideAutoSave();

        statusEl.textContent = "Saving your details... ⏳";
        const payload = buildResumePayload();

        const saveRes = await fetch(SAVE_URL, {
            method: "POST", headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload),
        });
        if (!saveRes.ok) throw new Error("Failed to save details");

        statusEl.textContent = "Creating your PDF... 🚀";
        const pdfRes = await fetch(PDF_URL, {
            method: "POST", headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload),
        });
        if (!pdfRes.ok) throw new Error("PDF generation failed");

        const blob = await pdfRes.blob();
        window.open(window.URL.createObjectURL(blob), "_blank");
        statusEl.textContent = "Success! Your resume is ready. 📄";

    } catch (e) {
        statusEl.textContent = "Error: " + e.message + " ❌";
    }
}

/* ────────────────────────────────────
   LOGOUT
   ──────────────────────────────────── */

function logout() {
    localStorage.removeItem(DRAFT_KEY);
    localStorage.removeItem(LOADED_KEY);
    localStorage.clear();
    window.location.href = "index.html";
}

/* ────────────────────────────────────
   GLOBAL EXPOSURE + BOOT
   ──────────────────────────────────── */

Object.assign(window, {
    addSkill, removeSkill,
    addLanguage, removeLanguage,
    addProject, removeProject,
    addInternship, removeInternship,
    addEducation, removeEducation,
    generateResume,
    autoResize,
    saveDraft,
    logout,
});

renderAll();
attachFieldListeners();
window.addEventListener("load", loadSavedData);