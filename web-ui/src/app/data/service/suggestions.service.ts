import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Suggestion} from '../model/suggestion';
import {environment} from '../../../environments/environment';

// @ts-ignore
@Injectable({
  providedIn: 'root'
})
export class SuggestionsService {

  readonly API_URL = environment.apiServer + '/api/suggestions';

  constructor(private httpClient: HttpClient) { }

  public getSuggestion(pageURL: string): Observable<Suggestion> {
    const httpParams = new HttpParams().set('pageURL', pageURL);
    return this.httpClient.get<Suggestion>(this.API_URL, {params: httpParams});
  }
}
