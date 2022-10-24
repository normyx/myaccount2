import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IBudgetItemPeriod } from '../budget-item-period.model';

@Component({
  selector: 'jhi-budget-item-period-detail',
  templateUrl: './budget-item-period-detail.component.html',
})
export class BudgetItemPeriodDetailComponent implements OnInit {
  budgetItemPeriod: IBudgetItemPeriod | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ budgetItemPeriod }) => {
      this.budgetItemPeriod = budgetItemPeriod;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
