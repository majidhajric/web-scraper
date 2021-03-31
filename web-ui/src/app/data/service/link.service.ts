import { Injectable } from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Page} from '../api/page';
import {Link} from '../model/link';
import {LinkRequest} from '../model/link-request';

@Injectable({
  providedIn: 'root'
})
export class LinkService {

  readonly API_URL = environment.apiServer + '/api/links';

  constructor(private httpClient: HttpClient) {
  }

  public getLinksPage(filter = '', page = 0, size = 5): Observable<Page<Link>> {
    return this.httpClient.get<Page<Link>>(this.API_URL + '/all',
      {
        params: new HttpParams()
          .set('filter', filter)
          .set('page', page.toString())
          .set('size', size.toString())
      });
  }

  public saveLink(linkRequest: LinkRequest): void {
    this.httpClient.post<LinkRequest>(this.API_URL, linkRequest)
      .subscribe();
  }

  public deleteLink(linkId: number): void {
    this.httpClient.delete(this.API_URL + '/' + linkId)
      .subscribe();
  }
}
