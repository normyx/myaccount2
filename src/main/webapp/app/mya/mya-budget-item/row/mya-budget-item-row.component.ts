import { HttpResponse } from '@angular/common/http';
import { Component, Input, OnChanges, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import dayjs from 'dayjs';
import { Subscription } from 'rxjs';
import { EventManager } from '../../../core/util/event-manager.service';
import { IBudgetItemPeriod } from '../../../entities/budget-item-period/budget-item-period.model';
import { EntityArrayResponseType } from '../../../entities/budget-item-period/service/budget-item-period.service';
import { IBudgetItem } from '../../../entities/budget-item/budget-item.model';
import { EVENT_LOAD_BUDGET_ITEMS, EVENT_REORDER_BUDGET_ITEM, EVENT_UPDATE_BUDGET_ITEM_ROW } from '../../config/mya.event.constants';
import { MyaBudgetItemPeriodService } from '../../mya-budget-item-period/service/mya-budget-item-period.service';
import { MyaBudgetItemDeleteDialogComponent } from '../delete/mya-budget-item-delete-dialog.component';
import { MyaBudgetItemService } from '../service/mya-budget-item.service';
import { MyaBudgetItemUpdateDialogComponent } from '../update/mya-budget-item-update-dialog.component';

@Component({
  selector: '[jhi-mya-budget-item-row]',
  templateUrl: './mya-budget-item-row.component.html',
})
export class MyaBudgetItemRowComponent implements OnInit, OnChanges, OnDestroy {
  @Input() budgetItem: IBudgetItem | null = null;
  @Input() monthSelected: Date | null = null;
  @Input() numberOfMonthsToDisplay = 0;
  @Input() isFirst: boolean | null = null;
  @Input() isLast: boolean | null = null;
  budgetItemPeriodss: IBudgetItemPeriod[][] | null = null;
  eventSubscriber: Subscription | null = null;

  constructor(
    protected activatedRoute: ActivatedRoute,
    private budgetItemPeriodService: MyaBudgetItemPeriodService,
    private budgetItemService: MyaBudgetItemService,
    private eventManager: EventManager,
    protected modalService: NgbModal
  ) {}

  ngOnInit(): void {
    this.registerChangeInBudgetItemRow();
  }
  ngOnChanges(): void {
    this.load();
  }
  ngOnDestroy(): void {
    this.eventManager.destroy(this.eventSubscriber!);
  }

  load(): void {
    if (this.monthSelected && this.budgetItem) {
      this.budgetItemPeriodService
        .findFromBudgetItemInRange(this.budgetItem, dayjs(this.monthSelected), this.numberOfMonthsToDisplay)
        .subscribe({
          next: (res: EntityArrayResponseType) => {
            this.budgetItemPeriodss = this.budgetItemPeriodService.groupByMonth(res.body, this.getMonthsToDisplay());
          },
        });
    }
  }

  reload(): void {
    if (this.budgetItem) {
      this.budgetItemService.find(this.budgetItem.id).subscribe((budgetItem: HttpResponse<IBudgetItem>) => {
        this.budgetItem = budgetItem.body;
        this.load();
      });
    }
  }

  getMonthsToDisplay(): dayjs.Dayjs[] {
    const months: dayjs.Dayjs[] = new Array(this.numberOfMonthsToDisplay);
    for (let i = 0; i < this.numberOfMonthsToDisplay; i++) {
      months[i] = dayjs(this.monthSelected).date(1).add(i, 'month');
    }
    return months;
  }

  isbudgetItemPeriodsEmpty(): boolean {
    if (this.budgetItemPeriodss) {
      for (let i = 0; i < this.budgetItemPeriodss.length; i++) {
        if (this.budgetItemPeriodss[i].length !== 0) {
          return false;
        }
      }
    }
    return true;
  }

  previousState(): void {
    window.history.back();
  }

  registerChangeInBudgetItemRow(): void {
    this.eventSubscriber = this.eventManager.subscribe(EVENT_UPDATE_BUDGET_ITEM_ROW + String(this.budgetItem!.id), () => this.reload());
  }

  delete(): void {
    const modalRef = this.modalService.open(MyaBudgetItemDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.budgetItem = this.budgetItem;
    modalRef.closed.subscribe({
      next: () => {
        this.eventManager.destroy(this.eventSubscriber!);
        this.eventManager.broadcast({ name: EVENT_LOAD_BUDGET_ITEMS, content: 'OK' });
      },
    });
    // unsubscribe not needed because closed completes on modal close
  }

  reorder(dir: string): void {
    this.eventManager.broadcast({ name: EVENT_REORDER_BUDGET_ITEM, content: { id: this.budgetItem?.id, direction: dir } });
  }

  edit(): void {
    const modalRef = this.modalService.open(MyaBudgetItemUpdateDialogComponent, { size: 'lg', backdrop: 'static', animation: true });
    modalRef.componentInstance.setBudgetItem(this.budgetItem);
  }
}
