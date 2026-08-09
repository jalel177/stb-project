import React, { useState, useEffect } from 'react';
import { userService } from '../services/api';
import './Profile.css';   // ← changed from './MyProfile.css'

function MyProfile({ onClose }) {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    userService.getMyProfile()
      .then(res => setProfile(res.data))
      .catch(() => setError('Failed to load your profile'))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-box" onClick={(e) => e.stopPropagation()}>
        <button className="modal-close" onClick={onClose}>✕</button>
        <h2>👤 My Profile</h2>

        {loading && <p>Loading...</p>}
        {error && <p>{error}</p>}

        {profile && (
          <>
            <p><strong>Username:</strong> {profile.username}</p>
            <p><strong>Role:</strong> {profile.role}</p>
      

            <h3>Account</h3>
            {profile.accounts.length === 0 && <p>No accounts linked.</p>}
            <ul>
              {profile.accounts.map(acc => (
                <li key={acc.id}>
                  #{acc.accountNumber} — {acc.balance} — {acc.status}
                </li>
              ))}
            </ul>
          </>
        )}
      </div>
    </div>
  );
}

export default MyProfile;