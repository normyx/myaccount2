import dayjs from 'dayjs/esm';

import { IStockMarketData, NewStockMarketData } from './stock-market-data.model';

export const sampleWithRequiredData: IStockMarketData = {
  id: 54150,
  symbol: 'Borders Pa',
  dataDate: dayjs('2022-11-10'),
  closeValue: 53135,
};

export const sampleWithPartialData: IStockMarketData = {
  id: 92949,
  symbol: 'Alsace wit',
  dataDate: dayjs('2022-11-10'),
  closeValue: 89765,
};

export const sampleWithFullData: IStockMarketData = {
  id: 42355,
  symbol: 'real-time',
  dataDate: dayjs('2022-11-10'),
  closeValue: 50805,
};

export const sampleWithNewData: NewStockMarketData = {
  symbol: 'SCSI a',
  dataDate: dayjs('2022-11-10'),
  closeValue: 49430,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
