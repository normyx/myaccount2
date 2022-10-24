import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../budget-item.test-samples';

import { BudgetItemFormService } from './budget-item-form.service';

describe('BudgetItem Form Service', () => {
  let service: BudgetItemFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BudgetItemFormService);
  });

  describe('Service methods', () => {
    describe('createBudgetItemFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createBudgetItemFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            order: expect.any(Object),
            category: expect.any(Object),
            account: expect.any(Object),
          })
        );
      });

      it('passing IBudgetItem should create a new form with FormGroup', () => {
        const formGroup = service.createBudgetItemFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            order: expect.any(Object),
            category: expect.any(Object),
            account: expect.any(Object),
          })
        );
      });
    });

    describe('getBudgetItem', () => {
      it('should return NewBudgetItem for default BudgetItem initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createBudgetItemFormGroup(sampleWithNewData);

        const budgetItem = service.getBudgetItem(formGroup) as any;

        expect(budgetItem).toMatchObject(sampleWithNewData);
      });

      it('should return NewBudgetItem for empty BudgetItem initial value', () => {
        const formGroup = service.createBudgetItemFormGroup();

        const budgetItem = service.getBudgetItem(formGroup) as any;

        expect(budgetItem).toMatchObject({});
      });

      it('should return IBudgetItem', () => {
        const formGroup = service.createBudgetItemFormGroup(sampleWithRequiredData);

        const budgetItem = service.getBudgetItem(formGroup) as any;

        expect(budgetItem).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IBudgetItem should not enable id FormControl', () => {
        const formGroup = service.createBudgetItemFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewBudgetItem should disable id FormControl', () => {
        const formGroup = service.createBudgetItemFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
