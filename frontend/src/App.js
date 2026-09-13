import React, { useState } from "react";
import AuthForm from "./components/AuthForm";
import AnalyzerForm from "./components/AnalyzerForm";
import History from "./components/History";
import "./App.css";

function App() {
  const [username, setUsername] = useState(localStorage.getItem("username"));
  const [refreshKey, setRefreshKey] = useState(0);

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    setUsername(null);
  };

  if (!username) {
    return (
      <div className="app">
        <header className="app-header">
          <h1>AI Resume Analyzer</h1>
          <p>Match your resume against any job description using AI</p>
        </header>
        <main className="app-main centered">
          <AuthForm onAuthSuccess={setUsername} />
        </main>
      </div>
    );
  }

  return (
    <div className="app">
      <header className="app-header">
        <div className="header-row">
          <div>
            <h1>AI Resume Analyzer</h1>
            <p>Welcome back, {username}</p>
          </div>
          <button className="secondary" onClick={handleLogout}>Logout</button>
        </div>
      </header>

      <main className="app-main">
        <AnalyzerForm onNewAnalysis={() => setRefreshKey((k) => k + 1)} />
        <History refreshKey={refreshKey} />
      </main>
    </div>
  );
}

export default App;
