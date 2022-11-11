import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IStockMarketData } from '../stock-market-data.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../stock-market-data.test-samples';

import { StockMarketDataService, RestStockMarketData } from './stock-market-data.service';

const requireRestSample: RestStockMarketData = {
  ...sampleWithRequiredData,
  dataDate: sampleWithRequiredData.dataDate?.format(DATE_FORMAT),
};

describe('StockMarketData Service', () => {
  let service: StockMarketDataService;
  let httpMock: HttpTestingController;
  let expectedResult: IStockMarketData | IStockMarketData[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(StockMarketDataService);
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

    it('should create a StockMarketData', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const stockMarketData = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(stockMarketData).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a StockMarketData', () => {
      const stockMarketData = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(stockMarketData).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a StockMarketData', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of StockMarketData', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a StockMarketData', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addStockMarketDataToCollectionIfMissing', () => {
      it('should add a StockMarketData to an empty array', () => {
        const stockMarketData: IStockMarketData = sampleWithRequiredData;
        expectedResult = service.addStockMarketDataToCollectionIfMissing([], stockMarketData);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(stockMarketData);
      });

      it('should not add a StockMarketData to an array that contains it', () => {
        const stockMarketData: IStockMarketData = sampleWithRequiredData;
        const stockMarketDataCollection: IStockMarketData[] = [
          {
            ...stockMarketData,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addStockMarketDataToCollectionIfMissing(stockMarketDataCollection, stockMarketData);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a StockMarketData to an array that doesn't contain it", () => {
        const stockMarketData: IStockMarketData = sampleWithRequiredData;
        const stockMarketDataCollection: IStockMarketData[] = [sampleWithPartialData];
        expectedResult = service.addStockMarketDataToCollectionIfMissing(stockMarketDataCollection, stockMarketData);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(stockMarketData);
      });

      it('should add only unique StockMarketData to an array', () => {
        const stockMarketDataArray: IStockMarketData[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const stockMarketDataCollection: IStockMarketData[] = [sampleWithRequiredData];
        expectedResult = service.addStockMarketDataToCollectionIfMissing(stockMarketDataCollection, ...stockMarketDataArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const stockMarketData: IStockMarketData = sampleWithRequiredData;
        const stockMarketData2: IStockMarketData = sampleWithPartialData;
        expectedResult = service.addStockMarketDataToCollectionIfMissing([], stockMarketData, stockMarketData2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(stockMarketData);
        expect(expectedResult).toContain(stockMarketData2);
      });

      it('should accept null and undefined values', () => {
        const stockMarketData: IStockMarketData = sampleWithRequiredData;
        expectedResult = service.addStockMarketDataToCollectionIfMissing([], null, stockMarketData, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(stockMarketData);
      });

      it('should return initial array if no StockMarketData is added', () => {
        const stockMarketDataCollection: IStockMarketData[] = [sampleWithRequiredData];
        expectedResult = service.addStockMarketDataToCollectionIfMissing(stockMarketDataCollection, undefined, null);
        expect(expectedResult).toEqual(stockMarketDataCollection);
      });
    });

    describe('compareStockMarketData', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareStockMarketData(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareStockMarketData(entity1, entity2);
        const compareResult2 = service.compareStockMarketData(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareStockMarketData(entity1, entity2);
        const compareResult2 = service.compareStockMarketData(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareStockMarketData(entity1, entity2);
        const compareResult2 = service.compareStockMarketData(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
