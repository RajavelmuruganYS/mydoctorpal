import { HeartHandshake, Users, Building2, ArrowRight } from "lucide-react";
import Reveal from "../../components/landing/Reveal";

const CARDS = [
  { image: "/assets/images/mydoctorpal-patients.jpg", icon: HeartHandshake, title: "For patients", text: "Book visits, view records, track results, and stay connected with your care team." },
  { image: "/assets/images/mydoctorpal-careteams.jpg", icon: Users, title: "For care teams", text: "A unified workspace for doctors, nurses, lab staff and pharmacists." },
  { image: "/assets/images/mydoctorpal-corridor.jpg", icon: Building2, title: "For healthier communities", text: "Smarter care today for a brighter tomorrow." },
];

export default function WhyGrid() {
  return (
    <section className="px-6 py-24 lg:px-16">
      <div className="mx-auto max-w-7xl">
        <div className="flex flex-wrap items-end justify-between gap-6">
          <Reveal className="max-w-xl">
            <p className="eyebrow">Why mydoctorpal</p>
            <h2 className="mt-3 font-display text-4xl font-extrabold tracking-tight text-forest md:text-5xl">
              A healthier tomorrow starts with clarity today.
            </h2>
          </Reveal>
          <Reveal delay={100} className="max-w-sm text-sm leading-6 muted">
            From preventive care to ongoing treatment, mydoctorpal helps you
            stay informed, organized and in control — so you can focus on
            what matters most.
          </Reveal>
        </div>

        <div className="mt-14 grid gap-5 md:grid-cols-3">
          {CARDS.map((c, i) => (
            <Reveal
              key={c.title}
              delay={i * 100}
              className="group overflow-hidden rounded-3xl bg-white shadow-card transition-shadow hover:shadow-soft"
            >
              <div className="relative h-48 overflow-hidden">
                <img
                  src={c.image}
                  alt={c.title}
                  className="h-full w-full object-cover transition-transform duration-500 group-hover:scale-105"
                />
                <span className="absolute left-4 top-4 grid h-10 w-10 place-items-center rounded-xl bg-white/90 text-forest shadow-card">
                  <c.icon size={18} />
                </span>
              </div>
              <div className="flex items-start justify-between gap-3 p-6">
                <div>
                  <h3 className="font-bold text-forest">{c.title}</h3>
                  <p className="mt-1.5 text-sm leading-6 muted">{c.text}</p>
                </div>
                <ArrowRight size={16} className="mt-1 shrink-0 text-forest/40 transition-transform group-hover:translate-x-1 group-hover:text-forest" />
              </div>
            </Reveal>
          ))}
        </div>
      </div>
    </section>
  );
}