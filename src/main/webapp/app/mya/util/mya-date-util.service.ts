import { Injectable } from '@angular/core';
import { Dayjs } from 'dayjs';
import { DATE_FORMAT } from '../../config/input.constants';

@Injectable({
  providedIn: 'root',
})
export class MyaDateUtils {
  /**
   * Method to find the byte size of the string provides
   */
  convertToString(date: Dayjs): string {
    return date.format(DATE_FORMAT);
  }
}
