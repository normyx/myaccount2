jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { StockPortfolioItemService } from '../service/stock-portfolio-item.service';

import { StockPortfolioItemDeleteDialogComponent } from './stock-portfolio-item-delete-dialog.component';

describe('StockPortfolioItem Management Delete Component', () => {
  let comp: StockPortfolioItemDeleteDialogComponent;
  let fixture: ComponentFixture<StockPortfolioItemDeleteDialogComponent>;
  let service: StockPortfolioItemService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [StockPortfolioItemDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(StockPortfolioItemDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(StockPortfolioItemDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(StockPortfolioItemService);
    mockActiveModal = TestBed.inject(NgbActiveModal);
  });

  describe('confirmDelete', () => {
    it('Should call delete service on confirmDelete', inject(
      [],
      fakeAsync(() => {
        // GIVEN
        jest.spyOn(service, 'delete').mockReturnValue(of(new HttpResponse({ body: {} })));

        // WHEN
        comp.confirmDelete(123);
        tick();

        // THEN
        expect(service.delete).toHaveBeenCalledWith(123);
        expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
      })
    ));

    it('Should not call delete service on clear', () => {
      // GIVEN
      jest.spyOn(service, 'delete');

      // WHEN
      comp.cancel();

      // THEN
      expect(service.delete).not.toHaveBeenCalled();
      expect(mockActiveModal.close).not.toHaveBeenCalled();
      expect(mockActiveModal.dismiss).toHaveBeenCalled();
    });
  });
});
