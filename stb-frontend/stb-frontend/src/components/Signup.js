import React, { useState } from 'react';
import { authService } from '../services/api';
import './Signup.css';

function Signup({ onSwitchToLogin }) {
  const [form, setForm] = useState({ username: '', password: '', role: 'CUSTOMER' });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setError('');
    setSuccess('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      await authService.signup(form.username, form.password, form.role);
      setSuccess('Account created! You can now login.');
      setForm({ username: '', password: '', role: 'CUSTOMER' });
      setTimeout(() => onSwitchToLogin(), 2000);
    } catch (err) {
      setError(err.response?.data?.message || 'Signup failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="signup-container">
      <div className="signup-card">
        <h1>🏦 Create Account</h1>
        <form onSubmit={handleSubmit}>
          <input
            name="username"
            type="text"
            placeholder="Username"
            value={form.username}
            onChange={handleChange}
            required
          />
          <input
            name="password"
            type="password"
            placeholder="Password"
            value={form.password}
            onChange={handleChange}
            required
            minLength="6"
          />
          {error && <div className="error">{error}</div>}
          {success && <div className="success">{success}</div>}
          <button type="submit" disabled={loading}>
            {loading ? 'Creating...' : 'Sign Up'}
          </button>
        </form>
        <p>
          Already have an account?{' '}
          <span className="link" onClick={onSwitchToLogin}>Login</span>
        </p>
      </div>
    </div>
  );
}

export default Signup;