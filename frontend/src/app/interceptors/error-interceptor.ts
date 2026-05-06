import {HttpErrorResponse, HttpEvent, HttpHandlerFn, HttpInterceptorFn, HttpRequest} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {inject} from '@angular/core';
import {NotificationService} from "../services/notification/notificationService";

export const errorInterceptor: HttpInterceptorFn = (
  request: HttpRequest<unknown>,
  next: HttpHandlerFn
): Observable<HttpEvent<unknown>> => {
  const notificationService = inject(NotificationService);

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      handleError(error, notificationService);
      return throwError(() => error);
    })
  );
};

function handleError(error: HttpErrorResponse, notificationService: NotificationService) {
  console.log('STATUS:', error.status);
  console.log('ERROR.ERROR:', error.error);

  if (error.status === 0) {
    const url = (error as any)?.url ?? '';
    if (url.includes('accounts.google.com')) return;

    notificationService.showError('Impossible de contacter le serveur');
  } else if (error.error instanceof ErrorEvent) {
    notificationService.showError(`Erreur client: ${error.error.message}`);
  } else {
    const errorDetails = error.error;
    const message = errorDetails?.message || 'Une erreur est survenue';
    const status = error.status;
    const code = errorDetails?.code || 'UNKNOWN_ERROR';

    notificationService.showError(`Erreur (${status}): ${message} (Code: ${code})`);
  }
}
