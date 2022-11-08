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
  stockPriceAtAcquisitionDate: 49639,
};

export const sampleWithPartialData: IStockPortfolioItem = {
  id: 76332,
  stockSymbol: 'du',
  stockCurrency: Currency['GBP'],
  stockAcquisitionDate: dayjs('2022-11-05'),
  stockSharesNumber: 5765,
  stockAcquisitionPrice: 41341,
  stockCurrentPrice: 99472,
  stockCurrentDate: dayjs('2022-11-06'),
  stockAcquisitionCurrencyFactor: 84930,
  stockCurrentCurrencyFactor: 45964,
  stockPriceAtAcquisitionDate: 87919,
};

export const sampleWithFullData: IStockPortfolioItem = {
  id: 9741,
  stockSymbol: 'Borders',
  stockCurrency: Currency['EUR'],
  stockAcquisitionDate: dayjs('2022-11-06'),
  stockSharesNumber: 62871,
  stockAcquisitionPrice: 61696,
  stockCurrentPrice: 16785,
  stockCurrentDate: dayjs('2022-11-06'),
  stockAcquisitionCurrencyFactor: 71647,
  stockCurrentCurrencyFactor: 67490,
  stockPriceAtAcquisitionDate: 19198,
};

export const sampleWithNewData: NewStockPortfolioItem = {
  stockSymbol: 'a Steel na',
  stockCurrency: Currency['GBP'],
  stockAcquisitionDate: dayjs('2022-11-06'),
  stockSharesNumber: 61988,
  stockAcquisitionPrice: 83435,
  stockCurrentPrice: 59583,
  stockCurrentDate: dayjs('2022-11-06'),
  stockAcquisitionCurrencyFactor: 39314,
  stockCurrentCurrencyFactor: 34612,
  stockPriceAtAcquisitionDate: 67736,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
