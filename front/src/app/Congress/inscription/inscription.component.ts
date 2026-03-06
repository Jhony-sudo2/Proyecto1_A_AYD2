import { Component } from '@angular/core';
import { InscriptionService } from '../../Services/Inscription/inscription.service';
import { ActivatedRoute } from '@angular/router';
import { CongressResponse } from '../../interfaces/Congress';
import { PayCongress } from '../../interfaces/Inscription';
import { CongressService } from '../../Services/Congress/congress.service';
import { AuthService } from '../../Services/Auth/auth.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-inscription',
  imports: [CommonModule, FormsModule],
  templateUrl: './inscription.component.html',
  styleUrl: './inscription.component.css'
})
export class InscriptionComponent {
  congress: CongressResponse = {} as CongressResponse
  payCongress: PayCongress = {} as PayCongress
  userId: number = 0
  constructor(private service: InscriptionService, private route: ActivatedRoute, private congressService: CongressService, private authService: AuthService) { }

  ngOnInit() {
    const congressId = parseInt(this.route.snapshot.paramMap.get('id')!);
    this.userId = this.authService.getUserId();

    this.congressService.getCongressById(congressId).subscribe({
      next: (response) => {
        this.congress = response;
        // Pre-rellena los IDs ocultos
        this.payCongress.congressId = congressId;
        this.payCongress.userId = this.userId;
      },
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });
  }

  inscription() {
    Swal.fire({
      title: '¿Confirmar pago?',
      html: `Se descontará <strong>Q${this.congress.price}</strong> de tu wallet`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Sí, pagar',
      cancelButtonText: 'Cancelar'
    }).then(result => {
      if (result.isConfirmed) {
        this.service.pay(this.payCongress).subscribe({
          next: () => Swal.fire({ title: '¡Inscrito!', text: 'Tu inscripción fue confirmada', icon: 'success' }),
          error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
        });
      }
    });
  }


}
