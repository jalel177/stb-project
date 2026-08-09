import React, { useState, useEffect } from 'react';
import { adminService } from '../services/api';
import './UserManagement.css';

function UserManagement() {
  const [users, setUsers] = useState([]);
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [expandedUserId, setExpandedUserId] = useState(null);

  const loadData = () => {
    setLoading(true);
    Promise.all([adminService.getUsers(), adminService.getAllAccounts()])
      .then(([usersRes, accountsRes]) => {
        setUsers(Array.isArray(usersRes.data) ? usersRes.data : []);
        setAccounts(Array.isArray(accountsRes.data) ? accountsRes.data : []);
      })
      .catch(err => console.error('Failed to load data:', err))
      .finally(() => setLoading(false));
  };

  useEffect(() => { loadData(); }, []);

  const changeRole = async (id, role) => {
    const previous = users;
    setUsers(users.map(u => u.id === id ? { ...u, role } : u));
    try {
      await adminService.updateUserRole(id, role);
    } catch (err) {
      setUsers(previous);
      alert('Failed to update role: ' + (err.response?.data?.message || 'Unknown error'));
    }
  };

  const changeAccountStatus = async (accountNumber, status) => {
    const previous = accounts;
    setAccounts(accounts.map(a => a.accountNumber === accountNumber ? { ...a, status } : a));
    try {
      await adminService.updateAccountStatus(accountNumber, status);
    } catch (err) {
      setAccounts(previous);
      alert('Failed to update account status: ' + (err.response?.data?.message || 'Unknown error'));
    }
  };

  const getAccountsForUser = (username) =>
    accounts.filter(a => a.ownerName === username);

  // Only show users who have at least one linked account
  const usersWithAccounts = users.filter(u => getAccountsForUser(u.username).length > 0);

  if (loading) return <div className="history">Loading users...</div>;

  return (
    <div className="history">
      <h2>👥 User & Account Management</h2>
      <table>
        <thead>
          <tr><th></th><th>Username</th><th>Role</th><th>Change Role</th></tr>
        </thead>
        <tbody>
          {usersWithAccounts.map(u => (
            <React.Fragment key={u.id}>
              <tr>
                <td>
                  <button
                    className="expand-btn"
                    onClick={() => setExpandedUserId(expandedUserId === u.id ? null : u.id)}
                  >
                    {expandedUserId === u.id ? '▼' : '▶'}
                  </button>
                </td>
                <td>{u.username}</td>
                <td>{u.role}</td>
                <td>
                  <select value={u.role} onChange={(e) => changeRole(u.id, e.target.value)}>
                    <option value="CUSTOMER">Customer</option>
                    <option value="EMPLOYEE">Employee</option>
                    <option value="ADMIN">Admin</option>
                  </select>
                </td>
              </tr>
              {expandedUserId === u.id && (
                <tr className="accounts-row">
                  <td colSpan="4">
                    <div className="nested-accounts">
                      {getAccountsForUser(u.username).map(a => (
                        <div key={a.accountNumber} className="account-line">
                          <span>#{a.accountNumber}</span>
                          <span>Balance: {a.balance}</span>
                          <select value={a.status} onChange={(e) => changeAccountStatus(a.accountNumber, e.target.value)}>
                            <option value="ACTIVE">Active</option>
                            <option value="FROZEN">Frozen</option>
                            <option value="BLOCKED">Blocked</option>
                            <option value="CLOSED">Closed</option>
                          </select>
                        </div>
                      ))}
                    </div>
                  </td>
                </tr>
              )}
            </React.Fragment>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default UserManagement;