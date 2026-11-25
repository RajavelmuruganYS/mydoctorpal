import Counter from "../../components/landing/Counter";
import Reveal from "../../components/landing/Reveal";

const STATS = [
  { to: 100000, suffix: "+", label: "Patient records managed" },
  { to: 500, suffix: "+", label: "Concurrent users supported" },
  { to: 7, suffix: "", label: "Dedicated care-team roles" },
  { to: 24, suffix: "/7", label: "Always-on access" },
];

export default function StatsBar() {
  return (
    <section id="stats" className="bg-forest px-6 py-14 lg:px-16">
      <div className="mx-auto grid max-w-7xl grid-cols-2 gap-8 md:grid-cols-4">
        {STATS.map((s, i) => (
          <Reveal key={s.label} delay={i * 100} className="text-center">
            <div className="font-display text-4xl font-extrabold text-white md:text-5xl">
              <Counter to={s.to} suffix={s.suffix} />
            </div>
            <div className="mt-2 text-sm font-semibold text-mint/80">{s.label}</div>
          </Reveal>
        ))}
      </div>
    </section>
  );
}