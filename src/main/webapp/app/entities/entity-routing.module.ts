import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'application-user',
        data: { pageTitle: 'myaccount21App.applicationUser.home.title' },
        loadChildren: () => import('./application-user/application-user.module').then(m => m.ApplicationUserModule),
      },
      {
        path: 'bank-account',
        data: { pageTitle: 'myaccount21App.bankAccount.home.title' },
        loadChildren: () => import('./bank-account/bank-account.module').then(m => m.BankAccountModule),
      },
      {
        path: 'budget-item',
        data: { pageTitle: 'myaccount21App.budgetItem.home.title' },
        loadChildren: () => import('./budget-item/budget-item.module').then(m => m.BudgetItemModule),
      },
      {
        path: 'budget-item-period',
        data: { pageTitle: 'myaccount21App.budgetItemPeriod.home.title' },
        loadChildren: () => import('./budget-item-period/budget-item-period.module').then(m => m.BudgetItemPeriodModule),
      },
      {
        path: 'category',
        data: { pageTitle: 'myaccount21App.category.home.title' },
        loadChildren: () => import('./category/category.module').then(m => m.CategoryModule),
      },
      {
        path: 'operation',
        data: { pageTitle: 'myaccount21App.operation.home.title' },
        loadChildren: () => import('./operation/operation.module').then(m => m.OperationModule),
      },
      {
        path: 'sub-category',
        data: { pageTitle: 'myaccount21App.subCategory.home.title' },
        loadChildren: () => import('./sub-category/sub-category.module').then(m => m.SubCategoryModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
