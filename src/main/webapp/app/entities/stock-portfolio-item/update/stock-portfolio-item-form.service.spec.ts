import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../stock-portfolio-item.test-samples';

import { StockPortfolioItemFormService } from './stock-portfolio-item-form.service';

describe('StockPortfolioItem Form Service', () => {
  let service: StockPortfolioItemFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StockPortfolioItemFormService);
  });

  describe('Service methods', () => {
    describe('createStockPortfolioItemFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createStockPortfolioItemFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            stockSymbol: expect.any(Object),
            stockCurrency: expect.any(Object),
            stockAcquisitionDate: expect.any(Object),
            stockSharesNumber: expect.any(Object),
            stockAcquisitionPrice: expect.any(Object),
            stockCurrentPrice: expect.any(Object),
            stockCurrentDate: expect.any(Object),
            stockAcquisitionCurrencyFactor: expect.any(Object),
            stockCurrentCurrencyFactor: expect.any(Object),
          })
        );
      });

      it('passing IStockPortfolioItem should create a new form with FormGroup', () => {
        const formGroup = service.createStockPortfolioItemFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            stockSymbol: expect.any(Object),
            stockCurrency: expect.any(Object),
            stockAcquisitionDate: expect.any(Object),
            stockSharesNumber: expect.any(Object),
            stockAcquisitionPrice: expect.any(Object),
            stockCurrentPrice: expect.any(Object),
            stockCurrentDate: expect.any(Object),
            stockAcquisitionCurrencyFactor: expect.any(Object),
            stockCurrentCurrencyFactor: expect.any(Object),
          })
        );
      });
    });

    describe('getStockPortfolioItem', () => {
      it('should return NewStockPortfolioItem for default StockPortfolioItem initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createStockPortfolioItemFormGroup(sampleWithNewData);

        const stockPortfolioItem = service.getStockPortfolioItem(formGroup) as any;

        expect(stockPortfolioItem).toMatchObject(sampleWithNewData);
      });

      it('should return NewStockPortfolioItem for empty StockPortfolioItem initial value', () => {
        const formGroup = service.createStockPortfolioItemFormGroup();

        const stockPortfolioItem = service.getStockPortfolioItem(formGroup) as any;

        expect(stockPortfolioItem).toMatchObject({});
      });

      it('should return IStockPortfolioItem', () => {
        const formGroup = service.createStockPortfolioItemFormGroup(sampleWithRequiredData);

        const stockPortfolioItem = service.getStockPortfolioItem(formGroup) as any;

        expect(stockPortfolioItem).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IStockPortfolioItem should not enable id FormControl', () => {
        const formGroup = service.createStockPortfolioItemFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewStockPortfolioItem should disable id FormControl', () => {
        const formGroup = service.createStockPortfolioItemFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
