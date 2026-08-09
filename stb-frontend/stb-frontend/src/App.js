import React, { useState, useEffect } from 'react';
import Login from './components/Login';
import Signup from './components/Signup';
import Dashboard from './components/Dashboard';
import './App.css';

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [user, setUser] = useState(null);
  const [showSignup, setShowSignup] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('token');
    const userData = localStorage.getItem('user');
    if (token && userData) {
      setUser(JSON.parse(userData));
      setIsLoggedIn(true);
    }
  }, []);

  const handleLogin = (userData) => {
    setUser(userData);
    setIsLoggedIn(true);
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
    setIsLoggedIn(false);
  };

  return (
    <div className="App">
      {!isLoggedIn ? (
        showSignup ? (
          <Signup onSwitchToLogin={() => setShowSignup(false)} />
        ) : (
          <Login onLogin={handleLogin} onSwitchToSignup={() => setShowSignup(true)} />
        )
      ) : (
        <Dashboard user={user} onLogout={handleLogout} />
      )}
    </div>
  );
}

export default App;