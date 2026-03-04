import { Routes } from '@angular/router';
import { CreateComponent } from './User/create/create.component';

export const routes: Routes = [
    {path: 'auth/register',component:CreateComponent,title:'Crear Usuario'}
];
