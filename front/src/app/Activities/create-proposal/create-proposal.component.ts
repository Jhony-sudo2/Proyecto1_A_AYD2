import { Component } from '@angular/core';
import { ActivityService } from '../../Services/Activity/activity.service';
import { CongressResponse } from '../../interfaces/Congress';
import { CongressService } from '../../Services/Congress/congress.service';
import { AuthService } from '../../Services/Auth/auth.service';
import { ActivatedRoute } from '@angular/router';
import { CreateProposal } from '../../interfaces/Activity';
import Swal from 'sweetalert2';
import { ProposalType } from '../../interfaces/Enums';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-create-proposal',
  imports: [FormsModule,CommonModule],
  templateUrl: './create-proposal.component.html',
  styleUrl: './create-proposal.component.css'
})
export class CreateProposalComponent {
  congress: CongressResponse = {} as CongressResponse
  userId: number = 0
  proposalCreate: CreateProposal = {} as CreateProposal
  ProposalType = ProposalType;
  constructor(private service: ActivityService, private congressService: CongressService, private authService: AuthService, private route: ActivatedRoute) { }
  ngOnInit() {
    const congressId = parseInt(this.route.snapshot.paramMap.get('id')!);
    this.userId = this.authService.getUserId();

    this.congressService.getCongressById(congressId).subscribe({
      next: (response) => {
        this.congress = response;
        this.proposalCreate.congressId = congressId;
        this.proposalCreate.userId = this.userId;
      }
    });
  }

  createProposal() {
    this.service.createProposal(this.proposalCreate).subscribe({
      next: () => Swal.fire({ title: 'OK', text: 'Propuesta enviada correctamente', icon: 'success' }),
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });
  }
}