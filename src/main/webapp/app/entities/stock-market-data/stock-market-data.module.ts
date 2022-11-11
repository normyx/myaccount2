import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { StockMarketDataComponent } from './list/stock-market-data.component';
import { StockMarketDataDetailComponent } from './detail/stock-market-data-detail.component';
import { StockMarketDataUpdateComponent } from './update/stock-market-data-update.component';
import { StockMarketDataDeleteDialogComponent } from './delete/stock-market-data-delete-dialog.component';
import { StockMarketDataRoutingModule } from './route/stock-market-data-routing.module';

@NgModule({
  imports: [SharedModule, StockMarketDataRoutingModule],
  declarations: [
    StockMarketDataComponent,
    StockMarketDataDetailComponent,
    StockMarketDataUpdateComponent,
    StockMarketDataDeleteDialogComponent,
  ],
})
export class StockMarketDataModule {}
