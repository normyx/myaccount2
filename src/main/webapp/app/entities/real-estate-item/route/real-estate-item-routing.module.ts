import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { RealEstateItemComponent } from '../list/real-estate-item.component';
import { RealEstateItemDetailComponent } from '../detail/real-estate-item-detail.component';
import { RealEstateItemUpdateComponent } from '../update/real-estate-item-update.component';
import { RealEstateItemRoutingResolveService } from './real-estate-item-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const realEstateItemRoute: Routes = [
  {
    path: '',
    component: RealEstateItemComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RealEstateItemDetailComponent,
    resolve: {
      realEstateItem: RealEstateItemRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RealEstateItemUpdateComponent,
    resolve: {
      realEstateItem: RealEstateItemRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RealEstateItemUpdateComponent,
    resolve: {
      realEstateItem: RealEstateItemRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(realEstateItemRoute)],
  exports: [RouterModule],
})
export class RealEstateItemRoutingModule {}
