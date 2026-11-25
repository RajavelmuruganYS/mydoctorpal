import { Link } from "react-router-dom";
export default function AuthShell({
  title,
  subtitle,
  children,
  quote = "More context. Less to carry.",
}) {
  return (
    <div className="grid min-h-screen lg:grid-cols-2">
      <div className="relative hidden overflow-hidden bg-pine p-10 text-white lg:flex lg:flex-col lg:justify-between">
        <div className="absolute inset-0 opacity-45">
          <img
            src="/assets/images/mydoctorpal-auth.jpg"
            className="h-full w-full object-cover"
          />
        </div>
        <div className="absolute inset-0 bg-pine/75" />
        <div className="relative">
          <Link
            to="/"
            className="flex items-center gap-2 font-display text-xl font-extrabold"
          >
            <span className="grid h-9 w-9 place-items-center rounded-xl bg-mint text-pine">
              ✚
            </span>
            mydoctorpal
          </Link>
        </div>
        <div className="relative max-w-md">
          <p className="eyebrow text-mint">Care, made human</p>
          <h2 className="mt-4 font-display text-6xl font-extrabold leading-[.95] tracking-[-.05em]">
            {quote}
          </h2>
          <div className="mt-8 space-y-3 text-sm text-white/75">
            <p>✦ One secure home for your health story.</p>
            <p>✦ Built around the people who care for you.</p>
            <p>✦ Always clear. Always connected.</p>
          </div>
        </div>
        <div className="relative text-xs text-white/50">
          Private by design · Connected by care
        </div>
      </div>
      <div className="flex items-center justify-center p-6 sm:p-12">
        <div className="w-full max-w-md">
          <Link
            to="/"
            className="mb-10 flex items-center gap-2 font-display text-xl font-extrabold text-forest lg:hidden"
          >
            <span className="grid h-9 w-9 place-items-center rounded-xl bg-forest text-white">
              ✚
            </span>
            mydoctorpal
          </Link>
          <p className="eyebrow">Your care space</p>
          <h1 className="mt-3 font-display text-4xl font-extrabold tracking-tight text-forest">
            {title}
          </h1>
          <p className="mt-3 text-sm leading-6 muted">{subtitle}</p>
          <div className="mt-8">{children}</div>
        </div>
      </div>
    </div>
  );
}
