import { NavLink } from "react-router-dom";
import {
  Activity,
  BedDouble,
  Bell,
  CalendarDays,
  LayoutDashboard,
  Pill,
  ScanLine,
  ScrollText,
  ShieldCheck,
  ShoppingBag,
  Tablets,
  TestTube2,
  UserRound,
  Users,
  UsersRound,
} from "lucide-react";

const ICONS = { Activity, BedDouble, Bell, CalendarDays, LayoutDashboard, Pill, ScanLine, ScrollText, ShieldCheck, ShoppingBag, Tablets, TestTube2, UserRound, Users, UsersRound };
import { NAV_ITEMS } from "../../utils/permissions";
import { useAuth } from "../../context/AuthContext";
export default function Sidebar({ mobile = false, onClose }) {
  const { role } = useAuth();
  return (
    <aside
      className={`${mobile ? "fixed inset-y-0 left-0 z-40 w-72" : "hidden lg:flex w-64"} shrink-0 flex-col bg-pine p-5 text-white`}
    >
      <div className="mb-8 flex items-center gap-3">
        <div className="grid h-10 w-10 place-items-center rounded-2xl bg-mint text-pine">
          <span className="text-xl">✚</span>
        </div>
        <div>
          <div className="font-display text-lg font-extrabold tracking-tight">
            mydoctorpal
          </div>
          <div className="text-[10px] uppercase tracking-[.2em] text-mint/70">
            care, held together
          </div>
        </div>
      </div>
      <div className="mb-3 px-3 text-[10px] font-bold uppercase tracking-[.18em] text-mint/60">
        Workspace
      </div>
      <nav className="space-y-1">
        {NAV_ITEMS.filter((n) => n.roles.includes(role)).map((item) => {
          const I = ICONS[item.icon];
          return (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.end}
              onClick={onClose}
              className={({ isActive }) =>
                `flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-semibold transition ${isActive ? "bg-white text-pine shadow-soft" : "text-white/70 hover:bg-white/10 hover:text-white"}`
              }
            >
              <I size={17} />
              {item.label}
            </NavLink>
          );
        })}
      </nav>
      <div className="mt-auto rounded-2xl border border-white/10 bg-white/5 p-4">
        <div className="mb-2 text-2xl">🌿</div>
        <p className="text-sm font-bold">A healthier tomorrow starts today.</p>
        <p className="mt-1 text-xs text-white/55">
          Keep your care in one calm place.
        </p>
      </div>
    </aside>
  );
}
