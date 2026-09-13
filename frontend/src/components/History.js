import React, { useEffect, useState } from "react";
import { resumeService } from "../services/api";

// Shows the logged-in user's past analyses, most recent first.
export default function History({ refreshKey }) {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    resumeService
      .history()
      .then((res) => setHistory(res.data))
      .catch(() => setHistory([]))
      .finally(() => setLoading(false));
  }, [refreshKey]);

  if (loading) return <p>Loading history...</p>;
  if (history.length === 0) return <p className="empty-state">No past analyses yet.</p>;

  return (
    <div className="history-card">
      <h2>Past Analyses</h2>
      <ul className="history-list">
        {history.map((item) => (
          <li key={item.id}>
            <span className="history-score">{item.matchScore}%</span>
            <span className="history-date">
              {new Date(item.createdAt).toLocaleString()}
            </span>
            <p>{item.suggestions}</p>
          </li>
        ))}
      </ul>
    </div>
  );
}
