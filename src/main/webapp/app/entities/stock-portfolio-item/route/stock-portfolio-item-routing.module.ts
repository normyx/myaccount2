import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { StockPortfolioItemComponent } from '../list/stock-portfolio-item.component';
import { StockPortfolioItemDetailComponent } from '../detail/stock-portfolio-item-detail.component';
import { StockPortfolioItemUpdateComponent } from '../update/stock-portfolio-item-update.component';
import { StockPortfolioItemRoutingResolveService } from './stock-portfolio-item-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const stockPortfolioItemRoute: Routes = [
  {
    path: '',
    component: StockPortfolioItemComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: StockPortfolioItemDetailComponent,
    resolve: {
      stockPortfolioItem: StockPortfolioItemRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: StockPortfolioItemUpdateComponent,
    resolve: {
      stockPortfolioItem: StockPortfolioItemRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: StockPortfolioItemUpdateComponent,
    resolve: {
      stockPortfolioItem: StockPortfolioItemRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(stockPortfolioItemRoute)],
  exports: [RouterModule],
})
export class StockPortfolioItemRoutingModule {}
