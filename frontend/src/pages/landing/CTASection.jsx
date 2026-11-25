import { ArrowRight, Users } from "lucide-react";
import { Link } from "react-router-dom";
import Reveal from "../../components/landing/Reveal";

export default function CTASection() {
  return (
    <section className="px-6 py-24 lg:px-16">
      <div className="mx-auto grid max-w-7xl items-center gap-14 rounded-[40px] bg-sage/40 p-10 lg:grid-cols-2 lg:p-14">
        <Reveal>
          <p className="eyebrow">Ready when you are</p>
          <h2 className="mt-3 font-display text-4xl font-extrabold tracking-tight text-forest md:text-5xl">
            Your care team, connected — starting today.
          </h2>
          <p className="mt-5 max-w-md leading-7 muted">
            Join patients and clinicians already keeping every visit, result
            and prescription in one calm, secure place.
          </p>
          <div className="mt-8 flex flex-wrap gap-3">
            <Link to="/register" className="btn-primary">
              Create your account <ArrowRight size={17} />
            </Link>
            <Link to="/login" className="btn-secondary">Sign in</Link>
          </div>
        </Reveal>

        <Reveal delay={150} className="relative">
          <div className="relative overflow-hidden rounded-[32px]">
            <img src="/assets/images/mydoctorpal-cta.jpg" alt="Calm waiting space" className="h-[340px] w-full object-cover" />
            <div className="absolute inset-0 bg-gradient-to-t from-forest/70 via-forest/10 to-transparent" />
            <p className="absolute right-6 top-6 max-w-[10rem] text-right font-display text-xl font-bold leading-tight text-white">
              Healthier people. Brighter tomorrows.
            </p>
          </div>
          <div className="absolute -bottom-6 left-6 flex items-center gap-3 rounded-2xl bg-white p-4 shadow-card">
            <span className="grid h-9 w-9 place-items-center rounded-xl bg-mint text-forest">
              <Users size={16} />
            </span>
            <div>
              <div className="text-sm font-bold text-forest">Care today</div>
              <div className="text-xs muted">A brighter tomorrow</div>
            </div>
          </div>
        </Reveal>
      </div>
    </section>
  );
}