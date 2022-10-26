import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from '../../../core/auth/user-route-access.service';

import { ASC } from '../../../config/navigation.constants';
import { MyaBudgetItemPeriodRoutingResolveService } from '../../mya-budget-item-period/route/mya-budget-item-period-routing-resolve.service';
import { MyaBudgetItemPeriodUpdateDialogComponent } from '../../mya-budget-item-period/update/mya-budget-item-period-update-dialog.component';
import { MyaBudgetItemListPageComponent } from '../list/mya-budget-item-list-page.component';

const myaBudgetItemRoute: Routes = [
  {
    path: '',
    component: MyaBudgetItemListPageComponent,
    data: {
      defaultSort: 'order,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit-budget-item-period',
    component: MyaBudgetItemPeriodUpdateDialogComponent,
    resolve: {
      budgetItemPeriod: MyaBudgetItemPeriodRoutingResolveService,
    },
  },
];

@NgModule({
  imports: [RouterModule.forChild(myaBudgetItemRoute)],
  exports: [RouterModule],
})
export class MyaBudgetItemRoutingModule {}
