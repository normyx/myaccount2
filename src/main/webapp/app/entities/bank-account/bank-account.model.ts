import { IApplicationUser } from 'app/entities/application-user/application-user.model';
import { BankAccountType } from 'app/entities/enumerations/bank-account-type.model';

export interface IBankAccount {
  id: number;
  accountName?: string | null;
  accountBank?: string | null;
  initialAmount?: number | null;
  archived?: boolean | null;
  shortName?: string | null;
  accountType?: BankAccountType | null;
  account?: Pick<IApplicationUser, 'id' | 'nickName'> | null;
}

export type NewBankAccount = Omit<IBankAccount, 'id'> & { id: null };
