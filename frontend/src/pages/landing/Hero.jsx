import { ArrowRight, Play, HeartHandshake } from "lucide-react";
import { Link } from "react-router-dom";
import RotatingWord from "../../components/landing/RotatingWord";
import Reveal from "../../components/landing/Reveal";

const AVATARS = [
  "/assets/avatars/avatar-1.jpg",
  "/assets/avatars/avatar-2.jpg",
  "/assets/avatars/avatar-3.jpg",
];

export default function Hero() {
  return (
    <section className="relative overflow-hidden px-6 pb-8 pt-24 lg:px-16 lg:pb-12 lg:pt-28">      <div className="pointer-events-none absolute right-0 top-40 h-64 w-64 rounded-full bg-lilac/40 blur-3xl animate-float" />

      <div className="mx-auto grid max-w-7xl items-center gap-14 lg:grid-cols-[0.9fr_1.1fr]">
        <div>
          <Reveal className="inline-flex items-center gap-2 rounded-full bg-white/80 px-4 py-2 text-xs font-bold uppercase tracking-wider text-forest shadow-card">
            A calmer care experience
          </Reveal>

          <Reveal delay={100}>
            <h1 className="mt-6 max-w-xl font-display text-6xl font-extrabold leading-[.98] tracking-[-.06em] text-forest md:text-7xl">
              Your health.
              <br />
              <span className="text-gradient">
                <RotatingWord
                  words={["Held together.", "Always in reach.", "Truly secure.", "Finally simple."]}
                />
              </span>
            </h1>
          </Reveal>

          <Reveal delay={200}>
            <p className="mt-7 max-w-lg text-lg leading-8 muted">
              mydoctorpal brings appointments, records, prescriptions, labs,
              imaging and the people you trust into one calm, connected place.
            </p>
          </Reveal>

          <Reveal delay={300} className="mt-9 flex flex-wrap items-center gap-3">
            <Link to="/register" className="btn-primary group">
              Open your care space
              <ArrowRight size={17} className="transition-transform group-hover:translate-x-1" />
            </Link>
            <a href="#how-it-works" className="btn-secondary inline-flex items-center gap-2">
              <span className="grid h-6 w-6 place-items-center rounded-full bg-forest text-white">
                <Play size={11} fill="currentColor" />
              </span>
              See how it works
            </a>
          </Reveal>

          <Reveal delay={400} className="mt-10 flex items-center gap-4">
            <div className="flex -space-x-3">
              {AVATARS.map((src) => (
                <img
                  key={src}
                  src={src}
                  alt=""
                  className="h-10 w-10 rounded-full border-2 border-cream object-cover"
                />
              ))}
            </div>
            <span className="text-sm font-semibold text-forest/70">
              Trusted by patients, doctors and families
            </span>
          </Reveal>
        </div>

        <Reveal delay={200} className="relative flex min-h-[650px] items-center justify-center sm:min-h-[700px]">
          <div className="pointer-events-none absolute left-1/2 top-1/2 h-[650px] w-[650px] -translate-x-1/2 -translate-y-1/2 rounded-full bg-[#edf5df] blur-3xl" />

          {/* One oversized pink blob behind the photo */}
          <div
            aria-hidden="true"
            className="absolute left-1/2 top-1/2 h-[640px] w-[600px] -translate-x-[39%] -translate-y-[53%] rotate-[11deg] bg-[#f3d9dc]"
            style={{ borderRadius: "46% 54% 41% 59% / 43% 37% 63% 57%" }}
          />

          {/* One oversized green blob behind the photo, offset to the lower-left */}
          <div
            aria-hidden="true"
            className="absolute left-1/2 top-1/2 h-[650px] w-[620px] -translate-x-[59%] -translate-y-[43%] rotate-[-9deg] bg-[#dcebd1]"
            style={{ borderRadius: "54% 46% 58% 42% / 40% 50% 50% 60%" }}
          />

          {/* Wide rectangular photo, organic uneven corners instead of a plain box */}
          <div
            className="absolute left-1/2 top-1/2 z-10 h-[560px] w-[560px] -translate-x-1/2 -translate-y-1/2 rotate-[1deg] overflow-hidden shadow-soft sm:h-[580px] sm:w-[600px]"
            style={{ borderRadius: "64px 96px 48px 120px" }}
          >
            <img
              src="/assets/images/mydoctorpal-hero.jpg"
              alt="Care team"
              className="h-full w-full object-cover object-center"
            />
            <div className="absolute inset-0 bg-gradient-to-t from-forest/25 via-transparent to-transparent" />
          </div>

          <div className="absolute left-[0%] top-[21%] z-20 animate-float rounded-2xl bg-white/95 p-4 shadow-card backdrop-blur">
            <div className="text-2xl font-extrabold text-forest">24/7</div>
            <div className="text-xs font-semibold text-forest/60">care, when you need it</div>
          </div>

          <div className="absolute bottom-[8%] right-[-1%] z-20 flex animate-float-slow items-center gap-3 rounded-2xl bg-white/95 p-4 shadow-card">
            <span className="grid h-9 w-9 shrink-0 place-items-center rounded-xl bg-mint text-forest">
              <HeartHandshake size={18} />
            </span>
            <div>
              <div className="text-sm font-bold text-forest">All in one place</div>
              <div className="text-xs muted">Appointments, labs, prescriptions</div>
            </div>
          </div>

          <span className="script-text absolute right-4 top-[4%] hidden text-xl text-forest/50 md:block">
            Care beyond walls
          </span>
        </Reveal>
      </div>

            <div className="mt-6 hidden justify-center md:flex">
        <a href="#stats" className="animate-bounce-arrow text-2xl text-forest/40">↓</a>
      </div>
    </section>
  );
}