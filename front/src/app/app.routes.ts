import { Routes } from '@angular/router';
import { CreateComponent } from './User/create/create.component';
import { LoginComponent } from './Auth/login/login.component';
import { CreateCongressComponent } from './Congress/create-congress/create-congress.component';
import { ViewCongressComponent } from './Congress/view-congress/view-congress.component';
import { DetailCongressComponent } from './Congress/detail-congress/detail-congress.component';
import { CreateLocationComponent } from './Locations/create-location/create-location.component';
import { CreateRoomsComponent } from './Locations/create-rooms/create-rooms.component';
import { ProfileComponent } from './User/profile/profile.component';
import { CreateProposalComponent } from './Activities/create-proposal/create-proposal.component';
import { CreateActivityComponent } from './Activities/create-activity/create-activity.component';
import { UserManagerComponent } from './User/user-manager/user-manager.component';
import { InscriptionComponent } from './Congress/inscription/inscription.component';

export const routes: Routes = [
    {path:'auth/login',component:LoginComponent,title:'Login'},
    {path: 'auth/register',component:CreateComponent,title:'Crear Usuario'},
    {path:'congress/create', component: CreateCongressComponent, title: 'Crear Congreso'},
    {path:'congress',component:ViewCongressComponent,title:'Congresos'},
    {path:'congressDetails/:id',component:DetailCongressComponent,title:'Detalle congreso'},
    {path:'Locations',component:CreateLocationComponent,title:'Administracion de Ubicaciones'},
    {path:'locationRoom/:id',component:CreateRoomsComponent,title:'Administracion de salones'},
    {path:'profile',component:ProfileComponent,title:'Perfil'},
    {path:'activity/proposalCreate/:id',component:CreateProposalComponent,title:'Proponer trabajo'},
    {path:'activity/create/:id',component:CreateActivityComponent,title:'CrearActividad'},
    {path:'userManager',component:UserManagerComponent,title:'Administrar usuarios'},
    {path:'inscription/:id',component:InscriptionComponent,title:'Inscripcion'}


];
