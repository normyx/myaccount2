import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRealEstateItem } from '../real-estate-item.model';
import { RealEstateItemService } from '../service/real-estate-item.service';

@Injectable({ providedIn: 'root' })
export class RealEstateItemRoutingResolveService implements Resolve<IRealEstateItem | null> {
  constructor(protected service: RealEstateItemService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IRealEstateItem | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((realEstateItem: HttpResponse<IRealEstateItem>) => {
          if (realEstateItem.body) {
            return of(realEstateItem.body);
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
