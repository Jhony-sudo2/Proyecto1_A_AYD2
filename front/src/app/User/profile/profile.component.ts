import { Component } from '@angular/core';
import { UserService } from '../../Services/User/user.service';
import { AuthService } from '../../Services/Auth/auth.service';
import Swal from 'sweetalert2';
import { Wallet, WalletRecharge } from '../../interfaces/Wallet';
import { UpdatePassword, UpdateUser, User } from '../../interfaces/User';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InscriptionService } from '../../Services/Inscription/inscription.service';
import { Pay } from '../../interfaces/Inscription';
import { Pdf } from '../../Utils/PdfCreator';

@Component({
  selector: 'app-profile',
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent {
  usuario: User = {} as User;
  wallet: Wallet = {} as Wallet
  payments: Pay[] = []
  usuarioUpadte: UpdateUser = { image: '' } as UpdateUser
  updatePassword: UpdatePassword = {} as UpdatePassword
  rechargeWallet: WalletRecharge = {} as WalletRecharge
  historyRecharges: WalletRecharge[] = []
  activeTab: string = 'profile';
  showCurrent: boolean = false;
  showNew: boolean = false;
  pdfUtil: Pdf = new Pdf()
  tabs = [
    { id: 'profile', label: '👤 Perfil' },
    { id: 'password', label: '🔒 Contraseña' },
    { id: 'wallet', label: '💰 Wallet' },
    { id: 'payments', label: '🧾 Mis pagos' }

  ];
  constructor(private service: UserService, private authService: AuthService, private inscriptionService: InscriptionService) { }

  ngOnInit() {
    const id = this.authService.getUserId();
    this.service.getUserById(id).subscribe({
      next: (response) => {
        this.usuario = response;
        this.usuarioUpadte = {
          name: response.name, lastName: response.lastName,
          email: response.email, phone: response.phone, image: response.imageUrl
        };
      },
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });
    this.service.getWalletByUserId(id).subscribe({
      next: (response) => { this.wallet = response },
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });
    this.inscriptionService.getPaymentsByUserId(id).subscribe({
      next: (response) => { this.payments = response },
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    })
  }

  onImageSelected(event: Event) {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = () => {
      this.usuarioUpadte.image = reader.result as string;
    };
    reader.readAsDataURL(file);
  }

  guardarPerfil() {
    const id = this.authService.getUserId();
    this.service.updateUser(this.usuarioUpadte, id).subscribe({
      next: () => Swal.fire({ title: 'OK', text: 'Perfil actualizado', icon: 'success' }),
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });
  }

  cambiarPassword() {
    const id = this.authService.getUserId();
    this.service.udpatePassword(this.updatePassword, id).subscribe({
      next: () => {
        Swal.fire({ title: 'OK', text: 'Contraseña actualizada', icon: 'success' });
        this.updatePassword = {} as UpdatePassword;
      },
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });
  }

  recargar() {
    const id = this.authService.getUserId();
    this.service.rechargeWallet(this.rechargeWallet, id).subscribe({
      next: (response) => {
        this.wallet = response;
        Swal.fire({ title: 'OK', text: `Recarga de Q${this.rechargeWallet.amount} exitosa`, icon: 'success' });
        this.rechargeWallet = {} as WalletRecharge;
        this.cargarHistorial();
      },
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });
  }

  cargarHistorial() {
    const id = this.authService.getUserId();
    this.service.getRechargeHistory(id).subscribe({
      next: (response: any) => { this.historyRecharges = response; },
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });

  }
  get totalPagado(): number {
    return this.payments.reduce((s, p) => s + p.total, 0);
  }
  descargarComprobante(pay: Pay) {
    this.pdfUtil.generarPdfPago(pay);
  }
}
