import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MaterialModule} from './shared/material.module';
import { ViewComponent } from './layout/view/view.component';
import { CreateLinkComponent } from './layout/create-link/create-link.component';
import {FormsModule} from '@angular/forms';
import { LinkComponent } from './layout/link/link.component';
import {ChipsMultiSelectComponent} from './layout/chips-multi-select/chips-multi-select.component';
import {CoreModule} from './core/core.module';
import { LandingComponent } from './layout/landing/landing.component';
import {HTTP_INTERCEPTORS} from '@angular/common/http';
import {HttpConfigInterceptor} from './core/interceptor/http-config-interceptor';
import {MAT_SNACK_BAR_DEFAULT_OPTIONS} from '@angular/material/snack-bar';

@NgModule({
  declarations: [
    AppComponent,
    ViewComponent,
    CreateLinkComponent,
    LinkComponent,
    ChipsMultiSelectComponent,
    LandingComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MaterialModule,
    FormsModule,
    CoreModule,
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: HttpConfigInterceptor, multi: true },
    {provide: MAT_SNACK_BAR_DEFAULT_OPTIONS, useValue: {duration: 2500}}
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
