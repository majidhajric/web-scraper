import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import {MatTableDataSource} from '@angular/material/table';
import {Link} from '../../data/model/link';
import {tap} from 'rxjs/operators';
import {LinkService} from '../../data/service/link.service';
import {Page} from '../../data/api/page';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-view',
  templateUrl: './view.component.html',
  styleUrls: ['./view.component.scss']
})
export class ViewComponent implements AfterViewInit, OnDestroy {

  private sub: Subscription;
  page: Page<Link>;
  search: string;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  links: Link[] = [];

  constructor(private linksService: LinkService) {
  }

  ngAfterViewInit(): void {
    this.paginator.page
      .pipe(
        tap(() => this.loadLinks())
      )
      .subscribe();
    this.loadLinks();
  }

  loadLinks(): void {
    this.sub = this.linksService.getLinksPage(this.search, this.paginator.pageIndex, this.paginator.pageSize)
      .subscribe(page => {
        this.page = page;
        this.links = page.content;
      });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  deleteLink(linkId: number): void {
    this.linksService.deleteLink(linkId);
    this.loadLinks();
  }
}
