import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'mya-operation',
        data: { pageTitle: 'myaccount21App.operation.home.title' },
        loadChildren: () => import('./mya-operation/mya-operation.module').then(m => m.MyaOperationModule),
      },
      {
        path: 'mya-budget-item',
        data: { pageTitle: 'myaccount21App.budgetItem.home.title' },
        loadChildren: () => import('./mya-budget-item/mya-budget-item.module').then(m => m.MyaBudgetItemModule),
      },
      {
        path: 'mya-dashboard',
        data: { pageTitle: 'myaccount21App.budgetItem.home.title' },
        loadChildren: () => import('./mya-dashboard/mya-dashboard.module').then(m => m.MyaDashboardModule),
      },
      {
        path: 'mya-bank-account',
        data: { pageTitle: 'myaccount21App.budgetItem.home.title' },
        loadChildren: () => import('./mya-bank-account/mya-bank-account.module').then(m => m.MyaBankAccountModule),
      },
    ]),
  ],
})
export class MyaRoutingModule {}
