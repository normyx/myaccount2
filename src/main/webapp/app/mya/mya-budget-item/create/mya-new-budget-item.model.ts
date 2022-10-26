import { Dayjs } from 'dayjs';

export interface IMyaNewBudgetItem {
  name?: string | null;
  categoryId?: number | null;
  amount?: number | null;
  month?: Dayjs | null;
  dayInMonth?: number | null;
  isSmoothed?: boolean | null;
}
