jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { StockMarketDataService } from '../service/stock-market-data.service';

import { StockMarketDataDeleteDialogComponent } from './stock-market-data-delete-dialog.component';

describe('StockMarketData Management Delete Component', () => {
  let comp: StockMarketDataDeleteDialogComponent;
  let fixture: ComponentFixture<StockMarketDataDeleteDialogComponent>;
  let service: StockMarketDataService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [StockMarketDataDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(StockMarketDataDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(StockMarketDataDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(StockMarketDataService);
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
