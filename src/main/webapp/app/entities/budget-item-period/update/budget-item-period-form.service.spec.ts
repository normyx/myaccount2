import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../budget-item-period.test-samples';

import { BudgetItemPeriodFormService } from './budget-item-period-form.service';

describe('BudgetItemPeriod Form Service', () => {
  let service: BudgetItemPeriodFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BudgetItemPeriodFormService);
  });

  describe('Service methods', () => {
    describe('createBudgetItemPeriodFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createBudgetItemPeriodFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            month: expect.any(Object),
            amount: expect.any(Object),
            isSmoothed: expect.any(Object),
            isRecurrent: expect.any(Object),
            operation: expect.any(Object),
            budgetItem: expect.any(Object),
          })
        );
      });

      it('passing IBudgetItemPeriod should create a new form with FormGroup', () => {
        const formGroup = service.createBudgetItemPeriodFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            month: expect.any(Object),
            amount: expect.any(Object),
            isSmoothed: expect.any(Object),
            isRecurrent: expect.any(Object),
            operation: expect.any(Object),
            budgetItem: expect.any(Object),
          })
        );
      });
    });

    describe('getBudgetItemPeriod', () => {
      it('should return NewBudgetItemPeriod for default BudgetItemPeriod initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createBudgetItemPeriodFormGroup(sampleWithNewData);

        const budgetItemPeriod = service.getBudgetItemPeriod(formGroup) as any;

        expect(budgetItemPeriod).toMatchObject(sampleWithNewData);
      });

      it('should return NewBudgetItemPeriod for empty BudgetItemPeriod initial value', () => {
        const formGroup = service.createBudgetItemPeriodFormGroup();

        const budgetItemPeriod = service.getBudgetItemPeriod(formGroup) as any;

        expect(budgetItemPeriod).toMatchObject({});
      });

      it('should return IBudgetItemPeriod', () => {
        const formGroup = service.createBudgetItemPeriodFormGroup(sampleWithRequiredData);

        const budgetItemPeriod = service.getBudgetItemPeriod(formGroup) as any;

        expect(budgetItemPeriod).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IBudgetItemPeriod should not enable id FormControl', () => {
        const formGroup = service.createBudgetItemPeriodFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewBudgetItemPeriod should disable id FormControl', () => {
        const formGroup = service.createBudgetItemPeriodFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
