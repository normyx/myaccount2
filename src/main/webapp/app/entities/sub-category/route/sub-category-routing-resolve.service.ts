import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISubCategory } from '../sub-category.model';
import { SubCategoryService } from '../service/sub-category.service';

@Injectable({ providedIn: 'root' })
export class SubCategoryRoutingResolveService implements Resolve<ISubCategory | null> {
  constructor(protected service: SubCategoryService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISubCategory | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((subCategory: HttpResponse<ISubCategory>) => {
          if (subCategory.body) {
            return of(subCategory.body);
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
