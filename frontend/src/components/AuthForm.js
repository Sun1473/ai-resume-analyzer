import React, { useState } from "react";
import { authService } from "../services/api";

// Single component handling both login and register, toggled by `mode`.
// Keeps the auth UI simple for a small project like this.
export default function AuthForm({ onAuthSuccess }) {
  const [mode, setMode] = useState("login"); // "login" | "register"
  const [form, setForm] = useState({ username: "", email: "", password: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const res =
        mode === "login"
          ? await authService.login({ username: form.username, password: form.password })
          : await authService.register(form);

      localStorage.setItem("token", res.data.token);
      localStorage.setItem("username", res.data.username);
      onAuthSuccess(res.data.username);
    } catch (err) {
      setError(
        err.response?.data?.message || err.response?.data || "Something went wrong"
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-card">
      <h2>{mode === "login" ? "Login" : "Create an account"}</h2>

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Username</label>
          <input name="username" value={form.username} onChange={handleChange} required />
        </div>

        {mode === "register" && (
          <div className="form-group">
            <label>Email</label>
            <input type="email" name="email" value={form.email} onChange={handleChange} required />
          </div>
        )}

        <div className="form-group">
          <label>Password</label>
          <input
            type="password"
            name="password"
            value={form.password}
            onChange={handleChange}
            required
          />
        </div>

        {error && <p className="error">{typeof error === "string" ? error : "Invalid input"}</p>}

        <button type="submit" disabled={loading}>
          {loading ? "Please wait..." : mode === "login" ? "Login" : "Register"}
        </button>
      </form>

      <p className="switch-mode">
        {mode === "login" ? "Don't have an account?" : "Already have an account?"}{" "}
        <button
          type="button"
          className="link-btn"
          onClick={() => setMode(mode === "login" ? "register" : "login")}
        >
          {mode === "login" ? "Register" : "Login"}
        </button>
      </p>
    </div>
  );
}
