import { ICategory } from 'app/entities/category/category.model';
import { IApplicationUser } from 'app/entities/application-user/application-user.model';

export interface IBudgetItem {
  id: number;
  name?: string | null;
  order?: number | null;
  category?: Pick<ICategory, 'id' | 'categoryName'> | null;
  account?: Pick<IApplicationUser, 'id' | 'nickName'> | null;
}

export type NewBudgetItem = Omit<IBudgetItem, 'id'> & { id: null };
