import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IRealEstateItem, NewRealEstateItem } from '../real-estate-item.model';

export type PartialUpdateRealEstateItem = Partial<IRealEstateItem> & Pick<IRealEstateItem, 'id'>;

type RestOf<T extends IRealEstateItem | NewRealEstateItem> = Omit<T, 'itemDate'> & {
  itemDate?: string | null;
};

export type RestRealEstateItem = RestOf<IRealEstateItem>;

export type NewRestRealEstateItem = RestOf<NewRealEstateItem>;

export type PartialUpdateRestRealEstateItem = RestOf<PartialUpdateRealEstateItem>;

export type EntityResponseType = HttpResponse<IRealEstateItem>;
export type EntityArrayResponseType = HttpResponse<IRealEstateItem[]>;

@Injectable({ providedIn: 'root' })
export class RealEstateItemService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/real-estate-items');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/real-estate-items');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(realEstateItem: NewRealEstateItem): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(realEstateItem);
    return this.http
      .post<RestRealEstateItem>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(realEstateItem: IRealEstateItem): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(realEstateItem);
    return this.http
      .put<RestRealEstateItem>(`${this.resourceUrl}/${this.getRealEstateItemIdentifier(realEstateItem)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(realEstateItem: PartialUpdateRealEstateItem): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(realEstateItem);
    return this.http
      .patch<RestRealEstateItem>(`${this.resourceUrl}/${this.getRealEstateItemIdentifier(realEstateItem)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestRealEstateItem>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestRealEstateItem[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestRealEstateItem[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getRealEstateItemIdentifier(realEstateItem: Pick<IRealEstateItem, 'id'>): number {
    return realEstateItem.id;
  }

  compareRealEstateItem(o1: Pick<IRealEstateItem, 'id'> | null, o2: Pick<IRealEstateItem, 'id'> | null): boolean {
    return o1 && o2 ? this.getRealEstateItemIdentifier(o1) === this.getRealEstateItemIdentifier(o2) : o1 === o2;
  }

  addRealEstateItemToCollectionIfMissing<Type extends Pick<IRealEstateItem, 'id'>>(
    realEstateItemCollection: Type[],
    ...realEstateItemsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const realEstateItems: Type[] = realEstateItemsToCheck.filter(isPresent);
    if (realEstateItems.length > 0) {
      const realEstateItemCollectionIdentifiers = realEstateItemCollection.map(
        realEstateItemItem => this.getRealEstateItemIdentifier(realEstateItemItem)!
      );
      const realEstateItemsToAdd = realEstateItems.filter(realEstateItemItem => {
        const realEstateItemIdentifier = this.getRealEstateItemIdentifier(realEstateItemItem);
        if (realEstateItemCollectionIdentifiers.includes(realEstateItemIdentifier)) {
          return false;
        }
        realEstateItemCollectionIdentifiers.push(realEstateItemIdentifier);
        return true;
      });
      return [...realEstateItemsToAdd, ...realEstateItemCollection];
    }
    return realEstateItemCollection;
  }

  protected convertDateFromClient<T extends IRealEstateItem | NewRealEstateItem | PartialUpdateRealEstateItem>(
    realEstateItem: T
  ): RestOf<T> {
    return {
      ...realEstateItem,
      itemDate: realEstateItem.itemDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restRealEstateItem: RestRealEstateItem): IRealEstateItem {
    return {
      ...restRealEstateItem,
      itemDate: restRealEstateItem.itemDate ? dayjs(restRealEstateItem.itemDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestRealEstateItem>): HttpResponse<IRealEstateItem> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestRealEstateItem[]>): HttpResponse<IRealEstateItem[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
