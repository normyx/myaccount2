import dayjs from 'dayjs/esm';
import { IOperation } from 'app/entities/operation/operation.model';
import { IBudgetItem } from 'app/entities/budget-item/budget-item.model';

export interface IBudgetItemPeriod {
  id: number;
  date?: dayjs.Dayjs | null;
  month?: dayjs.Dayjs | null;
  amount?: number | null;
  isSmoothed?: boolean | null;
  isRecurrent?: boolean | null;
  operation?: Pick<IOperation, 'id'> | null;
  budgetItem?: Pick<IBudgetItem, 'id' | 'name'> | null;
}

export type NewBudgetItemPeriod = Omit<IBudgetItemPeriod, 'id'> & { id: null };
