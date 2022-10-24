import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IBudgetItemPeriod } from '../budget-item-period.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../budget-item-period.test-samples';

import { BudgetItemPeriodService, RestBudgetItemPeriod } from './budget-item-period.service';

const requireRestSample: RestBudgetItemPeriod = {
  ...sampleWithRequiredData,
  date: sampleWithRequiredData.date?.format(DATE_FORMAT),
  month: sampleWithRequiredData.month?.format(DATE_FORMAT),
};

describe('BudgetItemPeriod Service', () => {
  let service: BudgetItemPeriodService;
  let httpMock: HttpTestingController;
  let expectedResult: IBudgetItemPeriod | IBudgetItemPeriod[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(BudgetItemPeriodService);
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

    it('should create a BudgetItemPeriod', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const budgetItemPeriod = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(budgetItemPeriod).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a BudgetItemPeriod', () => {
      const budgetItemPeriod = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(budgetItemPeriod).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a BudgetItemPeriod', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of BudgetItemPeriod', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a BudgetItemPeriod', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addBudgetItemPeriodToCollectionIfMissing', () => {
      it('should add a BudgetItemPeriod to an empty array', () => {
        const budgetItemPeriod: IBudgetItemPeriod = sampleWithRequiredData;
        expectedResult = service.addBudgetItemPeriodToCollectionIfMissing([], budgetItemPeriod);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(budgetItemPeriod);
      });

      it('should not add a BudgetItemPeriod to an array that contains it', () => {
        const budgetItemPeriod: IBudgetItemPeriod = sampleWithRequiredData;
        const budgetItemPeriodCollection: IBudgetItemPeriod[] = [
          {
            ...budgetItemPeriod,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addBudgetItemPeriodToCollectionIfMissing(budgetItemPeriodCollection, budgetItemPeriod);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a BudgetItemPeriod to an array that doesn't contain it", () => {
        const budgetItemPeriod: IBudgetItemPeriod = sampleWithRequiredData;
        const budgetItemPeriodCollection: IBudgetItemPeriod[] = [sampleWithPartialData];
        expectedResult = service.addBudgetItemPeriodToCollectionIfMissing(budgetItemPeriodCollection, budgetItemPeriod);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(budgetItemPeriod);
      });

      it('should add only unique BudgetItemPeriod to an array', () => {
        const budgetItemPeriodArray: IBudgetItemPeriod[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const budgetItemPeriodCollection: IBudgetItemPeriod[] = [sampleWithRequiredData];
        expectedResult = service.addBudgetItemPeriodToCollectionIfMissing(budgetItemPeriodCollection, ...budgetItemPeriodArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const budgetItemPeriod: IBudgetItemPeriod = sampleWithRequiredData;
        const budgetItemPeriod2: IBudgetItemPeriod = sampleWithPartialData;
        expectedResult = service.addBudgetItemPeriodToCollectionIfMissing([], budgetItemPeriod, budgetItemPeriod2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(budgetItemPeriod);
        expect(expectedResult).toContain(budgetItemPeriod2);
      });

      it('should accept null and undefined values', () => {
        const budgetItemPeriod: IBudgetItemPeriod = sampleWithRequiredData;
        expectedResult = service.addBudgetItemPeriodToCollectionIfMissing([], null, budgetItemPeriod, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(budgetItemPeriod);
      });

      it('should return initial array if no BudgetItemPeriod is added', () => {
        const budgetItemPeriodCollection: IBudgetItemPeriod[] = [sampleWithRequiredData];
        expectedResult = service.addBudgetItemPeriodToCollectionIfMissing(budgetItemPeriodCollection, undefined, null);
        expect(expectedResult).toEqual(budgetItemPeriodCollection);
      });
    });

    describe('compareBudgetItemPeriod', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareBudgetItemPeriod(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareBudgetItemPeriod(entity1, entity2);
        const compareResult2 = service.compareBudgetItemPeriod(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareBudgetItemPeriod(entity1, entity2);
        const compareResult2 = service.compareBudgetItemPeriod(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareBudgetItemPeriod(entity1, entity2);
        const compareResult2 = service.compareBudgetItemPeriod(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
