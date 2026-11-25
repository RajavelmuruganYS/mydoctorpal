import { useState } from "react";
import { Bell, CheckCheck, Plus } from "lucide-react";
import PageHeader from "../../components/common/PageHeader";
import Async from "../../components/common/Async";
import EmptyState from "../../components/common/EmptyState";
import Segmented from "../../components/common/Segmented";
import Modal from "../../components/common/Modal";
import Input from "../../components/common/Input";
import Select from "../../components/common/Select";
import Textarea from "../../components/common/Textarea";
import Button from "../../components/common/Button";
import { useApi } from "../../hooks/useApi";
import { useForm } from "../../hooks/useForm";
import { useMutation } from "../../hooks/useMutation";
import { useAuth } from "../../context/AuthContext";
import { useNotifications } from "../../context/NotificationContext";
import { notificationApi } from "../../api/notificationApi";
import { adminApi } from "../../api/adminApi";
import { asList, personName } from "../../utils/payload";
import { formatDateTime } from "../../utils/formatDate";

const TYPES = ["INFO", "APPOINTMENT", "LAB_RESULT", "PRESCRIPTION", "ALERT"];

function SendModal({ open, onClose }) {
  const users = useApi(() => adminApi.users(), [], open);
  const form = useForm({ userId: "", type: "INFO", title: "", message: "" });
  const save = useMutation(notificationApi.create, { success: "Notification sent." });
  const submit = async (event) => {
    event.preventDefault();
    if (await save.run({ ...form.values, userId: Number(form.values.userId) })) {
      form.setValues({ userId: "", type: "INFO", title: "", message: "" });
      onClose();
    }
  };
  return (
    <Modal open={open} onClose={onClose} title="Send a notification">
      <form onSubmit={submit} className="space-y-4">
        <Select label="Recipient" required {...form.bind("userId", save.fieldErrors)}>
          <option value="">Select a user…</option>
          {asList(users.data).map((u) => <option key={u.userId} value={u.userId}>{personName(u)} — {u.email}</option>)}
        </Select>
        <Select label="Type" {...form.bind("type", save.fieldErrors)}>
          {TYPES.map((t) => <option key={t}>{t}</option>)}
        </Select>
        <Input label="Title" required maxLength={255} {...form.bind("title", save.fieldErrors)} />
        <Textarea label="Message" required {...form.bind("message", save.fieldErrors)} />
        <div className="flex justify-end gap-3">
          <Button variant="secondary" onClick={onClose}>Cancel</Button>
          <Button type="submit" loading={save.loading}>Send</Button>
        </div>
      </form>
    </Modal>
  );
}

export default function Notifications() {
  const { hasRole } = useAuth();
  const { refresh } = useNotifications();
  const [view, setView] = useState("ALL");
  const [send, setSend] = useState(false);
  const state = useApi(() => (view === "UNREAD" ? notificationApi.unread() : notificationApi.mine()), [view]);
  const markRead = useMutation(notificationApi.markRead, { silent: true });

  const read = async (n) => {
    if (n.read) return;
    if (await markRead.run(n.id)) {
      state.reload();
      refresh();
    }
  };

  return (
    <>
      <PageHeader
        eyebrow="Notifications"
        title="Notifications"
        actions={hasRole("ADMIN") && <Button onClick={() => setSend(true)}><Plus size={16} /> Send notification</Button>}
      />
      <div className="mb-4"><Segmented options={["ALL", "UNREAD"]} value={view} onChange={setView} /></div>
      <Async state={state}>
        {(data) => {
          const rows = asList(data);
          if (!rows.length) return <EmptyState title="You're all caught up" message="New notifications will appear here." />;
          return (
            <div className="space-y-3">
              {rows.map((n) => (
                <div key={n.id} className={`surface flex items-start gap-4 p-5 ${n.read ? "opacity-70" : ""}`}>
                  <div className="grid h-10 w-10 shrink-0 place-items-center rounded-2xl bg-sage text-forest"><Bell size={18} /></div>
                  <div className="min-w-0 flex-1">
                    <div className="flex flex-wrap items-center gap-2">
                      <h3 className="font-bold">{n.title}</h3>
                      <span className="rounded-full bg-sage px-2 py-0.5 text-[10px] font-bold uppercase text-forest">{n.type}</span>
                      {!n.read && <span className="h-2 w-2 rounded-full bg-coral" aria-label="Unread" />}
                    </div>
                    <p className="mt-1 text-sm">{n.message}</p>
                    <p className="mt-2 text-xs muted">{formatDateTime(n.createdAt)}</p>
                  </div>
                  {!n.read && (
                    <Button variant="secondary" onClick={() => read(n)}><CheckCheck size={15} /> Mark read</Button>
                  )}
                </div>
              ))}
            </div>
          );
        }}
      </Async>
      <SendModal open={send} onClose={() => setSend(false)} />
    </>
  );
}
