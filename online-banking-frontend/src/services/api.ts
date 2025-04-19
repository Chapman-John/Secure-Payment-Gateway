import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

export interface Account {
  id: number;
  accountNumber: string;
  accountType: 'CHECKING' | 'SAVINGS' | 'CREDIT';
  balance: number;
}

export interface Transaction {
  id: number;
  amount: number;
  type: 'DEPOSIT' | 'WITHDRAWAL' | 'TRANSFER';
  description: string;
  timestamp: string;
  accountId: number;
}

export const accountService = {
  getAccounts: () => api.get<Account[]>('/accounts'),
  getAccount: (id: number) => api.get<Account>(`/accounts/${id}`),
  createAccount: (data: Partial<Account>) => api.post<Account>('/accounts', data),
  getTransactions: (accountId: number) => api.get<Transaction[]>(`/accounts/${accountId}/transactions`),
  transfer: (fromId: number, toId: number, amount: number) => 
    api.post(`/accounts/${fromId}/transfer`, { toAccountId: toId, amount }),
};

export default api; 