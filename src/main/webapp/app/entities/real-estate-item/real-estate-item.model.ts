import dayjs from 'dayjs/esm';
import { IBankAccount } from 'app/entities/bank-account/bank-account.model';

export interface IRealEstateItem {
  id: number;
  loanValue?: number | null;
  totalValue?: number | null;
  percentOwned?: number | null;
  itemDate?: dayjs.Dayjs | null;
  bankAccount?: Pick<IBankAccount, 'id' | 'accountName'> | null;
}

export type NewRealEstateItem = Omit<IRealEstateItem, 'id'> & { id: null };
