import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IRealEstateItem } from '../real-estate-item.model';
import { RealEstateItemService } from '../service/real-estate-item.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './real-estate-item-delete-dialog.component.html',
})
export class RealEstateItemDeleteDialogComponent {
  realEstateItem?: IRealEstateItem;

  constructor(protected realEstateItemService: RealEstateItemService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.realEstateItemService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
