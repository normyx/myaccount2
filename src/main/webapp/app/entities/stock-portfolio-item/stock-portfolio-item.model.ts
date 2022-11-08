import dayjs from 'dayjs/esm';
import { IBankAccount } from 'app/entities/bank-account/bank-account.model';
import { Currency } from 'app/entities/enumerations/currency.model';

export interface IStockPortfolioItem {
  id: number;
  stockSymbol?: string | null;
  stockCurrency?: Currency | null;
  stockAcquisitionDate?: dayjs.Dayjs | null;
  stockSharesNumber?: number | null;
  stockAcquisitionPrice?: number | null;
  stockCurrentPrice?: number | null;
  stockCurrentDate?: dayjs.Dayjs | null;
  stockAcquisitionCurrencyFactor?: number | null;
  stockCurrentCurrencyFactor?: number | null;
  stockPriceAtAcquisitionDate?: number | null;
  bankAccount?: Pick<IBankAccount, 'id' | 'accountName'> | null;
}

export type NewStockPortfolioItem = Omit<IStockPortfolioItem, 'id'> & { id: null };
