import React, { useState, useEffect } from 'react';
import { transactionService } from '../services/api';
import './PendingTransactions.css';

function PendingTransactions() {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const loadPending = () => {
    setLoading(true);
    transactionService.getPending()
      .then(res => setTransactions(Array.isArray(res.data) ? res.data : []))
      .catch(() => setError('Failed to load pending transactions'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { loadPending(); }, []);

  const handleApprove = async (id) => {
    try {
      await transactionService.approve(id);
      loadPending();
    } catch (err) {
      alert('Approve failed: ' + (err.response?.data?.message || 'Unknown error'));
    }
  };

  const handleReject = async (id) => {
    const reason = window.prompt('Reason for rejection (optional):') || '';
    try {
      await transactionService.reject(id, reason);
      loadPending();
    } catch (err) {
      alert('Reject failed: ' + (err.response?.data?.message || 'Unknown error'));
    }
  };

  if (loading) return <div className="pending-transactions">Loading...</div>;
  if (error) return <div className="pending-transactions">{error}</div>;

  return (
    <div className="pending-transactions">
      <h2>⏳ Pending Transactions</h2>
      {transactions.length === 0 && <p>No pending transactions.</p>}
      {transactions.length > 0 && (
        <table>
          <thead>
            <tr>
              <th>Reference</th>
              <th>From</th>
              <th>To</th>
              <th>Amount</th>
              <th>Date</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {transactions.map(t => (
              <tr key={t.id}>
                <td>{t.reference}</td>
                <td>{t.senderAccount}</td>
                <td>{t.receiverAccount}</td>
                <td>{t.amount}</td>
                <td>{t.createdAt ? new Date(t.createdAt).toLocaleString() : ''}</td>
                <td>
                  <button onClick={() => handleApprove(t.id)}>✅ Approve</button>
                  <button onClick={() => handleReject(t.id)}>❌ Reject</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default PendingTransactions;