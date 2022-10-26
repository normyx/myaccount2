import { HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';
import { IBudgetItemPeriod } from '../../../entities/budget-item-period/budget-item-period.model';
import { BudgetItemPeriodService } from '../../../entities/budget-item-period/service/budget-item-period.service';

@Injectable({ providedIn: 'root' })
export class MyaBudgetItemPeriodRoutingResolveService implements Resolve<IBudgetItemPeriod | null> {
  constructor(protected service: BudgetItemPeriodService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBudgetItemPeriod | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((budgetItemPeriod: HttpResponse<IBudgetItemPeriod>) => {
          if (budgetItemPeriod.body) {
            return of(budgetItemPeriod.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
