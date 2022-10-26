import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { BsDatepickerModule } from 'ngx-bootstrap/datepicker';
import { MyaBudgetItemPeriodModule } from '../mya-budget-item-period/mya-budget-item-period.module';
import { MyaCategoryIconModule } from '../mya-category/icon/mya-category-icon.module';
import { MyaBudgetItemCreateDialogComponent } from './create/mya-budget-item-create-dialog.component';
import { MyaBudgetItemDeleteDialogComponent } from './delete/mya-budget-item-delete-dialog.component';
import { MyaBudgetItemListPageComponent } from './list/mya-budget-item-list-page.component';
import { MyaBudgetItemListComponent } from './list/mya-budget-item-list.component';

import { MyaBudgetItemRoutingModule } from './route/mya-budget-item-routing.module';
import { MyaBudgetItemRowComponent } from './row/mya-budget-item-row.component';
import { MyaBudgetItemUpdateDialogComponent } from './update/mya-budget-item-update-dialog.component';

@NgModule({
  imports: [SharedModule, MyaBudgetItemRoutingModule, MyaCategoryIconModule, MyaBudgetItemPeriodModule, BsDatepickerModule.forRoot()],
  declarations: [
    MyaBudgetItemListComponent,
    MyaBudgetItemRowComponent,
    MyaBudgetItemUpdateDialogComponent,
    MyaBudgetItemDeleteDialogComponent,
    MyaBudgetItemCreateDialogComponent,
    MyaBudgetItemListPageComponent,
  ],
  exports: [MyaBudgetItemRowComponent, MyaBudgetItemListComponent],
  entryComponents: [MyaBudgetItemRowComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class MyaBudgetItemModule {}
