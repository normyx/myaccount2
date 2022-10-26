import { Component, Input, OnChanges, OnInit } from '@angular/core';
import { ActivatedRoute, Data, ParamMap, Router } from '@angular/router';
import { combineLatest, Observable, Subscription, switchMap, tap } from 'rxjs';

import { IBudgetItem } from '../../../entities/budget-item/budget-item.model';

import dayjs, { Dayjs } from 'dayjs';
import { ASC, DEFAULT_SORT_DATA, DESC, SORT } from '../../../config/navigation.constants';
import { EventManager, EventWithContent } from '../../../core/util/event-manager.service';
import { EntityArrayResponseType } from '../../../entities/budget-item/service/budget-item.service';
import { SortService } from '../../../shared/sort/sort.service';
import { EVENT_LOAD_BUDGET_ITEMS, EVENT_REORDER_BUDGET_ITEM, MyaReorderBudgetItem } from '../../config/mya.event.constants';
import { MyaBudgetItemService } from '../service/mya-budget-item.service';

@Component({
  selector: 'jhi-mya-budget-item-list',
  templateUrl: './mya-budget-item-list.component.html',
})
export class MyaBudgetItemListComponent implements OnInit, OnChanges {
  static readonly MONTH_TO_DISPLAY = 6;

  private static readonly NOT_SORTABLE_FIELDS_AFTER_SEARCH = ['name'];

  budgetItems?: IBudgetItem[];
  isLoading = false;

  predicate = 'order';
  ascending = true;

  rowNumber = 0;

  @Input() monthSelected: Date | null = null;
  @Input() currentSearch = '';
  @Input() selectedCategoryId: number | null = null;

  eventSubscriber: Subscription | null = null;

  constructor(
    protected budgetItemService: MyaBudgetItemService,
    protected activatedRoute: ActivatedRoute,
    public router: Router,
    protected sortService: SortService,
    private eventManager: EventManager
  ) {}

  trackId = (_index: number, item: IBudgetItem): number => this.budgetItemService.getBudgetItemIdentifier(item);

  search(query: string): void {
    if (query && MyaBudgetItemListComponent.NOT_SORTABLE_FIELDS_AFTER_SEARCH.includes(this.predicate)) {
      this.predicate = 'order';
      this.ascending = true;
    }
    this.currentSearch = query;
    this.navigateToWithComponentValues();
  }

  ngOnInit(): void {
    this.registerEvents();
  }

  ngOnChanges(): void {
    this.load();
  }

  load(): void {
    this.loadFromBackendWithRouteInformations().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
  }

  registerEvents(): void {
    this.eventSubscriber = this.eventManager.subscribe(EVENT_LOAD_BUDGET_ITEMS, () => this.load());
    this.eventSubscriber = this.eventManager.subscribe(EVENT_REORDER_BUDGET_ITEM, (event: EventWithContent<unknown> | string) =>
      this.reorder(event)
    );
  }

  reorder(event: EventWithContent<unknown> | string): void {
    const eventWithContent = event as EventWithContent<unknown>;
    const content = eventWithContent.content as MyaReorderBudgetItem;
    if (this.budgetItems) {
      for (let i = 0; i < this.budgetItems.length; i++) {
        // if it is the one to reorder
        if (this.budgetItems[i].id === content.id) {
          if (content.direction === 'up' && i !== 0) {
            this.budgetItemService.reorderUp(this.budgetItems[i], this.budgetItems[i - 1]).subscribe(() => {
              this.load();
            });
          } else if (content.direction === 'down' && i !== this.budgetItems.length - 1) {
            this.budgetItemService.reorderDown(this.budgetItems[i], this.budgetItems[i + 1]).subscribe(() => {
              this.load();
            });
          }
        }
      }
    }
  }

  navigateToWithComponentValues(): void {
    this.handleNavigation(this.predicate, this.ascending, this.currentSearch);
  }

  protected loadFromBackendWithRouteInformations(): Observable<EntityArrayResponseType> {
    return combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data]).pipe(
      tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
      switchMap(() => this.queryBackend(this.predicate, this.ascending, this.currentSearch))
    );
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const sort = (params.get(SORT) ?? data[DEFAULT_SORT_DATA] ?? 'order,asc').split(',');
    this.predicate = sort[0];
    this.ascending = sort[1] === ASC;
    if (params.has('search') && params.get('search') !== '') {
      this.currentSearch = params.get('search') as string;
      if (MyaBudgetItemListComponent.NOT_SORTABLE_FIELDS_AFTER_SEARCH.includes(this.predicate)) {
        this.predicate = '';
      }
    }
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.budgetItems = this.refineData(dataFromBody);
  }

  protected refineData(data: IBudgetItem[]): IBudgetItem[] {
    return data.sort(this.sortService.startSort(this.predicate, this.ascending ? 1 : -1));
  }

  protected fillComponentAttributesFromResponseBody(data: IBudgetItem[] | null): IBudgetItem[] {
    return data ?? [];
  }

  protected queryBackend(predicate?: string, ascending?: boolean, currentSearch?: string): Observable<EntityArrayResponseType> {
    this.isLoading = true;

    const queryObject: any = {
      eagerload: true,
      query: currentSearch,
      sort: this.getSortQueryParam(predicate, ascending),
    };

    if (this.currentSearch && this.currentSearch !== '') {
      return this.budgetItemService.searchWithSignedInUser(queryObject).pipe(tap(() => (this.isLoading = false)));
    } else {
      return this.budgetItemService
        .findWithSignedInUserBetween(dayjs(this.monthSelected), MyaBudgetItemListComponent.MONTH_TO_DISPLAY, this.selectedCategoryId)
        .pipe(tap(() => (this.isLoading = false)));
    }
  }

  protected handleNavigation(predicate?: string, ascending?: boolean, currentSearch?: string): void {
    const queryParamsObj = {
      search: currentSearch,
      sort: this.getSortQueryParam(predicate, ascending),
    };

    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: queryParamsObj,
    });
  }

  protected getSortQueryParam(predicate = this.predicate, ascending = this.ascending): string[] {
    const ascendingQueryParam = ascending ? ASC : DESC;
    if (predicate === '') {
      return [];
    } else {
      return [predicate + ',' + ascendingQueryParam];
    }
  }

  protected getMonthsToDisplay(): Dayjs[] | null {
    if (this.monthSelected) {
      const monthsToDisplay = new Array<Dayjs>(MyaBudgetItemListComponent.MONTH_TO_DISPLAY);
      for (let i = 0; i < MyaBudgetItemListComponent.MONTH_TO_DISPLAY; i++) {
        monthsToDisplay[i] = dayjs(this.monthSelected).date(1).add(i, 'month');
      }
      return monthsToDisplay;
    }
    return null;
  }

  protected getNumberOfMonthsToDisplay(): number {
    return MyaBudgetItemListComponent.MONTH_TO_DISPLAY;
  }
}
