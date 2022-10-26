import { Component, Input } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IBudgetItemPeriod } from '../../../entities/budget-item-period/budget-item-period.model';
import { IBudgetItem } from '../../../entities/budget-item/budget-item.model';

@Component({
  selector: '[mya-budget-item-period-cell]',
  templateUrl: './mya-budget-item-period-cell.component.html',
  styleUrls: ['./mya-budget-item-period-cell.component.scss'],
})
export class MyaBudgetItemPeriodCellComponent {
  @Input() budgetItemPeriods: IBudgetItemPeriod[] | null = null;
  @Input() budgetItem: IBudgetItem | null = null;
  @Input() editable: boolean | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}
}
