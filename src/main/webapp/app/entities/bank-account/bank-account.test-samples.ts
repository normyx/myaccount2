import { BankAccountType } from 'app/entities/enumerations/bank-account-type.model';

import { IBankAccount, NewBankAccount } from './bank-account.model';

export const sampleWithRequiredData: IBankAccount = {
  id: 9495,
  accountName: 'Checking Account',
  accountBank: 'up syndicate',
  initialAmount: 83918,
  archived: true,
  accountType: BankAccountType['CURRENTACCOUNT'],
  adjustmentAmount: 63048,
};

export const sampleWithPartialData: IBankAccount = {
  id: 14310,
  accountName: 'Auto Loan Account',
  accountBank: 'port b Configurable',
  initialAmount: 75593,
  archived: false,
  shortName: 'Fully-configurable channels Home',
  accountType: BankAccountType['SAVINGSACCOUNT'],
  adjustmentAmount: 41431,
};

export const sampleWithFullData: IBankAccount = {
  id: 13497,
  accountName: 'Home Loan Account',
  accountBank: 'compressing',
  initialAmount: 35754,
  archived: true,
  shortName: 'FTP',
  accountType: BankAccountType['SAVINGSACCOUNT'],
  adjustmentAmount: 43392,
};

export const sampleWithNewData: NewBankAccount = {
  accountName: 'Auto Loan Account',
  accountBank: 'maroon b',
  initialAmount: 34881,
  archived: false,
  accountType: BankAccountType['CURRENTACCOUNT'],
  adjustmentAmount: 47869,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
