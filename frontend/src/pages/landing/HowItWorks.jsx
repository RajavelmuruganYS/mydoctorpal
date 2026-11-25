import { CalendarDays, FileText, Pill, FlaskConical, Users, ShieldCheck } from "lucide-react";
import Reveal from "../../components/landing/Reveal";

const FEATURES = [
  { icon: CalendarDays, title: "Appointments", text: "Book and manage visits with ease." },
  { icon: FileText, title: "Medical Records", text: "Access your health information anytime." },
  { icon: Pill, title: "Prescriptions", text: "View and manage your medications." },
  { icon: FlaskConical, title: "Labs & Imaging", text: "Results, reports and images in one place." },
  { icon: Users, title: "Care Teams", text: "Stay connected with your healthcare team." },
  { icon: ShieldCheck, title: "Secure & Private", text: "Your data, your control." },
];

export default function HowItWorks() {
  return (
    <section id="how-it-works" className="px-6 py-20 lg:px-16">
      <div className="mx-auto max-w-7xl">
        <div className="flex flex-wrap items-end justify-between gap-4">
          <Reveal>
            <p className="eyebrow">All your care, connected</p>
            <h2 className="mt-3 font-display text-4xl font-extrabold tracking-tight text-forest md:text-5xl">
              Everything you need. One platform.
            </h2>
          </Reveal>
          <Reveal delay={100}>
            <a href="#care" className="inline-flex items-center gap-1 text-sm font-bold text-forest hover:text-pine">
              Explore all features →
            </a>
          </Reveal>
        </div>

        <div className="mt-14 grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-6">
          {FEATURES.map((f, i) => (
            <Reveal
              key={f.title}
              delay={i * 80}
              className="group rounded-2xl bg-white p-5 text-center shadow-card transition-transform duration-300 hover:-translate-y-1.5"
            >
              <div className="mx-auto mb-4 grid h-12 w-12 place-items-center rounded-2xl bg-mint text-forest transition-colors group-hover:bg-forest group-hover:text-white">
                <f.icon size={20} />
              </div>
              <h3 className="text-sm font-bold text-forest">{f.title}</h3>
              <p className="mt-1.5 text-xs leading-5 muted">{f.text}</p>
            </Reveal>
          ))}
        </div>
      </div>
    </section>
  );
}