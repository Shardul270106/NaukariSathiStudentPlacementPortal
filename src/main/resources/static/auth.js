/**
 * auth.js — NaukriSathi shared authentication controller
 * Include this script in every HTML page BEFORE </body>:
 *   <script src="auth.js"></script>
 *
 * ✅ SAFE: Does NOT remove or replace any existing JS on the page.
 * ✅ FIXES: Back button bypass after sign out.
 */

(function () {

  /* ── Keys stored in localStorage ── */
  var AUTH_KEYS = [
    "userId",
    "userToken",
    "userName",
    "userEmail",
    "userRole",
    "sessionData",
    "authData",
  ];

  /* ── Pages that do NOT need a login check ── */
  var PUBLIC_PAGES = ["login.html", "register.html", "signup.html", "index.html"];

  function isPublicPage() {
    var path = window.location.pathname.split("/").pop().toLowerCase();
    return PUBLIC_PAGES.some(function (p) { return path === p || path === ""; });
  }

  function isLoggedIn() {
    return !!localStorage.getItem("userId");
  }

  /* ─────────────────────────────────────────
     signOut()
     Clears all credentials and redirects to login.
     Uses location.replace() so the back button
     cannot return to the protected page.
  ───────────────────────────────────────── */
  function signOut() {
    AUTH_KEYS.forEach(function (key) {
      localStorage.removeItem(key);
    });
    sessionStorage.clear();

    window.location.replace("login.html");
  }

  /* ─────────────────────────────────────────
     guardPage()
     Protects any page that is not public.
     Also re-checks on back-button navigation
     via the pageshow event (bfcache fix).
  ───────────────────────────────────────── */
  function guardPage() {
    if (isPublicPage()) return;

    if (!isLoggedIn()) {
      window.location.replace("login.html");
      return;
    }

    /*
      pageshow fires when the browser restores a page from
      the back/forward cache (bfcache). If credentials are
      gone at that point, kick the user out immediately.
    */
    window.addEventListener("pageshow", function () {
      if (!isLoggedIn()) {
        window.location.replace("login.html");
      }
    });
  }

  /* ─────────────────────────────────────────
     wireButtons()
     Automatically attaches signOut to every
     button whose label contains "Sign Out".
  ───────────────────────────────────────── */
  function wireButtons() {
    document.querySelectorAll("button").forEach(function (btn) {
      if (/sign\s*out/i.test(btn.textContent)) {
        btn.onclick = null; /* remove any inline onclick */
        btn.addEventListener("click", signOut);
      }
    });

    /* Also support <anything data-action="signout"> */
    document.querySelectorAll("[data-action='signout']").forEach(function (el) {
      el.addEventListener("click", signOut);
    });
  }

  /* ─────────────────────────────────────────
     init() — runs automatically on every page
  ───────────────────────────────────────── */
  function init() {
    guardPage();   /* kick out if not logged in  */
    wireButtons(); /* hook up Sign Out buttons   */
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }

  /* ── Expose globally for optional manual use ── */
  window.NaukriAuth = {
    signOut: signOut,
    guardPage: guardPage,
    isLoggedIn: isLoggedIn,
  };

})();
