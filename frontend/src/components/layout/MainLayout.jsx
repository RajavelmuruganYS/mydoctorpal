import { Suspense, useState } from "react";
import { Outlet } from "react-router-dom";
import Sidebar from "./Sidebar";
import Navbar from "./Navbar";
import Breadcrumbs from "./Breadcrumbs";
import LoadingPage from "../common/LoadingPage";
export default function MainLayout() {
  const [open, setOpen] = useState(false);
  return (
    <div className="flex min-h-screen bg-cream">
      <Sidebar />
      <div className="flex min-w-0 flex-1 flex-col">
        <Navbar onMenu={() => setOpen(true)} />
        {open && (
          <div className="lg:hidden">
            <Sidebar mobile onClose={() => setOpen(false)} />
            <div
              onClick={() => setOpen(false)}
              className="fixed inset-0 z-30 bg-ink/30"
            />
          </div>
        )}
        <main className="mx-auto w-full max-w-[1500px] flex-1 px-5 py-6 lg:px-10">
          <Breadcrumbs />
          <Suspense fallback={<LoadingPage />}>
            <Outlet />
          </Suspense>
        </main>
      </div>
    </div>
  );
}
