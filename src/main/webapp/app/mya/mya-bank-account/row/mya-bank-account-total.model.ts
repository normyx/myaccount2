import { IBankAccount } from 'app/entities/bank-account/bank-account.model';

export interface IBankAccountTotal {
  bankAccount: IBankAccount;
  total: number;
}
