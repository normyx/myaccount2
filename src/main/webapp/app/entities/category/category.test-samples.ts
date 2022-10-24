import { CategoryType } from 'app/entities/enumerations/category-type.model';

import { ICategory, NewCategory } from './category.model';

export const sampleWithRequiredData: ICategory = {
  id: 2529,
  categoryName: 'Namibia Sausages',
  categoryType: CategoryType['OTHER'],
};

export const sampleWithPartialData: ICategory = {
  id: 554,
  categoryName: 'USB Down-sized c',
  categoryType: CategoryType['SPENDING'],
};

export const sampleWithFullData: ICategory = {
  id: 82331,
  categoryName: 'task-force Garden target',
  categoryType: CategoryType['REVENUE'],
};

export const sampleWithNewData: NewCategory = {
  categoryName: 'SDD c Computer',
  categoryType: CategoryType['REVENUE'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
