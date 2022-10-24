import { IBankAccount, NewBankAccount } from './bank-account.model';

export const sampleWithRequiredData: IBankAccount = {
  id: 9495,
  accountName: 'Checking Account',
  accountBank: 'up syndicate',
  initialAmount: 83918,
  archived: true,
};

export const sampleWithPartialData: IBankAccount = {
  id: 63048,
  accountName: 'Credit Card Account',
  accountBank: 'programming',
  initialAmount: 28895,
  archived: false,
};

export const sampleWithFullData: IBankAccount = {
  id: 95324,
  accountName: 'Home Loan Account',
  accountBank: 'Configurable bypass Fully-configurable',
  initialAmount: 43068,
  archived: false,
  shortName: 'Home technologies Aquitaine',
};

export const sampleWithNewData: NewBankAccount = {
  accountName: 'Money Market Account',
  accountBank: 'extensible FTP',
  initialAmount: 57064,
  archived: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
