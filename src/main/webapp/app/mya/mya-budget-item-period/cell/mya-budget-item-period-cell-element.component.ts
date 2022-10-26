import { HttpResponse } from '@angular/common/http';
import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import dayjs, { Dayjs } from 'dayjs';
import { IBudgetItemPeriod } from '../../../entities/budget-item-period/budget-item-period.model';
import { IBudgetItem } from '../../../entities/budget-item/budget-item.model';
import { MyaOperationService } from '../../mya-operation/service/mya-operation.service';
import { MyaBudgetItemPeriodDeleteDialogComponent } from '../delete/mya-budget-item-period-delete-dialog.component';
import { MyaBudgetItemPeriodUpdateDialogComponent } from '../update/mya-budget-item-period-update-dialog.component';

@Component({
  selector: '[mya-budget-item-period-cell-element]', // eslint-disable-line no-use-before-define
  templateUrl: './mya-budget-item-period-cell-element.component.html',
  styleUrls: ['./mya-budget-item-period-cell.component.scss'],
})
export class MyaBudgetItemPeriodCellElementComponent implements OnInit {
  @Input() budgetItemPeriod: IBudgetItemPeriod | null = null;
  @Input() budgetItem: IBudgetItem | null = null;
  @Input() editable: boolean | null = null;
  numberOfOperationsClose: number | null = null;

  constructor(protected activatedRoute: ActivatedRoute, private modalService: NgbModal, private myaOperationService: MyaOperationService) {}

  ngOnInit(): void {
    this.countOperationsCloseToBudgetItemPeriod();
  }

  edit(): void {
    const modalRef = this.modalService.open(MyaBudgetItemPeriodUpdateDialogComponent, { size: 'lg', backdrop: 'static', animation: true });
    modalRef.componentInstance.setBudgetItemPeriod(this.budgetItemPeriod, this.budgetItem);
  }

  new(): void {
    const modalRef = this.modalService.open(MyaBudgetItemPeriodUpdateDialogComponent, { size: 'lg', backdrop: 'static', animation: true });
    modalRef.componentInstance.setBudgetItemPeriod({ month: this.budgetItemPeriod?.month, modifyNexts: false }, this.budgetItem);
  }

  delete(): void {
    const modalRef = this.modalService.open(MyaBudgetItemPeriodDeleteDialogComponent, { backdrop: 'static', animation: true });
    modalRef.componentInstance.budgetItemPeriod = this.budgetItemPeriod;
  }

  countOperationsCloseToBudgetItemPeriod(): void {
    if (
      this.budgetItemPeriod &&
      this.budgetItem &&
      this.budgetItem.category &&
      this.editable &&
      !this.budgetItemPeriod.isSmoothed &&
      this.budgetItemPeriod.amount !== 0
    ) {
      this.myaOperationService
        .countOperationsCloseToBudgetItemPeriod(this.budgetItemPeriod.amount, this.budgetItem.category.id, this.budgetItemPeriod.date)
        .subscribe((res: HttpResponse<number>) => {
          this.numberOfOperationsClose = res.body!;
        });
    }
  }

  dateInFuture(date: Dayjs): boolean {
    const today = dayjs();
    return today.isBefore(date);
  }
}
