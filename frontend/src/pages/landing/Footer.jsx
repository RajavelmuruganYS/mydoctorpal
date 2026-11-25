const LINKS = [
  { label: "Home", href: "/" },
  { label: "Features", href: "#how-it-works" },
  { label: "For patients", href: "#care" },
  { label: "For care teams", href: "#care" },
  { label: "About", href: "#" },
];

function SocialIcon({ path }) {
  return (
    <svg width="15" height="15" viewBox="0 0 24 24" fill="currentColor">
      <path d={path} />
    </svg>
  );
}

const ICONS = {
  linkedin: "M4.98 3.5C4.98 4.88 3.94 6 2.5 6S0 4.88 0 3.5 1.04 1 2.5 1 4.98 2.12 4.98 3.5zM.24 8.5h4.5V23H.24V8.5zM8.5 8.5h4.31v1.98h.06c.6-1.14 2.07-2.34 4.26-2.34 4.56 0 5.4 3 5.4 6.9V23h-4.5v-6.98c0-1.66-.03-3.8-2.32-3.8-2.32 0-2.68 1.81-2.68 3.68V23H8.5V8.5z",
  twitter: "M23.95 4.57a10 10 0 0 1-2.82.77 4.93 4.93 0 0 0 2.16-2.72 9.86 9.86 0 0 1-3.13 1.2 4.92 4.92 0 0 0-8.38 4.48A13.94 13.94 0 0 1 1.64 3.15a4.92 4.92 0 0 0 1.52 6.57A4.9 4.9 0 0 1 .96 9v.06a4.92 4.92 0 0 0 3.95 4.83 4.94 4.94 0 0 1-2.22.08 4.93 4.93 0 0 0 4.6 3.42A9.87 9.87 0 0 1 0 19.54a13.94 13.94 0 0 0 7.55 2.21c9.06 0 14.01-7.5 14.01-14.01 0-.21 0-.42-.02-.63a10 10 0 0 0 2.46-2.55z",
  youtube: "M23.5 6.19a3.02 3.02 0 0 0-2.12-2.14C19.51 3.5 12 3.5 12 3.5s-7.51 0-9.38.55A3.02 3.02 0 0 0 .5 6.19 31.6 31.6 0 0 0 0 12a31.6 31.6 0 0 0 .5 5.81 3.02 3.02 0 0 0 2.12 2.14c1.87.55 9.38.55 9.38.55s7.51 0 9.38-.55a3.02 3.02 0 0 0 2.12-2.14A31.6 31.6 0 0 0 24 12a31.6 31.6 0 0 0-.5-5.81zM9.55 15.57V8.43L15.82 12l-6.27 3.57z",
};

export default function Footer() {
  return (
    <footer className="bg-pine px-6 pb-8 pt-14 text-white lg:px-16">
      <div className="mx-auto flex max-w-7xl flex-wrap items-start justify-between gap-10">
        <div>
          <div className="flex items-center gap-2 font-display text-lg font-bold">
            <span className="grid h-8 w-8 place-items-center rounded-lg bg-white/15">✚</span>
            mydoctorpal
          </div>
          <p className="mt-2 text-xs uppercase tracking-wider text-white/50">Care, held together</p>
        </div>

        <nav className="flex flex-wrap gap-x-8 gap-y-2 text-sm font-semibold text-white/70">
          {LINKS.map((l) => (
            <a key={l.label} href={l.href} className="transition-colors hover:text-white">{l.label}</a>
          ))}
        </nav>

        <div className="flex gap-3">
          {Object.entries(ICONS).map(([name, path]) => (
            <a
              key={name}
              href="#"
              aria-label={name}
              className="grid h-9 w-9 place-items-center rounded-full bg-white/10 transition-colors hover:bg-white/20"
            >
              <SocialIcon path={path} />
            </a>
          ))}
        </div>
      </div>

      <div className="mx-auto mt-10 flex max-w-7xl flex-wrap items-center justify-between gap-3 border-t border-white/10 pt-6 text-xs text-white/50">
        <span>© 2026 mydoctorpal. All rights reserved.</span>
        <div className="flex gap-5">
          <a href="#" className="hover:text-white">Privacy</a>
          <a href="#" className="hover:text-white">Terms</a>
          <a href="#" className="hover:text-white">Contact</a>
        </div>
        <span className="italic text-mint/70">A healthier tomorrow, together.</span>
      </div>
    </footer>
  );
}