import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { MyaOperationListComponent } from './list/mya-operation-list.component';

import { MyaCategoryIconModule } from '../mya-category/icon/mya-category-icon.module';
import { MyaOperationDeleteDialogComponent } from './delete/mya-operation-delete-dialog.component';
import { MyaOperationsImportComponent } from './import/mya-operations-import.component';
import { MyaOperationListPageComponent } from './list/mya-operation-list-page.component';
import { MyaOperationRoutingModule } from './route/mya-operation-routing.module';
import { MyaOperationUpdateDialogComponent } from './update/mya-operation-update-dialog.component';

@NgModule({
  imports: [SharedModule, MyaOperationRoutingModule, MyaCategoryIconModule],
  declarations: [
    MyaOperationListComponent,
    MyaOperationDeleteDialogComponent,
    MyaOperationUpdateDialogComponent,
    MyaOperationListPageComponent,
    MyaOperationsImportComponent,
  ],
  exports: [MyaOperationListComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class MyaOperationModule {}
