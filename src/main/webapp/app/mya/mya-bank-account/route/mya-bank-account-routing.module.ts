import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from '../../../core/auth/user-route-access.service';

import { DESC, ASC } from '../../../config/navigation.constants';
import { MyaBankAccountSummaryComponent } from '../summary/mya-bank-account-summary.component';
import { MyaBankAccountListComponent } from '../list/mya-bank-account-list.component';

const myaBankAccountRoute: Routes = [
  {
    path: '',
    component: MyaBankAccountListComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'detail',
    component: MyaBankAccountSummaryComponent,
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
