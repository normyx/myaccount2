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
import { IStockPortfolioItem, NewStockPortfolioItem } from '../stock-portfolio-item.model';

export type PartialUpdateStockPortfolioItem = Partial<IStockPortfolioItem> & Pick<IStockPortfolioItem, 'id'>;

type RestOf<T extends IStockPortfolioItem | NewStockPortfolioItem> = Omit<T, 'stockAcquisitionDate' | 'stockCurrentDate'> & {
  stockAcquisitionDate?: string | null;
  stockCurrentDate?: string | null;
};

export type RestStockPortfolioItem = RestOf<IStockPortfolioItem>;

export type NewRestStockPortfolioItem = RestOf<NewStockPortfolioItem>;

export type PartialUpdateRestStockPortfolioItem = RestOf<PartialUpdateStockPortfolioItem>;

export type EntityResponseType = HttpResponse<IStockPortfolioItem>;
export type EntityArrayResponseType = HttpResponse<IStockPortfolioItem[]>;

@Injectable({ providedIn: 'root' })
export class StockPortfolioItemService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/stock-portfolio-items');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/stock-portfolio-items');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(stockPortfolioItem: NewStockPortfolioItem): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(stockPortfolioItem);
    return this.http
      .post<RestStockPortfolioItem>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(stockPortfolioItem: IStockPortfolioItem): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(stockPortfolioItem);
    return this.http
      .put<RestStockPortfolioItem>(`${this.resourceUrl}/${this.getStockPortfolioItemIdentifier(stockPortfolioItem)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(stockPortfolioItem: PartialUpdateStockPortfolioItem): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(stockPortfolioItem);
    return this.http
      .patch<RestStockPortfolioItem>(`${this.resourceUrl}/${this.getStockPortfolioItemIdentifier(stockPortfolioItem)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestStockPortfolioItem>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestStockPortfolioItem[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestStockPortfolioItem[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getStockPortfolioItemIdentifier(stockPortfolioItem: Pick<IStockPortfolioItem, 'id'>): number {
    return stockPortfolioItem.id;
  }

  compareStockPortfolioItem(o1: Pick<IStockPortfolioItem, 'id'> | null, o2: Pick<IStockPortfolioItem, 'id'> | null): boolean {
    return o1 && o2 ? this.getStockPortfolioItemIdentifier(o1) === this.getStockPortfolioItemIdentifier(o2) : o1 === o2;
  }

  addStockPortfolioItemToCollectionIfMissing<Type extends Pick<IStockPortfolioItem, 'id'>>(
    stockPortfolioItemCollection: Type[],
    ...stockPortfolioItemsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const stockPortfolioItems: Type[] = stockPortfolioItemsToCheck.filter(isPresent);
    if (stockPortfolioItems.length > 0) {
      const stockPortfolioItemCollectionIdentifiers = stockPortfolioItemCollection.map(
        stockPortfolioItemItem => this.getStockPortfolioItemIdentifier(stockPortfolioItemItem)!
      );
      const stockPortfolioItemsToAdd = stockPortfolioItems.filter(stockPortfolioItemItem => {
        const stockPortfolioItemIdentifier = this.getStockPortfolioItemIdentifier(stockPortfolioItemItem);
        if (stockPortfolioItemCollectionIdentifiers.includes(stockPortfolioItemIdentifier)) {
          return false;
        }
        stockPortfolioItemCollectionIdentifiers.push(stockPortfolioItemIdentifier);
        return true;
      });
      return [...stockPortfolioItemsToAdd, ...stockPortfolioItemCollection];
    }
    return stockPortfolioItemCollection;
  }

  protected convertDateFromClient<T extends IStockPortfolioItem | NewStockPortfolioItem | PartialUpdateStockPortfolioItem>(
    stockPortfolioItem: T
  ): RestOf<T> {
    return {
      ...stockPortfolioItem,
      stockAcquisitionDate: stockPortfolioItem.stockAcquisitionDate?.format(DATE_FORMAT) ?? null,
      stockCurrentDate: stockPortfolioItem.stockCurrentDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restStockPortfolioItem: RestStockPortfolioItem): IStockPortfolioItem {
    return {
      ...restStockPortfolioItem,
      stockAcquisitionDate: restStockPortfolioItem.stockAcquisitionDate ? dayjs(restStockPortfolioItem.stockAcquisitionDate) : undefined,
      stockCurrentDate: restStockPortfolioItem.stockCurrentDate ? dayjs(restStockPortfolioItem.stockCurrentDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestStockPortfolioItem>): HttpResponse<IStockPortfolioItem> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestStockPortfolioItem[]>): HttpResponse<IStockPortfolioItem[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
