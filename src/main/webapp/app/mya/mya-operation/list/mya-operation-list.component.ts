import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { ActivatedRoute, Data, ParamMap, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { combineLatest, filter, Observable, Subscription, switchMap, tap } from 'rxjs';

import { ASC, DEFAULT_SORT_DATA, DESC, SORT } from '../../../config/navigation.constants';
import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from '../../../config/pagination.constants';

import dayjs from 'dayjs';
import { DATE_FORMAT } from '../../../config/input.constants';
import { ITEM_DELETED_EVENT } from '../../../config/navigation.constants';
import { EventManager } from '../../../core/util/event-manager.service';
import { IBudgetItemPeriod } from '../../../entities/budget-item-period/budget-item-period.model';
import { BudgetItemPeriodService } from '../../../entities/budget-item-period/service/budget-item-period.service';
import { IBudgetItem } from '../../../entities/budget-item/budget-item.model';
import { BudgetItemService } from '../../../entities/budget-item/service/budget-item.service';
import { ICategory } from '../../../entities/category/category.model';
import { CategoryService } from '../../../entities/category/service/category.service';
import { IOperation } from '../../../entities/operation/operation.model';
import { EntityArrayResponseType } from '../../../entities/operation/service/operation.service';
import { SubCategoryService } from '../../../entities/sub-category/service/sub-category.service';
import { ISubCategory } from '../../../entities/sub-category/sub-category.model';
import { FilterOption, FilterOptions, IFilterOptions } from '../../../shared/filter/filter.model';
import { EVENT_LOAD_OPERATIONS } from '../../config/mya.event.constants';
import { MyaBudgetItemPeriodUpdateDialogComponent } from '../../mya-budget-item-period/update/mya-budget-item-period-update-dialog.component';
import { MyaOperationDeleteDialogComponent } from '../delete/mya-operation-delete-dialog.component';
import { MyaOperationService } from '../service/mya-operation.service';
import { MyaOperationUpdateDialogComponent } from '../update/mya-operation-update-dialog.component';
import { IBankAccount } from '../../../entities/bank-account/bank-account.model';

@Component({
  selector: 'jhi-mya-operation-list',
  templateUrl: './mya-operation-list.component.html',
})
export class MyaOperationListComponent implements OnInit, OnChanges {
  private static readonly NOT_SORTABLE_FIELDS_AFTER_SEARCH = ['label', 'note', 'checkNumber'];

  operations?: IOperation[];
  isLoading = false;

  predicate = 'date';
  ascending = false;

  filters: IFilterOptions = new FilterOptions();

  itemsPerPage = ITEMS_PER_PAGE;
  totalItems = 0;
  page = 1;

  eventSubscriber: Subscription | null = null;
  subCategories: ISubCategory[] | null = null;

  @Input() dateFrom: Date | null = null;
  @Input() dateTo: Date | null = null;
  @Input() currentSearch = '';
  @Input() selectedCategory: ICategory | null = null;
  @Input() selectedBankAccount: IBankAccount | null = null;
  @Input() contains: string | null = null;

  constructor(
    protected operationService: MyaOperationService,
    protected categoryService: CategoryService,
    protected subCategoryService: SubCategoryService,
    protected budgetItemService: BudgetItemService,
    protected budgetItemPeriodService: BudgetItemPeriodService,
    protected activatedRoute: ActivatedRoute,
    public router: Router,
    protected modalService: NgbModal,
    private eventManager: EventManager
  ) {}

  trackId = (_index: number, item: IOperation): number => this.operationService.getOperationIdentifier(item);

  search(query: string): void {
    if (query && MyaOperationListComponent.NOT_SORTABLE_FIELDS_AFTER_SEARCH.includes(this.predicate)) {
      this.predicate = 'id';
      this.ascending = true;
    }
    this.page = 1;
    this.currentSearch = query;
    this.navigateToWithComponentValues();
  }

  ngOnInit(): void {
    this.registerEvents();
  }

  ngOnChanges(changes: SimpleChanges): void {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
    if (changes['currentSearch'] !== undefined) {
      this.search(changes['currentSearch'].currentValue);
    }
    this.navigateToWithComponentValues();
    this.loadAll();
  }

  loadData(): void {
    this.loadFromBackendWithRouteInformations().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
  }

  loadAll(): void {
    if (!this.subCategories) {
      this.subCategoryService.query().subscribe((subCategories: HttpResponse<ISubCategory[]>) => {
        this.subCategories = subCategories.body;
        this.loadData();
      });
    } else {
      this.loadData();
    }
  }

  getSubCategoryIds(category: ICategory): number[] {
    let subCategoryIds = new Array<number>();

    if (this.subCategories) {
      const scFiltered = this.subCategories.filter(sc => sc.category?.id === category.id);
      subCategoryIds = scFiltered.map(sc => sc.id);
    }

    return subCategoryIds;
  }

  registerEvents(): void {
    this.eventSubscriber = this.eventManager.subscribe(EVENT_LOAD_OPERATIONS, () => this.loadAll());
  }
  navigateToWithComponentValues(): void {
    this.handleNavigation(this.page, this.predicate, this.ascending, this.filters, this.currentSearch);
  }

  navigateToPage(page = this.page): void {
    this.handleNavigation(page, this.predicate, this.ascending, this.filters, this.currentSearch);
  }

  loadFromBackendWithRouteInformations(): Observable<EntityArrayResponseType> {
    return combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data]).pipe(
      tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
      switchMap(() => this.queryBackend(this.page, this.predicate, this.ascending, this.filters, this.currentSearch))
    );
  }

  fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const page = params.get(PAGE_HEADER);
    this.page = +(page ?? 1);
    const sort = (params.get(SORT) ?? data[DEFAULT_SORT_DATA]).split(',');
    this.predicate = sort[0];
    this.ascending = sort[1] === ASC;
    this.filters.initializeFromParams(params);

    if (params.has('search') && params.get('search') !== '') {
      this.currentSearch = params.get('search') as string;
      if (MyaOperationListComponent.NOT_SORTABLE_FIELDS_AFTER_SEARCH.includes(this.predicate)) {
        this.predicate = '';
      }
    }
  }

  onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.operations = dataFromBody;
  }

  fillComponentAttributesFromResponseBody(data: IOperation[] | null): IOperation[] {
    return data ?? [];
  }

  fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    this.totalItems = Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER));
  }

  queryBackend(
    page?: number,
    predicate?: string,
    ascending?: boolean,
    filters?: IFilterOptions,
    currentSearch?: string
  ): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const pageToLoad: number = page ?? 1;
    const queryObject: any = {
      page: pageToLoad - 1,
      size: this.itemsPerPage,
      eagerload: true,
      query: currentSearch,
      sort: this.getSortQueryParam(predicate, ascending),
    };
    this.filters.clear();
    if (this.dateFrom) {
      this.filters.addFilter('date.greaterThan', dayjs(this.dateFrom).add(-1, 'day').format(DATE_FORMAT));
    }
    if (this.dateTo) {
      this.filters.addFilter('date.lessThan', dayjs(this.dateTo).add(1, 'day').format(DATE_FORMAT));
    }
    if (this.selectedCategory) {
      this.filters.addFilter('subCategoryId.in', this.getSubCategoryIds(this.selectedCategory).toString());
    }
    if (this.selectedBankAccount) {
      this.filters.addFilter('bankAccountId.equals', this.selectedBankAccount.id.toString());
    }
    if (this.contains) {
      this.filters.addFilter('label.contains', this.contains);
    }
    this.filters.filterOptions.forEach(filterOption => {
      queryObject[filterOption.name] = filterOption.values;
    });

    if (this.currentSearch && this.currentSearch !== '') {
      return this.operationService.searchWithSignedInUser(queryObject).pipe(tap(() => (this.isLoading = false)));
    } else {
      return this.operationService.queryWithSignedInUser(queryObject).pipe(tap(() => (this.isLoading = false)));
    }
  }

  handleNavigation(page = this.page, predicate?: string, ascending?: boolean, filters?: IFilterOptions, currentSearch?: string): void {
    const queryParamsObj: any = {
      search: currentSearch,
      page,
      size: this.itemsPerPage,
      sort: this.getSortQueryParam(predicate, ascending),
    };

    if (filters?.hasAnyFilterSet()) {
      filters.filterOptions.forEach(filterOption => {
        queryParamsObj[filterOption.nameAsQueryParam()] = filterOption.values;
      });
    }

    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: queryParamsObj,
    });
  }

  getSortQueryParam(predicate = this.predicate, ascending = this.ascending): string[] {
    const ascendingQueryParam = ascending ? ASC : DESC;
    if (predicate === '') {
      return [];
    } else {
      return [predicate + ',' + ascendingQueryParam];
    }
  }

  delete(operation: IOperation): void {
    const modalRef = this.modalService.open(MyaOperationDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.setOperation(operation);
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        switchMap(() => this.loadFromBackendWithRouteInformations())
      )
      .subscribe({
        next: (res: EntityArrayResponseType) => {
          this.onResponseSuccess(res);
        },
      });
  }

  edit(operation: IOperation): void {
    const modalRef = this.modalService.open(MyaOperationUpdateDialogComponent, { size: 'lg', backdrop: 'static', animation: true });
    modalRef.componentInstance.setOperation(operation);
  }

  editBudgetItemPeriod(bip: IBudgetItemPeriod): void {
    this.budgetItemPeriodService.find(bip.id).subscribe((budgetItemPeriod: HttpResponse<IBudgetItemPeriod>) => {
      if (budgetItemPeriod.body?.budgetItem) {
        const budgetItemId = budgetItemPeriod.body.budgetItem.id;
        this.budgetItemService.find(budgetItemId).subscribe((budgetItem: HttpResponse<IBudgetItem>) => {
          const modalRef = this.modalService.open(MyaBudgetItemPeriodUpdateDialogComponent, {
            size: 'lg',
            backdrop: 'static',
            animation: true,
          });
          modalRef.componentInstance.setBudgetItemPeriod(budgetItemPeriod.body, budgetItem.body);
        });
      }
    });
  }
}
