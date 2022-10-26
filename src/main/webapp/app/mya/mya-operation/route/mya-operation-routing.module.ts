import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import { DESC } from '../../../config/navigation.constants';
import { MyaOperationsImportComponent } from '../import/mya-operations-import.component';
import { MyaOperationListPageComponent } from '../list/mya-operation-list-page.component';

const myaOperationRoute: Routes = [
  {
    path: '',
    component: MyaOperationListPageComponent,
    data: {
      defaultSort: 'date,' + DESC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'import',
    component: MyaOperationsImportComponent,

    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(myaOperationRoute)],
  exports: [RouterModule],
})
export class MyaOperationRoutingModule {}
