import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { BudgetItemPeriodComponent } from '../list/budget-item-period.component';
import { BudgetItemPeriodDetailComponent } from '../detail/budget-item-period-detail.component';
import { BudgetItemPeriodUpdateComponent } from '../update/budget-item-period-update.component';
import { BudgetItemPeriodRoutingResolveService } from './budget-item-period-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const budgetItemPeriodRoute: Routes = [
  {
    path: '',
    component: BudgetItemPeriodComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BudgetItemPeriodDetailComponent,
    resolve: {
      budgetItemPeriod: BudgetItemPeriodRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BudgetItemPeriodUpdateComponent,
    resolve: {
      budgetItemPeriod: BudgetItemPeriodRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BudgetItemPeriodUpdateComponent,
    resolve: {
      budgetItemPeriod: BudgetItemPeriodRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(budgetItemPeriodRoute)],
  exports: [RouterModule],
})
export class BudgetItemPeriodRoutingModule {}
