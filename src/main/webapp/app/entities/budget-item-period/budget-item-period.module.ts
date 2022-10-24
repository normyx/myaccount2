import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { BudgetItemPeriodComponent } from './list/budget-item-period.component';
import { BudgetItemPeriodDetailComponent } from './detail/budget-item-period-detail.component';
import { BudgetItemPeriodUpdateComponent } from './update/budget-item-period-update.component';
import { BudgetItemPeriodDeleteDialogComponent } from './delete/budget-item-period-delete-dialog.component';
import { BudgetItemPeriodRoutingModule } from './route/budget-item-period-routing.module';

@NgModule({
  imports: [SharedModule, BudgetItemPeriodRoutingModule],
  declarations: [
    BudgetItemPeriodComponent,
    BudgetItemPeriodDetailComponent,
    BudgetItemPeriodUpdateComponent,
    BudgetItemPeriodDeleteDialogComponent,
  ],
})
export class BudgetItemPeriodModule {}
