import axios from "axios";

const API_BASE_URL = "https://ai-resume-analyzer-c7ui.onrender.com/api";
const api = axios.create({
  baseURL: API_BASE_URL,
});

// Attach the JWT token (if present) to every outgoing request automatically,
// so individual components don't need to worry about auth headers.
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const authService = {
  register: (data) => api.post("/auth/register", data),
  login: (data) => api.post("/auth/login", data),
};

export const resumeService = {
  analyze: (data) => api.post("/resume/analyze", data),
  history: () => api.get("/resume/history"),
};

export default api;
