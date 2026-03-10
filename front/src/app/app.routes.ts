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
import { MyCertificatesComponent } from './User/my-certificates/my-certificates.component';
import { authGuard, rolGuard } from './Guards/auth.Guard';

export const routes: Routes = [
    // Públicas (sin guard)
    { path: 'auth/login', component: LoginComponent, title: 'Login' },
    { path: 'auth/register', component: CreateComponent, title: 'Crear Usuario' },
    { path: 'forgot-password', component: ForgotPasswordComponent, title: 'Recuperar contraseña' },
    { path: 'congress', component: ViewCongressComponent, title: 'Congresos' },

    // Requieren login (cualquier rol)
    { path: 'congressDetails/:id', component: DetailCongressComponent, title: 'Detalle congreso', canActivate: [authGuard] },
    { path: 'profile', component: ProfileComponent, title: 'Perfil', canActivate: [authGuard] },
    { path: 'myCertificates', component: MyCertificatesComponent, title: 'Mis Certificados', canActivate: [authGuard] },
    { path: 'activity/proposalCreate/:id', component: CreateProposalComponent, title: 'Proponer trabajo', canActivate: [authGuard] },

    // Solo admin sistema (rol 1)
    { path: 'sysconfiguration', component: SystemConfigComponent, title: 'Configuración del sistema', canActivate: [rolGuard([1])] },
    { path: 'organizations', component: OrganizationComponent, title: 'Organizaciones', canActivate: [rolGuard([1])] },
    { path: 'userManager', component: UserManagerComponent, title: 'Administrar usuarios', canActivate: [rolGuard([1])] },
    { path: 'reports', component: ReportsComponent, title: 'Reportes', canActivate: [rolGuard([1])] },

    // Admin sistema (1) y admin congreso (2)
    { path: 'congress/create', component: CreateCongressComponent, title: 'Crear Congreso', canActivate: [rolGuard([1, 2])] },
    { path: 'Locations', component: CreateLocationComponent, title: 'Ubicaciones', canActivate: [rolGuard([1, 2])] },
    { path: 'locationRoom/:id', component: CreateRoomsComponent, title: 'Salones', canActivate: [rolGuard([1, 2])] },
    { path: 'congressManagement', component: CongressManagementComponent, title: 'Administración', canActivate: [rolGuard([1, 2])] },
    { path: 'activity/create/:id', component: CreateActivityComponent, title: 'Crear Actividad', canActivate: [rolGuard([1, 2])] },
    { path: 'proposalManager/:id', component: ProposalManagerComponent, title: 'Propuestas', canActivate: [rolGuard([1, 2])] },
    { path: 'atteendance/:id', component: AttendanceComponent, title: 'Asistencias', canActivate: [rolGuard([1, 2])] },
    { path: 'scientificCommittee/:id', component: ScientificCommitteeComponent, title: 'Comité científico', canActivate: [rolGuard([1, 2])] },
    { path: 'inscription/:id', component: InscriptionComponent, title: 'Inscripción', canActivate: [rolGuard([1, 2])] },

    // Fallback
    { path: '', redirectTo: 'congress', pathMatch: 'full' },
    { path: '**', redirectTo: 'congress' }
];
