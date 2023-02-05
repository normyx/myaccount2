import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RealEstateItemComponent } from './list/real-estate-item.component';
import { RealEstateItemDetailComponent } from './detail/real-estate-item-detail.component';
import { RealEstateItemUpdateComponent } from './update/real-estate-item-update.component';
import { RealEstateItemDeleteDialogComponent } from './delete/real-estate-item-delete-dialog.component';
import { RealEstateItemRoutingModule } from './route/real-estate-item-routing.module';

@NgModule({
  imports: [SharedModule, RealEstateItemRoutingModule],
  declarations: [
    RealEstateItemComponent,
    RealEstateItemDetailComponent,
    RealEstateItemUpdateComponent,
    RealEstateItemDeleteDialogComponent,
  ],
})
export class RealEstateItemModule {}
