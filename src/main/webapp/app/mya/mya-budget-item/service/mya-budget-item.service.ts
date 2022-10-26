import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { BudgetItemService } from 'app/entities/budget-item/service/budget-item.service';

import dayjs, { Dayjs } from 'dayjs';
import { Observable } from 'rxjs';
import { DATE_FORMAT } from '../../../config/input.constants';
import { IBudgetItem, NewBudgetItem } from '../../../entities/budget-item/budget-item.model';
import { EntityArrayResponseType, EntityResponseType } from '../../../entities/budget-item/service/budget-item.service';
import { MyaDateUtils } from '../../util/mya-date-util.service';

@Injectable({ providedIn: 'root' })
export class MyaBudgetItemService extends BudgetItemService {
  static readonly MYA_BUDGET_ITEM_API = 'api/mya-budget-items';
  static readonly MYA_BUDGET_ITEM_SEARCH_API = 'api/_search/mya-budget-items';
  static readonly MYA_WITH_SIGNEDIN_USER_SUFFIX = '/with-signedin-user';
  static readonly MYA_BETWEEN_SUFFIX = '/between';
  static readonly MYA_REORDER_SUFFIX = '/reorder';
  static readonly MYA_UP_SUFFIX = '/up';
  static readonly MYA_DOWN_SUFFIX = '/down';
  static readonly MYA_WITH_PERIODS_SUFFIX = '/with-periods';
  protected myaResourceUrl = this.applicationConfigService.getEndpointFor(MyaBudgetItemService.MYA_BUDGET_ITEM_API);
  protected myaResourceWithSignedInUserUrl = this.applicationConfigService.getEndpointFor(
    MyaBudgetItemService.MYA_BUDGET_ITEM_API + MyaBudgetItemService.MYA_WITH_SIGNEDIN_USER_SUFFIX
  );
  protected myaResourceWithSignedInUserSearchUrl = this.applicationConfigService.getEndpointFor(
    MyaBudgetItemService.MYA_BUDGET_ITEM_SEARCH_API + MyaBudgetItemService.MYA_WITH_SIGNEDIN_USER_SUFFIX
  );

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService, protected dateUtils: MyaDateUtils) {
    super(http, applicationConfigService);
  }

  searchWithSignedInUser(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IBudgetItem[]>(this.myaResourceWithSignedInUserSearchUrl, { params: options, observe: 'response' });
  }

  findWithSignedInUserBetween(from: Dayjs, numberOfMonths: number, categoryId: number | null): Observable<EntityArrayResponseType> {
    const fromStr = this.dateUtils.convertToString(dayjs(from).date(1));
    const toStr = this.dateUtils.convertToString(
      dayjs(from)
        .date(1)
        .add(numberOfMonths - 1, 'month')
    );
    let url = `${this.myaResourceWithSignedInUserUrl}${MyaBudgetItemService.MYA_BETWEEN_SUFFIX}/${fromStr}/${toStr}`;
    if (categoryId) {
      url = `${url}/${categoryId}`;
    }
    return this.http.get<IBudgetItem[]>(url, { observe: 'response' });
  }

  deleteWithPeriods(budgetItem: IBudgetItem): Observable<any> {
    return this.http.delete<any>(`${this.myaResourceUrl}${MyaBudgetItemService.MYA_WITH_PERIODS_SUFFIX}/${budgetItem.id}`, {
      observe: 'response',
    });
  }

  reorderUp(biToUp: IBudgetItem, withBi: IBudgetItem): Observable<any> {
    return this.http.get<any>(
      `${this.myaResourceUrl}${MyaBudgetItemService.MYA_REORDER_SUFFIX}${MyaBudgetItemService.MYA_UP_SUFFIX}/${biToUp.id}/${withBi.id}`,
      {
        observe: 'response',
      }
    );
  }

  reorderDown(biToUp: IBudgetItem, withBi: IBudgetItem): Observable<any> {
    return this.http.get<any>(
      `${this.myaResourceUrl}${MyaBudgetItemService.MYA_REORDER_SUFFIX}${MyaBudgetItemService.MYA_DOWN_SUFFIX}/${biToUp.id}/${withBi.id}`,
      {
        observe: 'response',
      }
    );
  }

  createWithBudgetItemPeriods(
    budgetItem: NewBudgetItem,
    isSmoothed: boolean,
    monthFrom: Dayjs,
    amount: number,
    dayOfMonth: number
  ): Observable<EntityResponseType> {
    monthFrom = dayjs(monthFrom);
    const monthStr: string = monthFrom.format(DATE_FORMAT);
    return this.http.post<IBudgetItem>(
      `${this.myaResourceUrl}/${MyaBudgetItemService.MYA_WITH_PERIODS_SUFFIX}/${String(isSmoothed)}/${monthStr}/${amount}/${dayOfMonth}`,
      budgetItem,
      { observe: 'response' }
    );
  }
}
