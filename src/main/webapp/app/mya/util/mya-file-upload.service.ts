import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, Subscription } from 'rxjs';

export class MyaFileUploadModel {
  data: File | null = null;
  state: string | null = null;
  inProgress: boolean | null = null;
  progress: number | null = null;
  canRetry: boolean | null = null;
  canCancel: boolean | null = null;
  sub?: Subscription;
}

export function uploadFile(file: MyaFileUploadModel, url: string, http: HttpClient): Observable<HttpResponse<any>> {
  const fd = new FormData();
  fd.append('file', file.data!);
  return http.post(url, fd, {
    reportProgress: true,
    observe: 'response',
  });
}
