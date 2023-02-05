import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IRealEstateItem } from '../real-estate-item.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../real-estate-item.test-samples';

import { RealEstateItemService, RestRealEstateItem } from './real-estate-item.service';

const requireRestSample: RestRealEstateItem = {
  ...sampleWithRequiredData,
  itemDate: sampleWithRequiredData.itemDate?.format(DATE_FORMAT),
};

describe('RealEstateItem Service', () => {
  let service: RealEstateItemService;
  let httpMock: HttpTestingController;
  let expectedResult: IRealEstateItem | IRealEstateItem[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(RealEstateItemService);
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

    it('should create a RealEstateItem', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const realEstateItem = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(realEstateItem).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a RealEstateItem', () => {
      const realEstateItem = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(realEstateItem).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a RealEstateItem', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of RealEstateItem', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a RealEstateItem', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addRealEstateItemToCollectionIfMissing', () => {
      it('should add a RealEstateItem to an empty array', () => {
        const realEstateItem: IRealEstateItem = sampleWithRequiredData;
        expectedResult = service.addRealEstateItemToCollectionIfMissing([], realEstateItem);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(realEstateItem);
      });

      it('should not add a RealEstateItem to an array that contains it', () => {
        const realEstateItem: IRealEstateItem = sampleWithRequiredData;
        const realEstateItemCollection: IRealEstateItem[] = [
          {
            ...realEstateItem,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addRealEstateItemToCollectionIfMissing(realEstateItemCollection, realEstateItem);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a RealEstateItem to an array that doesn't contain it", () => {
        const realEstateItem: IRealEstateItem = sampleWithRequiredData;
        const realEstateItemCollection: IRealEstateItem[] = [sampleWithPartialData];
        expectedResult = service.addRealEstateItemToCollectionIfMissing(realEstateItemCollection, realEstateItem);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(realEstateItem);
      });

      it('should add only unique RealEstateItem to an array', () => {
        const realEstateItemArray: IRealEstateItem[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const realEstateItemCollection: IRealEstateItem[] = [sampleWithRequiredData];
        expectedResult = service.addRealEstateItemToCollectionIfMissing(realEstateItemCollection, ...realEstateItemArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const realEstateItem: IRealEstateItem = sampleWithRequiredData;
        const realEstateItem2: IRealEstateItem = sampleWithPartialData;
        expectedResult = service.addRealEstateItemToCollectionIfMissing([], realEstateItem, realEstateItem2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(realEstateItem);
        expect(expectedResult).toContain(realEstateItem2);
      });

      it('should accept null and undefined values', () => {
        const realEstateItem: IRealEstateItem = sampleWithRequiredData;
        expectedResult = service.addRealEstateItemToCollectionIfMissing([], null, realEstateItem, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(realEstateItem);
      });

      it('should return initial array if no RealEstateItem is added', () => {
        const realEstateItemCollection: IRealEstateItem[] = [sampleWithRequiredData];
        expectedResult = service.addRealEstateItemToCollectionIfMissing(realEstateItemCollection, undefined, null);
        expect(expectedResult).toEqual(realEstateItemCollection);
      });
    });

    describe('compareRealEstateItem', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareRealEstateItem(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareRealEstateItem(entity1, entity2);
        const compareResult2 = service.compareRealEstateItem(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareRealEstateItem(entity1, entity2);
        const compareResult2 = service.compareRealEstateItem(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareRealEstateItem(entity1, entity2);
        const compareResult2 = service.compareRealEstateItem(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
