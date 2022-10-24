import dayjs from 'dayjs/esm';
import { ISubCategory } from 'app/entities/sub-category/sub-category.model';
import { IApplicationUser } from 'app/entities/application-user/application-user.model';
import { IBankAccount } from 'app/entities/bank-account/bank-account.model';

export interface IOperation {
  id: number;
  label?: string | null;
  date?: dayjs.Dayjs | null;
  amount?: number | null;
  note?: string | null;
  checkNumber?: string | null;
  isUpToDate?: boolean | null;
  deletingHardLock?: boolean | null;
  subCategory?: Pick<ISubCategory, 'id' | 'subCategoryName'> | null;
  account?: Pick<IApplicationUser, 'id' | 'nickName'> | null;
  bankAccount?: Pick<IBankAccount, 'id'> | null;
}

export type NewOperation = Omit<IOperation, 'id'> & { id: null };
