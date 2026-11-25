import { useRef } from "react";
import { Quote, ArrowLeft, ArrowRight } from "lucide-react";
import Reveal from "../../components/landing/Reveal";

const QUOTES = [
  { text: "Everything about my care is finally in one place. Booking a visit takes seconds now.", name: "Asha R.", role: "Patient", avatar: "/assets/avatars/avatar-1.jpg" },
  { text: "I can see labs, vitals and prescription history before I even walk into the room.", name: "Dr. Kavya Nair", role: "Attending physician", avatar: "/assets/avatars/avatar-2.jpg" },
  { text: "Dispensing used to mean five phone calls. Now it's one screen.", name: "Farhan S.", role: "Pharmacist", avatar: "/assets/avatars/avatar-3.jpg" },
];

export default function Testimonials() {
  const trackRef = useRef(null);
  const scroll = (dir) => trackRef.current?.scrollBy({ left: dir * 340, behavior: "smooth" });

  return (
    <section className="bg-forest/[.03] px-6 py-24 lg:px-16">
      <div className="mx-auto max-w-7xl">
        <div className="flex flex-wrap items-end justify-between gap-4">
          <Reveal>
            <p className="eyebrow">Stories that inspire</p>
            <h2 className="mt-3 font-display text-4xl font-extrabold tracking-tight text-forest">
              Real people. Real progress.
            </h2>
          </Reveal>
          <Reveal delay={100} className="flex gap-2">
            <button onClick={() => scroll(-1)} aria-label="Previous" className="grid h-10 w-10 place-items-center rounded-full border border-forest/15 text-forest transition-colors hover:bg-forest hover:text-white">
              <ArrowLeft size={16} />
            </button>
            <button onClick={() => scroll(1)} aria-label="Next" className="grid h-10 w-10 place-items-center rounded-full border border-forest/15 text-forest transition-colors hover:bg-forest hover:text-white">
              <ArrowRight size={16} />
            </button>
          </Reveal>
        </div>

        <div ref={trackRef} className="mt-14 flex snap-x snap-mandatory gap-5 overflow-x-auto pb-2 [scrollbar-width:none] [&::-webkit-scrollbar]:hidden">
          {QUOTES.map((q, i) => (
            <Reveal key={q.name} delay={i * 100} className="w-full shrink-0 snap-start rounded-3xl bg-white p-7 shadow-card sm:w-[320px]">
              <Quote size={22} className="text-coral" />
              <p className="mt-4 leading-7 text-forest/85">&quot;{q.text}&quot;</p>
              <div className="mt-6 flex items-center gap-3">
                <img src={q.avatar} alt="" className="h-10 w-10 rounded-full object-cover" />
                <div>
                  <div className="text-sm font-bold text-forest">{q.name}</div>
                  <div className="text-xs muted">{q.role}</div>
                </div>
              </div>
            </Reveal>
          ))}
        </div>
      </div>
    </section>
  );
}