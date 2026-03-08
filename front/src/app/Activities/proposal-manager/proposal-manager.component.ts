import { Component } from '@angular/core';
import { ActivityService } from '../../Services/Activity/activity.service';
import { ActivatedRoute } from '@angular/router';
import { Proposal } from '../../interfaces/Activity';
import { ProposalState } from '../../interfaces/Enums';
import Swal from 'sweetalert2';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-proposal-manager',
  imports: [CommonModule],
  templateUrl: './proposal-manager.component.html',
  styleUrl: './proposal-manager.component.css'
})
export class ProposalManagerComponent {
  proprosals: Proposal[] = []
  ProposalState = ProposalState;
  activeFilter: string = 'all';

  filters = [
    { label: 'Todas', value: 'all', activeClass: 'bg-slate-800 text-white border-slate-600' },
    { label: 'Pendientes', value: ProposalState.PENDING, activeClass: 'bg-amber-500/20 text-amber-400 border-amber-500/40' },
    { label: 'Aprobadas', value: ProposalState.APPROVED, activeClass: 'bg-emerald-500/20 text-emerald-400 border-emerald-500/40' },
    { label: 'Rechazadas', value: ProposalState.REJECTED, activeClass: 'bg-red-500/20 text-red-400 border-red-500/40' },
  ];
  constructor(protected service: ActivityService, private route: ActivatedRoute) { }

  ngOnInit() {
    const id = parseInt(this.route.snapshot.paramMap.get('id')!)
    this.service.getProposalsByCongressId(id).subscribe({
      next: (response) => { this.proprosals = response }
    })
  }

  

  get filteredProposals(): Proposal[] {
    if (this.activeFilter === 'all') return this.proprosals;
    return this.proprosals.filter(p => p.state === this.activeFilter);
  }

  countByState(filter: string): number {
    if (filter === 'all') return this.proprosals.length;
    return this.proprosals.filter(p => p.state === filter).length;
  }

  stateLabel(state: ProposalState): string {
    const labels: Record<string, string> = {
      PENDING: 'Pendiente',
      APPROVED: 'Aprobada',
      REJECTED: 'Rechazada'
    };
    return labels[state] ?? state;
  }

  changeState(id: number, state: ProposalState) {
    const labels: Record<string, string> = {
      APPROVED: 'aprobar',
      REJECTED: 'rechazar',
      PENDING: 'revertir a pendiente'
    };
    Swal.fire({
      title: `¿Deseas ${labels[state]} esta propuesta?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Sí, confirmar',
      cancelButtonText: 'Cancelar'
    }).then(result => {
      if (result.isConfirmed) {
        this.service.updateProposal(id, state).subscribe({
          next: () => {
            const proposal = this.proprosals.find(p => p.id === id);
            if (proposal) proposal.state = state; // actualiza local sin recargar
            Swal.fire({ title: 'OK', text: 'Propuesta actualizada', icon: 'success' });
          },
          error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
        });
      }
    });
  }


}
