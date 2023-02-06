import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from '../../../core/auth/user-route-access.service';

import { BankAccountRoutingResolveService } from 'app/entities/bank-account/route/bank-account-routing-resolve.service';
import { DESC } from '../../../config/navigation.constants';
import { MyaBankAccountListComponent } from '../list/mya-bank-account-list.component';
import { MyaCurrentBankAccountSummaryComponent } from '../summary/mya-current-bank-account-summary.component';
import { MyaSavingsBankAccountSummaryComponent } from '../summary/mya-savings-bank-account-summary.component';
import { MyaPortfolioBankAccountSummaryComponent } from '../summary/mya-portfolio-bank-account-summary.component';
import { MyaRealEstateBankAccountSummaryComponent } from '../summary/mya-real-estate-bank-account-summary.component';

const myaBankAccountRoute: Routes = [
  {
    path: '',
    component: MyaBankAccountListComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/current-detail',
    component: MyaCurrentBankAccountSummaryComponent,
    resolve: {
      bankAccount: BankAccountRoutingResolveService,
    },
    data: {
      defaultSort: 'date,' + DESC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/savings-detail',
    component: MyaSavingsBankAccountSummaryComponent,
    resolve: {
      bankAccount: BankAccountRoutingResolveService,
    },
    data: {
      defaultSort: 'date,' + DESC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/real-estate-detail',
    component: MyaRealEstateBankAccountSummaryComponent,
    resolve: {
      bankAccount: BankAccountRoutingResolveService,
    },
    data: {
      defaultSort: 'date,' + DESC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/portfolio-detail',
    component: MyaPortfolioBankAccountSummaryComponent,
    resolve: {
      bankAccount: BankAccountRoutingResolveService,
    },
    data: {
      defaultSort: 'date,' + DESC,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(myaBankAccountRoute)],
  exports: [RouterModule],
})
export class MyaBankAccountRoutingModule {}
