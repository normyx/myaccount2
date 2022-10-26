import { Component, Input } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IconName, IconProp } from '@fortawesome/fontawesome-svg-core';

@Component({
  selector: 'jhi-mya-category-icon',
  templateUrl: './mya-category-icon.component.html',
  styleUrls: ['./mya-category-icon.scss'],
})
export class MyaCategoryIconComponent {
  @Input()
  categoryId: string | null = null;
  @Input()
  iconSize: string | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  getCategoryIconName(): IconName {
    let icon: IconName;

    switch (this.categoryId) {
      case '1': {
        icon = 'home';
        break;
      }
      case '2': {
        icon = 'cart-shopping';
        break;
      }
      case '3': {
        icon = 'money-bill-transfer';
        break;
      }
      case '4': {
        icon = 'children';
        break;
      }
      case '5': {
        icon = 'gamepad';
        break;
      }
      case '6': {
        icon = 'school';
        break;
      }
      case '7': {
        icon = 'utensils';
        break;
      }
      case '8': {
        icon = 'car';
        break;
      }
      case '9': {
        icon = 'bell-concierge';
        break;
      }
      case '10': {
        icon = 'circle-h';
        break;
      }
      case '11': {
        icon = 'euro-sign';
        break;
      }
      case '12': {
        icon = 'chart-pie';
        break;
      }
      case '13': {
        icon = 'circle-question';
        break;
      }
      default: {
        icon = 'close';
        break;
      }
    }
    return icon;
  }

  getCategoryIcon(): IconProp {
    return ['fas', this.getCategoryIconName()];
  }
}
