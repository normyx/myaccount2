import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from '../../../core/auth/user-route-access.service';

import { DESC } from '../../../config/navigation.constants';
import { MyaBankAccountSummaryComponent } from '../summary/mya-bank-account-summary.component';

const myaBankAccountRoute: Routes = [
  {
    path: '',
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
