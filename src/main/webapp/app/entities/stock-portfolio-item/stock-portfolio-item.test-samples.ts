import dayjs from 'dayjs/esm';

import { Currency } from 'app/entities/enumerations/currency.model';
import { StockType } from 'app/entities/enumerations/stock-type.model';

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
  stockPriceAtAcquisitionDate: 49639,
  stockType: StockType['CRYPTO'],
};

export const sampleWithPartialData: IStockPortfolioItem = {
  id: 22574,
  stockSymbol: 'cX',
  stockCurrency: Currency['USD'],
  stockAcquisitionDate: dayjs('2022-11-05'),
  stockSharesNumber: 5247,
  stockAcquisitionPrice: 84930,
  stockCurrentPrice: 45964,
  stockCurrentDate: dayjs('2022-11-05'),
  stockAcquisitionCurrencyFactor: 9741,
  stockCurrentCurrencyFactor: 1564,
  stockPriceAtAcquisitionDate: 49677,
  stockType: StockType['CRYPTO'],
};

export const sampleWithFullData: IStockPortfolioItem = {
  id: 13101,
  stockSymbol: 'bluetooth',
  stockCurrency: Currency['EUR'],
  stockAcquisitionDate: dayjs('2022-11-06'),
  stockSharesNumber: 71647,
  stockAcquisitionPrice: 67490,
  stockCurrentPrice: 19198,
  stockCurrentDate: dayjs('2022-11-05'),
  stockAcquisitionCurrencyFactor: 92338,
  stockCurrentCurrencyFactor: 13773,
  stockPriceAtAcquisitionDate: 1170,
  stockType: StockType['STOCK'],
  lastStockUpdate: dayjs('2022-11-06T20:29'),
  lastCurrencyUpdate: dayjs('2022-11-06T04:55'),
};

export const sampleWithNewData: NewStockPortfolioItem = {
  stockSymbol: 'generate P',
  stockCurrency: Currency['USD'],
  stockAcquisitionDate: dayjs('2022-11-06'),
  stockSharesNumber: 8325,
  stockAcquisitionPrice: 86179,
  stockCurrentPrice: 32415,
  stockCurrentDate: dayjs('2022-11-06'),
  stockAcquisitionCurrencyFactor: 1757,
  stockCurrentCurrencyFactor: 33364,
  stockPriceAtAcquisitionDate: 63345,
  stockType: StockType['CRYPTO'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
