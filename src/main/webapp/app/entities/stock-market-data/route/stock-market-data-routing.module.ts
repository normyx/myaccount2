import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { StockMarketDataComponent } from '../list/stock-market-data.component';
import { StockMarketDataDetailComponent } from '../detail/stock-market-data-detail.component';
import { StockMarketDataUpdateComponent } from '../update/stock-market-data-update.component';
import { StockMarketDataRoutingResolveService } from './stock-market-data-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const stockMarketDataRoute: Routes = [
  {
    path: '',
    component: StockMarketDataComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: StockMarketDataDetailComponent,
    resolve: {
      stockMarketData: StockMarketDataRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: StockMarketDataUpdateComponent,
    resolve: {
      stockMarketData: StockMarketDataRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: StockMarketDataUpdateComponent,
    resolve: {
      stockMarketData: StockMarketDataRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(stockMarketDataRoute)],
  exports: [RouterModule],
})
export class StockMarketDataRoutingModule {}
