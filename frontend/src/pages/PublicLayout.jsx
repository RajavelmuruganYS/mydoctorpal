import { useEffect, useState } from "react";
import { Link, Outlet } from "react-router-dom";

export default function PublicLayout() {
  const [scrolled, setScrolled] = useState(false);

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 24);
    onScroll();
    window.addEventListener("scroll", onScroll, { passive: true });
    return () => window.removeEventListener("scroll", onScroll);
  }, []);

  return (
    <div className="min-h-screen bg-cream">
      <header
        className={`fixed left-0 right-0 top-0 z-50 flex items-center justify-between px-6 py-4 transition-all duration-300 lg:px-16 ${
          scrolled ? "bg-cream/85 shadow-card backdrop-blur-md" : "bg-transparent py-5"
        }`}
      >
        <Link to="/" className="flex items-center gap-2 font-display text-xl font-extrabold text-forest">
          <span className="grid h-9 w-9 place-items-center rounded-xl bg-forest text-white">✚</span>
          mydoctorpal
        </Link>
        <nav className="hidden items-center gap-7 text-sm font-semibold text-forest/70 md:flex">
          <Link to="/" className="transition-colors hover:text-forest">Home</Link>
          <a href="#how-it-works" className="transition-colors hover:text-forest">Features</a>
          <a href="#care" className="transition-colors hover:text-forest">For patients</a>
          <a href="#care" className="transition-colors hover:text-forest">For care teams</a>
          <Link to="/login" className="transition-colors hover:text-forest">Sign in</Link>
          <Link to="/register" className="btn-primary px-4 py-2.5">Get started</Link>
        </nav>
      </header>
      <Outlet />
    </div>
  );
}