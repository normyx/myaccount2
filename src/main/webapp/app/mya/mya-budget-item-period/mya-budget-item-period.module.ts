import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { MyaBudgetItemPeriodCellElementComponent } from './cell/mya-budget-item-period-cell-element.component';
import { MyaBudgetItemPeriodCellComponent } from './cell/mya-budget-item-period-cell.component';
import { MyaBudgetItemPeriodDeleteDialogComponent } from './delete/mya-budget-item-period-delete-dialog.component';
import { MyaBudgetItemPeriodUpdateDialogComponent } from './update/mya-budget-item-period-update-dialog.component';

@NgModule({
  imports: [SharedModule, RouterModule],
  exports: [MyaBudgetItemPeriodCellComponent],
  declarations: [
    MyaBudgetItemPeriodCellComponent,
    MyaBudgetItemPeriodCellElementComponent,
    MyaBudgetItemPeriodUpdateDialogComponent,
    MyaBudgetItemPeriodDeleteDialogComponent,
  ],
  entryComponents: [MyaBudgetItemPeriodCellComponent, MyaBudgetItemPeriodCellElementComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class MyaBudgetItemPeriodModule {}
