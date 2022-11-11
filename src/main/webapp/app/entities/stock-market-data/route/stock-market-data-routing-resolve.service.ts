import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IStockMarketData } from '../stock-market-data.model';
import { StockMarketDataService } from '../service/stock-market-data.service';

@Injectable({ providedIn: 'root' })
export class StockMarketDataRoutingResolveService implements Resolve<IStockMarketData | null> {
  constructor(protected service: StockMarketDataService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IStockMarketData | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((stockMarketData: HttpResponse<IStockMarketData>) => {
          if (stockMarketData.body) {
            return of(stockMarketData.body);
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
