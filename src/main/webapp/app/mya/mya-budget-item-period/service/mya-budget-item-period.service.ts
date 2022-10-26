import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import dayjs, { Dayjs } from 'dayjs';
import { IBudgetItemPeriod } from '../../../entities/budget-item-period/budget-item-period.model';
import { BudgetItemPeriodService, RestBudgetItemPeriod } from '../../../entities/budget-item-period/service/budget-item-period.service';
import { IBudgetItem } from '../../../entities/budget-item/budget-item.model';
import { EntityArrayResponseType } from '../../../entities/budget-item/service/budget-item.service';
import { MyaDateUtils } from '../../util/mya-date-util.service';

@Injectable({ providedIn: 'root' })
export class MyaBudgetItemPeriodService extends BudgetItemPeriodService {
  static readonly MYA_BUDGET_ITEM_PERIOD_API = 'api/mya-budget-item-periods';
  static readonly MYA_WITH_NEXT = '/with-nexts';

  protected myaResourceUrl = this.applicationConfigService.getEndpointFor(MyaBudgetItemPeriodService.MYA_BUDGET_ITEM_PERIOD_API);

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService, protected dateUtils: MyaDateUtils) {
    super(http, applicationConfigService);
  }

  findFromBudgetItemInRange(budgetItem: IBudgetItem, from: Dayjs, numberOfMonths: number): Observable<EntityArrayResponseType> {
    const budgetItemId = budgetItem.id;
    const options = createRequestOption(null);
    const fromStr = this.dateUtils.convertToString(dayjs(from).date(1));
    const toStr = this.dateUtils.convertToString(
      dayjs(from)
        .date(1)
        .add(numberOfMonths - 1, 'month')
    );
    return this.http
      .get<RestBudgetItemPeriod[]>(`${this.myaResourceUrl}/${budgetItemId}/${fromStr}/${toStr}`, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  groupByMonth(bipArray: IBudgetItemPeriod[] | null, monthsToDisplay: dayjs.Dayjs[]): IBudgetItemPeriod[][] {
    const budgetItemPeriods: IBudgetItemPeriod[][] = new Array(monthsToDisplay.length);
    let i: number;
    if (bipArray) {
      // if result is defined
      for (i = 0; i < budgetItemPeriods.length; i++) {
        const month: dayjs.Dayjs = monthsToDisplay[i];
        // find corresponding budgetItemPeriod
        const correspondingBudgetItemPeriod: IBudgetItemPeriod[] = bipArray.filter(function (el) {
          return el.month!.month() === month.month() && el.month!.year() === month.year();
        });
        budgetItemPeriods[i] = correspondingBudgetItemPeriod;
        // console.log(correspondingBudgetItemPeriod);
      }
    }
    return budgetItemPeriods;
  }
  updateWithNext(budgetItemPeriod: IBudgetItemPeriod, day: number | null, withNext: boolean): Observable<HttpResponse<any>> {
    const copy = super.convertDateFromClient(budgetItemPeriod);
    return this.http.put<IBudgetItemPeriod>(
      `${this.myaResourceUrl}${MyaBudgetItemPeriodService.MYA_WITH_NEXT}/${String(day)}/${String(withNext)}`,
      copy,
      {
        observe: 'response',
      }
    );
  }

  deleteBudgetItemPeriodWithNext(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.myaResourceUrl}${MyaBudgetItemPeriodService.MYA_WITH_NEXT}/${id}`, { observe: 'response' });
  }
}
