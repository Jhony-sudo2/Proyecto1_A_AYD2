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
import { ProposalManagerComponent } from './Activities/proposal-manager/proposal-manager.component';
import { AttendanceComponent } from './Activities/attendance/attendance.component';
import { SystemConfigComponent } from './system-config/system-config.component';
import { ScientificCommitteeComponent } from './Congress/scientific-committee/scientific-committee.component';
import { ForgotPasswordComponent } from './User/forgot-password/forgot-password.component';
import { ReportsComponent } from './reports/reports.component';
import { OrganizationComponent } from './organization/organization.component';
import { CongressManagementComponent } from './Congress/congress-management/congress-management.component';

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
    {path:'userManager',component:UserManagerComponent,title:'Administrar usuarios'},


    {path:'activity/create/:id',component:CreateActivityComponent,title:'CrearActividad'},
    {path:'inscription/:id',component:InscriptionComponent,title:'Inscripcion'},
    {path:'proposalManager/:id',component:ProposalManagerComponent,title:'Adminsitracion de propuestas'},
    {path:'atteendance/:id',component:AttendanceComponent,title:'Manejo de asistencias'},
    {path:'scientificCommittee/:id',component:ScientificCommitteeComponent,title:'Administrado comite cientifo'},

    {path:'sysconfiguration',component:SystemConfigComponent,title:'Configuracion del sistema'},
    {path:'forgot-password',component:ForgotPasswordComponent,title:'Recuperar contrasenia'},
    {path:'reports',component:ReportsComponent,title:'Reportes'},
    {path:'organizations',component:OrganizationComponent,title:'Administracion de organizaciones'},
    {path:'congressManagement',component:CongressManagementComponent,title:'Adminsitracion de congresos'}

];
