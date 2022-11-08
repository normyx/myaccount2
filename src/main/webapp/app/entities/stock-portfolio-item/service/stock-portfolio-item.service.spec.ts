import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IStockPortfolioItem } from '../stock-portfolio-item.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../stock-portfolio-item.test-samples';

import { StockPortfolioItemService, RestStockPortfolioItem } from './stock-portfolio-item.service';

const requireRestSample: RestStockPortfolioItem = {
  ...sampleWithRequiredData,
  stockAcquisitionDate: sampleWithRequiredData.stockAcquisitionDate?.format(DATE_FORMAT),
  stockCurrentDate: sampleWithRequiredData.stockCurrentDate?.format(DATE_FORMAT),
  lastStockUpdate: sampleWithRequiredData.lastStockUpdate?.toJSON(),
  lastCurrencyUpdate: sampleWithRequiredData.lastCurrencyUpdate?.toJSON(),
};

describe('StockPortfolioItem Service', () => {
  let service: StockPortfolioItemService;
  let httpMock: HttpTestingController;
  let expectedResult: IStockPortfolioItem | IStockPortfolioItem[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(StockPortfolioItemService);
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

    it('should create a StockPortfolioItem', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const stockPortfolioItem = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(stockPortfolioItem).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a StockPortfolioItem', () => {
      const stockPortfolioItem = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(stockPortfolioItem).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a StockPortfolioItem', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of StockPortfolioItem', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a StockPortfolioItem', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addStockPortfolioItemToCollectionIfMissing', () => {
      it('should add a StockPortfolioItem to an empty array', () => {
        const stockPortfolioItem: IStockPortfolioItem = sampleWithRequiredData;
        expectedResult = service.addStockPortfolioItemToCollectionIfMissing([], stockPortfolioItem);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(stockPortfolioItem);
      });

      it('should not add a StockPortfolioItem to an array that contains it', () => {
        const stockPortfolioItem: IStockPortfolioItem = sampleWithRequiredData;
        const stockPortfolioItemCollection: IStockPortfolioItem[] = [
          {
            ...stockPortfolioItem,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addStockPortfolioItemToCollectionIfMissing(stockPortfolioItemCollection, stockPortfolioItem);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a StockPortfolioItem to an array that doesn't contain it", () => {
        const stockPortfolioItem: IStockPortfolioItem = sampleWithRequiredData;
        const stockPortfolioItemCollection: IStockPortfolioItem[] = [sampleWithPartialData];
        expectedResult = service.addStockPortfolioItemToCollectionIfMissing(stockPortfolioItemCollection, stockPortfolioItem);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(stockPortfolioItem);
      });

      it('should add only unique StockPortfolioItem to an array', () => {
        const stockPortfolioItemArray: IStockPortfolioItem[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const stockPortfolioItemCollection: IStockPortfolioItem[] = [sampleWithRequiredData];
        expectedResult = service.addStockPortfolioItemToCollectionIfMissing(stockPortfolioItemCollection, ...stockPortfolioItemArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const stockPortfolioItem: IStockPortfolioItem = sampleWithRequiredData;
        const stockPortfolioItem2: IStockPortfolioItem = sampleWithPartialData;
        expectedResult = service.addStockPortfolioItemToCollectionIfMissing([], stockPortfolioItem, stockPortfolioItem2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(stockPortfolioItem);
        expect(expectedResult).toContain(stockPortfolioItem2);
      });

      it('should accept null and undefined values', () => {
        const stockPortfolioItem: IStockPortfolioItem = sampleWithRequiredData;
        expectedResult = service.addStockPortfolioItemToCollectionIfMissing([], null, stockPortfolioItem, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(stockPortfolioItem);
      });

      it('should return initial array if no StockPortfolioItem is added', () => {
        const stockPortfolioItemCollection: IStockPortfolioItem[] = [sampleWithRequiredData];
        expectedResult = service.addStockPortfolioItemToCollectionIfMissing(stockPortfolioItemCollection, undefined, null);
        expect(expectedResult).toEqual(stockPortfolioItemCollection);
      });
    });

    describe('compareStockPortfolioItem', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareStockPortfolioItem(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareStockPortfolioItem(entity1, entity2);
        const compareResult2 = service.compareStockPortfolioItem(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareStockPortfolioItem(entity1, entity2);
        const compareResult2 = service.compareStockPortfolioItem(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareStockPortfolioItem(entity1, entity2);
        const compareResult2 = service.compareStockPortfolioItem(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
