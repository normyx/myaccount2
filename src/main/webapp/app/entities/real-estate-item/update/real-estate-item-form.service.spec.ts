import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../real-estate-item.test-samples';

import { RealEstateItemFormService } from './real-estate-item-form.service';

describe('RealEstateItem Form Service', () => {
  let service: RealEstateItemFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RealEstateItemFormService);
  });

  describe('Service methods', () => {
    describe('createRealEstateItemFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createRealEstateItemFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            loanValue: expect.any(Object),
            totalValue: expect.any(Object),
            percentOwned: expect.any(Object),
            itemDate: expect.any(Object),
            bankAccount: expect.any(Object),
          })
        );
      });

      it('passing IRealEstateItem should create a new form with FormGroup', () => {
        const formGroup = service.createRealEstateItemFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            loanValue: expect.any(Object),
            totalValue: expect.any(Object),
            percentOwned: expect.any(Object),
            itemDate: expect.any(Object),
            bankAccount: expect.any(Object),
          })
        );
      });
    });

    describe('getRealEstateItem', () => {
      it('should return NewRealEstateItem for default RealEstateItem initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createRealEstateItemFormGroup(sampleWithNewData);

        const realEstateItem = service.getRealEstateItem(formGroup) as any;

        expect(realEstateItem).toMatchObject(sampleWithNewData);
      });

      it('should return NewRealEstateItem for empty RealEstateItem initial value', () => {
        const formGroup = service.createRealEstateItemFormGroup();

        const realEstateItem = service.getRealEstateItem(formGroup) as any;

        expect(realEstateItem).toMatchObject({});
      });

      it('should return IRealEstateItem', () => {
        const formGroup = service.createRealEstateItemFormGroup(sampleWithRequiredData);

        const realEstateItem = service.getRealEstateItem(formGroup) as any;

        expect(realEstateItem).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IRealEstateItem should not enable id FormControl', () => {
        const formGroup = service.createRealEstateItemFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewRealEstateItem should disable id FormControl', () => {
        const formGroup = service.createRealEstateItemFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
