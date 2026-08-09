import React, { useState, useEffect } from 'react';
import { transactionService } from '../services/api';
import './AllTransactions.css';

function AllTransactions() {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    transactionService.getAll()
      .then(res => {
        const data = Array.isArray(res.data) ? res.data : [];
        setTransactions(data.filter(t => t.status));
      })
      .catch(() => setError('Failed to load transactions'))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="all-transactions">Loading transactions...</div>;
  if (error) return <div className="all-transactions">{error}</div>;

  return (
    <div className="all-transactions">
      <h2>💳 All Transactions</h2>
      <table>
        <thead>
          <tr>
            <th>Reference</th>
            <th>Sender</th>
            <th>Receiver</th>
            <th>Amount</th>
            <th>Status</th>
            <th>Date</th>
          </tr>
        </thead>
        <tbody>
          {transactions.map(t => (
            <tr key={t.id}>
              <td>{t.reference}</td>
              <td>{t.senderAccount}</td>
              <td>{t.receiverAccount}</td>
              <td>{t.amount} TND</td>
              <td>{t.status}</td>
              <td>{t.createdAt ? new Date(t.createdAt).toLocaleString() : ''}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default AllTransactions;