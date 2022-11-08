import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { StockPortfolioItemComponent } from './list/stock-portfolio-item.component';
import { StockPortfolioItemDetailComponent } from './detail/stock-portfolio-item-detail.component';
import { StockPortfolioItemUpdateComponent } from './update/stock-portfolio-item-update.component';
import { StockPortfolioItemDeleteDialogComponent } from './delete/stock-portfolio-item-delete-dialog.component';
import { StockPortfolioItemRoutingModule } from './route/stock-portfolio-item-routing.module';

@NgModule({
  imports: [SharedModule, StockPortfolioItemRoutingModule],
  declarations: [
    StockPortfolioItemComponent,
    StockPortfolioItemDetailComponent,
    StockPortfolioItemUpdateComponent,
    StockPortfolioItemDeleteDialogComponent,
  ],
})
export class StockPortfolioItemModule {}
