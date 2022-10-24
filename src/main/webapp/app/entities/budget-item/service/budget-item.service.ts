import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IBudgetItem, NewBudgetItem } from '../budget-item.model';

export type PartialUpdateBudgetItem = Partial<IBudgetItem> & Pick<IBudgetItem, 'id'>;

export type EntityResponseType = HttpResponse<IBudgetItem>;
export type EntityArrayResponseType = HttpResponse<IBudgetItem[]>;

@Injectable({ providedIn: 'root' })
export class BudgetItemService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/budget-items');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/budget-items');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(budgetItem: NewBudgetItem): Observable<EntityResponseType> {
    return this.http.post<IBudgetItem>(this.resourceUrl, budgetItem, { observe: 'response' });
  }

  update(budgetItem: IBudgetItem): Observable<EntityResponseType> {
    return this.http.put<IBudgetItem>(`${this.resourceUrl}/${this.getBudgetItemIdentifier(budgetItem)}`, budgetItem, {
      observe: 'response',
    });
  }

  partialUpdate(budgetItem: PartialUpdateBudgetItem): Observable<EntityResponseType> {
    return this.http.patch<IBudgetItem>(`${this.resourceUrl}/${this.getBudgetItemIdentifier(budgetItem)}`, budgetItem, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IBudgetItem>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IBudgetItem[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IBudgetItem[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getBudgetItemIdentifier(budgetItem: Pick<IBudgetItem, 'id'>): number {
    return budgetItem.id;
  }

  compareBudgetItem(o1: Pick<IBudgetItem, 'id'> | null, o2: Pick<IBudgetItem, 'id'> | null): boolean {
    return o1 && o2 ? this.getBudgetItemIdentifier(o1) === this.getBudgetItemIdentifier(o2) : o1 === o2;
  }

  addBudgetItemToCollectionIfMissing<Type extends Pick<IBudgetItem, 'id'>>(
    budgetItemCollection: Type[],
    ...budgetItemsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const budgetItems: Type[] = budgetItemsToCheck.filter(isPresent);
    if (budgetItems.length > 0) {
      const budgetItemCollectionIdentifiers = budgetItemCollection.map(budgetItemItem => this.getBudgetItemIdentifier(budgetItemItem)!);
      const budgetItemsToAdd = budgetItems.filter(budgetItemItem => {
        const budgetItemIdentifier = this.getBudgetItemIdentifier(budgetItemItem);
        if (budgetItemCollectionIdentifiers.includes(budgetItemIdentifier)) {
          return false;
        }
        budgetItemCollectionIdentifiers.push(budgetItemIdentifier);
        return true;
      });
      return [...budgetItemsToAdd, ...budgetItemCollection];
    }
    return budgetItemCollection;
  }
}
