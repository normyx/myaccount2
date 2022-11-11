import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';

import { MyaCategoryIconModule } from '../mya-category/icon/mya-category-icon.module';
import { MyaDashboardModule } from '../mya-dashboard/mya-dashboard.module';
import { MyaOperationModule } from '../mya-operation/mya-operation.module';
import { MyaBankAccountBalanceUpdateDialogComponent } from './balanceupdate/mya-bank-account-balanceupdate-dialog.component';
import { MyaBankAccountListComponent } from './list/mya-bank-account-list.component';
import { MyaBankAccountRoutingModule } from './route/mya-bank-account-routing.module';
import { MyaBankAccountRowComponent } from './row/mya-bank-account-row.component';
import { MyaCurrentBankAccountSummaryComponent } from './summary/mya-current-bank-account-summary.component';
import { MyaPortfolioBankAccountSummaryComponent } from './summary/mya-portfolio-bank-account-summary.component';
import { MyaSavingsBankAccountSummaryComponent } from './summary/mya-savings-bank-account-summary.component';

@NgModule({
  imports: [SharedModule, MyaBankAccountRoutingModule, MyaCategoryIconModule, MyaDashboardModule, MyaOperationModule],
  declarations: [
    MyaCurrentBankAccountSummaryComponent,
    MyaSavingsBankAccountSummaryComponent,
    MyaPortfolioBankAccountSummaryComponent,
    MyaBankAccountListComponent,
    MyaBankAccountRowComponent,
    MyaBankAccountBalanceUpdateDialogComponent,
  ],
  exports: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class MyaBankAccountModule {}
