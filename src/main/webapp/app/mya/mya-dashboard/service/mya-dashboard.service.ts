import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Dayjs } from 'dayjs';
import { DATE_FORMAT } from '../../../config/input.constants';
import { ApplicationConfigService } from '../../../core/config/application-config.service';

type EntityResponseType = HttpResponse<any>;

@Injectable({ providedIn: 'root' })
export class MyaDashboardService {
  constructor(private http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  getEvolutionInMonth(month: Dayjs, categoryId: number | null): Observable<EntityResponseType> {
    const resourceUrl = this.applicationConfigService.getEndpointFor('api/mya-reports/evolution-in-month');
    if (categoryId) {
      return this.http.get<any>(`${resourceUrl}/${month.format(DATE_FORMAT)}/${categoryId}`, { observe: 'response' });
    } else {
      return this.http.get<any>(`${resourceUrl}/${month.format(DATE_FORMAT)}`, { observe: 'response' });
    }
  }

  getEvolutionByMonth(categoryId: number | null, monthFrom: Dayjs, monthTo: Dayjs): Observable<EntityResponseType> {
    const resourceUrl = this.applicationConfigService.getEndpointFor('api/mya-reports/evolution-by-month');
    if (categoryId) {
      return this.http.get<any>(`${resourceUrl}/${categoryId}/${monthFrom.format(DATE_FORMAT)}/${monthTo.format(DATE_FORMAT)}`, {
        observe: 'response',
      });
    } else {
      return this.http.get<any>(`${resourceUrl}/${monthFrom.format(DATE_FORMAT)}/${monthTo.format(DATE_FORMAT)}`, {
        observe: 'response',
      });
    }
  }

  getCategoryAmountPerMonthWithMarked(categoryId: number, monthFrom: Dayjs, monthTo: Dayjs): Observable<EntityResponseType> {
    const resourceUrl = this.applicationConfigService.getEndpointFor('api/mya-reports/category-amount-per-month-with-marked');
    return this.http.get<any>(`${resourceUrl}/${categoryId}/${monthFrom.format(DATE_FORMAT)}/${monthTo.format(DATE_FORMAT)}`, {
      observe: 'response',
    });
  }

  getSubCategorySplit(categoryId: number, month: Dayjs, numberOfMonths: number): Observable<EntityResponseType> {
    const resourceUrl = this.applicationConfigService.getEndpointFor('api/mya-reports/sub-category-split');
    return this.http.get<any>(`${resourceUrl}/${categoryId}/${month.format(DATE_FORMAT)}/${numberOfMonths}`, {
      observe: 'response',
    });
  }

  getEvolutionBetweenDates(dateFrom: Dayjs, dateTo: Dayjs, bankAccountId: number | null): Observable<HttpResponse<any>> {
    const resourceUrl = this.applicationConfigService.getEndpointFor('api/mya-reports/evolution-between-dates');
    return this.http.get<any>(
      `${resourceUrl}/${dateFrom.format(DATE_FORMAT)}/${dateTo.format(DATE_FORMAT)}/${bankAccountId ? bankAccountId : 'null'}`,
      {
        observe: 'response',
      }
    );
  }
}
