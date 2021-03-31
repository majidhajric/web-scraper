import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Link} from '../../data/model/link';

@Component({
  selector: 'app-link',
  templateUrl: './link.component.html',
  styleUrls: ['./link.component.scss']
})
export class LinkComponent implements OnInit {

  @Input() link: Link;
  @Output() deleteEvent: EventEmitter<number> = new EventEmitter<number>();

  constructor() { }

  ngOnInit(): void {
  }

  doDelete() {
    this.deleteEvent.emit(this.link.id);
  }
}
