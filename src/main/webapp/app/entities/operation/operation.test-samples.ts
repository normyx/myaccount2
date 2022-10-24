import dayjs from 'dayjs/esm';

import { IOperation, NewOperation } from './operation.model';

export const sampleWithRequiredData: IOperation = {
  id: 32808,
  label: 'Account action-items fuchsia',
  date: dayjs('2022-08-16'),
  amount: 39387,
  isUpToDate: true,
};

export const sampleWithPartialData: IOperation = {
  id: 68211,
  label: 'withdrawal parsing',
  date: dayjs('2022-08-16'),
  amount: 74756,
  isUpToDate: true,
  deletingHardLock: true,
};

export const sampleWithFullData: IOperation = {
  id: 44284,
  label: 'Granite Rwanda Managed',
  date: dayjs('2022-08-17'),
  amount: 17591,
  note: 'Ergonomic olive',
  checkNumber: 'Cotton Plastic Money',
  isUpToDate: true,
  deletingHardLock: true,
};

export const sampleWithNewData: NewOperation = {
  label: 'Rhône-Alpes productize',
  date: dayjs('2022-08-16'),
  amount: 50483,
  isUpToDate: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
