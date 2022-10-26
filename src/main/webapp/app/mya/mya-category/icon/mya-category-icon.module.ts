import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MyaCategoryIconComponent } from './mya-category-icon.component';
import { SharedModule } from 'app/shared/shared.module';

@NgModule({
  imports: [SharedModule, CommonModule],
  declarations: [MyaCategoryIconComponent],
  exports: [MyaCategoryIconComponent],
  entryComponents: [MyaCategoryIconComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class MyaCategoryIconModule {}
