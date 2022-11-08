import dayjs from 'dayjs/esm';

import { Currency } from 'app/entities/enumerations/currency.model';

import { IStockPortfolioItem, NewStockPortfolioItem } from './stock-portfolio-item.model';

export const sampleWithRequiredData: IStockPortfolioItem = {
  id: 16345,
  stockSymbol: 'hack b inn',
  stockCurrency: Currency['EUR'],
  stockAcquisitionDate: dayjs('2022-11-06'),
  stockSharesNumber: 88040,
  stockAcquisitionPrice: 23686,
  stockCurrentPrice: 58399,
  stockCurrentDate: dayjs('2022-11-06'),
  stockAcquisitionCurrencyFactor: 21249,
  stockCurrentCurrencyFactor: 57422,
};

export const sampleWithPartialData: IStockPortfolioItem = {
  id: 49639,
  stockSymbol: 'implementa',
  stockCurrency: Currency['USD'],
  stockAcquisitionDate: dayjs('2022-11-05'),
  stockSharesNumber: 9741,
  stockAcquisitionPrice: 1564,
  stockCurrentPrice: 49677,
  stockCurrentDate: dayjs('2022-11-06'),
  stockAcquisitionCurrencyFactor: 13101,
  stockCurrentCurrencyFactor: 21794,
};

export const sampleWithFullData: IStockPortfolioItem = {
  id: 79141,
  stockSymbol: 'Savings fe',
  stockCurrency: Currency['GBP'],
  stockAcquisitionDate: dayjs('2022-11-05'),
  stockSharesNumber: 13773,
  stockAcquisitionPrice: 1170,
  stockCurrentPrice: 14418,
  stockCurrentDate: dayjs('2022-11-06'),
  stockAcquisitionCurrencyFactor: 67341,
  stockCurrentCurrencyFactor: 83868,
};

export const sampleWithNewData: NewStockPortfolioItem = {
  stockSymbol: 'transmitti',
  stockCurrency: Currency['EUR'],
  stockAcquisitionDate: dayjs('2022-11-06'),
  stockSharesNumber: 34612,
  stockAcquisitionPrice: 67736,
  stockCurrentPrice: 8325,
  stockCurrentDate: dayjs('2022-11-06'),
  stockAcquisitionCurrencyFactor: 32415,
  stockCurrentCurrencyFactor: 75750,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
