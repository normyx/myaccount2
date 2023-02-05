import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IRealEstateItem } from '../real-estate-item.model';

@Component({
  selector: 'jhi-real-estate-item-detail',
  templateUrl: './real-estate-item-detail.component.html',
})
export class RealEstateItemDetailComponent implements OnInit {
  realEstateItem: IRealEstateItem | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ realEstateItem }) => {
      this.realEstateItem = realEstateItem;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
