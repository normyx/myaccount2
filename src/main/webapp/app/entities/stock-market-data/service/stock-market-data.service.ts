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
import { IStockMarketData, NewStockMarketData } from '../stock-market-data.model';

export type PartialUpdateStockMarketData = Partial<IStockMarketData> & Pick<IStockMarketData, 'id'>;

type RestOf<T extends IStockMarketData | NewStockMarketData> = Omit<T, 'dataDate'> & {
  dataDate?: string | null;
};

export type RestStockMarketData = RestOf<IStockMarketData>;

export type NewRestStockMarketData = RestOf<NewStockMarketData>;

export type PartialUpdateRestStockMarketData = RestOf<PartialUpdateStockMarketData>;

export type EntityResponseType = HttpResponse<IStockMarketData>;
export type EntityArrayResponseType = HttpResponse<IStockMarketData[]>;

@Injectable({ providedIn: 'root' })
export class StockMarketDataService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/stock-market-data');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/stock-market-data');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(stockMarketData: NewStockMarketData): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(stockMarketData);
    return this.http
      .post<RestStockMarketData>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(stockMarketData: IStockMarketData): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(stockMarketData);
    return this.http
      .put<RestStockMarketData>(`${this.resourceUrl}/${this.getStockMarketDataIdentifier(stockMarketData)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(stockMarketData: PartialUpdateStockMarketData): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(stockMarketData);
    return this.http
      .patch<RestStockMarketData>(`${this.resourceUrl}/${this.getStockMarketDataIdentifier(stockMarketData)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestStockMarketData>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestStockMarketData[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestStockMarketData[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getStockMarketDataIdentifier(stockMarketData: Pick<IStockMarketData, 'id'>): number {
    return stockMarketData.id;
  }

  compareStockMarketData(o1: Pick<IStockMarketData, 'id'> | null, o2: Pick<IStockMarketData, 'id'> | null): boolean {
    return o1 && o2 ? this.getStockMarketDataIdentifier(o1) === this.getStockMarketDataIdentifier(o2) : o1 === o2;
  }

  addStockMarketDataToCollectionIfMissing<Type extends Pick<IStockMarketData, 'id'>>(
    stockMarketDataCollection: Type[],
    ...stockMarketDataToCheck: (Type | null | undefined)[]
  ): Type[] {
    const stockMarketData: Type[] = stockMarketDataToCheck.filter(isPresent);
    if (stockMarketData.length > 0) {
      const stockMarketDataCollectionIdentifiers = stockMarketDataCollection.map(
        stockMarketDataItem => this.getStockMarketDataIdentifier(stockMarketDataItem)!
      );
      const stockMarketDataToAdd = stockMarketData.filter(stockMarketDataItem => {
        const stockMarketDataIdentifier = this.getStockMarketDataIdentifier(stockMarketDataItem);
        if (stockMarketDataCollectionIdentifiers.includes(stockMarketDataIdentifier)) {
          return false;
        }
        stockMarketDataCollectionIdentifiers.push(stockMarketDataIdentifier);
        return true;
      });
      return [...stockMarketDataToAdd, ...stockMarketDataCollection];
    }
    return stockMarketDataCollection;
  }

  protected convertDateFromClient<T extends IStockMarketData | NewStockMarketData | PartialUpdateStockMarketData>(
    stockMarketData: T
  ): RestOf<T> {
    return {
      ...stockMarketData,
      dataDate: stockMarketData.dataDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restStockMarketData: RestStockMarketData): IStockMarketData {
    return {
      ...restStockMarketData,
      dataDate: restStockMarketData.dataDate ? dayjs(restStockMarketData.dataDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestStockMarketData>): HttpResponse<IStockMarketData> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestStockMarketData[]>): HttpResponse<IStockMarketData[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
