import { CategoryType } from 'app/entities/enumerations/category-type.model';

export interface ICategory {
  id: number;
  categoryName?: string | null;
  categoryType?: CategoryType | null;
}

export type NewCategory = Omit<ICategory, 'id'> & { id: null };
