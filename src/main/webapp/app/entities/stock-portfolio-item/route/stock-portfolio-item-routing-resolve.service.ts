import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IStockPortfolioItem } from '../stock-portfolio-item.model';
import { StockPortfolioItemService } from '../service/stock-portfolio-item.service';

@Injectable({ providedIn: 'root' })
export class StockPortfolioItemRoutingResolveService implements Resolve<IStockPortfolioItem | null> {
  constructor(protected service: StockPortfolioItemService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IStockPortfolioItem | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((stockPortfolioItem: HttpResponse<IStockPortfolioItem>) => {
          if (stockPortfolioItem.body) {
            return of(stockPortfolioItem.body);
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
