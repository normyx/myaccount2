import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { MyaDashboardAccountComponent } from '../account/mya-dashboard-account.component';
import { MyaDashboardCategoryDetailsComponent } from '../category-details/mya-dashboard-category-details.component';
import { MyaDashboardCategoryComponent } from '../category/mya-dashboard-category.component';
import { MyaDashboardCategoryRoutingResolveService } from './mya-dashboard-category-routing-resolve.service';

const myaDashBoardRoute: Routes = [
  {
    path: 'account',
    component: MyaDashboardAccountComponent,
  },
  {
    path: 'category/:id/:month',
    component: MyaDashboardCategoryComponent,
    resolve: {
      category: MyaDashboardCategoryRoutingResolveService,
    },
  },
  {
    path: 'category/details/:id/:month',
    component: MyaDashboardCategoryDetailsComponent,
    resolve: {
      category: MyaDashboardCategoryRoutingResolveService,
    },
  },
];

@NgModule({
  imports: [RouterModule.forChild(myaDashBoardRoute)],
  exports: [RouterModule],
})
export class MyaDashboardRoutingModule {}
