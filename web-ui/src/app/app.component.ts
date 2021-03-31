import { Component } from '@angular/core';
import {AuthService} from './core/service/auth.service';
import {Subscription} from 'rxjs';
import {UserInfo} from 'angular-oauth2-oidc';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'web';

  isAuthenticated: boolean;
  sub: Subscription;
  constructor(private authService: AuthService) {
    this.sub = this.authService.isAuthenticated().subscribe(value => this.isAuthenticated = value);
  }

  logIn(): void {
    this.authService.logIn();
  }

  logOut(): void {
    this.authService.logOut();
  }


}
