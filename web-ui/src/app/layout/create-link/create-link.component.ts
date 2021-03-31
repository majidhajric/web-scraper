import {AfterViewInit, Component, OnDestroy, OnInit, QueryList, ViewChildren} from '@angular/core';
import {BehaviorSubject, Observable, of} from 'rxjs';
import {LinkRequest} from '../../data/model/link-request';
import {ChipsMultiSelectComponent} from '../chips-multi-select/chips-multi-select.component';
import {Suggestion} from '../../data/model/suggestion';
import {SuggestionsService} from '../../data/service/suggestions.service';
import {LinkService} from '../../data/service/link.service';
import {catchError, finalize} from 'rxjs/operators';

@Component({
  selector: 'app-create-link',
  templateUrl: './create-link.component.html',
  styleUrls: ['./create-link.component.scss']
})
export class CreateLinkComponent implements OnInit, AfterViewInit, OnDestroy {
  private loadingSubject = new BehaviorSubject<boolean>(false);
  public loading$ = this.loadingSubject.asObservable();

  @ViewChildren(ChipsMultiSelectComponent) tagsSelectComponents: QueryList<ChipsMultiSelectComponent>;

  keywordsSelect: ChipsMultiSelectComponent;
  tagsSelect: ChipsMultiSelectComponent;

  url = '';
  keywords: string[];
  tags: string[];
  suggestion: Suggestion;
  linkRequest = {} as LinkRequest;
  selection: Set<string> = new Set<string>();

  constructor(private suggestionsService: SuggestionsService, private linkService: LinkService) { }

  ngOnInit(): void {
    this.resetValues();
  }

  ngAfterViewInit(): void {
    const selectComponents = this.tagsSelectComponents.toArray();
    this.keywordsSelect = selectComponents[0];
    this.tagsSelect = selectComponents[1];
    this.keywordsSelect.resetValues();
    this.tagsSelect.resetValues();
  }

  doAnalyse(): void {
    this.loadingSubject.next(true);
    this.suggestionsService.getSuggestion(this.url)
      .pipe(
        catchError(() => of({} as Suggestion)),
        finalize(() => this.loadingSubject.next(false))
      )
      .subscribe(suggestion => {
        this.suggestion = suggestion;
        this.keywords = suggestion.keywords;
        this.tags = suggestion.tags;
        this.linkRequest.tags = [];
        this.linkRequest.url = suggestion.url;
        this.linkRequest.title = suggestion.title;
        this.keywordsSelect.resetValues();
        this.tagsSelect.resetValues();
      });
  }

  tagsSelected(values: string[]): void {
    values.forEach(value => this.selection.add(value));
  }


  ngOnDestroy(): void {
    this.loadingSubject.complete();
  }

  doSave(): void {
    this.linkRequest.tags.push(...Array.from(this.selection));
    this.linkService.saveLink(this.linkRequest);
    this.resetValues();
  }

  private resetValues(): void {
    this.url = '';
    this.keywords = [];
    this.tags = [];
    this.selection.clear();
  }
}
