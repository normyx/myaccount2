import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';

import { MyaCategoryIconModule } from '../mya-category/icon/mya-category-icon.module';
import { MyaDashboardModule } from '../mya-dashboard/mya-dashboard.module';
import { MyaOperationModule } from '../mya-operation/mya-operation.module';
import { MyaBankAccountListComponent } from './list/mya-bank-account-list.component';
import { MyaBankAccountRoutingModule } from './route/mya-bank-account-routing.module';
import { MyaBankAccountRowComponent } from './row/mya-bank-account-row.component';
import { MyaBankAccountSummaryComponent } from './summary/mya-bank-account-summary.component';

@NgModule({
  imports: [SharedModule, MyaBankAccountRoutingModule, MyaCategoryIconModule, MyaDashboardModule, MyaOperationModule],
  declarations: [MyaBankAccountSummaryComponent, MyaBankAccountListComponent, MyaBankAccountRowComponent],
  exports: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class MyaBankAccountModule {}
