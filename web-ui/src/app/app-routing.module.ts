import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {ViewComponent} from './layout/view/view.component';
import {CreateLinkComponent} from './layout/create-link/create-link.component';
import {LandingComponent} from './layout/landing/landing.component';
import {AuthGuard} from './core/guards/auth.guard';

const routes: Routes = [
  {
    path: '',
    component: LandingComponent
  },
  {
    path: 'home',
    component: ViewComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'new',
    component: CreateLinkComponent,
    canActivate: [AuthGuard]
  },
  {
    path: '**',
    redirectTo: '/',
    pathMatch: 'full'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
