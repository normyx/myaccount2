import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IBudgetItemPeriod } from '../budget-item-period.model';
import { BudgetItemPeriodService } from '../service/budget-item-period.service';

import { BudgetItemPeriodRoutingResolveService } from './budget-item-period-routing-resolve.service';

describe('BudgetItemPeriod routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: BudgetItemPeriodRoutingResolveService;
  let service: BudgetItemPeriodService;
  let resultBudgetItemPeriod: IBudgetItemPeriod | null | undefined;

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
    routingResolveService = TestBed.inject(BudgetItemPeriodRoutingResolveService);
    service = TestBed.inject(BudgetItemPeriodService);
    resultBudgetItemPeriod = undefined;
  });

  describe('resolve', () => {
    it('should return IBudgetItemPeriod returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultBudgetItemPeriod = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultBudgetItemPeriod).toEqual({ id: 123 });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultBudgetItemPeriod = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultBudgetItemPeriod).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<IBudgetItemPeriod>({ body: null })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultBudgetItemPeriod = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultBudgetItemPeriod).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
