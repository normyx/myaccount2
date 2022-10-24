import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISubCategory } from '../sub-category.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../sub-category.test-samples';

import { SubCategoryService } from './sub-category.service';

const requireRestSample: ISubCategory = {
  ...sampleWithRequiredData,
};

describe('SubCategory Service', () => {
  let service: SubCategoryService;
  let httpMock: HttpTestingController;
  let expectedResult: ISubCategory | ISubCategory[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SubCategoryService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a SubCategory', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const subCategory = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(subCategory).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SubCategory', () => {
      const subCategory = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(subCategory).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SubCategory', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SubCategory', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SubCategory', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addSubCategoryToCollectionIfMissing', () => {
      it('should add a SubCategory to an empty array', () => {
        const subCategory: ISubCategory = sampleWithRequiredData;
        expectedResult = service.addSubCategoryToCollectionIfMissing([], subCategory);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(subCategory);
      });

      it('should not add a SubCategory to an array that contains it', () => {
        const subCategory: ISubCategory = sampleWithRequiredData;
        const subCategoryCollection: ISubCategory[] = [
          {
            ...subCategory,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSubCategoryToCollectionIfMissing(subCategoryCollection, subCategory);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SubCategory to an array that doesn't contain it", () => {
        const subCategory: ISubCategory = sampleWithRequiredData;
        const subCategoryCollection: ISubCategory[] = [sampleWithPartialData];
        expectedResult = service.addSubCategoryToCollectionIfMissing(subCategoryCollection, subCategory);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(subCategory);
      });

      it('should add only unique SubCategory to an array', () => {
        const subCategoryArray: ISubCategory[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const subCategoryCollection: ISubCategory[] = [sampleWithRequiredData];
        expectedResult = service.addSubCategoryToCollectionIfMissing(subCategoryCollection, ...subCategoryArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const subCategory: ISubCategory = sampleWithRequiredData;
        const subCategory2: ISubCategory = sampleWithPartialData;
        expectedResult = service.addSubCategoryToCollectionIfMissing([], subCategory, subCategory2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(subCategory);
        expect(expectedResult).toContain(subCategory2);
      });

      it('should accept null and undefined values', () => {
        const subCategory: ISubCategory = sampleWithRequiredData;
        expectedResult = service.addSubCategoryToCollectionIfMissing([], null, subCategory, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(subCategory);
      });

      it('should return initial array if no SubCategory is added', () => {
        const subCategoryCollection: ISubCategory[] = [sampleWithRequiredData];
        expectedResult = service.addSubCategoryToCollectionIfMissing(subCategoryCollection, undefined, null);
        expect(expectedResult).toEqual(subCategoryCollection);
      });
    });

    describe('compareSubCategory', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSubCategory(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareSubCategory(entity1, entity2);
        const compareResult2 = service.compareSubCategory(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareSubCategory(entity1, entity2);
        const compareResult2 = service.compareSubCategory(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareSubCategory(entity1, entity2);
        const compareResult2 = service.compareSubCategory(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
