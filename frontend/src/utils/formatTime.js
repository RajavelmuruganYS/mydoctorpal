export const formatTime = (value) =>
  value
    ? new Intl.DateTimeFormat("en", {
        hour: "numeric",
        minute: "2-digit",
      }).format(new Date(`1970-01-01T${value}`))
    : "—";
