import { Routes } from '@angular/router';
import { CreateComponent } from './User/create/create.component';
import { LoginComponent } from './Auth/login/login.component';
import { CreateCongressComponent } from './Congress/create-congress/create-congress.component';
import { ViewCongressComponent } from './Congress/view-congress/view-congress.component';
import { DetailCongressComponent } from './Congress/detail-congress/detail-congress.component';

export const routes: Routes = [
    {path:'auth/login',component:LoginComponent,title:'Login'},
    {path: 'auth/register',component:CreateComponent,title:'Crear Usuario'},
    {path:'congress/create', component: CreateCongressComponent, title: 'Crear Congreso'},
    {path:'congress',component:ViewCongressComponent,title:'Congresos'},
    {path:'congressDetails/:id',component:DetailCongressComponent,title:'Detalle congreso'}
];
