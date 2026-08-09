import React, { useState } from 'react';
import TransactionForm from './TransactionForm';
import TransactionHistory from './TransactionHistory';
import UserManagement from './UserManagement';
import AllTransactions from './AllTransactions';
import PendingTransactions from './PendingTransactions';
import Profile from './Profile';
import { authService } from '../services/api';
import './Dashboard.css';

function Dashboard({ user, onLogout }) {
  const [activeTab, setActiveTab] = useState('transaction');
  const [showProfile, setShowProfile] = useState(false);

  const canReview = user?.role === 'ADMIN' || user?.role === 'EMPLOYEE';
  const isAdmin = user?.role === 'ADMIN';

  const handleLogout = () => {
    authService.logout();
    onLogout();
  };

  return (
    <div className="dashboard">
      <nav className="navbar">
        <div className="brand">🏦 STB</div>
        <div className="user-info">
          <span className="clickable-username" onClick={() => setShowProfile(true)}>
            {user?.username} ({user?.role})
          </span>
          <button onClick={handleLogout}>Logout</button>
        </div>
      </nav>

      <div className="content">
        <div className="sidebar">
          <button
            className={activeTab === 'transaction' ? 'active' : ''}
            onClick={() => setActiveTab('transaction')}
          >
             New Transaction
          </button>
          <button
            className={activeTab === 'history' ? 'active' : ''}
            onClick={() => setActiveTab('history')}
          >
             History
          </button>

          {canReview && (
            <>
              <button className={activeTab === 'allTransactions' ? 'active' : ''} onClick={() => setActiveTab('allTransactions')}>
                💳 All Transactions
              </button>
              <button className={activeTab === 'pending' ? 'active' : ''} onClick={() => setActiveTab('pending')}>
                ⏳ Pending
              </button>
            </>
          )}

          {isAdmin && (
            <button className={activeTab === 'admin' ? 'active' : ''} onClick={() => setActiveTab('admin')}>
              👥 Users
            </button>
          )}
        </div>

        <div className="main">
          {activeTab === 'transaction' && <TransactionForm />}
          {activeTab === 'history' && <TransactionHistory />}
          {activeTab === 'allTransactions' && canReview && <AllTransactions />}
          {activeTab === 'pending' && canReview && <PendingTransactions />}
          {activeTab === 'admin' && isAdmin && <UserManagement />}
        </div>
      </div>

      {showProfile && <Profile onClose={() => setShowProfile(false)} />}
    </div>
  );
}

export default Dashboard;