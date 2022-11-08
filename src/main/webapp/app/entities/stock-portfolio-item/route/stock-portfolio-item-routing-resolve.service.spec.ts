import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IStockPortfolioItem } from '../stock-portfolio-item.model';
import { StockPortfolioItemService } from '../service/stock-portfolio-item.service';

import { StockPortfolioItemRoutingResolveService } from './stock-portfolio-item-routing-resolve.service';

describe('StockPortfolioItem routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: StockPortfolioItemRoutingResolveService;
  let service: StockPortfolioItemService;
  let resultStockPortfolioItem: IStockPortfolioItem | null | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(StockPortfolioItemRoutingResolveService);
    service = TestBed.inject(StockPortfolioItemService);
    resultStockPortfolioItem = undefined;
  });

  describe('resolve', () => {
    it('should return IStockPortfolioItem returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultStockPortfolioItem = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultStockPortfolioItem).toEqual({ id: 123 });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultStockPortfolioItem = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultStockPortfolioItem).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<IStockPortfolioItem>({ body: null })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultStockPortfolioItem = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultStockPortfolioItem).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
