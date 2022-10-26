import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';

import { MyaCategoryIconModule } from '../mya-category/icon/mya-category-icon.module';
import { MyaDashboardModule } from '../mya-dashboard/mya-dashboard.module';
import { MyaOperationModule } from '../mya-operation/mya-operation.module';
import { MyaBankAccountRoutingModule } from './route/mya-bank-account-routing.module';
import { MyaBankAccountSummaryComponent } from './summary/mya-bank-account-summary.component';

@NgModule({
  imports: [SharedModule, MyaBankAccountRoutingModule, MyaCategoryIconModule, MyaDashboardModule, MyaOperationModule],
  declarations: [MyaBankAccountSummaryComponent],
  exports: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class MyaBankAccountModule {}
