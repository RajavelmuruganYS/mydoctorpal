import { CircleCheck } from "lucide-react";
import { Link } from "react-router-dom";
import Reveal from "../../components/landing/Reveal";

const CHECKLIST = ["Appointments synced", "Lab results available", "Prescriptions ready", "Care team connected"];

export default function RoleShowcase() {
  return (
    <section id="care" className="bg-sage/50 px-6 py-24 lg:px-16">
      <div className="mx-auto grid max-w-7xl items-center gap-14 lg:grid-cols-2">
        <Reveal>
          <p className="eyebrow">Made for real care</p>
          <h2 className="mt-3 font-display text-4xl font-extrabold tracking-tight text-forest md:text-5xl">
            Less complexity.
            <br />
            More care.
          </h2>
          <p className="mt-5 max-w-md leading-7 muted">
            Separate spaces for patients, clinicians, nurses, lab teams,
            pharmacy and administrators keep every workflow focused while
            your care story stays connected.
          </p>
          <Link to="/register" className="btn-primary mt-8 inline-flex">
            See how it works →
          </Link>
        </Reveal>

        <Reveal delay={150} className="relative">
          <img
            src="/assets/images/mydoctorpal-care.jpg"
            alt="Doctor with patient"
            className="h-[420px] w-full rounded-[36px] object-cover shadow-soft"
          />
          <div className="absolute -bottom-8 -left-6 w-56 rounded-2xl bg-white p-4 shadow-card sm:w-64">
            <ul className="space-y-2.5">
              {CHECKLIST.map((item) => (
                <li key={item} className="flex items-center gap-2 text-sm font-semibold text-forest/80">
                  <CircleCheck size={16} className="shrink-0 text-forest" />
                  {item}
                </li>
              ))}
            </ul>
          </div>
        </Reveal>
      </div>
    </section>
  );
}