import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IStockMarketData } from '../stock-market-data.model';
import { StockMarketDataService } from '../service/stock-market-data.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './stock-market-data-delete-dialog.component.html',
})
export class StockMarketDataDeleteDialogComponent {
  stockMarketData?: IStockMarketData;

  constructor(protected stockMarketDataService: StockMarketDataService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.stockMarketDataService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
