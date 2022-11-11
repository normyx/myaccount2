import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../stock-market-data.test-samples';

import { StockMarketDataFormService } from './stock-market-data-form.service';

describe('StockMarketData Form Service', () => {
  let service: StockMarketDataFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StockMarketDataFormService);
  });

  describe('Service methods', () => {
    describe('createStockMarketDataFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createStockMarketDataFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            symbol: expect.any(Object),
            dataDate: expect.any(Object),
            closeValue: expect.any(Object),
          })
        );
      });

      it('passing IStockMarketData should create a new form with FormGroup', () => {
        const formGroup = service.createStockMarketDataFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            symbol: expect.any(Object),
            dataDate: expect.any(Object),
            closeValue: expect.any(Object),
          })
        );
      });
    });

    describe('getStockMarketData', () => {
      it('should return NewStockMarketData for default StockMarketData initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createStockMarketDataFormGroup(sampleWithNewData);

        const stockMarketData = service.getStockMarketData(formGroup) as any;

        expect(stockMarketData).toMatchObject(sampleWithNewData);
      });

      it('should return NewStockMarketData for empty StockMarketData initial value', () => {
        const formGroup = service.createStockMarketDataFormGroup();

        const stockMarketData = service.getStockMarketData(formGroup) as any;

        expect(stockMarketData).toMatchObject({});
      });

      it('should return IStockMarketData', () => {
        const formGroup = service.createStockMarketDataFormGroup(sampleWithRequiredData);

        const stockMarketData = service.getStockMarketData(formGroup) as any;

        expect(stockMarketData).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IStockMarketData should not enable id FormControl', () => {
        const formGroup = service.createStockMarketDataFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewStockMarketData should disable id FormControl', () => {
        const formGroup = service.createStockMarketDataFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
