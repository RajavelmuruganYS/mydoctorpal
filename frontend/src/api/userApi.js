import api from "./axios";
export const userApi = { me: () => api.get("/users/me") };
