import { HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { BsDatepickerConfig, BsDatepickerViewMode } from 'ngx-bootstrap/datepicker';
import { setTheme } from 'ngx-bootstrap/utils';
import { EventManager } from '../../../core/util/event-manager.service';
import { ICategory } from '../../../entities/category/category.model';
import { CategoryService } from '../../../entities/category/service/category.service';
import { EVENT_LOAD_BUDGET_ITEMS } from '../../config/mya.event.constants';
import { MyaBudgetItemCreateDialogComponent } from '../create/mya-budget-item-create-dialog.component';

@Component({
  selector: 'jhi-mya-budget-item-list-page',
  templateUrl: './mya-budget-item-list-page.component.html',
})
export class MyaBudgetItemListPageComponent implements OnInit {
  currentSearch = '';
  monthSelected: Date = new Date();
  categories: ICategory[] | null = null;
  selectedCategoryId: number | null = null;

  bsConfig?: Partial<BsDatepickerConfig>;
  minMode: BsDatepickerViewMode = 'month';

  constructor(protected modalService: NgbModal, private eventManager: EventManager, protected categoryService: CategoryService) {
    setTheme('bs5');
  }

  ngOnInit(): void {
    this.categoryService.query().subscribe((res: HttpResponse<ICategory[]>) => (this.categories = res.body!));
    this.bsConfig = Object.assign(
      {},
      {
        minMode: this.minMode,
        containerClass: 'theme-default',
        isAnimated: true,
        dateInputFormat: 'MMM-YY',
      }
    );
  }

  create(): void {
    const modalRef = this.modalService.open(MyaBudgetItemCreateDialogComponent, { size: 'lg', backdrop: 'static', animation: true });
    modalRef.closed.subscribe({
      next: () => {
        this.load();
      },
    });
  }

  search(query: string): void {
    this.currentSearch = query;
  }

  load(): void {
    this.eventManager.broadcast({ name: EVENT_LOAD_BUDGET_ITEMS, content: 'OK' });
  }
}
