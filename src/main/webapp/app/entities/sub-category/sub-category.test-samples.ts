import { ISubCategory, NewSubCategory } from './sub-category.model';

export const sampleWithRequiredData: ISubCategory = {
  id: 68785,
  subCategoryName: 'payment',
};

export const sampleWithPartialData: ISubCategory = {
  id: 90188,
  subCategoryName: 'global matrix',
};

export const sampleWithFullData: ISubCategory = {
  id: 493,
  subCategoryName: 'THX',
};

export const sampleWithNewData: NewSubCategory = {
  subCategoryName: 'Rubber',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
