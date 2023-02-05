import { BankAccountType } from 'app/entities/enumerations/bank-account-type.model';

import { IBankAccount, NewBankAccount } from './bank-account.model';

export const sampleWithRequiredData: IBankAccount = {
  id: 9495,
  accountName: 'Checking Account',
  accountBank: 'up syndicate',
  initialAmount: 83918,
  archived: true,
  accountType: BankAccountType['CURRENTACCOUNT'],
};

export const sampleWithPartialData: IBankAccount = {
  id: 72807,
  accountName: 'Savings Account',
  accountBank: 'c collaborative Dominican',
  initialAmount: 55528,
  archived: true,
  shortName: 'open-source',
  accountType: BankAccountType['REAL_ESTATE'],
};

export const sampleWithFullData: IBankAccount = {
  id: 43068,
  accountName: 'Savings Account',
  accountBank: 'Home technologies Aquitaine',
  initialAmount: 28005,
  archived: false,
  shortName: 'Account algorithm',
  accountType: BankAccountType['SAVINGSACCOUNT'],
};

export const sampleWithNewData: NewBankAccount = {
  accountName: 'Auto Loan Account',
  accountBank: 'maroon b',
  initialAmount: 34881,
  archived: false,
  accountType: BankAccountType['CURRENTACCOUNT'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
