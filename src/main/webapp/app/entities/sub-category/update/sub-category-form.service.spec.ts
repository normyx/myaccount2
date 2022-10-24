import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../sub-category.test-samples';

import { SubCategoryFormService } from './sub-category-form.service';

describe('SubCategory Form Service', () => {
  let service: SubCategoryFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SubCategoryFormService);
  });

  describe('Service methods', () => {
    describe('createSubCategoryFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSubCategoryFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            subCategoryName: expect.any(Object),
            category: expect.any(Object),
          })
        );
      });

      it('passing ISubCategory should create a new form with FormGroup', () => {
        const formGroup = service.createSubCategoryFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            subCategoryName: expect.any(Object),
            category: expect.any(Object),
          })
        );
      });
    });

    describe('getSubCategory', () => {
      it('should return NewSubCategory for default SubCategory initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createSubCategoryFormGroup(sampleWithNewData);

        const subCategory = service.getSubCategory(formGroup) as any;

        expect(subCategory).toMatchObject(sampleWithNewData);
      });

      it('should return NewSubCategory for empty SubCategory initial value', () => {
        const formGroup = service.createSubCategoryFormGroup();

        const subCategory = service.getSubCategory(formGroup) as any;

        expect(subCategory).toMatchObject({});
      });

      it('should return ISubCategory', () => {
        const formGroup = service.createSubCategoryFormGroup(sampleWithRequiredData);

        const subCategory = service.getSubCategory(formGroup) as any;

        expect(subCategory).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISubCategory should not enable id FormControl', () => {
        const formGroup = service.createSubCategoryFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSubCategory should disable id FormControl', () => {
        const formGroup = service.createSubCategoryFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
