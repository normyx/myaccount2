import { IBudgetItem, NewBudgetItem } from './budget-item.model';

export const sampleWithRequiredData: IBudgetItem = {
  id: 64030,
  name: 'revolutionize pixel back-end',
  order: 66839,
};

export const sampleWithPartialData: IBudgetItem = {
  id: 70465,
  name: 'Champagne-Ardenne',
  order: 3329,
};

export const sampleWithFullData: IBudgetItem = {
  id: 85413,
  name: 'connect parsing Serbie',
  order: 50278,
};

export const sampleWithNewData: NewBudgetItem = {
  name: 'Lituanie multi-byte',
  order: 8388,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
