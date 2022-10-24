import { ICategory } from 'app/entities/category/category.model';

export interface ISubCategory {
  id: number;
  subCategoryName?: string | null;
  category?: Pick<ICategory, 'id' | 'categoryName'> | null;
}

export type NewSubCategory = Omit<ISubCategory, 'id'> & { id: null };
