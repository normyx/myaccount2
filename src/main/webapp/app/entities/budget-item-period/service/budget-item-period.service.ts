import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IBudgetItemPeriod, NewBudgetItemPeriod } from '../budget-item-period.model';

export type PartialUpdateBudgetItemPeriod = Partial<IBudgetItemPeriod> & Pick<IBudgetItemPeriod, 'id'>;

type RestOf<T extends IBudgetItemPeriod | NewBudgetItemPeriod> = Omit<T, 'date' | 'month'> & {
  date?: string | null;
  month?: string | null;
};

export type RestBudgetItemPeriod = RestOf<IBudgetItemPeriod>;

export type NewRestBudgetItemPeriod = RestOf<NewBudgetItemPeriod>;

export type PartialUpdateRestBudgetItemPeriod = RestOf<PartialUpdateBudgetItemPeriod>;

export type EntityResponseType = HttpResponse<IBudgetItemPeriod>;
export type EntityArrayResponseType = HttpResponse<IBudgetItemPeriod[]>;

@Injectable({ providedIn: 'root' })
export class BudgetItemPeriodService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/budget-item-periods');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/budget-item-periods');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(budgetItemPeriod: NewBudgetItemPeriod): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(budgetItemPeriod);
    return this.http
      .post<RestBudgetItemPeriod>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(budgetItemPeriod: IBudgetItemPeriod): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(budgetItemPeriod);
    return this.http
      .put<RestBudgetItemPeriod>(`${this.resourceUrl}/${this.getBudgetItemPeriodIdentifier(budgetItemPeriod)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(budgetItemPeriod: PartialUpdateBudgetItemPeriod): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(budgetItemPeriod);
    return this.http
      .patch<RestBudgetItemPeriod>(`${this.resourceUrl}/${this.getBudgetItemPeriodIdentifier(budgetItemPeriod)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestBudgetItemPeriod>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestBudgetItemPeriod[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestBudgetItemPeriod[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getBudgetItemPeriodIdentifier(budgetItemPeriod: Pick<IBudgetItemPeriod, 'id'>): number {
    return budgetItemPeriod.id;
  }

  compareBudgetItemPeriod(o1: Pick<IBudgetItemPeriod, 'id'> | null, o2: Pick<IBudgetItemPeriod, 'id'> | null): boolean {
    return o1 && o2 ? this.getBudgetItemPeriodIdentifier(o1) === this.getBudgetItemPeriodIdentifier(o2) : o1 === o2;
  }

  addBudgetItemPeriodToCollectionIfMissing<Type extends Pick<IBudgetItemPeriod, 'id'>>(
    budgetItemPeriodCollection: Type[],
    ...budgetItemPeriodsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const budgetItemPeriods: Type[] = budgetItemPeriodsToCheck.filter(isPresent);
    if (budgetItemPeriods.length > 0) {
      const budgetItemPeriodCollectionIdentifiers = budgetItemPeriodCollection.map(
        budgetItemPeriodItem => this.getBudgetItemPeriodIdentifier(budgetItemPeriodItem)!
      );
      const budgetItemPeriodsToAdd = budgetItemPeriods.filter(budgetItemPeriodItem => {
        const budgetItemPeriodIdentifier = this.getBudgetItemPeriodIdentifier(budgetItemPeriodItem);
        if (budgetItemPeriodCollectionIdentifiers.includes(budgetItemPeriodIdentifier)) {
          return false;
        }
        budgetItemPeriodCollectionIdentifiers.push(budgetItemPeriodIdentifier);
        return true;
      });
      return [...budgetItemPeriodsToAdd, ...budgetItemPeriodCollection];
    }
    return budgetItemPeriodCollection;
  }

  protected convertDateFromClient<T extends IBudgetItemPeriod | NewBudgetItemPeriod | PartialUpdateBudgetItemPeriod>(
    budgetItemPeriod: T
  ): RestOf<T> {
    return {
      ...budgetItemPeriod,
      date: budgetItemPeriod.date?.format(DATE_FORMAT) ?? null,
      month: budgetItemPeriod.month?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restBudgetItemPeriod: RestBudgetItemPeriod): IBudgetItemPeriod {
    return {
      ...restBudgetItemPeriod,
      date: restBudgetItemPeriod.date ? dayjs(restBudgetItemPeriod.date) : undefined,
      month: restBudgetItemPeriod.month ? dayjs(restBudgetItemPeriod.month) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestBudgetItemPeriod>): HttpResponse<IBudgetItemPeriod> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestBudgetItemPeriod[]>): HttpResponse<IBudgetItemPeriod[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
