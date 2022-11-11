import dayjs from 'dayjs/esm';

export interface IStockMarketData {
  id: number;
  symbol?: string | null;
  dataDate?: dayjs.Dayjs | null;
  closeValue?: number | null;
}

export type NewStockMarketData = Omit<IStockMarketData, 'id'> & { id: null };
