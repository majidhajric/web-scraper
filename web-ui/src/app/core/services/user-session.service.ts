import {Injectable, OnDestroy} from '@angular/core';
import {AuthConfig, JwksValidationHandler, NullValidationHandler, OAuthEvent, OAuthService, UserInfo} from 'angular-oauth2-oidc';
import {environment} from '../../../environments/environment';
import {BehaviorSubject, Observable, Subject, Subscription} from 'rxjs';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class UserSessionService implements OnDestroy {


  constructor(private oauthService: OAuthService, private router: Router) {
    this.configure();
  }

  private userInfoSubject: Subject<UserInfo> = new BehaviorSubject<UserInfo>(null);
  private authEventSubscription: Subscription;

  private readonly authConfig: AuthConfig = {
    issuer: environment.authServer,
    redirectUri: window.location.origin,
    clientId: environment.authClientId,
    scope: 'openid profile email offline_access links_manage',
    responseType: 'code',
    // at_hash is not present in JWT token
    disableAtHashCheck: true,
    showDebugInformation: environment.debug === true,
    requireHttps: environment.production === true
  };

  ngOnDestroy(): void {
    if (this.authEventSubscription) {
      this.authEventSubscription.unsubscribe();
    }
  }

  private configure() {
    this.oauthService.configure(this.authConfig);
    this.oauthService.tokenValidationHandler = new NullValidationHandler();
    this.authEventSubscription = this.oauthService.events.subscribe((e: OAuthEvent) => this.OAuthEventHandler(e));
    if (this.authConfig.issuer) {
      this.oauthService.loadDiscoveryDocumentAndTryLogin()
        .catch((error) => {
          console.log(error.message);
        }).then(v => {
        if (this.oauthService.hasValidAccessToken() && this.oauthService.hasValidIdToken()) {
          this.oauthService.refreshToken();
        }
        this.oauthService.setupAutomaticSilentRefresh();
      });
    }
  }

  private OAuthEventHandler(event: OAuthEvent) {
    switch (event.type) {
      case 'token_received':
        this.oauthService.loadUserProfile()
          .then(userInfo => this.userInfoSubject.next(userInfo));
        break;
      case 'user_profile_loaded':
         this.router.navigate(['/home']);
         break;
      case 'session_terminated':
      case 'session_error':
        this.oauthService.logOut();
        break;
      case 'logout':
        this.userInfoSubject.next(null);
        this.router.navigate(['/']);
        break;
      default:
        break;
    }
  }

  public showLogInPage() {
    this.oauthService.initLoginFlow();
  }

  public logOut() {
    this.oauthService.logOut();
  }

  public getUserInfo(): Observable<UserInfo> {
    return this.userInfoSubject.asObservable();
  }
}
