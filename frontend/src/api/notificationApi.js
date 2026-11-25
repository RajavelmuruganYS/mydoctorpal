import api from "./axios";
export const notificationApi = {
  create: (body) => api.post("/notifications", body),
  mine: () => api.get("/notifications/me"),
  unread: () => api.get("/notifications/me/unread"),
  count: () => api.get("/notifications/me/unread/count"),
  markRead: (id) => api.put(`/notifications/${id}/read`),
};
