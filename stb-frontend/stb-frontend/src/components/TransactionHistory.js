import React, { useState, useEffect } from 'react';
import { transactionService } from '../services/api';
import './TransactionHistory.css';

function TransactionHistory() {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchRef, setSearchRef] = useState('');
  const [searchAccount, setSearchAccount] = useState('');
  const [foundTransaction, setFoundTransaction] = useState(null);
  const [searchLoading, setSearchLoading] = useState(false);

  useEffect(() => {
    fetchHistory();
  }, []);

  const fetchHistory = async () => {
    try {
      const response = await transactionService.getHistory();
      const data = Array.isArray(response.data) ? response.data : [];
      setTransactions(data.filter(tx => tx.status));
      setError('');
    } catch (err) {
      console.error('Error fetching history:', err);
      setError('Failed to load history');
    } finally {
      setLoading(false);
    }
  };

  const searchByReference = async () => {
    if (!searchRef.trim()) return;
    setSearchLoading(true);
    setFoundTransaction(null);
    try {
      const response = await transactionService.getByReference(searchRef.trim());
      setFoundTransaction(response.data);
    } catch (err) {
      console.error('Error searching by reference:', err);
      setError(err.response?.data?.message || 'Transaction not found');
    } finally {
      setSearchLoading(false);
    }
  };

  const searchByAccount = async () => {
    if (!searchAccount.trim()) return;
    setSearchLoading(true);
    try {
      const response = await transactionService.getByAccount(searchAccount.trim());
      const data = Array.isArray(response.data) ? response.data : [];
      setTransactions(data.filter(tx => tx.status));
      setError('');
    } catch (err) {
      console.error('Error filtering by account:', err);
      setError(err.response?.data?.message || 'Failed to fetch account transactions');
    } finally {
      setSearchLoading(false);
    }
  };

  const resetHistory = () => {
    setSearchRef('');
    setSearchAccount('');
    setFoundTransaction(null);
    fetchHistory();
  };

  if (loading) return <div className="loading">Loading history...</div>;

  const displayTransactions = foundTransaction ? [foundTransaction] : transactions;

  return (
    <div className="history">
      <div className="header">
        <h2>📊 Transaction History</h2>
        <button onClick={fetchHistory} className="refresh">🔄 Refresh</button>
      </div>

      <div className="search-section">
        <div className="search-row">
          <input
            type="text"
            placeholder="Search by Reference (e.g., TX-1A2B3C4D)"
            value={searchRef}
            onChange={(e) => setSearchRef(e.target.value)}
          />
          <button onClick={searchByReference} disabled={searchLoading}>
            {searchLoading ? '...' : '🔍 Search'}
          </button>
          <button onClick={() => { setSearchRef(''); setFoundTransaction(null); }}>✕</button>
        </div>
        <div className="search-row">
          <input
            type="text"
            placeholder="Filter by Account Number"
            value={searchAccount}
            onChange={(e) => setSearchAccount(e.target.value)}
          />
          <button onClick={searchByAccount} disabled={searchLoading}>
            {searchLoading ? '...' : '📂 Filter'}
          </button>
          <button onClick={resetHistory}>↺ Reset</button>
        </div>
      </div>

      {error && <div className="error-box">{error}</div>}

      {displayTransactions.length === 0 ? (
        <div className="empty-state">No transactions found.</div>
      ) : (
        <div className="table-container">
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
         {displayTransactions.map((tx) => (
  <tr key={tx.id}>
    <td className="ref">{tx.reference}</td>
    <td>{tx.senderAccount}</td>
    <td>{tx.receiverAccount}</td>
    <td>{tx.amount} TND</td>
    <td>
      <span className={`status ${tx.status ? tx.status.toLowerCase() : 'pending'}`}>
        {tx.status || 'PENDING'}
      </span>
    </td>
    <td>{new Date(tx.createdAt).toLocaleString()}</td>
  </tr>
))}
          </tbody>
        </table>
        </div>
      )}
    </div>
  );
}

export default TransactionHistory;