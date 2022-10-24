import dayjs from 'dayjs/esm';

import { IBudgetItemPeriod, NewBudgetItemPeriod } from './budget-item-period.model';

export const sampleWithRequiredData: IBudgetItemPeriod = {
  id: 79767,
  month: dayjs('2022-08-17'),
  amount: 61532,
};

export const sampleWithPartialData: IBudgetItemPeriod = {
  id: 98955,
  date: dayjs('2022-08-16'),
  month: dayjs('2022-08-16'),
  amount: 24966,
  isSmoothed: false,
  isRecurrent: true,
};

export const sampleWithFullData: IBudgetItemPeriod = {
  id: 37702,
  date: dayjs('2022-08-17'),
  month: dayjs('2022-08-16'),
  amount: 16963,
  isSmoothed: false,
  isRecurrent: false,
};

export const sampleWithNewData: NewBudgetItemPeriod = {
  month: dayjs('2022-08-17'),
  amount: 28086,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
