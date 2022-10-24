import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IBudgetItem } from '../budget-item.model';
import { BudgetItemService } from '../service/budget-item.service';

@Injectable({ providedIn: 'root' })
export class BudgetItemRoutingResolveService implements Resolve<IBudgetItem | null> {
  constructor(protected service: BudgetItemService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBudgetItem | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((budgetItem: HttpResponse<IBudgetItem>) => {
          if (budgetItem.body) {
            return of(budgetItem.body);
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
