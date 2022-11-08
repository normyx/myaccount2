import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IStockPortfolioItem } from '../stock-portfolio-item.model';
import { StockPortfolioItemService } from '../service/stock-portfolio-item.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './stock-portfolio-item-delete-dialog.component.html',
})
export class StockPortfolioItemDeleteDialogComponent {
  stockPortfolioItem?: IStockPortfolioItem;

  constructor(protected stockPortfolioItemService: StockPortfolioItemService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.stockPortfolioItemService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
