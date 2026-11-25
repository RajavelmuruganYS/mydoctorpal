import { Bell, Menu, LogOut } from "lucide-react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { useNotifications } from "../../context/NotificationContext";
import { roleLabel } from "../../utils/roles";
import { personName } from "../../utils/payload";

export default function Navbar({ onMenu }) {
  const { user, role, logout } = useAuth();
  const { count } = useNotifications();
  const navigate = useNavigate();
  const name = personName(user) || user?.email?.split("@")[0] || "Care member";

  const signOut = async () => {
    await logout();
    navigate("/login", { replace: true });
  };

  return (
    <header className="flex items-center gap-4 border-b border-forest/10 bg-cream/85 px-5 py-4 backdrop-blur lg:px-8">
      <button onClick={onMenu} className="rounded-xl p-2 hover:bg-sage lg:hidden" aria-label="Open menu">
        <Menu size={20} />
      </button>
      <div className="ml-auto flex items-center gap-3">
        <Link to="/app/notifications" className="relative rounded-xl p-2.5 hover:bg-sage" aria-label="Notifications">
          <Bell size={19} />
          {count > 0 && (
            <span className="absolute -right-0.5 -top-0.5 grid h-5 min-w-5 place-items-center rounded-full bg-coral px-1 text-[10px] font-bold text-white">
              {count > 99 ? "99+" : count}
            </span>
          )}
        </Link>
        <Link to="/app/profile" className="hidden text-right sm:block">
          <div className="text-sm font-bold">{name}</div>
          <div className="text-xs muted">{roleLabel(role)}</div>
        </Link>
        <button
          onClick={signOut}
          title="Sign out"
          className="flex items-center gap-2 rounded-full bg-forest px-4 py-2.5 text-sm font-bold text-white hover:bg-pine"
        >
          <LogOut size={15} /> Sign out
        </button>
      </div>
    </header>
  );
}
