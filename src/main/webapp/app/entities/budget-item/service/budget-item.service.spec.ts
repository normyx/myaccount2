import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IBudgetItem } from '../budget-item.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../budget-item.test-samples';

import { BudgetItemService } from './budget-item.service';

const requireRestSample: IBudgetItem = {
  ...sampleWithRequiredData,
};

describe('BudgetItem Service', () => {
  let service: BudgetItemService;
  let httpMock: HttpTestingController;
  let expectedResult: IBudgetItem | IBudgetItem[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(BudgetItemService);
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

    it('should create a BudgetItem', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const budgetItem = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(budgetItem).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a BudgetItem', () => {
      const budgetItem = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(budgetItem).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a BudgetItem', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of BudgetItem', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a BudgetItem', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addBudgetItemToCollectionIfMissing', () => {
      it('should add a BudgetItem to an empty array', () => {
        const budgetItem: IBudgetItem = sampleWithRequiredData;
        expectedResult = service.addBudgetItemToCollectionIfMissing([], budgetItem);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(budgetItem);
      });

      it('should not add a BudgetItem to an array that contains it', () => {
        const budgetItem: IBudgetItem = sampleWithRequiredData;
        const budgetItemCollection: IBudgetItem[] = [
          {
            ...budgetItem,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addBudgetItemToCollectionIfMissing(budgetItemCollection, budgetItem);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a BudgetItem to an array that doesn't contain it", () => {
        const budgetItem: IBudgetItem = sampleWithRequiredData;
        const budgetItemCollection: IBudgetItem[] = [sampleWithPartialData];
        expectedResult = service.addBudgetItemToCollectionIfMissing(budgetItemCollection, budgetItem);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(budgetItem);
      });

      it('should add only unique BudgetItem to an array', () => {
        const budgetItemArray: IBudgetItem[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const budgetItemCollection: IBudgetItem[] = [sampleWithRequiredData];
        expectedResult = service.addBudgetItemToCollectionIfMissing(budgetItemCollection, ...budgetItemArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const budgetItem: IBudgetItem = sampleWithRequiredData;
        const budgetItem2: IBudgetItem = sampleWithPartialData;
        expectedResult = service.addBudgetItemToCollectionIfMissing([], budgetItem, budgetItem2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(budgetItem);
        expect(expectedResult).toContain(budgetItem2);
      });

      it('should accept null and undefined values', () => {
        const budgetItem: IBudgetItem = sampleWithRequiredData;
        expectedResult = service.addBudgetItemToCollectionIfMissing([], null, budgetItem, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(budgetItem);
      });

      it('should return initial array if no BudgetItem is added', () => {
        const budgetItemCollection: IBudgetItem[] = [sampleWithRequiredData];
        expectedResult = service.addBudgetItemToCollectionIfMissing(budgetItemCollection, undefined, null);
        expect(expectedResult).toEqual(budgetItemCollection);
      });
    });

    describe('compareBudgetItem', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareBudgetItem(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareBudgetItem(entity1, entity2);
        const compareResult2 = service.compareBudgetItem(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareBudgetItem(entity1, entity2);
        const compareResult2 = service.compareBudgetItem(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareBudgetItem(entity1, entity2);
        const compareResult2 = service.compareBudgetItem(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
