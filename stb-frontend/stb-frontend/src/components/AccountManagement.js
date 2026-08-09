import React, { useState, useEffect } from 'react';
import { adminService } from '../services/api';
import './AccountManagement.css';

function AccountManagement() {
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    adminService.getAllAccounts()
      .then(res => setAccounts(Array.isArray(res.data) ? res.data : []))
      .catch(err => console.error('Failed to load accounts:', err))
      .finally(() => setLoading(false));
  }, []);

  const changeStatus = async (accountNumber, status) => {
    const previous = accounts;
    setAccounts(accounts.map(a => a.accountNumber === accountNumber ? { ...a, status } : a));
    try {
      await adminService.updateAccountStatus(accountNumber, status);
    } catch (err) {
      setAccounts(previous);
      alert('Failed to update status: ' + (err.response?.data?.message || 'Unknown error'));
    }
  };

  if (loading) return <div className="account-management">Loading accounts...</div>;

  return (
    <div className="account-management">
      <h2>🏦 Account Management</h2>
      <table>
        <thead><tr><th>Account #</th><th>Owner</th><th>Balance</th><th>Status</th><th>Change To</th></tr></thead>
        <tbody>
          {accounts.map(a => (
            <tr key={a.accountNumber}>
              <td>{a.accountNumber}</td>
              <td>{a.ownerName}</td>
              <td>{a.balance}</td>
              <td>{a.status}</td>
              <td>
                <select value={a.status} onChange={(e) => changeStatus(a.accountNumber, e.target.value)}>
                  <option value="ACTIVE">Active</option>
                  <option value="FROZEN">Frozen</option>
                  <option value="BLOCKED">Blocked</option>
                  <option value="CLOSED">Closed</option>
                </select>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default AccountManagement;