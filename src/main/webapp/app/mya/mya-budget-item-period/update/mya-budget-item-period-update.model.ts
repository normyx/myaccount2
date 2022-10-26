import dayjs from 'dayjs/esm';
import { IBudgetItem } from '../../../entities/budget-item/budget-item.model';

export interface IMyaBudgetItemPeriodUpdate {
  id: number;
  month?: dayjs.Dayjs | null;
  dayInMonth?: number | null;
  amount?: number | null;
  isSmoothed?: boolean | null;
  modifyNexts?: boolean | null;
  operationId?: number | null;
  budgetItem?: Pick<IBudgetItem, 'id' | 'name'> | null;
}

export type NewMyaBudgetItemPeriodUpdate = Omit<IMyaBudgetItemPeriodUpdate, 'id'> & { id: null };
