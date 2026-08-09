import React, { useState } from 'react';
import { transactionService } from '../services/api';
import './TransactionForm.css';

function TransactionForm() {
  const [form, setForm] = useState({ senderAccount: '', receiverAccount: '', amount: '' });
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setError('');
    setResult(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setResult(null);

    const payload = {
      senderAccount: form.senderAccount,
      receiverAccount: form.receiverAccount,
      amount: parseFloat(form.amount),
    };

    try {
      const response = await transactionService.process(payload);
      setResult(response.data);
      if (response.data.status === 'APPROVED' || response.data.status === 'PENDING') {
        setForm({ senderAccount: '', receiverAccount: '', amount: '' });
      }
    } catch (err) {
      console.error('Transaction error:', err);
      setError(err.response?.data?.message || 'Transaction failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="transaction-form">
      <h2>📝 New Transaction</h2>
      <p style={{ fontSize: '14px', color: '#7f8c8d' }}>
        Send a transfer — it will be reviewed and approved by an admin.
      </p>

      <form onSubmit={handleSubmit}>
        <input
          name="senderAccount"
          placeholder="Sender Account"
          value={form.senderAccount}
          onChange={handleChange}
          required
        />
        <input
          name="receiverAccount"
          placeholder="Receiver Account"
          value={form.receiverAccount}
          onChange={handleChange}
          required
        />
        <input
          name="amount"
          type="number"
          placeholder="Amount (TND)"
          value={form.amount}
          onChange={handleChange}
          required
          min="0.01"
          step="0.01"
        />
        <button type="submit" disabled={loading}>
          {loading ? 'Processing...' : 'Send Transfer'}
        </button>
      </form>

      {error && <div className="error-box">{error}</div>}

      {result && (
        <div className={`result-box ${result.status === 'APPROVED' || result.status === 'PENDING' ? 'success' : 'error'}`}>
          <h3>✅ {result.status}</h3>
          {result.transactionReference && <p>Reference: {result.transactionReference}</p>}
          {result.reason && <p>Reason: {result.reason}</p>}
        </div>
      )}
    </div>
  );
}

export default TransactionForm;