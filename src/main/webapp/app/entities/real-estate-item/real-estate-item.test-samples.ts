import dayjs from 'dayjs/esm';

import { IRealEstateItem, NewRealEstateItem } from './real-estate-item.model';

export const sampleWithRequiredData: IRealEstateItem = {
  id: 36342,
  loanValue: 11350,
  totalValue: 73326,
  percentOwned: 52,
  itemDate: dayjs('2023-01-03'),
};

export const sampleWithPartialData: IRealEstateItem = {
  id: 90854,
  loanValue: 66541,
  totalValue: 95952,
  percentOwned: 24,
  itemDate: dayjs('2023-01-03'),
};

export const sampleWithFullData: IRealEstateItem = {
  id: 82887,
  loanValue: 92609,
  totalValue: 40370,
  percentOwned: 93,
  itemDate: dayjs('2023-01-03'),
};

export const sampleWithNewData: NewRealEstateItem = {
  loanValue: 55146,
  totalValue: 54485,
  percentOwned: 16,
  itemDate: dayjs('2023-01-02'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
