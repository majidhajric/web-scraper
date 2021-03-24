import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Link} from '../schema/link';
import {environment} from '../../../environments/environment';
import {Page} from '../schema/page';
import {LinkRequest} from '../schema/link-request';

@Injectable({
  providedIn: 'root'
})
export class LinksService {

  readonly API_URL = environment.apiURL + '/links';

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

  public getItem(id: string): Observable<Link> {
    return this.httpClient.get<Link>(this.API_URL + '/' + id);
  }

  public saveLink(linkRequest: LinkRequest) {
    return this.httpClient.post<LinkRequest>(this.API_URL, linkRequest)
   .subscribe();
  }
}
