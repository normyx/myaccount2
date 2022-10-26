import { HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';
import { ICategory } from '../../../entities/category/category.model';
import { CategoryService } from '../../../entities/category/service/category.service';

@Injectable({ providedIn: 'root' })
export class MyaDashboardCategoryRoutingResolveService implements Resolve<ICategory | null> {
  constructor(protected service: CategoryService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICategory | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((category: HttpResponse<ICategory>) => {
          if (category.body) {
            return of(category.body);
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
